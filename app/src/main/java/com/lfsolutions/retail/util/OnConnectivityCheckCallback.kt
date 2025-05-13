package com.videotel.digital.util

interface OnConnectivityCheckCallback {
    fun onInternetConnectivityStatusResponse(isConnected: Boolean)
}