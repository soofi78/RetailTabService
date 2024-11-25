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
import com.lfsolutions.retail.ui.customer.CustomerDetailActivity
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

class HistoryFragmentListing : Fragment() {


    private var stockReceiveDate: String = ""
    private var selectedType: HistoryType? = null
    private var customer: Customer? = null
    private var isCustomerSpecificHistory: Boolean = false
    private var endDate: String? = null
    private var startDate: String? = null
    private lateinit var binding: FragmentHistoryListingBinding
    private val order = ArrayList<HistoryItemInterface>()
    private val orderStockReceiveId = ArrayList<Int>()
    private val invoices = ArrayList<HistoryItemInterface>()
    private val receipts = ArrayList<HistoryItemInterface>()
    private val outgoing = ArrayList<HistoryItemInterface>()
    private val agreementMemo = ArrayList<HistoryItemInterface>()
    private val complaintService = ArrayList<HistoryItemInterface>()
    private val currentStock = ArrayList<HistoryItemInterface>()
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
        Main.app.getSession().userName?.let { binding.header.setName(it) }
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
        if (selectedType != null) getHistory(selectedType!!, true)
        else getHistory(HistoryType.Order)

        if (isCustomerSpecificHistory.not()) {
            binding.customerView.visibility = View.GONE
        }

        binding.fabStockRecord.setOnClickListener {
            stockReceived()
        }

