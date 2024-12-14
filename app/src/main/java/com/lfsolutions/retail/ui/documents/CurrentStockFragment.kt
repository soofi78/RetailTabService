package com.lfsolutions.retail.ui.documents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentCurrentStockBinding
import com.lfsolutions.retail.databinding.FragmentHistoryListingBinding
import com.lfsolutions.retail.model.CategoryItem
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerResponse
import com.lfsolutions.retail.model.HistoryRequest
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.SaleOrderToStockReceive
import com.lfsolutions.retail.model.memo.AgreementMemo
import com.lfsolutions.retail.model.memo.AgreementMemoHistoryResult
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.model.outgoingstock.StockTransfer
import com.lfsolutions.retail.model.outgoingstock.StockTransferHistoryResult
import com.lfsolutions.retail.model.sale.SaleReceipt
import com.lfsolutions.retail.model.sale.SaleReceiptResult
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListResult
import com.lfsolutions.retail.model.sale.order.SaleOrderListItem
import com.lfsolutions.retail.model.sale.order.SaleOrderListResult
import com.lfsolutions.retail.model.service.ComplaintService
import com.lfsolutions.retail.model.service.ServiceFormBody
import com.lfsolutions.retail.model.service.ComplaintServiceHistoryResult
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.customer.CustomerDetailActivity
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.documents.history.HistoryListAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryTypeAdapter
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.ui.widgets.options.OnOptionItemClick
import com.lfsolutions.retail.ui.widgets.options.OptionItem
import com.lfsolutions.retail.ui.widgets.options.OptionsBottomSheet
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.DateTime
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