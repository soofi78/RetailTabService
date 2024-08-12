package com.lfsolutions.retail.ui.documents.history

import com.lfsolutions.retail.R

enum class HistoryType(var type: String, var selected: Boolean, var icon: Int) {
    Order("Order", true, R.drawable.order),
    Invoices("Invoices", false, R.drawable.ic_note),
    Receipts("Receipts", false, R.drawable.ic_return),
    Returns("Returns", false, R.drawable.ic_return)
}