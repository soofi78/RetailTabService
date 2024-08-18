package com.lfsolutions.retail.ui.stocktransfer.outgoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentSaleOrderInvoiceStockEquipmentListBinding
import com.lfsolutions.retail.model.CategoryItem
import com.lfsolutions.retail.model.CategoryResult
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
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
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Category List"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
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
                Network.api()?.getProductCategories()
            ).execute()
    }

    private fun updateCategoryAdapter() {
        categoryAdapter = ProductCategoryAdapter(categories, object : OnCategoryItemClicked {
            override fun onCategoryItemClicked(categoryItem: CategoryItem) {
                updateSelectedCategoryProducts(categoryItem)
            }
        })
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun updateSelectedCategoryProducts(categoryItem: CategoryItem) {
        if (categoryItem.name.equals("All").not()) {
            updateEquipmentListView(productList.filter { it.categoryName == categoryItem.name })
        } else {
            updateEquipmentListView(productList.toList())
        }
    }

    private fun setData() {
        binding.header.setBackText("Product List")
        Main.app.getSession().name?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun getEquipmentList() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading products"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<EquipmentListResult>).result?.items?.toList()
                        ?.let { productList = it }
                    productList.toList()?.let { updateEquipmentListView(it) }
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get equipment list")
                }
            }).enque(
                Network.api()
                    ?.getEquipmentList(LocationIdRequestObject(Main.app.getSession().defaultLocationId))
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