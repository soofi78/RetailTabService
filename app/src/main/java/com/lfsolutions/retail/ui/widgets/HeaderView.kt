package com.lfsolutions.retail.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.lfsolutions.retail.databinding.HeadersBinding

class HeaderView : LinearLayout {

    var binding: HeadersBinding? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        binding = HeadersBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setName(name: String) {
        binding?.txtName?.setText(name)
    }

    fun setBackText(txt: String) {
        binding?.txtBack?.setText(txt)
    }

    fun setOnBackClick(onClickListener: OnClickListener) {
        binding?.icoBack?.setOnClickListener(onClickListener)
    }

}