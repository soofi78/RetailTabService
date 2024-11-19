package com.lfsolutions.retail.ui.forms

import java.io.Serializable

enum class FormType(val typeName: String) : Serializable {
    AgreementMemo("Agreement Memo"),
    ServiceForm("Complaint Service"),
    InvoiceForm("Sale Invoice"),
    SaleOrder("Sale Order"),
    DeliveryOrder("Delivery Order");

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