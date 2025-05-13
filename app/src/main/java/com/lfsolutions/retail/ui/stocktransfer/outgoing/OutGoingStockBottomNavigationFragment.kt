package com.lfsolutions.retail.ui.stocktransfer.outgoing

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
import com.lfsolutions.retail.databinding.FragmentOutGoingStockBottomNavigationBinding
import com.lfsolutions.retail.ui.agreementmemo.NewAgreementMemoFragmentArgs
import com.lfsolutions.retail.util.Constants


class OutGoingStockBottomNavigationFragment : Fragment() {

    private var _binding: FragmentOutGoingStockBottomNavigationBinding? = null
    private val args by navArgs<OutGoingStockBottomNavigationFragmentArgs>()
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutGoingStockBottomNavigationBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_outgoing_equipment_list_menu -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        OutGoingEquipmentListFragment(),
                        "NewFragmentTag"
                    )
                    ft.commit()
                    return@setOnItemSelectedListener true

                }

                R.id.navigation_outgoing_summary_menu -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        OutGoingStockSummaryFragment {
                            openEquipmentList()
                        },
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
            mBinding.navView.selectedItemId = R.id.navigation_outgoing_equipment_list_menu
        } else {
            mBinding.navView.selectedItemId = R.id.navigation_outgoing_summary_menu
        }
    }

    private fun openEquipmentList() {
        mBinding.navView.selectedItemId = R.id.navigation_outgoing_equipment_list_menu
    }

}