package com.lfsolutions.retail.ui.documents.history

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityHistoryFlowBinding
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.Constants

class HistoryFlowActivity : BaseActivity() {

    private lateinit var mBinding: ActivityHistoryFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHistoryFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.OrderId)) {
            findNavController(R.id.nav_host_fragment_activity_item_details).navigate(
                R.id.navigation_order_detail,
                bundleOf(
                    Constants.OrderId to intent.getIntExtra(Constants.OrderId, 0).toString()
                )
            )
        }
    }
}