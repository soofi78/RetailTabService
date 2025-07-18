package com.lfsolutions.retail.util

import com.lfsolutions.retail.BuildConfig

object Api {
    const val APP_BASE_URL :String =  BuildConfig.BASE_URL
    const val Base = "api/"
    const val ServicesApp = "services/app/"
    const val CommonLookup = "CommonLookup/"
    const val Feedback = "Feedback/"
    const val SaleInvoice = "salesInvoice/"
    const val SaleOrder = "salesOrder/"
    const val DeliveryOrder = "deliveryOrder/"


    object Name {
        const val CREATE_UPDATE_SALE_ORDER = "CreateOrUpdateSalesOrder"
        const val CREATE_UPDATE_SALE_INVOICE = "CreateOrUpdateSalesInvoice"
        const val CREATE_UPDATE_DELIVERY_ORDER = "CreateOrUpdateDeliveryOrder"
        const val CREATE_UPDATE_OUT_GOING_STOCK_TRANSFER = "Service/CreateOutGoingStockTransfer"
        const val CREATE_UPDATE_IN_COMING_STOCK_TRANSFER = "Service/CreateInComingStockTransfer"
        const val UPLOAD_SIGNATURE = "File/UploadAgreementMemoSignature"
        const val AUTHENTICATE = "Account/Authenticate"
        const val GET_CUSTOMERS = "Service/ApiGetCustomers"
        const val GET_ALL_CUSTOMERS = "Customer/apigetCustomers"
        const val GET_ALL_CUSTOMERS_WORK_AREA = "GetCustomerWorkAreaForCombobox"
        const val GET_CUSTOMERS_FORMS = "Service/GetCustomerForm"
        const val GET_CUSTOMERS_FOR_PAYMENT = "Service/ApiGetCustomersForPayments"
        const val CREATE_SALE_RECEIPT = "SalesReceipt/CreateOrUpdateSalesReceipt"
        const val GET_SALES_TRANSACTIONS = "SalesTransaction/GetSalesTransactions"
        const val GET_RECEIPT_TEMPLATE_PRINT = "ReceiptTemplate/GetReceiptTemplateByLocation"
        const val GET_SALES_ORDERS = "SalesOrder/GetAll"
        const val GET_DELIVERY_ORDERS = "DeliveryOrder/GetAll"
        const val GET_DELIVERY_ORDER_DETAIL = "deliveryOrder/GetDeliveryOrderForEdit"
        const val GET_SALES_ORDER_DETAIL = "SalesOrder/GetSalesOrderForEdit"
        const val GET_SALES_ORDER_PDF = "salesOrder/GetSalesOrderPrintPdfAttachment"
        const val GET_DRIVER_MEMO_PDF = "DriverMemo/GetDriverMemoPrintPdfAttachment"
        const val GET_DELIVERY_ORDER_PDF = "deliveryOrder/GetDeliveryOrderPrintPdfAttachment"
        const val GET_SALES_INVOICES = "SalesInvoice/GetAll"
        const val GET_SALE_INVOICE_DETAIL = "SalesInvoice/GetSalesInvoiceForEdit"
        const val GET_SALE_INVOICE_FOR_PRINT = "SalesInvoice/SalesInvoiceForPrint"
        const val GET_SALE_INVOICE_PDF = "salesInvoice/GetSalesInvoicePrintPdfAttachment"
        const val GET_SALE_RECEIPT = "SalesReceipt/GetAll"
        const val DELETE_SALE_RECEIPT = "salesReceipt/DeleteSalesReceipt"
        const val GET_LOCATIONS = "Location/GetLocations"
        const val GET_ALL_STOCK_TRANSFER_HISTORY = "stockTransfer/GetAll"
        const val GET_STOCK_TRANSFER_DETAIL = "stockTransfer/GetStockTransferForEdit"
        const val GET_STOCK_TRANSFER_PDF = "StockTransfer/GetStockTransferPrintPdfAtatchment"
        const val GET_EQUIPMENT_TYPE = "GetAgreementMemoTypesForCombobox"
        const val GET_ACTION_TYPES = "getComplaintActionTypesForCombobox"
        const val GET_COMPLAINT_TYPES = "getComplaintTypesforCombobox"
        const val GET_REPORT_TYPES = "GetComplaintVisitationActionType"
        const val GET_PAYMENTS_TYPES = "GetPaymentsForCombobox"
        const val GET_FEEDBACK = "GetAllActiveFeedBack"
        const val GET_FEEDBACK_FOR_COMPLAINT_SERVICE = "GetFeedBackTypesForCombobox"
        const val GET_EQUIPMENT = "Service/GetApiServiceProduct"
        const val GET_CUSTOMER_PRODUCT = "Service/GetApiCustomerServiceProduct"
        const val GET_CATEGORIES = "category/GetServiceProductCategories"
        const val GET_OUT_GOING_PRODUCT = "Service/GetOutGoingStockTransferProduct"
        const val GET_SERIAL_NUMBERS = "product/GetProductSerialNumbers"
        const val GET_SOLD_PRODUCTS_SERIAL_NUMBERS = "product/GetApiSoldProductSerialNumbers"
        const val GET_WEAR_HOUSE_SERIAL_NUMBERS = "product/GetApiWareHouseProductSerialNumbers"
        const val GET_SERIAL_NUMBERS_RETURN = "product/getApiSoldProductSerialNumbers"
        const val CREATE_UPDATE_MEMO = "agreementMemo/CreateOrUpdateAgreementMemo"
        const val CREATE_UPDATE_COMPLAINT_SERVICE =
            "complaintService/CreateOrUpdateComplaintService"
        const val GET_COMPLAINT_SERVICE_PDF =
            "ComplaintService/GetComplaintServicePrintPdfAttachment"
        const val GET_AGREEMENT_MEMO_PDF = "AgreementMemo/GetAgreementMemoPrintPdfAttachment"
        const val GET_SALE_RECEIPT_PDF = "SalesReceipt/GetSalesReceiptPrintPdfAttachment"
        const val GET_DAILY_SALE_RECORD_PDF = "service/GetApiDailySaleRecordPrintPdfAttachment"
        const val GET_DAILY_SALE_RECORD = "Service/GetDailySales"
        const val SALE_ORDER_BY_SALES_PERSON = "service/GetSalesOrderBySalesPerson"
        const val SALE_ORDER_STOCK_RECEIVE_FOR_DRIVER = "service/SalesOrderToStockReceiveForDriver"
        const val CREATE_UPDATE_DRIVER_MEMO = "driverMemo/CreateOrUpdateDriverMemo"
        const val GET_ALL_DRIVER_MEMOS = "driverMemo/GetAll"
        const val GET_USER_DETAILS = "User/GetUserForEdit"
        const val GET_SCHEDULED_VISITATION = "Service/ApiGetCustomerVisitationSchedule"
        const val GET_ALL_AGREEMENT_MEMO_LIST = "agreementMemo/GetAll"
        const val GET_ALL_COMPLAINT_SERVICE_LIST = "complaintService/GetAll"
        const val GET_DRIVER_MEMO_FOR_EDIT = "DriverMemo/GetDriverMemoForEdit"
        const val GET_AGREEMENT_MEMO_DETAILS = "AgreementMemo/GetAgreementMemoForEdit"
        const val GET_CUSTOMER = "Customer/GetCustomerById"
        const val GET_RECEIPT_DETAILS = "SalesReceipt/GetSalesReceiptForPrint"
        const val GET_COMPLAINT_SERVICE_DETAILS = "ComplaintService/GetComplaintServiceForEdit"
        const val ADD_CUSTOMER_TO_VISITATION_SCHEDULE = "Service/AddCustomerVisitationSchedule"
        const val DELETE_CUSTOMER_FROM_VISITATION_SCHEDULE =
            "Service/DeleteCustomerVisitationSchedule"
        const val DELETE_CUSTOMER_FROM_TO_VISIT =
            "Service/DeleteCustomerToVisits"
        const val GET_PRODUCT_CURRENT_STOCK =
            "Product/GetApiProductStocksForLocation"
        const val SALE_ORDER_FOR_INVOICE =
            "salesOrder/GetApiSalesOrderForInvoice"
        const val SALE_ORDER_FOR_DELIVERY_ORDER =
            "deliveryOrder/GetApiSalesOrderDetailItems"
        const val AssetManagement =
            "AssetManagement/GetAll"
        const val GET_STOCK_RECEIVED_DETAIL = "stockTransfer/GetStockReceiveForEdit"
        //http://rtlconnect.net/MyBossTest/api/services/app/stockTransfer/GetStockReceiveForEdit
    }
}