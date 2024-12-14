package com.lfsolutions.retail.ui.forms

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.BottomSheetNewFormsBinding

class NewFormsBottomSheet : BottomSheetDialogFragment() {

    private var onClickListener: View.OnClickListener? = null
    private lateinit var binding: BottomSheetNewFormsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetNewFormsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Main.app.getSession().isSupervisor?.not() == true) {
            binding.newServiceForm.visibility = View.GONE
            binding.newAgreementMemo.visibility = View.GONE
            binding.newSaleOrder.visibility = View.GONE
        }

        binding.newServiceForm.setOnClickListener {
            dialog?.dismiss()
            this.onClickListener?.onClick(binding.newServiceForm)
        }
        binding.newAgreementMemo.setOnClickListener {
            dialog?.dismiss()
            this.onClickListener?.onClick(binding.newAgreementMemo)
        }
        binding.newSaleOrder.setOnClickListener {
            dialog?.dismiss()
            this.onClickListener?.onClick(binding.newSaleOrder)
        }
        binding.newTaxInvoice.setOnClickListener {
            dialog?.dismiss()
            this.onClickListener?.onClick(binding.newTaxInvoice)
        }
        binding.newDeliveryOrder.setOnClickListener {
            dialog?.dismiss()
            this.onClickListener?.onClick(binding.newDeliveryOrder)
        }
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

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        this.onClickListener = onClickListener
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}