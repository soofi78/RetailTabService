package com.lfsolutions.retail.ui.forms

sealed class FormType {

    data object AgreementMemo : FormType()

    data object ServiceForm : FormType()

    data object InvoiceForm : FormType()
}