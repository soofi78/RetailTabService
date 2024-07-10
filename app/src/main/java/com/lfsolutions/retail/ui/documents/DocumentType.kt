package com.lfsolutions.retail.ui.documents


sealed class DocumentType {

    abstract val label: Int

    abstract val iconResId: Int

    data class OutGoingStockRecord(override val label: Int, override val iconResId: Int) : DocumentType()

    data class DailySalesRecord(override val label: Int, override val iconResId: Int) : DocumentType()

    data class DriverMemo(override val label: Int, override val iconResId: Int) : DocumentType()

}