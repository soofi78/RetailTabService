package com.lfsolutions.retail.ui.saleorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderInvoiceEquipmentListBinding
import com.lfsolutions.retail.model.CategoryItem
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.ui.adapter.ProductCategoryAdapter
import com.lfsolutions.retail.util.Constants

class SaleOrderEquipmentListFragment : Fragment() {

    private lateinit var customer: Customer
    private var _binding: FragmentSaleOrderInvoiceEquipmentListBinding? = null
    private lateinit var categoryAdapter: ProductCategoryAdapter
    private var equipmentlist: List<Product> = arrayListOf()
    private var categories: ArrayList<CategoryItem> = arrayListOf()
    private val mBinding get() = _binding!!

    private lateinit var mAdapter: SaleOrderEquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSaleOrderInvoiceEquipmentListBinding.inflate(layoutInflater)
        customer =
            Gson().fromJson(arguments?.getString(Constants.Customer), Customer::class.java)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = SaleOrderEquipmentAdapter(equipmentlist)
        mAdapter.setListener(object : SaleOrderEquipmentAdapter.OnEquipmentClickListener {
            override fun onEquipmentClick(product: Product) {
                mBinding.root.findNavController()
                    .navigate(
                        R.id.action_navigation_tax_invoice_equipment_list_to_navigation_tax_invoice_add_product_to_cart,
                        bundleOf(Constants.Product to Gson().toJson(product))
                    )
            }
        })

        mBinding.recyclerView.adapter = mAdapter
        addOnClickListener()
    }

    private fun addOnClickListener() {
        mBinding.btnCart.setOnClickListener {
//            it.findNavController()
//                .navigate(R.id.action_navigation_product_list_to_navigation_tax_invoice)
        }
    }
}