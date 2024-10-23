package com.lfsolutions.retail.ui.settings.printer

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.graphics.Bitmap
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

        if (printer == null || connection?.device?.address != device.address) {
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
            this.printer = EscPosPrinter(connection, 203, 48f, 32)
        }
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
        if (connection != null && connection?.isConnected == true)
            return connection else connection?.disconnect()

        connection = try {
            BluetoothConnection(getDefaultPrinterBluetoothDevice())
        } catch (exception: EscPosConnectionException) {
            exception.printStackTrace()
            null
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
        return connection?.connect()
    }

    @Synchronized
    fun print(printableText: String) {
        GlobalScope.launch(Dispatchers.IO) {
            if (printer == null || connection == null) {
                this@PrinterManager.connection = getDefaultPrinterBluetoothConnection()
                this@PrinterManager.printer = EscPosPrinter(connection, 203, 48f, 32)
            }
            var printText = printableText
            val urls = extractUrls(printText)
            urls.forEach {
                val bitmap: Bitmap = Glide.with(Main.app).asBitmap().load(it).submit().get()
                val targetWidth = 383 // 48mm printing zone with 203dpi => 383px
                val rescaledBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    targetWidth,
                    Math.round(
                        (bitmap.getHeight()
                            .toFloat()) * (targetWidth.toFloat()) / (bitmap.getWidth()
                            .toFloat())
                    ),
                    true
                )
                val imageBase64 =
                    PrinterTextParserImg.bitmapToHexadecimalString(printer, rescaledBitmap)
                printText = printText.replace("@@@$it", "[C]<img>$imageBase64</img>\n")
            }
            printer?.printFormattedTextAndCut(printText, 40f)
            connection?.write(byteArrayOf(0x1D, 0x56, 0x41, 0x10))

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


}