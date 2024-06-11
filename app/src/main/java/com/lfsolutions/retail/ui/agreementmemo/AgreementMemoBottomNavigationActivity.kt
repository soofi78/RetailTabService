package com.lfsolutions.retail.ui.agreementmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityAgreementMemoBottomNavigationBinding

class AgreementMemoBottomNavigationActivity : AppCompatActivity() {

    private var _binding: ActivityAgreementMemoBottomNavigationBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel: AgreementMemoBottomNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityAgreementMemoBottomNavigationBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity)

        mBinding.navView.setupWithNavController(navController)

        intent?.let {

            if (it.getBooleanExtra(IS_EQUIPMENT, true)) {

                navController.navigate(R.id.navigation_equipment)

                mBinding.txtBack.text = getString(R.string.label_equipment_list)

            } else {

                navController.navigate(R.id.navigation_order)

                mBinding.txtBack.text = getString(R.string.label_order_summary)

            }

        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_equipment, R.id.navigation_order -> showBottomNavigationBar()
                else -> hideBottomNavigationBar()
            }
        }

        addOnClickLister()

    }

    private fun showBottomNavigationBar() {

        mBinding.navView.visibility = View.VISIBLE

        mBinding.cardHeader.visibility = View.VISIBLE

    }

    private fun hideBottomNavigationBar() {

        mBinding.navView.visibility = View.GONE

        mBinding.cardHeader.visibility = View.GONE

    }

    private fun addOnClickLister() {

        mBinding.flowBack.setOnClickListener {

            finish()

        }

    }

    companion object {

        private const val IS_EQUIPMENT = "IsEquipment"

        fun getIntent(
            context: Context,
            isEquipment: Boolean
        ): Intent =
            Intent(context, AgreementMemoBottomNavigationActivity::class.java)
                .also {

                    it.putExtra(IS_EQUIPMENT, isEquipment)

                }

    }

}