package com.lfsolutions.retail.ui.documents.dailysale

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityDailySaleFlowBinding
import com.lfsolutions.retail.databinding.ActivityPaymentsFlowBinding
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.serviceform.ServiceFormEquipmentListFragment
import com.lfsolutions.retail.util.Constants

class DailySaleFlowActivity : BaseActivity() {

    private lateinit var mBinding: ActivityDailySaleFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDailySaleFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.dailySaleRcordFragmentHolde, DailySaleRecordFragment(), "TAG")
        ft.commit()
    }

    fun getSelectedDate(): String? {
        return intent.getStringExtra("date")
    }
}