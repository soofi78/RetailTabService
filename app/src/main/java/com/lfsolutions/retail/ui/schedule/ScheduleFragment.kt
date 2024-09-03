package com.lfsolutions.retail.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.Gson
import com.lfsolutions.retail.databinding.FragmentScheduleBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.DateRequest
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import retrofit2.Call
import retrofit2.Response

class ScheduleFragment : Fragment() {

    private var mScheduleAdapter: DeliveryItemAdapter? = null
    private var res: BaseResponse<CustomerResult>? = null
    private var date: String? = null
    private var _binding: FragmentScheduleBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        date = DateTime.getCurrentDateTime(DateTime.DateFormatSV)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.dateFilter.text = date
        mBinding.dateFilter.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    super.onDateSelected(year, month, day)
                    date = "$day-$month-$year"
                    mBinding.dateFilter.text = date
                    getScheduledVisitation()
                }
            })
        }
        mBinding.recyclerViewVisitationSchedule.adapter =
            DeliveryItemAdapter(ArrayList(), DeliveryItemAdapter.CustomerItemType.Scheduled)
        mBinding.recyclerViewVisitationSchedule.addItemDecoration(
            DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
        )
        mBinding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setAdapters(res, newText ?: "")
                return true
            }
        })
        getScheduledVisitation()
    }


    private fun setAdapters(getCustomersResponse: BaseResponse<CustomerResult>?, s: String) {
        mScheduleAdapter = DeliveryItemAdapter(
            getCustomersResponse?.result?.getScheduledVisitationCustomersList()
                ?.filter { isCandidateForFilter(s, it) },
            DeliveryItemAdapter.CustomerItemType.Scheduled
        )
        mBinding.recyclerViewVisitationSchedule.adapter = mScheduleAdapter
        mScheduleAdapter?.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                displayItemDetails(customer)
            }
        })
    }

    private fun displayItemDetails(customer: Customer) {
        startActivity(FormsActivity.getInstance(context = requireContext()).apply {
            putExtra(Constants.Customer, Gson().toJson(customer))
        })
    }

    private fun isCandidateForFilter(query: String, customer: Customer): Boolean {
        if (query.isNullOrEmpty())
            return true
        var contains = true
        query.split(" ").toSet().forEach {
            contains =
                contains && (customer.name?.lowercase()?.contains(it.lowercase()) == true
                        || customer.country?.lowercase()?.contains(it) == true
                        || customer.area?.lowercase()?.contains(it) == true
                        || customer.address1?.lowercase()?.contains(it) == true
                        || customer.address2?.lowercase()?.contains(it) == true
                        || customer.address3?.lowercase()?.contains(it) == true
                        || customer.customerCode?.lowercase()?.contains(it) == true
                        || customer.email?.lowercase()?.contains(it) == true
                        || customer.salespersonName?.lowercase()?.contains(it) == true
                        || customer.city?.lowercase()?.contains(it) == true)
        }
        return contains
    }


    private fun getScheduledVisitation() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                res = response?.body() as BaseResponse<CustomerResult>
                setAdapters(res, "")
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

            }
        }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getScheduledVisitation(DateRequest(date = this.date))).execute()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}