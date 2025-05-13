package com.lfsolutions.retail.ui.documents.payment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lfsolutions.retail.databinding.ActivityPaymentsFlowBinding
import com.lfsolutions.retail.ui.BaseActivity

class PaymentFlowActivity : BaseActivity() {

    private lateinit var mBinding: ActivityPaymentsFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPaymentsFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}