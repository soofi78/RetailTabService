package com.lfsolutions.retail.ui.forms

enum class FormType(val typeName: String) {
    AgreementMemo("Agreement Memo"),
    ServiceForm("Complaint Service"),
    InvoiceForm("Sale Invoice"),
    SaleOrder("Sale Order");

    companion object {
        fun find(title: String): FormType? {
            var type: FormType? = null
            entries.forEach {
                if (it.typeName == title) {
                    type = it
                }
            }

            return type
        }
    }
}