package com.lfsolutions.retail.ui.forms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityFormsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.ui.widgets.options.OnOptionItemClick
import com.lfsolutions.retail.ui.widgets.options.OptionItem
import com.lfsolutions.retail.ui.widgets.options.OptionsBottomSheet
import com.lfsolutions.retail.util.Constants
import com.videotel.digital.util.Notify

class FormsActivity : AppCompatActivity() {

    var customer: Customer? = null
    private lateinit var mBinding: ActivityFormsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFormsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val navView: BottomNavigationView = mBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_item_details)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_current_forms, R.id.navigation_history -> showBottomNavigationBar()
                else -> hideBottomNavigationBar()
            }
        }
        navView.setupWithNavController(navController)
        setupHeader()
        setCustomer()
    }

    private fun setupHeader() {
        Main.app.getSession().name?.let { mBinding.header.setName(it) }
        mBinding.header.setOnBackClick { finish() }
        mBinding.header.setBackText("Customer Forms")
    }

    private fun setCustomerData() {
        customer?.let { mBinding.customerView.setCustomer(it) }
        mBinding.customerView.setOnClickListener {
            OptionsBottomSheet.show(
                supportFragmentManager,
                arrayListOf(OptionItem("View Customer", R.drawable.person_black)),
                object : OnOptionItemClick {
                    override fun onOptionItemClick(optionItem: OptionItem) {
                        Notify.toastLong("Open Customer")
                    }
                })
        }
    }

    private fun setCustomer() {
        customer = Gson().fromJson(
            intent.getStringExtra(Constants.Customer), Customer::class.java
        )
        setCustomerData()
    }


    fun setTitle(title: String) {
        mBinding.header.setBackText(title)
    }


    private fun showBottomNavigationBar() {
        mBinding.navView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigationBar() {
        mBinding.navView.visibility = View.GONE
    }

    companion object {
        fun getInstance(context: Context): Intent = Intent(context, FormsActivity::class.java)
    }

}