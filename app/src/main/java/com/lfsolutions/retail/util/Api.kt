package com.lfsolutions.retail.util

object Api {
    const val Base = "api/"
    const val ServicesApp = "services/app/"
    const val CommonLookup = "CommonLookup/"
    const val Feedback = "Feedback/"
    const val SaleInvoice = "salesInvoice/"
    const val SaleOrder = "salesOrder/"

    object Name {
        const val CREATE_UPDATE_SALE_ORDER = "CreateOrUpdateSalesOrder"
        const val CREATE_UPDATE_SALE_INVOICE = "CreateOrUpdateSalesInvoice"
        const val CREATE_UPDATE_STOCK_TRANSFER = "service/CreateOutGoingStockTransfer"
        const val UPLOAD_SIGNATURE = "File/UploadAgreementMemoSignature"
        const val AUTHENTICATE = "Account/Authenticate"
        const val GET_CUSTOMERS = "Service/ApiGetCustomers"
        const val GET_CUSTOMERS_FORMS = "Service/GetCustomerForm"
        const val GET_CUSTOMERS_FOR_PAYMENT = "Service/ApiGetCustomersForPayments"
        const val CREATE_SALE_RECEIPT = "SalesReceipt/CreateOrUpdateSalesReceipt"
        const val GET_SALES_TRANSACTIONS = "SalesTransaction/GetSalesTransactions"
        const val GET_EQUIPMENT_TYPE = "GetAgreementMemoTypesForCombobox"
        const val GET_ACTION_TYPES = "getComplaintActionTypesForCombobox"
        const val GET_COMPLAINT_TYPES = "getComplaintTypesforCombobox"
        const val GET_PAYMENTS_TYPES = "GetPaymentsForCombobox"
        const val GET_FEEDBACK = "GetAllActiveFeedBack"
        const val GET_EQUIPMENT = "Service/GetApiServiceProduct"
        const val GET_PRODUCT_FOR_TAX_INVOICE = "Service/GetApiCustomerServiceProduct"
        const val GET_CATEGORIES = "category/GetCategories"
        const val GET_OUT_GOING_PRODUCT = "Service/GetOutGoingStockTransferProduct"
        const val GET_SERIAL_NUMBERS = "product/GetProductSerialNumbers"
        const val CREATE_UPDATE_MEMO = "agreementMemo/CreateOrUpdateAgreementMemo"
        const val CREATE_UPDATE_COMPLAINT_SERVICE =
            "complaintService/CreateOrUpdateComplaintService"
    }
}