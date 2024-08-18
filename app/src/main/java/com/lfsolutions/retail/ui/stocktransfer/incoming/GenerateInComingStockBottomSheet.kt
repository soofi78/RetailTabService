package com.lfsolutions.retail.ui.stocktransfer.incoming

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.databinding.OutgoingStockRecordBottomSheetBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import java.util.ArrayList

class GenerateInComingStockBottomSheet : BottomSheetDialogFragment() {

    private lateinit var list: ArrayList<Customer>
    private lateinit var onConfirmClick: OnClickListener
    private lateinit var binding: OutgoingStockRecordBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OutgoingStockRecordBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirm.setOnClickListener {
            if (::onConfirmClick.isInitialized)
                onConfirmClick.onClick(it)
            dialog?.dismiss()
        }
        binding.customers.adapter =
            DeliveryItemAdapter(list, DeliveryItemAdapter.CustomerItemType.Scheduled)
        binding.cancel.setOnClickListener { dialog?.dismiss() }
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

    fun setOnConfirmClickListener(onClickListener: View.OnClickListener) {
        onConfirmClick = onClickListener
    }

    fun setList(scheduledList: ArrayList<Customer>) {
        list = scheduledList;
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}