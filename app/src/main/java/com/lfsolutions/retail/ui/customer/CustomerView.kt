package com.lfsolutions.retail.ui.customer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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
        binding?.txtAddress2?.visibility = if (customer.address2.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding?.txtAddress3?.visibility = if (customer.address3.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding?.txtCustomerName?.text = customer.name
        binding?.txtAddress1?.text = customer.address1
        binding?.txtAddress2?.text = customer.address2
        binding?.txtAddress3?.text = customer.address3
    }


}