        binding.date.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    stockReceiveDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }
    }

    private fun stockReceived() {
        if (orderStockReceiveId.size == 0) {
            Notify.toastLong("Please select sale order first")
            return
        }

        if (stockReceiveDate.isNullOrEmpty()) {
            Notify.toastLong("Please select date")
            return
        }
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    Notify.toastLong("Stock Received Successfully")
                    getHistory(HistoryType.Order, true)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to receive stock")
                }
            }).enque(
                Network.api()?.SalesOrderToStockReceiveForDriver(
                    SaleOrderToStockReceive(
                        locationId = Main.app.getSession().defaultLocationId,
                        remarks = binding.remarks.text.toString(),
                        date = stockReceiveDate,
                        salesOrderIds = orderStockReceiveId
                    )
                )
            ).execute()
    }


    private fun customerSpecificHistoryViewChanges() {
        binding.filterView.visibility = if (isCustomerSpecificHistory) View.GONE else View.VISIBLE
    }

    private fun setHistoryTabAdapter() {
        if (historyTypeAdapter == null)
            historyTypeAdapter =
                HistoryTypeAdapter(
                    getHistoryTypeList(selectedType),
                    object : OnHistoryTypeClicked {
                        override fun onHistoryTypeClicked(type: HistoryType) {
                            getHistory(type)
                        }
                    },
                )
        binding.types.adapter = historyTypeAdapter
    }

    private fun getHistoryTypeList(selectedType: HistoryType?): java.util.ArrayList<HistoryType> {
        val historyList = arrayListOf(
            HistoryType.Order,
            HistoryType.Invoices,
            HistoryType.Receipts
        )


        if (Main.app.getSession().isSupervisor == true) {
            historyList.add(HistoryType.AgreementMemo)
            historyList.add(HistoryType.ServiceForm)
        }

        if (isCustomerSpecificHistory.not()) {
            historyList.add(HistoryType.OutgoingTransfer)
        }

        historyList.add(HistoryType.CurrentStock)

        selectedType?.let { selected ->
            historyList.forEach {
                it.selected = it.type == selected.type
            }
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

            HistoryType.ServiceForm -> {
                getComplaintServiceHistory(force)
            }

            HistoryType.CurrentStock -> {
                getCurrentProductStock(force)
            }
        }
    }

    private fun getCurrentProductStock(force: Boolean) {
        if (currentStock.isEmpty() || force) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading current products"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<ArrayList<Product>>
                    currentStock.clear()
                    res.result?.forEach {
                        currentStock.add(it)
                    }
                    setAdapter(currentStock)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get current stock")
                }
            }).enque(
                Network.api()
                    ?.getCurrentProductStockQuantity(LocationIdRequestObject(Main.app.getSession().defaultLocationId))
            ).execute()
        else setAdapter(currentStock, true)
    }

    private fun getComplaintServiceHistory(force: Boolean) {
        if (complaintService.isEmpty() || force) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Service Form"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<ComplaintServiceHistoryResult>
                    complaintService.clear()
                    res.result?.items?.forEach {
                        complaintService.add(it)
                    }
                    setAdapter(complaintService, true)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get Service Form list")
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
        else setAdapter(complaintService, true)
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
                    setAdapter(agreementMemo, true)
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
        else setAdapter(agreementMemo, true)
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
                    setAdapter(invoices, false)
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
            callSaleOrderApi()
        else setAdapter(order, false, Main.app.getSession().isSupervisor?.not() == true)
    }

    private fun callSaleOrderApi() {
        if (Main.app.getSession().isSupervisor == true) {
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
        } else {
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale orders"))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        val res = response?.body() as BaseResponse<SaleOrderListResult>
                        order.clear()
                        res.result?.items?.forEach {
                            order.add(it)
                        }
                        setAdapter(order, false, Main.app.getSession().isSupervisor?.not() == true)
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Unable to get sale receipt list")
                    }
                }).enque(
                    Network.api()?.getSaleOrderBySalePerson(
                        HistoryRequest().apply {
                            locationId = Main.app.getSession().defaultLocationId
                            userId = Main.app.getSession().userId
                            startDate = this@HistoryFragmentListing.startDate
                            endDate = this@HistoryFragmentListing.endDate
                            filter = customer?.name
                            status = "X"
                        }
                    )
                ).execute()
        }
    }

    private fun setAdapter(
        items: ArrayList<HistoryItemInterface>,
        clone: Boolean = false,
        checkable: Boolean = false
    ) {
        binding.items.adapter = HistoryListAdapter(
            items,
            object : HistoryListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    openDetailsFragment(saleOrderInvoiceItem)
                }

                override fun onCloneClicked(item: HistoryItemInterface) {
                    if (item is AgreementMemo) {
                        cloneAgreementMemo(item)
                    } else if (item is ComplaintService) {
                        cloneComplaintService(item)
                    }
                }
            }, onChecked = { buttonView, isChecked ->
                if (isChecked) {
                    orderStockReceiveId.add((buttonView.tag as HistoryItemInterface).getId())
                } else {
                    orderStockReceiveId.remove((buttonView.tag as HistoryItemInterface).getId())
                }
            }, clone, checkable

        )


        if (checkable && selectedType == HistoryType.Order) {
            binding.remarks.visibility = View.VISIBLE
            binding.fabStockRecord.visibility = View.VISIBLE
            binding.date.visibility = View.VISIBLE
        } else {
            binding.remarks.visibility = View.GONE
            binding.fabStockRecord.visibility = View.GONE
            binding.date.visibility = View.GONE
        }
    }

    private fun cloneComplaintService(complaintService: ComplaintService) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading service details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val service = (response?.body() as BaseResponse<ServiceFormBody>).result
                    Main.app.setComplaintService(service)
                    Main.app.getComplaintService()?.complaintService?.customerId =
                        service?.complaintService?.customerId
                    Main.app.getComplaintService()?.complaintService?.customerName =
                        complaintService.customerName
                    service?.complaintService?.customerId?.let {
                        getCustomerCompleteDetails(
                            it,
                            complaintService
                        )
                    }
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get service detail")
                }
            }).enque(
                Network.api()?.getComplaintServiceDetails(IdRequest(id = complaintService.id))
            ).execute()
    }

    private fun getCustomerCompleteDetails(id: Int, item: HistoryItemInterface) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading agreement memo"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val customer =
                        (response?.body() as BaseResponse<CustomerResponse>).result?.customer
                    navigateCloneObjectTo(item, customer)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Main.app.clearComplaintService()
                    Main.app.clearAgreementMemo()
                }
            }).enque(
                Network.api()?.getCustomer(IdRequest(id = id))
            ).execute()
    }

    private fun navigateCloneObjectTo(item: HistoryItemInterface, customer: Customer?) {
        if (item is AgreementMemo) {
            findNavController().navigate(R.id.action_navigation_history_listing_to_new_agreement_memo,
                Bundle().apply { putString(Constants.Customer, Gson().toJson(customer)) })
        } else if (item is ComplaintService) {
            findNavController().navigate(R.id.action_navigation_history_listing_to_serviceFormFragment,
                Bundle().apply { putString(Constants.Customer, Gson().toJson(customer)) })
        }

    }

    private fun cloneAgreementMemo(item: AgreementMemo) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading agreement memo"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val memo =
                        (response?.body() as BaseResponse<CreateUpdateAgreementMemoRequestBody>).result
                    Main.app.setAgreementMemo(memo)
                    memo?.AgreementMemo?.CustomerId?.let { getCustomerCompleteDetails(it, item) }
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get order detail")
                }
            }).enque(
                Network.api()?.getAgreementMemoDetails(IdRequest(id = item.Id))
            ).execute()
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
            findNavController().navigate(
                R.id.action_navigation_receipt_history_fragment_to_receipt_details,
                bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else if (historyitem is AgreementMemo) {
            findNavController().navigate(
                R.id.action_navigation_history_listing_to_agreement_memo_details,
                bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else if (historyitem is ComplaintService) {
            findNavController().navigate(
                R.id.action_navigation_history_listing_to_service_details,
                bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
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
}