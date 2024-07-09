package com.lfsolutions.retail.ui.delivery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.FragmentDeliveryBinding
import com.lfsolutions.retail.ui.forms.FormsActivity

class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel: DeliveryViewModel by viewModels()

    private lateinit var mUrgentAdapter : DeliveryItemAdapter

    private lateinit var mToVisitAdapter : DeliveryItemAdapter
    private lateinit var mScheduleAdapter: DeliveryItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mUrgentAdapter = DeliveryItemAdapter(1)

        mToVisitAdapter = DeliveryItemAdapter(3)
        mScheduleAdapter = DeliveryItemAdapter(2)

        mBinding.recyclerViewUrgent.adapter = mUrgentAdapter

        mBinding.recyclerViewToVisit.adapter = mToVisitAdapter
        mBinding.recyclerViewSchedule.adapter = mScheduleAdapter


        addVerticalItemDecoration(mBinding.recyclerViewToVisit, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewUrgent, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewSchedule, requireContext())


        mUrgentAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener{

            override fun onItemClick() {

                displayItemDetails()

            }

        })

        mToVisitAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener{

            override fun onItemClick() {

                displayItemDetails()

            }

        })

    }

    private fun displayItemDetails(){

        startActivity(FormsActivity.getInstance(context = requireContext()))

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}

fun addVerticalItemDecoration(recyclerView: RecyclerView, context: Context) {
    recyclerView.addItemDecoration(
        DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
    )
}