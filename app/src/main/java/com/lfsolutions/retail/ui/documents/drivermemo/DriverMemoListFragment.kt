package com.lfsolutions.retail.ui.documents.drivermemo

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
import com.lfsolutions.retail.databinding.FragmentDriverMemoListBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.GetAllDriverMemoResult
import com.lfsolutions.retail.model.HistoryRequest
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.memo.CreateUpdateDriverMemoRequestBody
import com.lfsolutions.retail.model.memo.DriverMemo
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.customer.CustomerOptionView
import com.lfsolutions.retail.ui.delivery.DeliveryItemAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryFilterSheet
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.documents.history.HistoryListAdapter
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class DriverMemoListFragment : Fragment() {

    private lateinit var binding: FragmentDriverMemoListBinding
    private val driverItems = ArrayList<HistoryItemInterface>()
    private var customer: Customer? = null
    private var endDate: String? = null
    private var startDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDriverMemoListBinding.inflate(inflater, container, false)
        }
        return binding.root

    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        setData()
    }


    private fun setData() {
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
        binding.filterView.setOnClickListener {
            val filterSheet = HistoryFilterSheet()
            filterSheet.setFilteredData(customer, startDate, endDate)
            filterSheet.setOnProductDetailsChangedListener(object :
                HistoryFilterSheet.OnHistoryFilter {
                override fun onFilter(startDate: String?, endDate: String?, customer: Customer?) {
                    this@DriverMemoListFragment.startDate = startDate
                    this@DriverMemoListFragment.endDate = endDate
                    this@DriverMemoListFragment.customer = customer
                    setDateFilterData()
                    setCustomerFilterData()
                    getDriverMemoList()
                }
            })
            requireActivity().supportFragmentManager.let {
                filterSheet.show(
                    it, HistoryFilterSheet.TAG
                )
            }
        }

        binding.addDriverMemo.setOnClickListener {
            val filterSheet = CustomerOptionView()
            filterSheet.setOnItemClicked(object : DeliveryItemAdapter.OnItemClickListener {
                override fun onItemClick(customer: Customer) {
                    createNewDriverMemo(customer)
                }

            })
            requireActivity().supportFragmentManager.let {
                filterSheet.show(
                    it, HistoryFilterSheet.TAG
                )
            }
        }
        getDriverMemoList(true)
    }

    private fun createNewDriverMemo(customer: Customer) {
        findNavController().navigate(
            R.id.action_navigation_driver_memo_list_to_driver_memo_edit,
            bundleOf(Constants.Customer to Gson().toJson(customer), Constants.Memo to null)
        )
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


    private fun addOnClickListener() {
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun getDriverMemoList(force: Boolean = false) {
        if (driverItems.isEmpty().not() && force.not()) {
            setDriverItems()
        }
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val items = response?.body() as BaseResponse<GetAllDriverMemoResult>
                    items.result?.items?.let {
                        driverItems.clear()
                        driverItems.addAll(it)
                    }
                    setDriverItems()
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable get driver memo list")
                }
            }).enque(Network.api()?.getAllDriverMemos(HistoryRequest().apply {
                locationId = Main.app.getSession().defaultLocationId
                userId = Main.app.getSession().userId
                startDate = this@DriverMemoListFragment.startDate
                endDate = this@DriverMemoListFragment.endDate
                customerId = (customer?.id ?: 0).toString()
            })).execute()
    }

    private fun setDriverItems() {
        binding.driverMemoList.adapter = HistoryListAdapter(
            driverItems,
            object : HistoryListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    getDriverMemoDetails(saleOrderInvoiceItem as DriverMemo)
                }
            }
        )
    }

    private fun getDriverMemoDetails(memo: DriverMemo) {
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res =
                        (response?.body() as BaseResponse<CreateUpdateDriverMemoRequestBody>).result
                    res?.driverMemo?.customerName = memo.customerName
                    res?.let { openDriverMemo(it) }

                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable get driver memo")
                }
            }).enque(Network.api()?.getDriverMemo(IdRequest(id = memo.id))).execute()
    }

    private fun openDriverMemo(res: CreateUpdateDriverMemoRequestBody) {
        findNavController().navigate(
            R.id.navigation_driver_memo_details, bundleOf(
                "driver_memo" to Gson().toJson(res)
            )
        )
    }

}