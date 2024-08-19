package com.lfsolutions.retail.ui.customer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.lfsolutions.retail.databinding.CustomerViewBinding
import com.lfsolutions.retail.model.Customer

class CustomerView : LinearLayout {

    var binding: CustomerViewBinding? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        binding = CustomerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setCustomer(customer: Customer) {
        binding?.txtCustomerName?.text = customer.name
        binding?.txtAddress?.text = customer.address1
    }
}