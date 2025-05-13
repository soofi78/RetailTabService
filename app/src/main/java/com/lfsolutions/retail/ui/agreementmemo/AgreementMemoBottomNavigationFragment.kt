package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.navArgs
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentAgreementMemoBottomNavigationBinding
import com.lfsolutions.retail.util.Constants


class AgreementMemoBottomNavigationFragment : Fragment() {

    private var _binding: FragmentAgreementMemoBottomNavigationBinding? = null
    private val args by navArgs<AgreementMemoBottomNavigationFragmentArgs>()
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgreementMemoBottomNavigationBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_agreement_memo_equipment -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        AgreementMemoEquipmentListFragment().apply {
                            arguments = bundleOf(
                                Constants.Customer to args.customer
                            )
                        },
                        "NewFragmentTag"
                    )
                    ft.commit()
                    return@setOnItemSelectedListener true

                }

                R.id.navigation_agreement_memo_summary -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        AgreementMemoSummaryFragment(),
                        "NewFragmentTag"
                    )
                    ft.commit()
                    return@setOnItemSelectedListener true
                }

                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
        arguments?.let {

            if (it.getBoolean("IsEquipment")) {
                mBinding.navView.selectedItemId = R.id.navigation_agreement_memo_equipment
            } else {
                mBinding.navView.selectedItemId = R.id.navigation_agreement_memo_summary
            }
        }
    }

}