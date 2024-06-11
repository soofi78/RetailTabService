package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lfsolutions.retail.databinding.FragmentOrderSummaryBinding

class OrderSummaryFragment : Fragment() {

    private var _binding: FragmentOrderSummaryBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: OrderSummaryAdapter

    private val mViewModel: AgreementMemoBottomNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mAdapter = OrderSummaryAdapter()

        mAdapter.setListener(object : OrderSummaryAdapter.OnOrderSummarySelectListener {

            override fun onOrderSummarySelect() {

                /*mBinding.root.findNavController()
                    .navigate(R.id.action_navigation_order_to_navigation_add_equipment)*/

            }

        })

        mBinding.recyclerView.adapter = mAdapter

        addOnClickListener()

    }

    private fun addOnClickListener() {

        mBinding.btnComplete.setOnClickListener {

            requireActivity().finish()

        }

    }

}