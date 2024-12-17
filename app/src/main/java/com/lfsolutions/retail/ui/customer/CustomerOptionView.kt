package com.lfsolutions.retail.ui.customer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.CustomerOptionsSheetBinding
import com.lfsolutions.retail.model.AllCustomersResult
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.LocationTenantIdRequestObject
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryFilterSheet
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class CustomerOptionView : BottomSheetDialogFragment() {

    private lateinit var onItemClicked: DeliveryItemAdapter.OnItemClickListener
    private lateinit var customers: ArrayList<Customer>
    private lateinit var binding: CustomerOptionsSheetBinding
    private lateinit var customerAdapter: DeliveryItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CustomerOptionsSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setCustomerAdapter(customers, newText ?: "")
                return true
            }
        })
        getCustomerDetails()
    }

    private fun getCustomerDetails() {
        if (::customers.isInitialized.not()) NetworkCall.make()
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    customers =
                        (response?.body() as BaseResponse<AllCustomersResult>).result?.items!!
                    setCustomerAdapter(customers, binding.searchView.query.toString())
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get customer data")
                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(
                Network.api()?.getAllCustomers(
                    LocationTenantIdRequestObject(
                        Main.app.getSession().defaultLocationId,
                        Main.app.getSession().tenantId
                    )
                )
            ).execute()
        else setCustomerAdapter(customers, binding.searchView.query.toString())
    }

    private fun setCustomerAdapter(customers: ArrayList<Customer>, query: String = "") {
        customerAdapter = DeliveryItemAdapter(
            customers.filter { isCandidateForFilter(query, it) } as ArrayList<Customer>?,
            DeliveryItemAdapter.CustomerItemType.All
        )
        customerAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                dialog?.dismiss()
                if (::onItemClicked.isInitialized) {
                    onItemClicked.onItemClick(customer)
                }
            }
        })
        customerAdapter.setProductInfoClick(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                openCustomerProducts(customer)
            }
        })
        binding.customers.adapter = customerAdapter
        binding.customers.visibility = View.VISIBLE
    }

    private fun openCustomerProducts(customer: Customer) {
        val filterSheet = CustomerProductsBottomSheet()
        filterSheet.customer = customer
        requireActivity().supportFragmentManager.let {
            filterSheet.show(
                it, HistoryFilterSheet.TAG
            )
        }
    }

    private fun isCandidateForFilter(query: String, customer: Customer): Boolean {
        if (query.isEmpty())
            return true
        var contains = true
        query.split(" ").toSet().forEach {
            contains =
                contains && (customer.name?.lowercase()?.contains(it.lowercase()) == true
                        || customer.customerCode?.lowercase()?.contains(it.lowercase()) == true
                        || customer.group?.lowercase()?.contains(it.lowercase()) == true
                        || customer.customerWorkArea?.lowercase()?.contains(it.lowercase()) == true
                        || customer.area?.lowercase()?.contains(it.lowercase()) == true
                        || customer.address1?.lowercase()?.contains(it.lowercase()) == true
                        || customer.address2?.lowercase()?.contains(it.lowercase()) == true
                        || customer.address3?.lowercase()?.contains(it.lowercase()) == true)
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

    fun setOnItemClicked(onItemClickListener: DeliveryItemAdapter.OnItemClickListener) {
        this.onItemClicked = onItemClickListener
    }

    companion object {
        const val TAG = "Customer Option Sheet"
    }
}
