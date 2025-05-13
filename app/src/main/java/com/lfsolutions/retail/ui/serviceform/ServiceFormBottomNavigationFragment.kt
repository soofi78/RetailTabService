package com.lfsolutions.retail.ui.serviceform

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
import com.lfsolutions.retail.databinding.FragmentServiceFormBottomNavigationBinding
import com.lfsolutions.retail.util.Constants


class ServiceFormBottomNavigationFragment : Fragment() {

    private var _binding: FragmentServiceFormBottomNavigationBinding? = null
    private val args by navArgs<ServiceFormBottomNavigationFragmentArgs>()
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceFormBottomNavigationBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_service_form_equipment -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity, ServiceFormEquipmentListFragment().apply {
                            arguments = bundleOf(
                                Constants.Customer to args.customer
                            )
                        }, "NewFragmentTag"
                    )
                    ft.commit()
                    return@setOnItemSelectedListener true

                }

                R.id.navigation_service_form_summary -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        ServiceFormSummaryFragment(),
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
        if (args.IsEquipment) {
            mBinding.navView.selectedItemId = R.id.navigation_service_form_equipment
        } else {
            mBinding.navView.selectedItemId = R.id.navigation_service_form_summary
        }
    }

}