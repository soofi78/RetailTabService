package com.lfsolutions.retail.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.lfsolutions.retail.databinding.FragmentScheduleBinding
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel : ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mBinding.recyclerViewVisitationSchedule.adapter = DeliveryItemAdapter(3)

        mBinding.recyclerViewVisitationSchedule.addItemDecoration(
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