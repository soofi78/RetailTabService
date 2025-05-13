package com.lfsolutions.retail.util

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import java.io.File


object DocumentDownloader {
    fun download(nameOfFile: String, url: String, context: Context): Long {
        val downloadDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        val file = File(downloadDirectory, nameOfFile)
        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            .setTitle(nameOfFile)
            .setDescription("Downloading")
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
        val downloadID = downloadManager!!.enqueue(request) //
        return downloadID
    }
}