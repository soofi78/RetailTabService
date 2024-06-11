package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentEquipmentListBinding

class EquipmentListFragment : Fragment() {

    private var _binding: FragmentEquipmentListBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: EquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEquipmentListBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mAdapter = EquipmentAdapter()

        mAdapter.setListener(object : EquipmentAdapter.OnEquipmentClickListener {

            override fun onEquipmentClick() {

                findNavController()
                    .navigate(R.id.action_navigation_agreement_memo_bottom_navigation_to_navigation_add_equipment)

            }

        })

        mBinding.recyclerView.adapter = mAdapter

    }

}