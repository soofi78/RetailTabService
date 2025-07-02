package com.lfsolutions.retail

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.google.gson.Gson
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.memo.CreateUpdateDriverMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.StockTransferRequestBody
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.model.sale.order.SaleOrderRequest
import com.lfsolutions.retail.model.service.ServiceFormBody
import com.lfsolutions.retail.ui.delivery.order.DeliveryOrderDTO
import com.lfsolutions.retail.ui.login.LoginActivity
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants


class Main : Application() {

    private var saleOrder: SaleOrderRequest? = null
    private var deliveryOrder: DeliveryOrderDTO? = null
    private var taxInvoice: SaleInvoiceObject? = null
    private var inComingStockTransferRequest: StockTransferRequestBody? = null
    private var outGoingStockTransferRequest: StockTransferRequestBody? = null
    private var complaintService: ServiceFormBody? = null
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
        if (driverMemo == null) driverMemo = CreateUpdateDriverMemoRequestBody()
        return driverMemo!!
    }

    fun getTaxInvoice(): SaleInvoiceObject? {
        if (taxInvoice == null) taxInvoice = SaleInvoiceObject()
        return taxInvoice
    }

    fun getSaleOrder(): SaleOrderRequest? {
        if (saleOrder == null) saleOrder = SaleOrderRequest()
        return saleOrder
    }

    fun getDeliveryOrder(): DeliveryOrderDTO? {
        if (deliveryOrder == null) deliveryOrder = DeliveryOrderDTO()
        return deliveryOrder
    }

    fun getComplaintService(): ServiceFormBody? {
        if (complaintService == null) complaintService = ServiceFormBody()
        return complaintService
    }

    fun setComplaintService(complaintService: ServiceFormBody?) {
        clearComplaintService()
        getComplaintService().apply {
            this?.complaintService = complaintService?.complaintService
            this?.complaintService?.id = null
            this?.complaintService?.csNo = null
            this?.complaintService?.csDate = null
            this?.complaintService?.creationTime = null
            this?.complaintService?.deletionTime = null
            this?.complaintService?.lastModificationTime = null
            this?.complaintService?.creatorUserId = null
            this?.complaintService?.deleterUserId = null
            this?.complaintService?.signature = null
            this?.complaintService?.status = "C"
            this?.complaintService?.type = "C"
            complaintService?.complaintServiceDetails?.let {
                this?.complaintServiceDetails?.addAll(it)
            }
            this?.complaintServiceDetails?.forEach { item ->
                item.id = null
            }
        }
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

    fun clearDeliveryOrder() {
        deliveryOrder = null
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

    fun setAgreementMemo(memo: CreateUpdateAgreementMemoRequestBody?) {
        clearAgreementMemo()
        getAgreementMemo()!!.apply {
            AgreementMemo = memo?.AgreementMemo
            AgreementMemo?.Id = null
            AgreementMemo?.AgreementDate = null
            AgreementMemo?.AgreementNo = null
            AgreementMemo?.creationTime = null
            AgreementMemo?.LastModificationTime = null
            AgreementMemo?.CreatorUserId = null
            AgreementMemo?.DeletionTime = null
            AgreementMemo?.Signature = null
            AgreementMemo?.Status = "C"
            memo?.AgreementMemoDetail?.let { AgreementMemoDetail.addAll(it) }
            AgreementMemoDetail.forEach { memoDetailItem ->
                memoDetailItem.Id = null
                memoDetailItem.AgreementMemoId = null
                memoDetailItem.CreatorUserId = null
                memoDetailItem.CreationTime = null
                memoDetailItem.LastModificationTime = null
                memoDetailItem.LastModifierUserId = null
            }
        }
        getAgreementMemo()?.updatePriceAndQty()
        getAgreementMemo()?.serializeItems()
    }

    fun restart(activity: Activity) {
        activity.startActivity(Intent(app, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        activity.finishAffinity()

    }


    companion object {
        lateinit var app: Main
    }
}