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
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityFormsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.Constants

class FormsActivity : BaseActivity() {

    var customer: Customer? = null
    private lateinit var mBinding: ActivityFormsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomer()
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
    }

    private fun setCustomer() {
        customer = Gson().fromJson(
            intent.getStringExtra(Constants.Customer), Customer::class.java
        )
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