package com.lfsolutions.retail.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.Gson
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentScheduleBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerIdRequest
import com.lfsolutions.retail.model.CustomersResult
import com.lfsolutions.retail.model.VisitDateRequest
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class ScheduleFragment : Fragment() {

    private var mScheduleAdapter: DeliveryItemAdapter? = null
    private var res: BaseResponse<CustomersResult>? = null
    private var date: String? = null
    private var _binding: FragmentScheduleBinding? = null
    private val mBinding get() = _binding!!
    private var sort = Constants.ASCENDING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        date = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.dateFilter.text =
            DateTime.getCurrentDateTime(DateTime.DateFormatWithMonthNameAndYear)
        mBinding.dateFilter.setOnClickListener {
            DateTime.showMonthYearPicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    super.onDateSelected(year, month, day)
                    date = "$year-$month-$day"
                    mBinding.dateFilter.text = DateTime.format(
                        DateTime.getDateFromString(date, DateTime.DateFormatRetail),
                        DateTime.DateFormatWithMonthNameAndYear
                    )
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

        mBinding.fabAddToScheduled.setOnClickListener {
            val list = mScheduleAdapter?.getCheckedItemList()
            if (list.isNullOrEmpty()) {
                Notify.toastLong("Please select items")
                return@setOnClickListener
            }

            val ids = arrayListOf<CustomerIdRequest>()
            list.forEach { customer ->
                customer.id?.let { customerId -> ids.add(CustomerIdRequest(id = customerId)) }
            }
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Adding to schedule..."))
                .enque(Network.api()?.addCustomerVisitationSchedule(ids))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(
                        call: Call<*>?,
                        response: Response<*>?,
                        tag: Any?
                    ) {
                        Notify.toastLong("Success")
                        findNavController().navigate(R.id.navigation_delivery)
                    }

                    override fun onFailure(
                        call: Call<*>?,
                        response: BaseResponse<*>?,
                        tag: Any?
                    ) {
                        Notify.toastLong("Unable to add to schedule")
                    }
                }).execute()
        }

        mBinding.sort.setOnClickListener {
            sort = if (sort == Constants.ASCENDING) Constants.DESCENDING else Constants.ASCENDING
            Notify.toastLong("Sort: $sort")
            setAdapters(res, (mBinding.searchView.query ?: "").toString())
        }
    }


    private fun setAdapters(getCustomersResponse: BaseResponse<CustomersResult>?, s: String) {
        val list = getCustomersResponse?.result?.getScheduledVisitationCustomersList()
            ?.filter { isCandidateForFilter(s, it) }
        val sortedResult = if (sort == Constants.ASCENDING) {
            list?.sortedBy { it.name }
        } else {
            list?.sortedByDescending { it.name }
        }
        mScheduleAdapter = DeliveryItemAdapter(
            ArrayList<Customer>().apply {
                sortedResult?.forEach {
                    add(it)
                }
            },
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
                        || customer.customerCode?.lowercase()?.contains(it) == true
                        || customer.address1?.lowercase()?.contains(it) == true
                        || customer.address2?.lowercase()?.contains(it) == true
                        || customer.address3?.lowercase()?.contains(it) == true)
        }
        return contains
    }


    private fun getScheduledVisitation() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                res = response?.body() as BaseResponse<CustomersResult>
                setAdapters(res, "")
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {

            }
        }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getScheduledVisitation(VisitDateRequest(VisitDate = this.date)))
            .execute()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}