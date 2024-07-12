package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentAgreementMemoBottomNavigationBinding
import com.lfsolutions.retail.util.Constants


class AgreementMemoBottomNavigationFragment : Fragment() {

    private var _binding: FragmentAgreementMemoBottomNavigationBinding? = null
    private val args by navArgs<AgreementMemoBottomNavigationFragmentArgs>()
    private val mBinding get() = _binding!!

    private val mViewModel: AgreementMemoBottomNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAgreementMemoBottomNavigationBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity)
        //mBinding.navView.setupWithNavController(navController)
        mBinding.navView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.navigation_equipment -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        EquipmentListFragment().apply {
                            arguments = bundleOf(
                                Constants.Form to args.form,
                                Constants.Customer to args.customer
                            )
                        },
                        "NewFragmentTag"
                    )
                    ft.commit()
                    return@setOnItemSelectedListener true

                }

                R.id.navigation_order -> {
                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.nav_host_fragment_activity,
                        OrderSummaryFragment(),
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
                mBinding.navView.selectedItemId = R.id.navigation_equipment
                mBinding.txtBack.text = getString(R.string.label_equipment_list)

            } else {
                mBinding.navView.selectedItemId = R.id.navigation_order
                mBinding.txtBack.text = getString(R.string.label_order_summary)
            }
        }

        /* navController.addOnDestinationChangedListener { _, destination, _ ->
             when (destination.id) {
                 R.id.navigation_equipment, R.id.navigation_order -> showBottomNavigationBar()
                 else -> hideBottomNavigationBar()
             }
         }*/

        addKeyListener()
        addOnClickLister()
        addDataObserver()
    }

    private fun addDataObserver() {

        mViewModel.isOrderCompleted.observe(viewLifecycleOwner) { isOrderCompleted ->
            if (isOrderCompleted) {
                mBinding.root.findNavController().popBackStack()
                mViewModel.setOrderCompleted(false)
            }
        }
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
            it.findNavController().popBackStack()
        }
    }

    private fun addKeyListener() {
        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                v.findNavController().popBackStack()
            }
            return@setOnKeyListener true;
        }
    }

}