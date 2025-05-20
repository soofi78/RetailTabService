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
    var isFromDeliveryFragment: Boolean = false

    private var navState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomer()
        mBinding = ActivityFormsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val navView: BottomNavigationView = mBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_item_details)

        // ✅ Restore NavController state
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle("nav_state")
            navState?.let {
                navController.restoreState(it)
            }
        }
        // ✅ Set up Bottom NavBar visibility
        mBinding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_current_forms, R.id.navigation_history -> showBottomNavigationBar()
                else -> hideBottomNavigationBar()
            }
        }
        //navView.setupWithNavController(navController)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val navController = findNavController(R.id.nav_host_fragment_activity_item_details)
        outState.putBundle("nav_state", navController.saveState())
    }

    private fun setCustomer() {
        customer = Gson().fromJson(
            intent.getStringExtra(Constants.Customer), Customer::class.java
        )
    }

    fun getCustomerId(): Int? {
        return customer?.id
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