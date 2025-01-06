package com.lfsolutions.retail.ui.stocktransfer.outgoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderInvoiceStockEquipmentListBinding
import com.lfsolutions.retail.model.CategoryItem
import com.lfsolutions.retail.model.CategoryResult
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.FilterRequest
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.ProductListRB
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.adapter.OnCategoryItemClicked
import com.lfsolutions.retail.ui.adapter.ProductCategoryAdapter
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class OutGoingEquipmentListFragment : Fragment() {
    private lateinit var binding: FragmentSaleOrderInvoiceStockEquipmentListBinding
    private lateinit var categoryAdapter: ProductCategoryAdapter
    private var categories: ArrayList<CategoryItem> = arrayListOf()
    private var productList: List<Product> = arrayListOf()
    private lateinit var mAdapter: OutGoingStockEquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentSaleOrderInvoiceStockEquipmentListBinding.inflate(layoutInflater)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        binding.filter.setOnCheckedChangeListener { _, _ ->
            getProductCategory()
        }
        if (categories.isEmpty()) {
            getProductCategory()
            return
        }
        if (productList.isEmpty()) {
            updateEquipmentListView(arrayListOf())
            getEquipmentList()
            return
        }
    }

    private fun getProductCategory() {
        val filter =
            if (binding.filter.isChecked) FilterRequest.on() else FilterRequest.off()
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Category List"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    categories.clear()
                    (response?.body() as BaseResponse<CategoryResult>).result?.items?.let {
                        categories = it
                    }
                    categories.add(0, CategoryItem("All", isSelected = true))
                    updateCategoryAdapter()
                    getEquipmentList()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to category list")
                }
            }).enque(
                Network.api()?.getProductCategories(filter)
            ).execute()
    }

    private fun updateCategoryAdapter() {
        categoryAdapter = ProductCategoryAdapter(categories, object : OnCategoryItemClicked {
            override fun onCategoryItemClicked(categoryItem: CategoryItem) {
                filterProducts(categoryItem, binding.searchView.query.toString())
            }
        })
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }


    private fun filterProducts(categoryItem: CategoryItem, query: String = "") {
        var filteredList = ArrayList<Product>()
        filteredList.addAll(productList.filter { it.categoryName == categoryItem.name })

        if (categoryItem.name.equals("ALL",true))
            categories.forEach { cat->
                filteredList.addAll(productList.filter { it.categoryName == cat.name })
            }

        filteredList =
            filteredList.filter {
                isFilterCandidate(
                    it,
                    query.split(" ").toSet()
                )
            } as ArrayList<Product>



        updateEquipmentListView(filteredList)
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

    private fun setData() {
        binding.header.setBackText("Product List")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterProducts(categoryAdapter.getSelectedItem(), newText)
                }
                return true
            }
        })
    }

    private fun getEquipmentList() {
        val filter =
            if (binding.filter.isChecked) "SR" else null
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading products"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<EquipmentListResult>).result?.items?.toList()
                        ?.let { productList = it }
                    filterProducts(categoryAdapter.getSelectedItem(), "")
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get equipment list")
                }
            }).enque(
                Network.api()
                    ?.getEquipmentList(ProductListRB(Main.app.getSession().defaultLocationId,filter))
            ).execute()
    }

    private fun updateEquipmentListView(products: List<Product>) {
        mAdapter = OutGoingStockEquipmentAdapter(products)
        mAdapter.setListener(object : OutGoingStockEquipmentAdapter.OnEquipmentClickListener {
            override fun onEquipmentClick(product: Product) {
                findNavController()
                    .navigate(
                        R.id.action_navigation_out_going_stock_product_listing_to_add_product,
                        bundleOf(Constants.Product to Gson().toJson(product))
                    )
            }
        })
        binding.recyclerView.adapter = mAdapter
    }

}