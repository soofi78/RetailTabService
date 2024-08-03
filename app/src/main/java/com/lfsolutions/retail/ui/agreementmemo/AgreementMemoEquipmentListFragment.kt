package com.lfsolutions.retail.ui.agreementmemo

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
import com.lfsolutions.retail.databinding.FragmentAgreementMemoEquipmentListBinding
import com.lfsolutions.retail.model.CategoryItem
import com.lfsolutions.retail.model.CategoryResult
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.EquipmentListResult
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

class AgreementMemoEquipmentListFragment : Fragment() {

    private var equipmentlist: List<Product> = arrayListOf()
    private lateinit var customer: Customer
    private lateinit var _binding: FragmentAgreementMemoEquipmentListBinding
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: EquipmentAdapter
    private lateinit var categoryAdapter: ProductCategoryAdapter
    private var categories = ArrayList<CategoryItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentAgreementMemoEquipmentListBinding.inflate(inflater, container, false)
            customer =
                Gson().fromJson(arguments?.getString(Constants.Customer), Customer::class.java)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        if (categories.isEmpty()) {
            getProductCategory()
            return
        }
        if (equipmentlist.isEmpty()) {
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
                if (categoryItem.name.equals("All").not()) {
                    updateEquipmentListView(equipmentlist.filter { it.categoryName == categoryItem.name })
                } else {
                    updateEquipmentListView(equipmentlist.toList())
                }
            }
        })
        mBinding.categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setData() {
        mBinding.header.setBackText("Equipment List")
        Main.app.getSession().name?.let { mBinding.header.setName(it) }
        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun getEquipmentList() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading products"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {

                    (response?.body() as RetailResponse<EquipmentListResult>).result?.items?.toList()
                        ?.let { equipmentlist = it }
                    equipmentlist.toList()?.let { updateEquipmentListView(it) }
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
        mAdapter = EquipmentAdapter(products)
        mAdapter.setListener(object : EquipmentAdapter.OnEquipmentClickListener {
            override fun onEquipmentClick(product: Product) {
                findNavController().navigate(
                    R.id.action_navigation_agreement_memo_bottom_navigation_to_navigation_add_equipment,
                    bundleOf(Constants.Product to Gson().toJson(product))
                )
            }
        })
        mBinding.recyclerView.adapter = mAdapter
    }

}