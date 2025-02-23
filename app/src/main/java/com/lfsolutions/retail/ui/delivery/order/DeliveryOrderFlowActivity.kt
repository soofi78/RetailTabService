package com.lfsolutions.retail.ui.delivery.order

import android.os.Bundle
import com.lfsolutions.retail.databinding.ActivityDeliveryOrderFlowBinding
import com.lfsolutions.retail.ui.BaseActivity

class DeliveryOrderFlowActivity : BaseActivity() {

    private lateinit var mBinding: ActivityDeliveryOrderFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDeliveryOrderFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}