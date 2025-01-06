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
import com.lfsolutions.retail.databinding.CustomerProudctSheetBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.LocationIdCustomerIdRequestObject
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.ProductListAdapter
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class CustomerProductsBottomSheet : BottomSheetDialogFragment() {

    private var products = ArrayList<Product>()
    private lateinit var binding: CustomerProudctSheetBinding
    lateinit var customer: Customer
    private lateinit var productAdapter: ProductListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CustomerProudctSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })
        getCustomerDetails()
    }

    private fun getCustomerDetails() {
        if (products.isEmpty()) NetworkCall.make()
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = (response?.body() as RetailResponse<EquipmentListResult>)
                    result.result?.items?.let { products.addAll(it) }
                    if (products.isEmpty()) {
                        Notify.toastLong("No Products Available")
                        dismiss()
                    } else {
                        setProductAdapter(binding.searchView.query.toString())
                    }
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get customer data")
                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(
                Network.api()?.getCustomerProduct(
                    LocationIdCustomerIdRequestObject(
                        Main.app.getSession().defaultLocationId,
                        customer.id
                    )
                )
            ).execute()
        else setProductAdapter(binding.searchView.query.toString())
    }

    private fun setProductAdapter(query: String = "") {
        productAdapter = ProductListAdapter(getFilteredProducts(query))
        productAdapter.quantityOnly=true
        binding.products.adapter = productAdapter
    }

    private fun getFilteredProducts(query: String): List<Product> {
        return products.filter {
            isFilterCandidate(
                it,
                query.split(" ").toSet()
            )
        } as ArrayList<Product>
    }

    private fun isFilterCandidate(
        product: Product,
        query: Set<String>
    ): Boolean {
        if (query.isEmpty())
            return true
        var contains = true
        query.forEach {
            contains =
                contains && (product.productName?.lowercase()?.contains(it.lowercase()) == true
                        || product.categoryName?.lowercase()?.contains(it) == true
                        || product.inventoryCode?.lowercase()?.contains(it) == true
                        || product.unitName?.lowercase()?.contains(it) == true)
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

    companion object {
        const val TAG = "Customer Products"
    }
}
