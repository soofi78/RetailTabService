package com.lfsolutions.retail.ui.customer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.CustomerOptionsSheetBinding
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
import java.util.ArrayList

class CustomerOptionView : BottomSheetDialogFragment() {

    private lateinit var onItemClicked: DeliveryItemAdapter.OnItemClickListener
    private var selectedCustomer: Customer? = null
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
        getCustomerDetails()
    }

    private fun getCustomerDetails() {
        if (::customers.isInitialized.not()) NetworkCall.make()
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    customers =
                        (response?.body() as RetailResponse<CustomerPaymentsResult>).result?.items!!
                    setCustomerAdapter()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get customer data")
                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.getCustomersForPayment()).execute()
        else setCustomerAdapter()
    }

    private fun setCustomerAdapter() {
        customerAdapter = DeliveryItemAdapter(customers, DeliveryItemAdapter.CustomerItemType.All)
        customerAdapter.setListener(object : DeliveryItemAdapter.OnItemClickListener {
            override fun onItemClick(customer: Customer) {
                dialog?.dismiss()
                if (::onItemClicked.isInitialized) {
                    onItemClicked.onItemClick(customer)
                }
            }
        })
        binding.customers.adapter = customerAdapter
        binding.customers.visibility = View.VISIBLE
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
