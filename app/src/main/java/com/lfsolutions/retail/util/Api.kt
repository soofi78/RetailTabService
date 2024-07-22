package com.lfsolutions.retail.util

object Api {
    const val Base = "api/"
    const val ServicesApp = "services/app/"
    const val CommonLookup = "CommonLookup/"
    const val Feedback = "Feedback/"
    const val SaleInvoice = "salesInvoice//"

    object Name {
        const val CREATE_UPDATE_SALE_ORDER = "CreateOrUpdateSalesOrder"
        const val CREATE_UPDATE_SALE_INVOICE = "CreateOrUpdateSalesInvoice"
        const val UPLOAD_SIGNATURE = "File/UploadAgreementMemoSignature"
        const val AUTHENTICATE = "Account/Authenticate"
        const val GET_CUSTOMERS = "Service/ApiGetCustomers"
        const val GET_CUSTOMERS_FORMS = "Service/GetCustomerForm"
        const val GET_EQUIPMENT_TYPE = "GetAgreementMemoTypesForCombobox"
        const val GET_ACTION_TYPES = "getComplaintActionTypesForCombobox"
        const val GET_COMPLAINT_TYPES = "getComplaintTypesforCombobox"
        const val GET_FEEDBACK = "GetAllActiveFeedBack"
        const val GET_EQUIPMENT = "Service/GetApiServiceProduct"
        const val GET_SERIAL_NUMBERS = "product/GetProductSerialNumbers"
        const val CREATE_UPDATE_MEMO = "agreementMemo/CreateOrUpdateAgreementMemo"
        const val CREATE_UPDATE_COMPLAINT_SERVICE =
            "complaintService/CreateOrUpdateComplaintService"
    }
}