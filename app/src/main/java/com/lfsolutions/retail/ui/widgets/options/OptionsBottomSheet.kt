package com.lfsolutions.retail.ui.widgets.options

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.databinding.BottomSheetOptionsBinding

class OptionsBottomSheet : BottomSheetDialogFragment() {

    private var options = ArrayList<OptionItem>()
    private var onOptionItemClick: OnOptionItemClick? = null
    private lateinit var binding: BottomSheetOptionsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.options.adapter = OptionsAdapter(options, object : OnOptionItemClick {
            override fun onOptionItemClick(optionItem: OptionItem) {
                optionSheet?.dismiss()
                this@OptionsBottomSheet.onOptionItemClick?.onOptionItemClick(optionItem)
            }
        })
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

    fun setOnClickListener(onOptionItemClick: OnOptionItemClick) {
        this.onOptionItemClick = onOptionItemClick
    }

    fun setOptions(options: ArrayList<OptionItem>) {
        this.options = options
    }

    companion object {
        const val TAG = "OptionsBottomSheetDialog"
        private var optionSheet: OptionsBottomSheet? = null
        fun show(
            supportFragmentManager: FragmentManager,
            options: ArrayList<OptionItem>,
            optionItemClick: OnOptionItemClick
        ) {
            optionSheet = OptionsBottomSheet()
            optionSheet?.setOnClickListener(optionItemClick)
            optionSheet?.setOptions(options)
            supportFragmentManager.let { optionSheet?.show(it, TAG) }
        }
    }
}