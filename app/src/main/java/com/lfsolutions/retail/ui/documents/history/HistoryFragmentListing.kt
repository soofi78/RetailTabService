package com.lfsolutions.retail.ui.documents.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentHistoryListingBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.HistoryRequest
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListResult
import com.lfsolutions.retail.model.sale.order.SaleOrderListItem
import com.lfsolutions.retail.model.sale.order.SaleOrderListResult
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.DateTime
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class HistoryFragmentListing : Fragment() {


    private var selectedType: HistoryType? = null
    private var customer: Customer? = null
    private var endDate: String? = null
    private var startDate: String? = null
    private lateinit var binding: FragmentHistoryListingBinding
    private val order = ArrayList<SaleOrderInvoiceItem>()
    private val invoices = ArrayList<SaleOrderInvoiceItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentHistoryListingBinding.inflate(inflater)
            binding.header.setBackText("History")
            binding.header.setOnBackClick {
                requireActivity().finish()
            }
            binding.types.adapter =
                HistoryTypeAdapter(arrayListOf(
                    HistoryType.Order,
                    HistoryType.Invoices,
                    HistoryType.Returns
                ),
                    object : OnHistoryTypeClicked {
                        override fun onHistoryTypeClicked(type: HistoryType) {
                            getHistory(type)
                        }
                    })
            getHistory(HistoryType.Order)
        }
        binding.filterView.setOnClickListener {
            val filterSheet = HistoryFilterSheet()
            filterSheet.setFilteredData(customer,startDate,endDate)
            filterSheet.setOnProductDetailsChangedListener(object :
                HistoryFilterSheet.OnHistoryFilter {
                override fun onFilter(startDate: String?, endDate: String?, customer: Customer?) {
                    this@HistoryFragmentListing.startDate = startDate
                    this@HistoryFragmentListing.endDate = endDate
                    this@HistoryFragmentListing.customer = customer
                    setDateFilterData()
                    setCustomerFilterData()
                    selectedType?.let { type -> getHistory(type, true) }
                }
            })
            requireActivity().supportFragmentManager.let {
                filterSheet.show(
                    it,
                    HistoryFilterSheet.TAG
                )
            }

        }
        return binding.root
    }

    private fun setCustomerFilterData() {
        if (customer == null) {
            binding.customersFilter.text = "All Customers"
        } else {
            binding.customersFilter.text = customer?.name
        }
    }

    fun setDateFilterData() {
        if (startDate === null || endDate == null) {
            binding.dateFilter.text = "All Dates"
        } else {
            val start = DateTime.getDateFromString(
                startDate?.replace("T", " ")?.replace("Z", ""), DateTime.DateTimetRetailFormat
            )
            val end = DateTime.getDateFromString(
                endDate?.replace("T", " ")?.replace("Z", ""), DateTime.DateTimetRetailFormat
            )
            binding.dateFilter.text =
                buildString {
                    append(DateTime.format(start, DateTime.DateFormatRetail))
                    append(" - ")
                    append(DateTime.format(end, DateTime.DateFormatRetail))
                }
        }
    }

    private fun getHistory(type: HistoryType, force: Boolean = false) {
        selectedType = type
        when (type) {
            HistoryType.Order -> {
                getOrderHistory(force)
            }

            HistoryType.Invoices -> {
                getInvoicesHistory(force)
            }

            HistoryType.Returns -> {
                getReturnsHistory(force)
            }
        }
    }

    private fun getReturnsHistory(force: Boolean = false) {
        setAdapter(arrayListOf())
    }

    private fun getInvoicesHistory(force: Boolean = false) {
        if (invoices.isEmpty() || force)
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale invoice"))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        val res = response?.body() as BaseResponse<SaleInvoiceListResult>
                        invoices.clear()
                        res.result?.items?.forEach {
                            invoices.add(it)
                        }
                        setAdapter(invoices)
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Unable to get sale invoice list")
                    }
                }).enque(
                    Network.api()?.getSaleInvoices(HistoryRequest().apply {
                        locationId = Main.app.getSession().defaultLocationId
                        userId = Main.app.getSession().userId
                        startDate = this@HistoryFragmentListing.startDate
                        endDate = this@HistoryFragmentListing.endDate
                        filter = customer?.name
                    })
                ).execute()
        else setAdapter(invoices)
    }

    private fun getOrderHistory(force: Boolean = false) {
        if (order.isEmpty() || force)
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale orders"))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        val res = response?.body() as BaseResponse<SaleOrderListResult>
                        order.clear()
                        res.result?.items?.forEach {
                            order.add(it)
                        }
                        setAdapter(order)
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Unable to get sale order list")
                    }
                }).enque(
                    Network.api()?.getSalesOrder(HistoryRequest().apply {
                        locationId = Main.app.getSession().defaultLocationId
                        userId = Main.app.getSession().userId
                        startDate = this@HistoryFragmentListing.startDate
                        endDate = this@HistoryFragmentListing.endDate
                        filter = customer?.name
                    })
                ).execute()
        else setAdapter(order)
    }

    private fun setAdapter(items: ArrayList<SaleOrderInvoiceItem>) {
        binding.items.adapter = SaleOrderInvoiceListAdapter(items,
            object : SaleOrderInvoiceListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: SaleOrderInvoiceItem) {
                    openDetailsFragment(saleOrderInvoiceItem)
                }
            })
    }

    private fun openDetailsFragment(saleOrderInvoiceItem: SaleOrderInvoiceItem) {
        if (saleOrderInvoiceItem is SaleOrderListItem) {
            findNavController().navigate(
                R.id.action_navigation_sale_order_invoice_history_fragment_to_order_details,
                bundleOf(
                    Constants.Item to Gson().toJson(saleOrderInvoiceItem)
                )
            )
        } else if (saleOrderInvoiceItem is SaleInvoiceListItem) {
            findNavController().navigate(
                R.id.action_navigation_sale_order_invoice_history_fragment_to_invoice_details,
                bundleOf(
                    Constants.Item to Gson().toJson(saleOrderInvoiceItem)
                )
            )
        } else {
            Notify.toastLong("Invalid item received")
        }
    }
}