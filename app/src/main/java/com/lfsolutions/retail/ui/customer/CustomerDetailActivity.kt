package com.lfsolutions.retail.ui.customer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.lfsolutions.retail.databinding.ActivityCustomerDetailBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.Constants

class CustomerDetailActivity : AppCompatActivity() {

    var customer: Customer? = null
    private var mBinding: ActivityCustomerDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomer()
        mBinding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
    }

    private fun setCustomer() {
        customer = Gson().fromJson(intent.getStringExtra(Constants.Customer), Customer::class.java)
    }

    companion object {
        fun start(context: Context, customer: Customer) {
            context.startActivity(Intent(context, CustomerDetailActivity::class.java).apply {
                putExtra(Constants.Customer, Gson().toJson(customer))
            })
        }
    }

}