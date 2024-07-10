package com.lfsolutions.retail.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lfsolutions.retail.Main
import com.videotel.digital.util.OnConnectivityCheckCallback
import java.net.*
import java.util.*


object Internet {
    fun isConnected(mContext: Context?): Boolean {
        if (mContext == null) return true
        val connectivity =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    fun getLocalIpAddress(): String {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress() ?: ""
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return ""
    }

    fun getWifiName(): String {
        var ssid = ""
        try {
            val wifiManager =
                Main.app.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var wifiInfo: WifiInfo? = null
            if (wifiManager != null) wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null) ssid = wifiInfo.ssid
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return ssid.replace("\"", "").toString()
        }
    }

    fun type(mContext: Context?): NetworkInfo? {
        val connManager = mContext?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        val wifi = connManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val etherNet = connManager!!.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)

        if (wifi!!.isConnected) {
            return wifi
        } else if (etherNet!!.isConnected) {
            return etherNet
        } else {
            return null
        }
    }


    @Synchronized
    fun hasActiveInternetConnection(callback: OnConnectivityCheckCallback) {
        Handler(Looper.getMainLooper()).postDelayed({
            Thread {
                if (isConnected(Main.app)) {
                    try {
                        val urlc =
                            URL("http://www.google.com").openConnection() as HttpURLConnection
                        urlc.setRequestProperty("User-Agent", "Test")
                        urlc.setRequestProperty("Connection", "close")
                        urlc.connectTimeout = 1500
                        urlc.connect()
                        if (urlc.responseCode == 200) callback.onInternetConnectivityStatusResponse(
                            true
                        )
                        urlc.disconnect()
                    } catch (e: Exception) {
                        Log.e("", "Error checking internet connection", e)
                        callback.onInternetConnectivityStatusResponse(false)
                    }
                } else {
                    callback.onInternetConnectivityStatusResponse(false)
                }
            }.start()
        }, 3000)
    }
}