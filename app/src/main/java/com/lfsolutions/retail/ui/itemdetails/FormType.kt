package com.lfsolutions.retail.ui.itemdetails

sealed class FormType {

    data object AgreementMemo : FormType()

    data object ServiceForm : FormType()

    data object InvoiceForm : FormType()
}