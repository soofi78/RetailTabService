package com.lfsolutions.retail.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lfsolutions.retail.databinding.FragmentCustomerDetailsBinding
import com.lfsolutions.retail.model.Customer

class CustomerDetailsFragment : Fragment() {

    private var customer: Customer? = null
    private lateinit var binding: FragmentCustomerDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setCustomer()
        binding = FragmentCustomerDetailsBinding.inflate(inflater)
        setHeader()
        setData()
        return binding.root
    }

    private fun setHeader() {
        binding.header.setBackText("Customer Details")
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
    }


    private fun setCustomer() {
        customer = (requireActivity() as CustomerDetailActivity).customer
    }


    private fun setData() {
        binding.customerName.text = customer?.name
        binding.customerCode.text = customer?.customerCode
        binding.area.text = customer?.area
        binding.group.text = customer?.group
        binding.address.text = customer?.address1
    }
}