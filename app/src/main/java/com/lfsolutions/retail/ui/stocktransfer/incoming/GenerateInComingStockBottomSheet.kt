package com.lfsolutions.retail.ui.stocktransfer.incoming

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.databinding.OutgoingStockRecordBottomSheetBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import com.lfsolutions.retail.util.setDebouncedClickListener

class GenerateInComingStockBottomSheet : BottomSheetDialogFragment() {

    private lateinit var list: ArrayList<Customer>
    private lateinit var onConfirmClick: OnClickListener
    private lateinit var binding: OutgoingStockRecordBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OutgoingStockRecordBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirm.setDebouncedClickListener {
            if (::onConfirmClick.isInitialized)
                onConfirmClick.onClick(it)
            dialog?.dismiss()
        }
        binding.customers.adapter =
            DeliveryItemAdapter(list, DeliveryItemAdapter.CustomerItemType.Scheduled)
        binding.cancel.setOnClickListener { dialog?.dismiss() }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                val layoutParams = bottomSheet.layoutParams

                val windowHeight = getWindowHeight()
                if (layoutParams != null) {
                    layoutParams.height = windowHeight
                }
                bottomSheet.layoutParams = layoutParams
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun setOnConfirmClickListener(onClickListener: OnClickListener) {
        onConfirmClick = onClickListener
    }

    fun setList(scheduledList: ArrayList<Customer>) {
        list = scheduledList
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}