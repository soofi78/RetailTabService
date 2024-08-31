package com.lfsolutions.retail.ui.documents.drivermemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityDailySaleFlowBinding
import com.lfsolutions.retail.databinding.ActivityDriverMemoFlowBinding
import com.lfsolutions.retail.databinding.ActivityPaymentsFlowBinding
import com.lfsolutions.retail.ui.documents.payment.CustomerForPaymentsFragment
import com.lfsolutions.retail.ui.serviceform.ServiceFormEquipmentListFragment
import com.lfsolutions.retail.util.Constants

class DriverMemoFlowActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDriverMemoFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDriverMemoFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}