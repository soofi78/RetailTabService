package com.lfsolutions.retail.ui.customer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.databinding.FragmentCustomerDetailsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.DateTime

class CustomerDetailsBottomSheet() : BottomSheetDialogFragment() {

    private var customer: Customer? = null
    private lateinit var binding: FragmentCustomerDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


    companion object {
        const val TAG = "CustomerDetails"
        private var customerDetails: CustomerDetailsBottomSheet? = null
        fun show(supportFragmentManager: FragmentManager, customer: Customer?) {
            customerDetails = CustomerDetailsBottomSheet()
            customerDetails?.customer = customer
            supportFragmentManager.let { customerDetails?.show(it, TAG) }
        }
    }
}