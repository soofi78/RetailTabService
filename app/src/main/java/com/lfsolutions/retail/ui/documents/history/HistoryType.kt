package com.lfsolutions.retail.ui.documents.history

import androidx.annotation.DrawableRes
import com.lfsolutions.retail.R

enum class HistoryType(var type: String, var selected: Boolean, @DrawableRes var icon: Int) {
    Order("Order", true, R.drawable.order),
    DeliveryOrder("Delivery Order", true, R.drawable.order),
    Invoices("Invoices", false, R.drawable.ic_note),
    Receipts("Receipts", false, R.drawable.ic_return),
    AgreementMemo("Agreement Memo", false, R.drawable.agreement_memo_white),
    ServiceForm("Service Form", false, R.drawable.ic_compliant_white),
    OutgoingTransfer("Outgoing Transfer", false, R.drawable.outgoing_stock_record),
    Returns("Returns", false, R.drawable.ic_return);

}