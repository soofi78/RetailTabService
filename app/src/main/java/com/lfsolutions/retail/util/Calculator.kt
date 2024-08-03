package com.lfsolutions.retail.util

import androidx.fragment.app.Fragment
import com.maltaisn.calcdialog.CalcDialog
import com.maltaisn.calcdialog.CalcNumpadLayout

object Calculator {
    fun show(fragment: Fragment) {
        val modal = CalcDialog()
        modal.settings.let {
            it.isExpressionShown = true
            it.isExpressionEditable = false
            it.isAnswerBtnShown = true
            it.numpadLayout = CalcNumpadLayout.CALCULATOR
        }
        fragment.childFragmentManager.let { modal.show(it, "CalcDialog") }
    }

}
