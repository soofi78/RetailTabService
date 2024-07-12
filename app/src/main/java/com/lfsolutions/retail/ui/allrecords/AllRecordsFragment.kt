package com.lfsolutions.retail.ui.allrecords

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.lfsolutions.retail.databinding.FragmentAllRecordsBinding
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter

class AllRecordsFragment : Fragment() {

    private var _binding: FragmentAllRecordsBinding? = null

    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllRecordsBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mBinding.recyclerViewAllCustomers.adapter = DeliveryItemAdapter(ArrayList())

        mBinding.recyclerViewAllCustomers.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}