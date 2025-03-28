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
        val printerWidth =
            AppSession[Constants.PRINTER_WIDTH, "48 mm"]?.replace("mm", "")?.trim()?.toFloat()
                ?: 48f
        val dpi = (203 / 48) * printerWidth
        this.printer = EscPosPrinter(
            connection, 300, printerWidth, AppSession.getInt(Constants.CHARACTER_PER_LINE, 32)
        )

        val rawText = """  
        [C]<b><font size='big'>Test Print</font></b>
        [C]================================
        [C]${device.name}
        [C]${device.address}
        [C]<b>Connection Successful</b>
        [C]================================
        """.trimIndent()
        this.printer?.printFormattedText(rawText)
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
                if (printer == null || connection == null) {
                    this@PrinterManager.connection = getDefaultPrinterBluetoothConnection()
                    val printerWidth =
                        AppSession[Constants.PRINTER_WIDTH, "48 mm"]?.replace("mm", "")?.trim()
                            ?.toFloat() ?: 48f
                    this@PrinterManager.printer = EscPosPrinter(
                        connection,
                        203,
                        printerWidth,
                        AppSession.getInt(Constants.CHARACTER_PER_LINE, 32)
                    )
                }
                var printText = printableText
                val urls = extractUrls(printText)

                val foreignTextRegex = Regex("<a>(.*?)</a>")

                // Use findAll to get all matches
                val matchResults = foreignTextRegex.findAll(printText).toList()
                if (matchResults != null) {
                    matchResults.forEach { matchResult ->
                        // Replace each matched text inside <a></a> with an <img> tag
                        val contentInsideATags = matchResult.groupValues[1] // Extract text inside <a></a>
                        val cleanedText = contentInsideATags.replace(Regex("^\\[.[^]]*]"), "").trim()

                        val direction = when {
                            contentInsideATags.startsWith("[C]") -> "C"
                            contentInsideATags.startsWith("[R]") -> "R"
                            else -> "L"
                        }

                        val imgTag = if (isEnglishOnly(cleanedText)) {
                            "[$direction]$cleanedText"
                        }else{
                            "[$direction]<img>${PrinterTextParserImg.bitmapToHexadecimalString(printer, getMultiLangTextAsImage(cleanedText,direction))}</img>"

                        }// Replace the matched text with the <img> tag in printText
                        printText = printText.replace(matchResult.value, imgTag)
                    }
                    // Finally, remove the <a></a> tags
                    printText = printText.replace("<a>", "").replace("</a>", "")

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
                printer?.printFormattedTextAndCut(printText, 40f)
                connection?.write(byteArrayOf(0x1D, 0x56, 0x41, 0x10))
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
        direction:String,
        textSize: Float = 28f,
        typeface: Typeface? = Typeface.MONOSPACE
    ): Bitmap {
        val align =
            if (direction.contentEquals("R"))
                Paint.Align.RIGHT
            else if (direction.contentEquals("C"))
                Paint.Align.CENTER
            else Paint.Align.LEFT

        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.textSize = textSize
        if (typeface != null) paint.typeface = typeface

        // A real printlabel width (pixel)
        val xWidth = 385f

        // A height per text line (pixel)
        var xHeight = textSize + 5

        // it can be changed if the align's value is CENTER or RIGHT
        var xPos = 0f

        // If the original string data's length is over the width of print label,
        // or '\n' character included,
        // it will be increased per line gerneating.
        var yPos = 27f

        // If the original string data's length is over the width of print label,
        // or '\n' character included,
        // each lines splitted from the original string are added in this list
        // 'PrintData' class has 3 members, x, y, and splitted string data.
        val printDataList: MutableList<PrintData> = java.util.ArrayList()

        // if '\n' character included in the original string
        val tmpSplitList = text.split("\\n".toRegex()).toTypedArray()
        for (i in 0..tmpSplitList.size - 1) {
            val tmpString = tmpSplitList[i]

            // calculate a width in each split string item.
            var widthOfString = paint.measureText(tmpString)

            // If the each split string item's length is over the width of print label,
            if (widthOfString > xWidth) {
                var lastString = tmpString
                while (!lastString.isEmpty()) {
                    var tmpSubString = ""

                    // retrieve repeatedly until each split string item's length is
                    // under the width of print label
                    while (widthOfString > xWidth) {
                        tmpSubString = if (tmpSubString.isEmpty()) lastString.substring(
                            0, lastString.length - 1
                        ) else tmpSubString.substring(0, tmpSubString.length - 1)
                        widthOfString = paint.measureText(tmpSubString)
                    }

                    // this each split string item is finally done.
                    if (tmpSubString.isEmpty()) {
                        // this last string to print is need to adjust align
                        if (align == Paint.Align.CENTER) {
                            if (widthOfString < xWidth) {
                                xPos = (xWidth - widthOfString) / 2
                            }
                        } else if (align == Paint.Align.RIGHT) {
                            if (widthOfString < xWidth) {
                                xPos = xWidth - widthOfString
                            }
                        }
                        printDataList.add(PrintData(xPos, yPos, lastString))
                        lastString = ""
                    } else {
                        // When this logic is reached out here, it means,
                        // it's not necessary to calculate the x position
                        // 'cause this string line's width is almost the same
                        // with the width of print label
                        printDataList.add(PrintData(0f, yPos, tmpSubString))

                        // It means line is needed to increase
                        yPos += 27f
                        xHeight += 30f
                        lastString = lastString.replaceFirst(tmpSubString.toRegex(), "")
                        widthOfString = paint.measureText(lastString)
                    }
                }
            } else {
                // This split string item's length is
                // under the width of print label already at first.
                if (align == Paint.Align.CENTER) {
                    if (widthOfString < xWidth) {
                        xPos = (xWidth - widthOfString) / 2
                    }
                } else if (align == Paint.Align.RIGHT) {
                    if (widthOfString < xWidth) {
                        xPos = xWidth - widthOfString
                    }
                }
                printDataList.add(PrintData(xPos, yPos, tmpString))
            }
            if (i != tmpSplitList.size - 1) {
                // It means the line is needed to increase
                yPos += 27f
                xHeight += 30f
            }
        }

        // If you want to print the text bold
        //paint.setTypeface(Typeface.create(null as String?, Typeface.BOLD))

        // create bitmap by calculated width and height as upper.
        val bm: Bitmap =
            Bitmap.createBitmap(xWidth.toInt(), xHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        canvas.drawColor(Color.WHITE)
        for (tmpItem in printDataList) canvas.drawText(
            tmpItem.text, tmpItem.xPos, tmpItem.yPos, paint
        )
        return bm
    }

    internal class PrintData(var xPos: Float, var yPos: Float, var text: String) {

        fun getxPos(): Float {
            return xPos
        }

        fun setxPos(xPos: Float) {
            this.xPos = xPos
        }

        fun getyPos(): Float {
            return yPos
        }

        fun setyPos(yPos: Float) {
            this.yPos = yPos
        }
    }

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