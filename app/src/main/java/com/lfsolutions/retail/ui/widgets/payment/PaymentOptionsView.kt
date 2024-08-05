package com.lfsolutions.retail.ui.widgets.payment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.databinding.PaymentOptionsSheetBinding
import com.lfsolutions.retail.model.PaymentType

class PaymentOptionsView : BottomSheetDialogFragment() {

    private lateinit var onPaymentOptionSelected: OnPaymentOptionSelected
    private lateinit var binding: PaymentOptionsSheetBinding
    private lateinit var paymentOptionAdapter: PaymentOptionAdapter
    var options: ArrayList<PaymentType> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (::binding.isInitialized.not()) {
            binding = PaymentOptionsSheetBinding.inflate(inflater, container, false)
            paymentOptionAdapter = PaymentOptionAdapter(options,
                object : OnPaymentOptionSelected {
                    override fun onPaymentOptionSelected(paymentType: PaymentType) {
                        onPaymentOptionSelected.onPaymentOptionSelected(paymentType)
                    }
                })
            binding.options.adapter = paymentOptionAdapter
        }
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    fun setOnPaymentOptionSelected(onPaymentOptionSelected: OnPaymentOptionSelected) {
        this.onPaymentOptionSelected = onPaymentOptionSelected
    }


    companion object {
        const val TAG = "Product Quantity Update Dialog"
    }
}
