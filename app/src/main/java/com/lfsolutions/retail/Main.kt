package com.lfsolutions.retail

import android.app.Application
import com.google.gson.Gson
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants

class Main : Application() {

    private var memo: CreateUpdateAgreementMemoRequestBody? = null

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    fun getSession(): UserSession {
        return Gson().fromJson(AppSession.get(Constants.SESSION), UserSession::class.java)
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

    fun getAgreementMemo(): CreateUpdateAgreementMemoRequestBody? {
        if (memo == null)
            memo = CreateUpdateAgreementMemoRequestBody()
        return memo
    }

    fun clearAgreementMemo() {
        memo = null
    }


    companion object {
        lateinit var app: Main
    }
}