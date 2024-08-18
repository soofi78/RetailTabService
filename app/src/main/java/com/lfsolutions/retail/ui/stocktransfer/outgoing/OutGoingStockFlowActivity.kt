package com.lfsolutions.retail.ui.stocktransfer.outgoing

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
import com.lfsolutions.retail.databinding.ActivityHistoryFlowBinding
import com.lfsolutions.retail.databinding.ActivityOutGoingStockTransferFlowBinding
import com.lfsolutions.retail.databinding.ActivityPaymentsFlowBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.Constants

class OutGoingStockFlowActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOutGoingStockTransferFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Main.app.clearOutGoingStockTransfer()
        Main.app.getOutGoingStockTransferRequestObject()
        mBinding = ActivityOutGoingStockTransferFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearOutGoingStockTransfer()
    }
}