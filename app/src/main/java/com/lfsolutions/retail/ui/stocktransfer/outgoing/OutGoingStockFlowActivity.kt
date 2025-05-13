package com.lfsolutions.retail.ui.stocktransfer.outgoing

import android.os.Bundle
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.ActivityOutGoingStockTransferFlowBinding
import com.lfsolutions.retail.ui.BaseActivity

class OutGoingStockFlowActivity : BaseActivity() {

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