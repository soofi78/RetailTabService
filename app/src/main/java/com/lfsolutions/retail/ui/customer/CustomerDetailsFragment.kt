package com.lfsolutions.retail.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lfsolutions.retail.databinding.FragmentCustomerDetailsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.DateTime

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
        binding.outletName.text = customer?.name
        binding.officialOpen.text = getFormattedDate(customer?.officialOpen)
        binding.picName.text = customer?.picName
        binding.contact.text = customer?.phoneNo
        binding.operatingHours.text = customer?.operatingHours
        binding.acNumber.text = customer?.customerCode
        binding.area.text = customer?.customerWorkArea
        binding.term.text = customer?.paymentTerm
        binding.address.text = customer?.address1
    }

    private fun getFormattedDate(officialOpen: String?): String {
        val date = DateTime.getDateFromString(
            officialOpen?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: officialOpen ?: ""
    }
}