package com.lfsolutions.retail

import android.app.Application
import android.content.Intent
import com.google.gson.Gson
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.memo.CreateUpdateDriverMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.StockTransferRequestBody
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceRequest
import com.lfsolutions.retail.model.sale.order.SaleOrderRequest
import com.lfsolutions.retail.model.service.ComplaintServiceRequest
import com.lfsolutions.retail.ui.login.LoginActivity
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants


class Main : Application() {

    private var saleOrder: SaleOrderRequest? = null
    private var taxInvoice: SaleInvoiceRequest? = null
    private var inComingStockTransferRequest: StockTransferRequestBody? = null
    private var outGoingStockTransferRequest: StockTransferRequestBody? = null
    private var complaintService: ComplaintServiceRequest? = null
    private var memo: CreateUpdateAgreementMemoRequestBody? = null
    private var driverMemo: CreateUpdateDriverMemoRequestBody? = null

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

    fun isLoggedIn(): Boolean {
        return AppSession.getBoolean(Constants.IS_LOGGED_IN)
    }

    fun getAgreementMemo(): CreateUpdateAgreementMemoRequestBody? {
        if (memo == null) memo = CreateUpdateAgreementMemoRequestBody()
        return memo
    }

    fun getDriverMemo(): CreateUpdateDriverMemoRequestBody {
        if (driverMemo == null)
            driverMemo = CreateUpdateDriverMemoRequestBody()
        return driverMemo!!
    }

    fun getTaxInvoice(): SaleInvoiceRequest? {
        if (taxInvoice == null) taxInvoice = SaleInvoiceRequest()
        return taxInvoice
    }

    fun getSaleOrder(): SaleOrderRequest? {
        if (saleOrder == null) saleOrder = SaleOrderRequest()
        return saleOrder
    }

    fun getComplaintService(): ComplaintServiceRequest? {
        if (complaintService == null) complaintService = ComplaintServiceRequest()
        return complaintService
    }

    fun clearAgreementMemo() {
        memo = null
    }

    fun clearDriverMemo() {
        driverMemo = null
    }

    fun clearTaxInvoice() {
        taxInvoice = null
    }

    fun clearSaleOrder() {
        saleOrder = null
    }

    fun clearComplaintService() {
        complaintService = null
    }

    fun getInComingStockTransferRequestObject(): StockTransferRequestBody {
        if (inComingStockTransferRequest == null) {
            inComingStockTransferRequest = StockTransferRequestBody()
            inComingStockTransferRequest?.locationId = getSession().defaultLocationId
        }
        return inComingStockTransferRequest!!
    }

    fun clearInComingStockTransfer() {
        inComingStockTransferRequest = null
    }

    fun getOutGoingStockTransferRequestObject(): StockTransferRequestBody {
        if (outGoingStockTransferRequest == null) {
            outGoingStockTransferRequest = StockTransferRequestBody()
            outGoingStockTransferRequest?.locationId = getSession().defaultLocationId
        }
        return outGoingStockTransferRequest!!
    }

    fun clearOutGoingStockTransfer() {
        outGoingStockTransferRequest = null
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