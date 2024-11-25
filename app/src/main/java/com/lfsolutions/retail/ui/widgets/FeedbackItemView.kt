package com.lfsolutions.retail.ui.widgets

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FeedbackItemBinding
import com.lfsolutions.retail.model.service.Feedback
import com.lfsolutions.retail.model.service.FeedbackValue

class FeedbackItemView : LinearLayout {

    private var binding: FeedbackItemBinding? = null
    lateinit var feedback: Feedback

    constructor(context: Context?, feedback: Feedback) : super(context) {
        this.feedback = feedback
    }

    init {
        binding = FeedbackItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setup(horizontal: Boolean = true) {
        binding!!.feedbackTitle.text = feedback.title
        if (horizontal) {
            binding!!.feedbackItems.orientation = HORIZONTAL
            binding!!.feedbackItems.gravity = Gravity.CENTER
        } else {
            binding!!.feedbackItems.orientation = VERTICAL
            binding!!.feedbackItems.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }

        feedback.feedbackValue.forEach { value ->
            binding!!.feedbackItems.addView(getRadioButton(value))
        }
        binding!!.feedbackItems.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = binding!!.feedbackItems.findViewById<RadioButton>(checkedId)
            feedback.selected = (radioButton.tag as FeedbackValue).value
        }
    }

    private fun getRadioButton(value: FeedbackValue): RadioButton {
        val button = RadioButton(context)
        button.setText(value.value)
        button.tag = value
        button.setTextColor(Color.BLACK)
        button.setPadding(5)
        button.layoutParams = RadioGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(5)
        }
        button.setButtonDrawable(R.drawable.radio_button_selector)
        return button
    }

}