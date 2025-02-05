package com.lfsolutions.retail.ui.stocktransfer.incoming

import android.os.Bundle
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityIncomingStockFlowBinding
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.Constants

class IncomingStockFlowActivity : BaseActivity() {

    private lateinit var mBinding: ActivityIncomingStockFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Main.app.clearInComingStockTransfer()
        Main.app.getInComingStockTransferRequestObject()
        getInComingProductsFromIntent()
        mBinding = ActivityIncomingStockFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun getInComingProductsFromIntent() {
        val json = intent.getStringExtra(Constants.InComingProducts)
        val type = TypeToken.getParameterized(
            ArrayList::class.java, StockTransferProduct::class.java
        ).type
        Main.app.getInComingStockTransferRequestObject().stockTransferDetails.addAll(
            Gson().fromJson(
                json, type
            )
        )
        Main.app.getInComingStockTransferRequestObject().customerIds =
            intent.getIntegerArrayListExtra(Constants.CustomerId)
        intent.getIntegerArrayListExtra(
            Constants.OrderIds
        )?.let {
            Main.app.getInComingStockTransferRequestObject().SalesOrderIds.addAll(
                it
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mBinding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_incoming_equipment_list_menu -> {
                    findNavController(R.id.nav_in_coming_host_fragment_activity).navigate(R.id.navigation_in_coming_stock_product_listing)
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_incoming_summary_menu -> {
                    findNavController(R.id.nav_in_coming_host_fragment_activity).navigate(R.id.navigation_incoming_stock_summary)
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearOutGoingStockTransfer()
    }
}