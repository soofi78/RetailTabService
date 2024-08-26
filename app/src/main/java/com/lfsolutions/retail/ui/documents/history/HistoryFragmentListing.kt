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
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.memo.AgreementMemo
import com.lfsolutions.retail.model.memo.AgreementMemoHistoryResult
import com.lfsolutions.retail.model.outgoingstock.StockTransfer
import com.lfsolutions.retail.model.outgoingstock.StockTransferHistoryResult
import com.lfsolutions.retail.model.sale.SaleReceipt
import com.lfsolutions.retail.model.sale.SaleReceiptResult
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListResult
import com.lfsolutions.retail.model.sale.order.SaleOrderListItem
import com.lfsolutions.retail.model.sale.order.SaleOrderListResult
import com.lfsolutions.retail.model.service.ComplaintService
import com.lfsolutions.retail.model.service.ComplaintServiceHistoryResult
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.customer.CustomerDetailActivity
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.ui.widgets.options.OnOptionItemClick
import com.lfsolutions.retail.ui.widgets.options.OptionItem
import com.lfsolutions.retail.ui.widgets.options.OptionsBottomSheet
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Dialogs
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.OnOptionDialogItemClicked
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class HistoryFragmentListing : Fragment() {


    private var selectedType: HistoryType? = null
    private var customer: Customer? = null
    private var isCustomerSpecificHistory: Boolean = false
    private var endDate: String? = null
    private var startDate: String? = null
    private lateinit var binding: FragmentHistoryListingBinding
    private val order = ArrayList<HistoryItemInterface>()
    private val invoices = ArrayList<HistoryItemInterface>()
    private val receipts = ArrayList<HistoryItemInterface>()
    private val outgoing = ArrayList<HistoryItemInterface>()
    private val agreementMemo = ArrayList<HistoryItemInterface>()
    private val complaintService = ArrayList<HistoryItemInterface>()
    private var historyTypeAdapter: HistoryTypeAdapter? = null

    private fun setCustomerFromIntent() {
        if (requireActivity() is FormsActivity && (requireActivity() as FormsActivity).customer != null) {
            customer = (requireActivity() as FormsActivity).customer
            isCustomerSpecificHistory = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentHistoryListingBinding.inflate(inflater)
        }
        binding.filterView.setOnClickListener {
            val filterSheet = HistoryFilterSheet()
            filterSheet.setFilteredData(customer, startDate, endDate)
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
                    it, HistoryFilterSheet.TAG
                )
            }

        }
        return binding.root
    }

    private fun setupHeader() {
        Main.app.getSession().name?.let { binding.header.setName(it) }
        binding.header.setOnBackClick { requireActivity().finish() }
        binding.header.setBackText("Customer History")
    }

    private fun setCustomerData() {
        customer?.let { binding.customerView.setCustomer(it) }
        binding.customerView.setOnClickListener {
            OptionsBottomSheet.show(requireActivity().supportFragmentManager,
                arrayListOf(OptionItem("View Customer", R.drawable.person_black)),
                object : OnOptionItemClick {
                    override fun onOptionItemClick(optionItem: OptionItem) {
                        customer?.let { it1 ->
                            CustomerDetailActivity.start(
                                requireActivity(),
                                it1
                            )
                        }
                    }
                })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomerFromIntent()
        customerSpecificHistoryViewChanges()
        setupHeader()
        setCustomerData()
        setHistoryTabAdapter()
        getHistory(HistoryType.Order)
        if (isCustomerSpecificHistory.not()) {
            binding.customerView.visibility = View.GONE
        }
    }


    private fun customerSpecificHistoryViewChanges() {
        binding.filterView.visibility = if (isCustomerSpecificHistory) View.GONE else View.VISIBLE
    }

    private fun setHistoryTabAdapter() {
        if (historyTypeAdapter == null)
            historyTypeAdapter =
                HistoryTypeAdapter(getHistoryTypeList(), object : OnHistoryTypeClicked {
                    override fun onHistoryTypeClicked(type: HistoryType) {
                        getHistory(type)
                    }
                })
        binding.types.adapter = historyTypeAdapter
    }

    private fun getHistoryTypeList(): java.util.ArrayList<HistoryType> {
        val historyList = arrayListOf(
            HistoryType.Order,
            HistoryType.Invoices,
            HistoryType.Receipts,
            HistoryType.AgreementMemo,
            HistoryType.ComplaintService
        )

        if (isCustomerSpecificHistory.not()) {
            historyList.add(HistoryType.OutgoingTransfer)
        }
        return historyList
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
            binding.dateFilter.text = buildString {
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

            HistoryType.Receipts -> {
                getReceiptHistory(force)
            }

            HistoryType.OutgoingTransfer -> {
                getOutGoingStockList(force)
            }

            HistoryType.Returns -> {
                getReturnsHistory(force)
            }

            HistoryType.AgreementMemo -> {
                getAgreementMemoHistory(force)
            }

            HistoryType.ComplaintService -> {
                getComplaintServiceHistory(force)
            }
        }
    }

    private fun getComplaintServiceHistory(force: Boolean) {
        if (complaintService.isEmpty() || force) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading complaint service"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<ComplaintServiceHistoryResult>
                    complaintService.clear()
                    res.result?.items?.forEach {
                        complaintService.add(it)
                    }
                    setAdapter(complaintService)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get complaint service list")
                }
            }).enque(
                Network.api()?.getAllComplaintServices(HistoryRequest().apply {
                    locationId = Main.app.getSession().defaultLocationId
                    userId = Main.app.getSession().userId
                    startDate = this@HistoryFragmentListing.startDate
                    endDate = this@HistoryFragmentListing.endDate
                    customerId = (customer?.id ?: 0).toString()
                    invoiceType = null
                    status = null
                })
            ).execute()
        else setAdapter(complaintService)
    }

    private fun getAgreementMemoHistory(force: Boolean) {
        if (agreementMemo.isEmpty() || force) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading agreement memo"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<AgreementMemoHistoryResult>
                    agreementMemo.clear()
                    res.result?.items?.forEach {
                        agreementMemo.add(it)
                    }
                    setAdapter(agreementMemo)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get agreement memo list")
                }
            }).enque(
                Network.api()?.getAllAgreementMemo(HistoryRequest().apply {
                    locationId = Main.app.getSession().defaultLocationId
                    userId = Main.app.getSession().userId
                    startDate = this@HistoryFragmentListing.startDate
                    endDate = this@HistoryFragmentListing.endDate
                    customerId = (customer?.id ?: 0).toString()
                    invoiceType = null
                    status = null
                })
            ).execute()
        else setAdapter(agreementMemo)
    }

    private fun getOutGoingStockList(force: Boolean) {
        if (outgoing.isEmpty() || force) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale receipts"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<StockTransferHistoryResult>
                    outgoing.clear()
                    res.result?.items?.forEach {
                        outgoing.add(it)
                    }
                    setAdapter(outgoing)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get sale invoice list")
                }
            }).enque(
                Network.api()?.getAllStockTransfer(HistoryRequest().apply {
                    locationId = Main.app.getSession().defaultLocationId
                    userId = Main.app.getSession().userId
                    startDate = this@HistoryFragmentListing.startDate
                    endDate = this@HistoryFragmentListing.endDate
                    customerId = (customer?.id ?: 0).toString()
                    invoiceType = null
                    status = null
                })
            ).execute()
        else setAdapter(outgoing)
    }

    private fun getReceiptHistory(force: Boolean) {
        if (receipts.isEmpty() || force) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale receipts"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<SaleReceiptResult>
                    receipts.clear()
                    res.result?.items?.forEach {
                        receipts.add(it)
                    }
                    setAdapter(receipts)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get sale invoice list")
                }
            }).enque(
                Network.api()?.getSaleReceipts(HistoryRequest().apply {
                    locationId = Main.app.getSession().defaultLocationId
                    userId = Main.app.getSession().userId
                    startDate = this@HistoryFragmentListing.startDate
                    endDate = this@HistoryFragmentListing.endDate
                    customerId = (customer?.id ?: 0).toString()
                })
            ).execute()
        else setAdapter(receipts)
    }

    private fun getReturnsHistory(force: Boolean = false) {
        setAdapter(arrayListOf())
    }

    private fun getInvoicesHistory(force: Boolean = false) {
        if (invoices.isEmpty() || force) NetworkCall.make()
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
        if (order.isEmpty() || force) NetworkCall.make()
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
                    Notify.toastLong("Unable to get sale receipt list")
                }
            }).enque(
                Network.api()?.getSalesOrder(HistoryRequest().apply {
                    locationId = Main.app.getSession().defaultLocationId
                    userId = Main.app.getSession().userId
                    startDate = this@HistoryFragmentListing.startDate
                    endDate = this@HistoryFragmentListing.endDate
                    filter = customer?.name
                    status = "X"
                })
            ).execute()
        else setAdapter(order)
    }

    private fun setAdapter(items: ArrayList<HistoryItemInterface>) {
        binding.items.adapter = SaleOrderInvoiceListAdapter(items,
            object : SaleOrderInvoiceListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    openDetailsFragment(saleOrderInvoiceItem)
                }
            })
    }

    private fun openDetailsFragment(historyitem: HistoryItemInterface) {
        if (historyitem is SaleOrderListItem) {
            findNavController().navigate(
                R.id.action_navigation_sale_order_invoice_history_fragment_to_order_details,
                bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else if (historyitem is SaleInvoiceListItem) {
            findNavController().navigate(
                R.id.action_navigation_sale_order_invoice_history_fragment_to_invoice_details,
                bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else if (historyitem is SaleReceipt) {
            Dialogs.optionsDialog(context = requireActivity(),
                options = arrayOf(Constants.Print, Constants.Delete),
                onOptionDialogItemClicked = object : OnOptionDialogItemClicked {
                    override fun onClick(option: String) {
                        when (option) {
                            Constants.Delete -> deleteSaleReceipt(historyitem)
                            Constants.Print -> Notify.toastLong("Printer not connected!")
                        }
                    }
                })
        } else if (historyitem is AgreementMemo) {
            Dialogs.optionsDialog(context = requireActivity(),
                options = arrayOf(Constants.DownloadPdf, Constants.Print),
                onOptionDialogItemClicked = object : OnOptionDialogItemClicked {
                    override fun onClick(option: String) {
                        when (option) {
                            Constants.DownloadPdf -> downloadAgreementMemoPdf(historyitem)
                            Constants.Print -> Notify.toastLong("Printer not connected!")
                        }
                    }
                })
        } else if (historyitem is ComplaintService) {
            Dialogs.optionsDialog(context = requireActivity(),
                options = arrayOf(Constants.DownloadPdf, Constants.Print),
                onOptionDialogItemClicked = object : OnOptionDialogItemClicked {
                    override fun onClick(option: String) {
                        when (option) {
                            Constants.DownloadPdf -> downloadComplaintServicePdf(historyitem)
                            Constants.Print -> Notify.toastLong("Printer not connected!")
                        }
                    }
                })
        } else if (historyitem is StockTransfer) {
            findNavController().navigate(
                R.id.action_navigation_stock_transfer_history_fragment_to_transfer_details,
                bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else {
            Notify.toastLong("Invalid item received")
        }
    }

    private fun downloadComplaintServicePdf(historyitem: ComplaintService) {
        Network.api()?.getComplaintServicePDF(IdRequest(historyitem.id))?.let { getPDFLink(it) }
    }

    private fun downloadAgreementMemoPdf(historyitem: AgreementMemo) {
        Network.api()?.getAgreementMemoPDF(IdRequest(historyitem.Id))?.let { getPDFLink(it) }
    }

    private fun getPDFLink(call: Call<*>) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val downloadPath =
                        (response?.body() as BaseResponse<String>).result?.split("develop\\")
                            ?.last()
                    val name =
                        DateTime.getCurrentDateTime(DateTime.DateFormatWithDayNameMonthNameAndTime) + "-" + downloadPath?.split(
                            "Upload\\"
                        )?.last().toString()
                    DocumentDownloader.download(
                        name,
                        AppSession[Constants.baseUrl] + downloadPath,
                        requireActivity()
                    )
                    Notify.toastLong("Downloading Started")
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Download Failed")
                }
            }).enque(call).execute()
    }

    private fun deleteSaleReceipt(receipt: SaleReceipt) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Deleting Sale Receipt"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    Notify.toastLong("Sale Receipt Deleted")
                    getReceiptHistory(true)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to delete sale receipt")
                }
            }).enque(
                Network.api()?.deleteSaleReceipt(IdRequest(receipt.id))
            ).execute()
    }
}