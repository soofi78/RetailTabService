package com.lfsolutions.retail

import android.app.Application
import android.content.Intent
import com.google.gson.Gson
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.StockTransferRequestBody
import com.lfsolutions.retail.model.service.ComplaintServiceRequest
import com.lfsolutions.retail.ui.login.LoginActivity
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants


class Main : Application() {

    private var stockTransferRequest: StockTransferRequestBody? = null
    private var complaintService: ComplaintServiceRequest? = null
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
        if (memo == null) memo = CreateUpdateAgreementMemoRequestBody()
        return memo
    }

    fun getComplaintService(): ComplaintServiceRequest? {
        if (complaintService == null) complaintService = ComplaintServiceRequest()
        return complaintService
    }

    fun clearAgreementMemo() {
        memo = null
    }

    fun clearComplaintService() {
        complaintService = null
    }

    fun getStockTransferRequestObject(): StockTransferRequestBody {
        if (stockTransferRequest == null) {
            stockTransferRequest = StockTransferRequestBody()
            stockTransferRequest?.locationId = getSession().defaultLocationId
        }
        return stockTransferRequest!!
    }

    fun cleaStockTransfer() {
        stockTransferRequest = null
    }

    fun sessionExpired() {
        AppSession.clearSharedPref()
        startActivity(Intent(app, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }


    companion object {
        lateinit var app: Main
    }
}