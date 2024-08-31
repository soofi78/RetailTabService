package com.lfsolutions.retail.ui.documents.payment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentCustomerForPaymentBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerPaymentsResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.documents.drivermemo.DriverMemoFlowActivity
import com.lfsolutions.retail.ui.serviceform.ServiceFormFragmentArgs
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class CustomerForPaymentsFragment : Fragment(), OnNetworkResponse {

    private var customers: ArrayList<Customer> = ArrayList()
    private lateinit var binding: FragmentCustomerForPaymentBinding
    private lateinit var customersAdapter: CustomerForPaymentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentCustomerForPaymentBinding.inflate(inflater, container, false)
            setHeaderData()
            setCustomerAdapter(customers)
            addVerticalItemDecoration(binding.customers, requireContext())
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCustomerDetails()
    }

    private fun setHeaderData() {
        binding.header.setBackText("Customers")
        Main.app.getSession().name?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                setCustomerAdapter(customers.filter {
                    isCandidateForFilter(
                        query?.split(" ")?.toSet(), it
                    )
                } as ArrayList<Customer>)
                return true
            }
        })
    }

    private fun isCandidateForFilter(query: Set<String>?, customer: Customer): Boolean {
        if (query.isNullOrEmpty()) return true
        var contains = true
        query.forEach {
            contains = contains && (customer.name?.lowercase()
                ?.contains(it.lowercase()) == true || customer.country?.lowercase()
                ?.contains(it) == true || customer.area?.lowercase()
                ?.contains(it) == true || customer.address1?.lowercase()
                ?.contains(it) == true || customer.address2?.lowercase()
                ?.contains(it) == true || customer.address3?.lowercase()
                ?.contains(it) == true || customer.customerCode?.lowercase()
                ?.contains(it) == true || customer.email?.lowercase()
                ?.contains(it) == true || customer.salespersonName?.lowercase()
                ?.contains(it) == true || customer.city?.lowercase()?.contains(it) == true)
        }
        return contains
    }

    private fun getCustomerDetails() {
        if (customers.isEmpty()) NetworkCall.make().setCallback(this)
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getCustomersForPayment()).execute()
    }

    private fun openSalesInvoicesFor(customer: Customer) {
        findNavController().navigate(
            R.id.action_navigation_customer_for_payments_to_customer_transactions,
            Bundle().apply {
                putString(Constants.Customer, Gson().toJson(customer))

            })
    }


    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        val customerResponse = response?.body() as RetailResponse<CustomerPaymentsResult>
        customerResponse.result?.items?.let {
            customers = it
            setCustomerAdapter(customers)
        }
    }

    private fun setCustomerAdapter(customer: ArrayList<Customer>) {
        customersAdapter = CustomerForPaymentAdapter(customer.filter {
            (it.balanceAmount != null && it.balanceAmount?.equals(
                0.0
            )?.not() == true)
        })
        binding.customers.adapter = customersAdapter
        customersAdapter.setListener(object : CustomerForPaymentAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                openSalesInvoicesFor(customer)
            }
        })
    }


    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
        Notify.toastLong("Unable to load list")
    }

    fun addVerticalItemDecoration(recyclerView: RecyclerView, context: Context) {
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL
            )
        )
    }

}