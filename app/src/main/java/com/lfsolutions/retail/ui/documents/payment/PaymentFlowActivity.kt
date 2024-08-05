package com.lfsolutions.retail.ui.documents.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityFormsBinding
import com.lfsolutions.retail.databinding.ActivityPaymentsFlowBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.Constants

class PaymentFlowActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPaymentsFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPaymentsFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}