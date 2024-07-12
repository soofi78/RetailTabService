package com.lfsolutions.retail.ui.delivery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lfsolutions.retail.databinding.FragmentDeliveryBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.ErrorResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import retrofit2.Call
import retrofit2.Response

class DeliveryFragment : Fragment(), OnNetworkResponse {

    private var _binding: FragmentDeliveryBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mUrgentAdapter: DeliveryItemAdapter

    private lateinit var mToVisitAdapter: DeliveryItemAdapter
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


        setAdapters(null)
        addVerticalItemDecoration(mBinding.recyclerViewToVisit, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewUrgent, requireContext())
        addVerticalItemDecoration(mBinding.recyclerViewSchedule, requireContext())

        getCustomerDetails()

    }

    private fun getCustomerDetails() {
        NetworkCall.make().setCallback(this)
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getCustomers())
            .execute()
    }

    private fun displayItemDetails(customer: Customer) {
        startActivity(FormsActivity.getInstance(context = requireContext()).apply {
            putExtra(Constants.Customer, Gson().toJson(customer))
        })
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        val getCustomersResponse = response?.body() as RetailResponse<CustomerResult>
        setAdapters(getCustomersResponse)
    }

    private fun setAdapters(getCustomersResponse: RetailResponse<CustomerResult>?) {
        mUrgentAdapter = DeliveryItemAdapter(getCustomersResponse?.result?.getUrgentCustomersList())
        mToVisitAdapter =
            DeliveryItemAdapter(getCustomersResponse?.result?.getToVisitsCustomersList())
        mScheduleAdapter =
            DeliveryItemAdapter(getCustomersResponse?.result?.getScheduledCustomersList())
        mBinding.recyclerViewUrgent.adapter = mUrgentAdapter
        mBinding.recyclerViewToVisit.adapter = mToVisitAdapter
        mBinding.recyclerViewSchedule.adapter = mScheduleAdapter
        mUrgentAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                displayItemDetails(customer)
            }
        })

        mToVisitAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                displayItemDetails(customer)
            }
        })

    }

    override fun onFailure(call: Call<*>?, response: ErrorResponse?, tag: Any?) {

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