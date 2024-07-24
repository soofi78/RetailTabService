package com.lfsolutions.retail.network

import com.lfsolutions.retail.model.ComplaintServiceResponse
import com.lfsolutions.retail.model.CustomerIdsList
import com.lfsolutions.retail.model.FormResult
import com.lfsolutions.retail.model.FormsRequest
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.LoginRequest
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.OutGoingStockProductsResults
import com.lfsolutions.retail.model.outgoingstock.StockTransferRequestBody
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceRequest
import com.lfsolutions.retail.model.sale.order.SaleOrderRequest
import com.lfsolutions.retail.model.service.ActionTypeResult
import com.lfsolutions.retail.model.service.ComplaintServiceRequest
import com.lfsolutions.retail.model.service.ComplaintTypeResult
import com.lfsolutions.retail.util.Api
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiServices {
    @POST(Api.Base.plus(Api.Name.AUTHENTICATE))
    fun login(@Body request: LoginRequest): Call<UserSession>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS))
    fun getCustomers(): Call<RetailResponse<CustomerResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FORMS))
    fun getCustomerForm(@Body formRequest: FormsRequest): Call<RetailResponse<FormResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_EQUIPMENT_TYPE))
    fun getEquipmentType(): Call<RetailResponse<EquipmentTypeResult>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_ACTION_TYPES))
    fun getActionTypes(): Call<RetailResponse<ActionTypeResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_COMPLAINT_TYPES))
    fun getComplaintTypes(): Call<RetailResponse<ComplaintTypeResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Feedback).plus(Api.Name.GET_FEEDBACK))
    fun getFeedback(): Call<RetailResponse<ArrayList<Feedback>>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_EQUIPMENT))
    fun getEquipmentList(@Body locationIdRequestObject: LocationIdRequestObject): Call<RetailResponse<EquipmentListResult>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_OUT_GOING_PRODUCT))
    fun getOutGoingStockTransferProductList(@Body customerIdsList: CustomerIdsList): Call<RetailResponse<OutGoingStockProductsResults>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SERIAL_NUMBERS))
    fun getSerialNumbers(
        @Query("productId") productId: Long?,
        @Query("locationId") locationId: Long?,
        @Query("isSold") isSold: Boolean = false
    ): Call<RetailResponse<ArrayList<SerialNumber>>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_MEMO))
    fun createUpdateMemo(@Body createUpdateAgreementMemoRequestBody: CreateUpdateAgreementMemoRequestBody): Call<RetailResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_COMPLAINT_SERVICE))
    fun createUpdateComplaintService(@Body complaintServiceRequest: ComplaintServiceRequest): Call<BaseResponse<ComplaintServiceResponse>>?

    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.SaleInvoice).plus(Api.Name.CREATE_UPDATE_SALE_ORDER)
    )
    fun createUpdateSaleOrder(@Body saleOrderRequest: SaleOrderRequest): Call<BaseResponse<*>>?

    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.SaleInvoice)
            .plus(Api.Name.CREATE_UPDATE_SALE_INVOICE)
    )
    fun createUpdateSaleInvoice(@Body saleInvoiceRequest: SaleInvoiceRequest): Call<BaseResponse<*>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_STOCK_TRANSFER))
    fun createUpdateStockTransfer(@Body stockTransferRequestBody: StockTransferRequestBody): Call<BaseResponse<Any>>?

    @Multipart
    @POST(Api.Name.UPLOAD_SIGNATURE)
    fun uploadSignature(@Part file: MultipartBody.Part): Call<RetailResponse<SignatureUploadResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FORMS))
    fun getApiServiceProduct(@Body locationIdRequestObject: LocationIdRequestObject)
}



