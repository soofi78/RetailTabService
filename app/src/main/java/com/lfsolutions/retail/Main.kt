package com.lfsolutions.retail

import android.app.Application
import com.google.gson.Gson
import com.lfsolutions.retail.model.LoginResponse
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants

class Main : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    fun getSession(): LoginResponse {
        return Gson().fromJson(AppSession.get(Constants.SESSION), LoginResponse::class.java)
    }

    fun getBaseUrl(): String {
        return AppSession[Constants.baseUrl]
    }

    fun logout() {
        AppSession.clearSharedPref()
    }

    fun isLoggedIn(): Boolean {
        return AppSession.getBoolean(Constants.IS_LOGGED_IN)
    }


    companion object {
        lateinit var app: Main
    }
}