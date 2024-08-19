package com.lfsolutions.retail.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentCustomerDetailsBinding
import com.lfsolutions.retail.databinding.FragmentInvoiceDetailsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.response.SaleInvoiceResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class CustomerDetailsFragment : Fragment() {

    private lateinit var customer: Customer
    private lateinit var binding: FragmentCustomerDetailsBinding
    private val args by navArgs<CustomerDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentCustomerDetailsBinding.inflate(inflater)
            customer = Gson().fromJson(args.customer, Customer::class.java)
        }
        return binding.root
    }


    private fun setData() {

    }
}