package com.lfsolutions.retail.ui.settings.printer

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.provider.Settings
import android.util.Log
import com.bumptech.glide.Glide
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.videotel.digital.util.Notify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Matcher
import java.util.regex.Pattern


object PrinterManager {

    var connection: BluetoothConnection? = null
    var printer: EscPosPrinter? = null

    @SuppressLint("MissingPermission")
    suspend fun sendConnectionPrint(device: BluetoothDevice): Boolean {
        printer?.disconnectPrinter()
        printer = null
        connection?.disconnect()
        connection = try {
            BluetoothConnection(device)
        } catch (exception: EscPosConnectionException) {
            exception.printStackTrace()
            null
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }

        if (connection == null) {
            withContext(Dispatchers.Main) {
                Notify.toastLong("Unable to connect printer")
            }
            return false
        }
        val printerWidth = AppSession[Constants.PRINTER_WIDTH, "80 mm"]?.replace("mm", "")?.trim()?.toFloat() ?: 80f
        val charactersPerLine =   AppSession.getInt(Constants.CHARACTER_PER_LINE, 32)
        Log.e("PrinterManager","sendConnectionPrint printerWidth $printerWidth")
        Log.e("PrinterManager","sendConnectionPrint charactersPerLine $charactersPerLine")
        val dpi = (203 / 48) * printerWidth
        this.printer = EscPosPrinter(connection, 203, printerWidth, charactersPerLine)

        val rawText = """  
        [C]<b><font size='big'>Test Print</font></b>
        [C]================================
        [C]${device.name}
        [C]${device.address}
        [C]<b>Connection Successful</b>
        [C]================================
        """.trimIndent()
        this.printer?.printFormattedTextAndCut(rawText)
        return true
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothPrinterDevices(): ArrayList<BluetoothDevice> {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = mBluetoothAdapter.bondedDevices
        val devices = ArrayList<BluetoothDevice>()
        if (pairedDevices.isNotEmpty()) {
            pairedDevices.forEach {
                devices.add(it)
            }
        }
        return devices
    }

    fun openBluetoothSettings(activity: Activity) {
        val intentOpenBluetoothSettings = Intent()
        intentOpenBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS)
        activity.startActivity(intentOpenBluetoothSettings)
    }

    fun setDefaultPrinter(device: BluetoothDevice) {
        AppSession.put(Constants.SELECTED_BLUETOOTH, device.address.toString())
    }

    @Synchronized
    fun getDefaultPrinterBluetoothDevice(): BluetoothDevice? {
        var device: BluetoothDevice? = null
        getBluetoothPrinterDevices().forEach {
            if (it.address == AppSession[Constants.SELECTED_BLUETOOTH]) {
                device = it
            }
        }
        return device
    }

