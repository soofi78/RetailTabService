package com.lfsolutions.retail.ui.settings.printer

data class PrinterDevice(
    var icon: Int,
    var name: String,
    var address: String,
    var isSelected: Boolean = false
)