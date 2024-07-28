package com.lfsolutions.retail.ui.saleorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderTaxInvoiceBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.makeTextBold

class SaleOrderFragment : Fragment() {

    private var _binding: FragmentSaleOrderTaxInvoiceBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: SaleOrderCartProductAdapter
    private val args by navArgs<SaleOrderFragmentArgs>()
    private lateinit var customer: Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customer = Gson().fromJson(args.customer, Customer::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaleOrderTaxInvoiceBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = SaleOrderCartProductAdapter()
        addOnClickListener()
        setHeaderData()
        setCustomerData()
    }

    private fun setHeaderData() {
        mBinding.header.setBackText("Sale Order")
        Main.app.getSession().name?.let { mBinding.header.setName(it) }
        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun setCustomerData() {
        mBinding.txtGroup.text =
            makeTextBold(
                text = getString(R.string.prefix_group, customer.group),
                startIndex = 8
            )
        mBinding.txtCustomerName.text = customer.name
        mBinding.txtAddress.text = customer.address1
        mBinding.txtAccountNo.text =
            makeTextBold(
                text = getString(R.string.prefix_account_no, customer.customerCode),
                startIndex = 8
            )

        mBinding.txtArea.text =
            makeTextBold(text = getString(R.string.prefix_area, customer.area), startIndex = 7)

    }

    private fun addOnClickListener() {


        mBinding.btnOpenEquipmentList.setOnClickListener {
            findNavController().navigate(R.id.navigation_tax_invoice)
        }
        mBinding.btnClearSign.setOnClickListener {
            mBinding.signaturePad.clear()
        }

        mBinding.btnSave.setOnClickListener {
            it.findNavController().popBackStack(R.id.navigation_current_forms, false)
        }

    }

}