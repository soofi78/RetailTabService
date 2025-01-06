package com.lfsolutions.retail.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentCurrentStockBinding
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.documents.history.HistoryListAdapter
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class CurrentStockFragment : Fragment() {
    private val currentStock = ArrayList<HistoryItemInterface>()
    private lateinit var binding: FragmentCurrentStockBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentCurrentStockBinding.inflate(inflater)
        }
        return binding.root
    }

    private fun setupHeader() {
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick { requireActivity().finish() }
        binding.header.setBackText("Current Stock")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
    }

    private fun filterProducts() {
        val query = binding.searchView.query.toString().trim()
        var filteredList = ArrayList<HistoryItemInterface>()
        filteredList =
            currentStock.filter {
                isFilterCandidate(
                    it as Product,
                    query.split(" ").toSet()
                )
            } as ArrayList<HistoryItemInterface>

        setAdapter(filteredList)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        getCurrentProductStock()
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts()
                return true
            }
        })
    }


    private fun getCurrentProductStock() {
        if (currentStock.isEmpty()) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading current products"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<ArrayList<Product>>
                    currentStock.clear()
                    res.result?.forEach {
                        currentStock.add(it)
                    }
                    filterProducts()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get current stock")
                }
            }).enque(
                Network.api()
                    ?.getCurrentProductStockQuantity(LocationIdRequestObject(Main.app.getSession().defaultLocationId))
            ).execute()
        else setAdapter(currentStock)
    }

    private fun setAdapter(
        items: ArrayList<HistoryItemInterface>,
    ) {
        binding.items.adapter = HistoryListAdapter(
            items,
            object : HistoryListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {

                }
            })
    }
}