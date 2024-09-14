package com.lfsolutions.retail.ui.documents.history

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.HistoryFilterSheetBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerPaymentsResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response
import kotlin.collections.ArrayList

class HistoryFilterSheet : BottomSheetDialogFragment() {

    private var selectedCustomer: Customer? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private lateinit var customers: ArrayList<Customer>
    private lateinit var onHistoryFilter: OnHistoryFilter
    private lateinit var binding: HistoryFilterSheetBinding
    private lateinit var customerAdapter: DeliveryItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = HistoryFilterSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSelectedCustomerData()
        setSelectedDateData()
        binding.selectedCustomer.setOnClickListener {
            getCustomerDetails()
        }
        binding.selectDate.setOnClickListener {
            getStartDate()
        }
        binding.allDate.setOnClickListener {
            startDate = null
            endDate = null
            setSelectedDateData()
        }
        binding.allCustomer.setOnClickListener {
            selectedCustomer = null
            setSelectedCustomerData()
        }
        binding.done.setOnClickListener {
            onHistoryFilter.onFilter(startDate, endDate, selectedCustomer)
            dismiss()
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setCustomerAdapter(customers, newText ?: "")
                return true
            }
        })
    }

    private fun setSelectedDateData() {
        if (startDate == null || endDate == null) {
            binding.selectDate.text = "Select Dates"
            binding.selectDate.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(
                    binding.root.context, R.drawable.arrow_right_gray
                ), null
            )
            binding.allDate.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(
                    binding.root.context, R.drawable.ic_check
                ), null
            )
        } else {
            binding.allDate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            binding.selectDate.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(binding.root.context, R.drawable.ic_check),
                null
            )
            setFormattedDates()

        }
    }

    private fun setSelectedCustomerData() {
        if (selectedCustomer == null) {
            binding.selectedCustomer.text = "Select Customer"
            binding.selectedCustomer.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(
                    binding.root.context, R.drawable.arrow_right_gray
                ), null
            )
            binding.allCustomer.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(
                    binding.root.context, R.drawable.ic_check
                ), null
            )
        } else {
            binding.selectedCustomer.text = selectedCustomer?.name
            binding.selectedCustomer.setCompoundDrawablesWithIntrinsicBounds(
                null, null, ContextCompat.getDrawable(
                    binding.root.context, R.drawable.ic_check
                ), null
            )
            binding.allCustomer.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    private fun getStartDate() {
        DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
            override fun onDateSelected(year: String, month: String, day: String) {
                startDate = year + "-" + month + "-" + day + "T00:00:00Z"
                getEndDate()
            }
        })
    }

    private fun getEndDate() {
        DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
            override fun onDateSelected(year: String, month: String, day: String) {
                endDate = year + "-" + month + "-" + day + "T00:00:00Z"
                setSelectedDateData()
            }
        })
    }

    fun setFormattedDates() {
        val start = DateTime.getDateFromString(
            startDate?.replace("T", " ")?.replace("Z", ""), DateTime.DateTimetRetailFormat
        )
        val end = DateTime.getDateFromString(
            endDate?.replace("T", " ")?.replace("Z", ""), DateTime.DateTimetRetailFormat
        )
        binding.selectDate.text =
            buildString {
                append(DateTime.format(start, DateTime.DateFormatRetail))
                append(" - ")
                append(DateTime.format(end, DateTime.DateFormatRetail))
            }
    }

    private fun getCustomerDetails() {
        if (::customers.isInitialized.not()) NetworkCall.make()
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    customers =
                        (response?.body() as RetailResponse<CustomerPaymentsResult>).result?.items!!
                    setCustomerAdapter(customers)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get customer data")
                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getCustomersForPayment()).execute()
        else setCustomerAdapter(customers)
    }

    private fun setCustomerAdapter(customers: ArrayList<Customer>, query: String = "") {
        customerAdapter = DeliveryItemAdapter(
            customers.filter { isCandidateForFilter(query, it) } as ArrayList<Customer>?,
            DeliveryItemAdapter.CustomerItemType.All
        )
        customerAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                binding.customersViewHolder.visibility = View.GONE
                selectedCustomer = customer
                setSelectedCustomerData()
            }
        })
        binding.customers.adapter = customerAdapter
        binding.customersViewHolder.visibility = View.VISIBLE
    }

    private fun isCandidateForFilter(query: String, customer: Customer): Boolean {
        if (query.isEmpty())
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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    fun setOnProductDetailsChangedListener(onHistoryFilter: OnHistoryFilter) {
        this.onHistoryFilter = onHistoryFilter
    }

    fun setFilteredData(customer: Customer?, startDate: String?, endDate: String?) {
        this.selectedCustomer = customer
        this.startDate = startDate
        this.endDate = endDate
    }

    companion object {
        const val TAG = "History Filter Sheet"
    }

    interface OnHistoryFilter {
        fun onFilter(startDate: String?, endDate: String?, customer: Customer?)
    }
}
