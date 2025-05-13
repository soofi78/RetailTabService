package com.lfsolutions.retail.ui.widgets.theme

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.databinding.BottomSheetThemesBinding
import com.lfsolutions.retail.ui.theme.RetailThemes
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.Notify

class ThemeSelectionSheet : BottomSheetDialogFragment() {

    private lateinit var onThemeSelected: OnThemeSelected
    private lateinit var theme: RetailThemes
    private var options = ArrayList<RetailThemes>().apply {
        addAll(RetailThemes.entries.toTypedArray())
    }
    private lateinit var binding: BottomSheetThemesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetThemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        options.forEach {
            it.selected =
                it.themeName == AppSession[Constants.AppTheme, RetailThemes.Yellow.themeName]
        }
        binding.themes.adapter = ThemeAdapter(options, object : OnThemeSelected {
            override fun onThemeSelected(theme: RetailThemes) {
                this@ThemeSelectionSheet.theme = theme
            }
        })

        binding.save.setDebouncedClickListener {
            optionSheet?.dismiss()
            if (::theme.isInitialized) {
                onThemeSelected.onThemeSelected(theme)
            } else {
                Notify.toastLong("No theme selected")
            }
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

    fun setOnClickListener(onThemSelected: OnThemeSelected) {
        this.onThemeSelected = onThemSelected
    }

    companion object {
        const val TAG = "ThemeSelectionSheet"
        private var optionSheet: ThemeSelectionSheet? = null
        fun show(
            supportFragmentManager: FragmentManager, optionItemClick: OnThemeSelected
        ) {
            optionSheet = ThemeSelectionSheet()
            optionSheet?.setOnClickListener(optionItemClick)
            supportFragmentManager.let { optionSheet?.show(it, TAG) }
        }
    }
}