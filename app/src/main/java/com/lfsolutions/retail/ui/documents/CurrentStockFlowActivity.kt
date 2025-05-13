package com.lfsolutions.retail.ui.documents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lfsolutions.retail.databinding.ActivityCurrentStockFlowBinding
import com.lfsolutions.retail.databinding.ActivityPaymentsFlowBinding
import com.lfsolutions.retail.ui.BaseActivity

class CurrentStockFlowActivity : BaseActivity() {

    private lateinit var mBinding: ActivityCurrentStockFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCurrentStockFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}