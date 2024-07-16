package com.lfsolutions.retail.util

object Api {
    const val Base = "api/"
    const val ServicesApp = "services/app/"

    object Name {
        const val UPLOAD_SIGNATURE ="File/UploadAgreementMemoSignature"
        const val AUTHENTICATE = "Account/Authenticate"
        const val GET_CUSTOMERS = "Service/ApiGetCustomers"
        const val GET_CUSTOMERS_FORMS = "Service/GetCustomerForm"
        const val GET_EQUIPMENT_LIST = "CommonLookup/GetAgreementMemoTypesForCombobox"
        const val GET_EQUIPMENT = "Service/GetApiServiceProduct"
        const val GET_SERIAL_NUMBERS = "product/GetProductSerialNumbers"
        const val CREATE_UPDATE_MEMO = "agreementMemo/CreateOrUpdateAgreementMemo"
    }
}