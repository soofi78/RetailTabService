package com.lfsolutions.retail.ui.widgets.payment

import com.lfsolutions.retail.model.PaymentType

interface OnPaymentOptionSelected {
    fun onPaymentOptionSelected(paymentType: PaymentType)
}