    @Synchronized
    fun getDefaultPrinterBluetoothConnection(): BluetoothConnection? {
        if (connection != null && connection?.isConnected == true) return connection else connection?.disconnect()

        connection = try {
            BluetoothConnection(getDefaultPrinterBluetoothDevice())
        } catch (exception: EscPosConnectionException) {
            exception.printStackTrace()
            null
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }

        try {
            return connection?.connect()
        } catch (exception: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Notify.toastLong("Unable to connect to printer...")
            }
            return null
        }
    }
    @Synchronized
    fun print(printableText: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val printerWidth = AppSession[Constants.PRINTER_WIDTH, "80 mm"]?.replace("mm", "")?.trim()?.toFloat() ?: 80f
                val charactersPerLine =   AppSession.getInt(Constants.CHARACTER_PER_LINE, 48)
                Log.i("PrinterManager","print printerWidth $printerWidth")
                Log.i("PrinterManager","print charactersPerLine $charactersPerLine")

                if (printer == null || connection == null) {
                    this@PrinterManager.connection = getDefaultPrinterBluetoothConnection()
                    this@PrinterManager.printer = EscPosPrinter(
                        connection,
                        203,
                        printerWidth,
                        charactersPerLine
                    )
                }
                var printText = printableText
                val urls = extractUrls(printText)

                val foreignTextRegex = Regex("<a(?:\\s+size=(\\d+))?>(.*?)</a>")

                val matchResults = foreignTextRegex.findAll(printText).toList()

                if (matchResults.isNotEmpty()) {
                    matchResults.forEach { matchResult ->
                        val sizeText = matchResult.groupValues[1]
                        val contentInsideATags = matchResult.groupValues[2]

                        val fontSize = sizeText.toIntOrNull() ?: 32  // default font size

                        val cleanedText = contentInsideATags.replace(Regex("^\\[.[^]]*]"), "")
                        val direction = when {
                            contentInsideATags.startsWith("[C]") -> "C"
                            contentInsideATags.startsWith("[R]") -> "R"
                            else -> "L"
                        }

                        val imgTag = if (isEnglishOnly(cleanedText)) {
                            "[$direction]$cleanedText"
                        } else {
                            val imgHex = PrinterTextParserImg.bitmapToHexadecimalString(
                                printer,
                                getMultiLangTextAsImage(cleanedText, direction, fontSize.toFloat())
                            )
                            "[$direction]<img>$imgHex</img>"
                        }

                        printText = printText.replace(matchResult.value, imgTag)
                    }
                }


                urls.forEach {
                    try {
                        val bitmap: Bitmap =
                            Glide.with(Main.app).asBitmap().load(it.replace("\\", "/").trim())
                                .submit().get()
                        val targetWidth = 383 // 48mm printing zone with 203dpi => 383px
                        val rescaledBitmap = Bitmap.createScaledBitmap(
                            bitmap, targetWidth, Math.round(
                                (bitmap.getHeight()
                                    .toFloat()) * (targetWidth.toFloat()) / (bitmap.getWidth()
                                    .toFloat())
                            ), true
                        )
                        val imageBase64 =
                            PrinterTextParserImg.bitmapToHexadecimalString(printer, rescaledBitmap)
                        printText =
                            printText.replace("@@@$it", "[C]<img>$imageBase64</img>\n".trim())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        printText = printText.replace("@@@$it", "")
                    }
                }

                Log.i("PrintManger",printText)
                printer?.printFormattedTextAndCut(printText, 100f)
//                connection?.write(byteArrayOf(0x1D, 0x56, 0x41, 0x10))
//                printer?.disconnectPrinter()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Notify.toastLong("Unable to print please check connection")
                }
            }
        }
    }

    fun extractUrls(text: String): List<String> {
        val containedUrls: MutableList<String> = ArrayList()
        val urlRegex =
            "((https?|@@@|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;\$()~_?\\+\\-=\\\\\\.&\\x20]*)"
        val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher: Matcher = pattern.matcher(text)

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)))
        }
        return containedUrls
    }


    fun getMultiLangTextAsImage(
        text: String,
        direction: String,
        textSize: Float,
        typeface: Typeface? = Typeface.MONOSPACE,
        paperWidthMm: Float = 80f,
        dpi: Int = 203,
        lineHeightPx: Int = 30 // ESC/POS line height
    ): Bitmap {
        val align = when (direction) {
            "R" -> Paint.Align.RIGHT
            "C" -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }

        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            this.textSize = textSize
            textAlign = align
            if (typeface != null) this.typeface = typeface
        }

        val fontMetrics = paint.fontMetrics
        val textBaselineOffset = (lineHeightPx / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)

        val xWidth = (paperWidthMm / 25.4f) * dpi
        val printDataList = mutableListOf<PrintData>()
        var yPos = lineHeightPx.toFloat()

        val splitLines = text.split("\\n".toRegex())
        for ((index, line) in splitLines.withIndex()) {
            var remaining = line
            while (remaining.isNotEmpty()) {
                var sub = remaining
                while (paint.measureText(sub) > xWidth && sub.length > 1) {
                    sub = sub.dropLast(1)
                }

                val xPos = when (align) {
                    Paint.Align.CENTER -> xWidth / 2f
                    Paint.Align.RIGHT -> xWidth
                    else -> 0f
                }

                printDataList.add(PrintData(xPos, yPos, sub))
                yPos += lineHeightPx
                remaining = remaining.removePrefix(sub)
            }

            if (index != splitLines.lastIndex) {
                yPos += lineHeightPx
            }
        }

        val bitmapHeight = (printDataList.size * lineHeightPx).coerceAtLeast(lineHeightPx)
        val bitmap = Bitmap.createBitmap(xWidth.toInt(), bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        for (data in printDataList) {
            canvas.drawText(data.text, data.xPos, data.yPos - (lineHeightPx - textBaselineOffset), paint)
        }

        return bitmap
    }
    data class PrintData(val xPos: Float, val yPos: Float, val text: String)

    private fun isEnglishOnly(s: String): Boolean {
        for (character in s) {
            val block = Character.UnicodeBlock.of(character)
            if (block !in setOf(
                    Character.UnicodeBlock.BASIC_LATIN,
                    Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
                    Character.UnicodeBlock.LATIN_EXTENDED_A,
                    Character.UnicodeBlock.GENERAL_PUNCTUATION
                )
            ) {
                return false // Found a non-English character
            }
        }
        return true // All characters are English
    }


}