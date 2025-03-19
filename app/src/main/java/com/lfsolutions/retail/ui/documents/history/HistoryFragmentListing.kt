package com.lfsolutions.retail.ui.documents.history

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
import com.lfsolutions.retail.databinding.FragmentHistoryListingBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerResponse
import com.lfsolutions.retail.model.HistoryRequest
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.SaleOrderToStockReceive
import com.lfsolutions.retail.model.deliveryorder.DeliveryOrderHistoryItem
import com.lfsolutions.retail.model.deliveryorder.DeliveryOrderHistoryList
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
import com.lfsolutions.retail.model.service.ComplaintServiceHistoryResult
import com.lfsolutions.retail.model.service.ServiceFormBody
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.customer.CustomerDetailsBottomSheet
import com.lfsolutions.retail.ui.forms.FormsActivity
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.setDebouncedClickListener
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
    private val deliveryOrder = ArrayList<HistoryItemInterface>()
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
        binding.filterView.setDebouncedClickListener {
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
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
    }

    private fun setCustomerData() {
        customer?.let { binding.customerView.setCustomer(it) }
        binding.customerView.setDebouncedClickListener {
            CustomerDetailsBottomSheet.show(requireActivity().supportFragmentManager, customer)
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

        binding.fabStockRecord.setDebouncedClickListener {
            stockReceived()
        }

        binding.date.setDebouncedClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    stockReceiveDate = year + "-" + month + "-" + day + "T00:00:00Z"
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
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
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
        if (historyTypeAdapter == null) historyTypeAdapter = HistoryTypeAdapter(
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
            HistoryType.Invoices,
        )

        if (Main.app.getSession().hideSalesOrderTab.not()) {
            historyList.add(0, HistoryType.Order)
        }

        if (Main.app.getSession().hideReceiptTab.not()) {
            historyList.add(HistoryType.Receipts)
        }

        if (Main.app.getSession().hideDeliveryOrder.not()) {
            historyList.add(HistoryType.DeliveryOrder)
        }

        if (Main.app.getSession().isSuperVisor == true) {
            historyList.add(HistoryType.AgreementMemo)
            historyList.add(HistoryType.ServiceForm)
        }

        if (isCustomerSpecificHistory.not()) {
            historyList.add(HistoryType.OutgoingTransfer)
        }

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

            HistoryType.DeliveryOrder -> {
                getDeliveryOrderHistory(force)
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
        }
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
                    customerId = customer?.id.toString()
                    filter = customer?.name
                    status = null
                })
            ).execute()
        else setAdapter(invoices)
    }

    private fun getDeliveryOrderHistory(force: Boolean = false) {
        if (deliveryOrder.isEmpty() || force) {
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale orders"))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        val res = response?.body() as BaseResponse<DeliveryOrderHistoryList>
                        deliveryOrder.clear()
                        res.result?.items?.forEach {
                            deliveryOrder.add(it)
                        }
                        setAdapter(deliveryOrder)
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Unable to get sale receipt list")
                    }
                }).enque(
                    Network.api()?.getDeliveryOrder(HistoryRequest().apply {
                        locationId = Main.app.getSession().defaultLocationId
                        userId = Main.app.getSession().userId
                        startDate = this@HistoryFragmentListing.startDate
                        endDate = this@HistoryFragmentListing.endDate
                        filter = customer?.name
                        customerId = customer?.id.toString()
                        status = "X"
                    })
                ).execute()
        } else {
            setAdapter(deliveryOrder)
        }
    }

    private fun getOrderHistory(force: Boolean = false) {
        if (order.isEmpty() || force) callSaleOrderApi()
        else setAdapter(order, false)
    }

    private fun callSaleOrderApi() {
//        if (Main.app.getSession().isSupervisor == true) {
//            NetworkCall.make()
//                .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale orders"))
//                .setCallback(object : OnNetworkResponse {
//                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
//                        val res = response?.body() as BaseResponse<SaleOrderListResult>
//                        order.clear()
//                        res.result?.items?.forEach {
//                            order.add(it)
//                        }
//                        setAdapter(order)
//                    }
//
//                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
//                        Notify.toastLong("Unable to get sale receipt list")
//                    }
//                }).enque(
//                    Network.api()?.getSalesOrder(HistoryRequest().apply {
//                        locationId = Main.app.getSession().defaultLocationId
//                        userId = Main.app.getSession().userId
//                        startDate = this@HistoryFragmentListing.startDate
//                        endDate = this@HistoryFragmentListing.endDate
//                        filter = customer?.name
//                        status = "X"
//                    })
//                ).execute()
//        } else {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading sale orders"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<SaleOrderListResult>
                    order.clear()
                    res.result?.items?.forEach {
                        order.add(it)
                    }
                    setAdapter(order, false)
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get sale receipt list")
                }
            }).enque(Network.api()?.getSaleOrderBySalePerson(HistoryRequest().apply {
                locationId = Main.app.getSession().defaultLocationId
                userId = Main.app.getSession().userId
                startDate = this@HistoryFragmentListing.startDate
                endDate = this@HistoryFragmentListing.endDate
                filter = customer?.name
                customerId = customer?.id.toString()
                status = "X"
            })).execute()
//        }
    }

    private fun setAdapter(
        items: ArrayList<HistoryItemInterface>,
        clone: Boolean = false
    ) {
        binding.items.adapter =
            HistoryListAdapter(items, object : HistoryListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(item: HistoryItemInterface) {
                    openDetailsFragment(item)
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
            }, clone)


        if (selectedType == HistoryType.Order) {
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
                            it, complaintService
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
            findNavController().navigate(
                R.id.action_navigation_history_listing_to_new_agreement_memo,
                Bundle().apply { putString(Constants.Customer, Gson().toJson(customer)) })
        } else if (item is ComplaintService) {
            findNavController().navigate(
                R.id.action_navigation_history_listing_to_serviceFormFragment,
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
                    Constants.OrderId to historyitem.id.toString()
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
                R.id.action_navigation_receipt_history_fragment_to_receipt_details, bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else if (historyitem is AgreementMemo) {
            findNavController().navigate(
                R.id.action_navigation_history_listing_to_agreement_memo_details, bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else if (historyitem is ComplaintService) {
            findNavController().navigate(
                R.id.action_navigation_history_listing_to_service_details, bundleOf(
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
        } else if (historyitem is DeliveryOrderHistoryItem) {
            findNavController().navigate(
                R.id.action_navigation_history_listing_delivery_order_to_details, bundleOf(
                    Constants.Item to Gson().toJson(historyitem)
                )
            )
        } else {
            Notify.toastLong("Invalid item received")
        }
    }
}