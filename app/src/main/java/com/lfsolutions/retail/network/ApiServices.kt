package com.lfsolutions.retail.network

import com.lfsolutions.retail.model.AllCustomersResult
import com.lfsolutions.retail.model.CategoryResult
import com.lfsolutions.retail.model.ComplaintServiceResponse
import com.lfsolutions.retail.model.CustomerIdRequest
import com.lfsolutions.retail.model.CustomerIdsList
import com.lfsolutions.retail.model.CustomerPaymentsResult
import com.lfsolutions.retail.model.CustomerResponse
import com.lfsolutions.retail.model.CustomerSaleTransaction
import com.lfsolutions.retail.model.CustomerWorkAreaTypeResult
import com.lfsolutions.retail.model.CustomersResult
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.FilterRequest
import com.lfsolutions.retail.model.FormResult
import com.lfsolutions.retail.model.FormsRequest
import com.lfsolutions.retail.model.GetAllDriverMemoResult
import com.lfsolutions.retail.model.GetLocationResult
import com.lfsolutions.retail.model.HistoryRequest
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.LocationIdCustomerIdRequestObject
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.LocationTenantIdRequestObject
import com.lfsolutions.retail.model.LoginRequest
import com.lfsolutions.retail.model.PaymentRequest
import com.lfsolutions.retail.model.PaymentTermsResult
import com.lfsolutions.retail.model.PrintTemplate
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.ProductListRB
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SaleOrderToStockReceive
import com.lfsolutions.retail.model.SaleTransactionRequestBody
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.TypeRequest
import com.lfsolutions.retail.model.UserIdDateRequestBody
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.VisitDateRequest
import com.lfsolutions.retail.model.dailysale.DailySaleRecord
import com.lfsolutions.retail.model.deliveryorder.DeliveryOrderHistoryList
import com.lfsolutions.retail.model.memo.AgreementMemoHistoryResult
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.memo.CreateUpdateDriverMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.OutGoingStockProductsResults
import com.lfsolutions.retail.model.outgoingstock.StockTransferDetailItem
import com.lfsolutions.retail.model.outgoingstock.StockTransferHistoryResult
import com.lfsolutions.retail.model.outgoingstock.StockTransferRequestBody
import com.lfsolutions.retail.model.product.AssetResult
import com.lfsolutions.retail.model.profile.UserProfile
import com.lfsolutions.retail.model.sale.SaleReceipt
import com.lfsolutions.retail.model.sale.SaleReceiptResult
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListResult
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.model.sale.order.SaleOrderListResult
import com.lfsolutions.retail.model.sale.order.SaleOrderRequest
import com.lfsolutions.retail.model.sale.order.response.SaleOrderResponse
import com.lfsolutions.retail.model.service.ActionTypeResult
import com.lfsolutions.retail.model.service.ComplaintServiceHistoryResult
import com.lfsolutions.retail.model.service.ComplaintTypeResult
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.model.service.FeedbackTypeResult
import com.lfsolutions.retail.model.service.ReportTypeResult
import com.lfsolutions.retail.model.service.ServiceFormBody
import com.lfsolutions.retail.model.service.ServiceTypeResult
import com.lfsolutions.retail.ui.delivery.order.DeliverOrderDetail
import com.lfsolutions.retail.ui.delivery.order.DeliveryOrderDTO
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
    fun getCustomers(): Call<RetailResponse<CustomersResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_ALL_CUSTOMERS))
    fun getAllCustomers(@Body location: LocationTenantIdRequestObject): Call<BaseResponse<AllCustomersResult>>?

    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup)
            .plus(Api.Name.GET_ALL_CUSTOMERS_WORK_AREA)
    )
    fun getAllCustomerWorkAreas(): Call<BaseResponse<CustomerWorkAreaTypeResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FORMS))
    fun getCustomerForm(@Body formRequest: FormsRequest): Call<RetailResponse<FormResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_EQUIPMENT_TYPE))
    fun getEquipmentType(): Call<RetailResponse<EquipmentTypeResult>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_ACTION_TYPES))
    fun getActionTypes(): Call<RetailResponse<ActionTypeResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_COMPLAINT_TYPES))
    fun getComplaintTypes(): Call<RetailResponse<ComplaintTypeResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_COMPLAINT_TYPES))
    fun getServiceType(): Call<RetailResponse<ServiceTypeResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_REPORT_TYPES))
    fun getReportType(): Call<RetailResponse<ReportTypeResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup).plus(Api.Name.GET_PAYMENTS_TYPES))
    fun getPaymentTerms(): Call<BaseResponse<PaymentTermsResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Feedback).plus(Api.Name.GET_FEEDBACK))
    fun getFeedback(@Query("isDeliverySchedule") isDeliverySchedule: Boolean = false): Call<RetailResponse<ArrayList<Feedback>>>

    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.CommonLookup)
            .plus(Api.Name.GET_FEEDBACK_FOR_COMPLAINT_SERVICE)
    )
    fun getFeedbackTypes(): Call<RetailResponse<FeedbackTypeResult>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_EQUIPMENT))
    fun getEquipmentList(@Body productListRB: ProductListRB): Call<RetailResponse<EquipmentListResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMER_PRODUCT))
    fun getCustomerProduct(@Body request: LocationIdCustomerIdRequestObject): Call<RetailResponse<EquipmentListResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_OUT_GOING_PRODUCT))
    fun getOutGoingStockTransferProductList(@Body customerIdsList: CustomerIdsList): Call<RetailResponse<OutGoingStockProductsResults>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CATEGORIES))
    fun getProductCategories(@Body filterRequest: FilterRequest = FilterRequest()): Call<BaseResponse<CategoryResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SERIAL_NUMBERS))
    fun getSerialNumbers(
        @Query("productId") productId: Long?,
        @Query("locationId") locationId: Long?,
        @Query("isSold") isSold: Boolean = false
    ): Call<RetailResponse<ArrayList<SerialNumber>>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_MEMO))
    fun createUpdateMemo(@Body createUpdateAgreementMemoRequestBody: CreateUpdateAgreementMemoRequestBody): Call<RetailResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_COMPLAINT_SERVICE))
    fun createUpdateComplaintService(@Body serviceFormBody: ServiceFormBody): Call<BaseResponse<ComplaintServiceResponse>>?

    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.SaleOrder).plus(Api.Name.CREATE_UPDATE_SALE_ORDER)
    )
    fun createUpdateSaleOrder(@Body saleOrderRequest: SaleOrderRequest): Call<BaseResponse<Any>>?

    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.SaleInvoice)
            .plus(Api.Name.CREATE_UPDATE_SALE_INVOICE)
    )
    fun createUpdateSaleInvoice(@Body saleInvoiceObject: SaleInvoiceObject): Call<BaseResponse<String>>?


    @POST(
        Api.Base.plus(Api.ServicesApp).plus(Api.DeliveryOrder)
            .plus(Api.Name.CREATE_UPDATE_DELIVERY_ORDER)
    )
    fun createDeliveryOrder(@Body deliveryOrderDTO: DeliveryOrderDTO): Call<BaseResponse<DeliverOrderDetail>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_IN_COMING_STOCK_TRANSFER))
    fun createUpdateInComingStockTransfer(@Body stockTransferRequestBody: StockTransferRequestBody): Call<BaseResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_OUT_GOING_STOCK_TRANSFER))
    fun createUpdateOutGoingStockTransfer(@Body stockTransferRequestBody: StockTransferRequestBody): Call<BaseResponse<String>>?

    @Multipart
    @POST(Api.Name.UPLOAD_SIGNATURE)
    fun uploadSignature(@Part file: MultipartBody.Part): Call<RetailResponse<SignatureUploadResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FORMS))
    fun getApiServiceProduct(@Body locationIdRequestObject: LocationIdRequestObject)

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FOR_PAYMENT))
    fun getCustomersForPayment(): Call<RetailResponse<CustomerPaymentsResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_SALE_RECEIPT))
    fun createSaleReceipt(@Body paymentRequest: PaymentRequest): Call<BaseResponse<String>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALES_TRANSACTIONS))
    fun getSalesTransactions(@Body saleTransactions: SaleTransactionRequestBody): Call<RetailResponse<ArrayList<CustomerSaleTransaction>>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_RECEIPT_TEMPLATE_PRINT))
    fun getReceiptTemplatePrint(@Body typeRequest: TypeRequest): Call<RetailResponse<ArrayList<PrintTemplate>>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALES_ORDERS))
    fun getSalesOrder(@Body historyRequest: HistoryRequest): Call<BaseResponse<SaleOrderListResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DELIVERY_ORDERS))
    fun getDeliveryOrder(@Body historyRequest: HistoryRequest): Call<BaseResponse<DeliveryOrderHistoryList>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DELIVERY_ORDER_DETAIL))
    fun getDeliveryOrderDetails(@Body idRequest: IdRequest): Call<BaseResponse<DeliveryOrderDTO>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALES_ORDER_DETAIL))
    fun getSalesOrderDetail(@Body idRequest: IdRequest): Call<BaseResponse<SaleOrderResponse>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_AGREEMENT_MEMO_DETAILS))
    fun getAgreementMemoDetails(@Body idRequest: IdRequest): Call<BaseResponse<CreateUpdateAgreementMemoRequestBody>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMER))
    fun getCustomer(@Body idRequest: IdRequest): Call<BaseResponse<CustomerResponse>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_RECEIPT_DETAILS))
    fun getReceiptDetails(@Body idRequest: IdRequest): Call<BaseResponse<SaleReceipt>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALES_INVOICES))
    fun getSaleInvoices(@Body historyRequest: HistoryRequest): Call<BaseResponse<SaleInvoiceListResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALE_INVOICE_DETAIL))
    fun getSaleInvoiceDetail(@Body idRequest: IdRequest): Call<BaseResponse<SaleInvoiceObject>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALE_INVOICE_FOR_PRINT))
    fun getSaleInvoiceForPrint(@Body idRequest: IdRequest): Call<BaseResponse<SaleInvoiceObject>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALE_RECEIPT))
    fun getSaleReceipts(@Body historyRequest: HistoryRequest): Call<BaseResponse<SaleReceiptResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.DELETE_SALE_RECEIPT))
    fun deleteSaleReceipt(@Body idRequest: IdRequest): Call<BaseResponse<Any>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_LOCATIONS))
    fun getLocations(): Call<BaseResponse<GetLocationResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_ALL_STOCK_TRANSFER_HISTORY))
    fun getAllStockTransfer(@Body request: HistoryRequest): Call<BaseResponse<StockTransferHistoryResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_ALL_AGREEMENT_MEMO_LIST))
    fun getAllAgreementMemo(@Body request: HistoryRequest): Call<BaseResponse<AgreementMemoHistoryResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_ALL_COMPLAINT_SERVICE_LIST))
    fun getAllComplaintServices(@Body request: HistoryRequest): Call<BaseResponse<ComplaintServiceHistoryResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_STOCK_TRANSFER_DETAIL))
    fun getTransferDetails(@Body idRequest: IdRequest): Call<BaseResponse<StockTransferDetailItem>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_STOCK_TRANSFER_PDF))
    fun getStockTransferPDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALE_INVOICE_PDF))
    fun getSaleInvoicePDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALES_ORDER_PDF))
    fun getSaleOrderPDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DRIVER_MEMO_PDF))
    fun getDriverMemoPDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DELIVERY_ORDER_PDF))
    fun getDeliveryOrderPDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_COMPLAINT_SERVICE_PDF))
    fun getComplaintServicePDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_AGREEMENT_MEMO_PDF))
    fun getAgreementMemoPDF(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SALE_RECEIPT_PDF))
    fun getSaleReceiptPdf(@Body idRequest: IdRequest): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DAILY_SALE_RECORD_PDF))
    fun getDailySaleRecordPdf(@Body dateRequestBody: UserIdDateRequestBody): Call<BaseResponse<String>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DAILY_SALE_RECORD))
    fun dailySaleRecord(@Body userIdDateRequestBody: UserIdDateRequestBody): Call<BaseResponse<DailySaleRecord>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.SALE_ORDER_BY_SALES_PERSON))
    fun getSaleOrderBySalePerson(@Body historyRequest: HistoryRequest): Call<BaseResponse<SaleOrderListResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.SALE_ORDER_STOCK_RECEIVE_FOR_DRIVER))
    fun SalesOrderToStockReceiveForDriver(@Body saleOrderToStockReceive: SaleOrderToStockReceive): Call<BaseResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_DRIVER_MEMO))
    fun getDriverMemo(@Body driverMemo: CreateUpdateDriverMemoRequestBody): Call<BaseResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_ALL_DRIVER_MEMOS))
    fun getAllDriverMemos(@Body request: HistoryRequest): Call<BaseResponse<GetAllDriverMemoResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_USER_DETAILS))
    fun getUserDetails(@Body idRequest: IdRequest): Call<BaseResponse<UserProfile>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SCHEDULED_VISITATION))
    fun getScheduledVisitation(@Body visitDateRequest: VisitDateRequest): Call<BaseResponse<CustomersResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_DRIVER_MEMO_FOR_EDIT))
    fun getDriverMemo(@Body idRequest: IdRequest): Call<BaseResponse<CreateUpdateDriverMemoRequestBody>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_COMPLAINT_SERVICE_DETAILS))
    fun getComplaintServiceDetails(@Body idRequest: IdRequest): Call<BaseResponse<ServiceFormBody>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.ADD_CUSTOMER_TO_VISITATION_SCHEDULE))
    fun addCustomerVisitationSchedule(@Body list: ArrayList<CustomerIdRequest>): Call<BaseResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.DELETE_CUSTOMER_FROM_VISITATION_SCHEDULE))
    fun deleteCustomerFromVisitationSchedule(@Body idRequest: IdRequest): Call<BaseResponse<Any>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_PRODUCT_CURRENT_STOCK))
    fun getCurrentProductStockQuantity(@Body location: LocationIdRequestObject): Call<BaseResponse<ArrayList<Product>>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.SALE_ORDER_FOR_INVOICE))
    fun convertToSaleInvoice(@Body idRequest: IdRequest): Call<BaseResponse<SaleInvoiceObject>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.SALE_ORDER_FOR_DELIVERY_ORDER))
    fun convertToDeliveryOrder(@Body idRequest: IdRequest): Call<BaseResponse<DeliveryOrderDTO>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.AssetManagement))
    fun getAllocatedAssets(@Body idRequest: HistoryRequest): Call<BaseResponse<AssetResult>>
}



