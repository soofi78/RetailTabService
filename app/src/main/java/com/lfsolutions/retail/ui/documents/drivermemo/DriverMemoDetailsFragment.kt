package com.lfsolutions.retail.ui.documents.drivermemo

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentDriverMemoDetailsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.memo.DriverMemo
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.stocktransfer.outgoing.OutGoingStockSummaryAdapter
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class DriverMemoDetailsFragment : Fragment() {

    private lateinit var itemSwipeHelper: ItemTouchHelper
    private var customer: Customer? = null
    private var memo: DriverMemo? = null
    private lateinit var binding: FragmentDriverMemoDetailsBinding
    private lateinit var adapter: DriverMemoProductAdapter
    private val args by navArgs<DriverMemoDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            Main.app.clearDriverMemo()
            Main.app.getDriverMemo()
            Main.app.getDriverMemo().driverMemo.locationId =
                Main.app.getSession().defaultLocationId
            Main.app.getDriverMemo().driverMemo.creationTime =
                DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat).replace(" ", "T")
                    .plus("Z")
            binding = FragmentDriverMemoDetailsBinding.inflate(inflater, container, false)
            setProductAdapter()
        }
        return binding.root

    }

    private fun setProductAdapter() {
        adapter = DriverMemoProductAdapter(
            requireActivity(),
            Main.app.getDriverMemo().driverMemoDetail
        )
        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper.attachToRecyclerView(binding.products)
        binding.products.adapter = adapter
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        if (args.customer != null) {
            customer = Gson().fromJson(args.customer, Customer::class.java)
            Main.app.getDriverMemo().driverMemo.customerId = customer?.id
            Main.app.getDriverMemo().driverMemo.customerName = customer?.name
            binding.outletName.text = customer?.name
            binding.officialOpen.text = getFormattedDate(customer?.officialOpen)
            binding.picName.text = customer?.picName
            binding.contact.text = customer?.phoneNo
            binding.operatingHours.text = customer?.operatingHours
            binding.acNumber.text = customer?.customerCode
            binding.area.text = customer?.customerWorkArea
            binding.term.text = customer?.paymentTerm
            binding.address.text = customer?.address1
        }
        if (args.memo != null) {
            memo = Gson().fromJson(args.memo, DriverMemo::class.java)
        }
        addOnClickListener()
        setData()
    }

    private fun getFormattedDate(officialOpen: String?): String {
        val date = DateTime.getDateFromString(
            officialOpen?.replace("T", " ")?.replace("Z", ""),
            DateTime.DateTimetRetailFormat
        )
        val formatted = DateTime.format(date, DateTime.DateFormatWithDayNameMonthNameAndYear)
        return formatted ?: officialOpen ?: ""
    }


    private fun setData() {
        getDriverMemoDetails()
        val today = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.date.text = today
        Main.app.getDriverMemo().driverMemo.agreementDate = today + "T00:00:00Z"
        binding.deliveryDate.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    binding.deliveryDate.text = "$year-$month-$day"
                    Main.app.getDriverMemo().driverMemo.deliveryDate =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }

        binding.addProduct.setOnClickListener {
            findNavController().navigate(R.id.navigation_driver_product_list)
        }
    }

    private fun getSwipeToDeleteListener(): ItemTouchHelper.SimpleCallback {
        return object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                adapter.remove(position)
                updateSummaryAmountAndQty()
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                super.getMovementFlags(recyclerView, viewHolder)
                if (viewHolder is DriverMemoProductAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as DriverMemoProductAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as DriverMemoProductAdapter.ViewHolder).getSwipableView())
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                getDefaultUIUtil().onDraw(
                    c,
                    recyclerView,
                    (viewHolder as DriverMemoProductAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onChildDrawOver(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                getDefaultUIUtil().onDrawOver(
                    c,
                    recyclerView,
                    (viewHolder as DriverMemoProductAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun updateSummaryAmountAndQty() {

    }


    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }


    private fun addOnClickListener() {
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {

            if (Main.app.getDriverMemo().driverMemo.deliveryDate == null) {
                Notify.toastLong("Please select delivery date")
                return@setOnClickListener
            }

            if (Main.app.getDriverMemo().driverMemoDetail.isEmpty()) {
                Notify.toastLong("Please add products")
                return@setOnClickListener
            }
            Main.app.getDriverMemo().updatePriceAndQty()
            Main.app.getDriverMemo().serializeItems()
            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
                .setCallback(
                    object : OnNetworkResponse {
                        override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                            Main.app.clearDriverMemo()
                            findNavController().popBackStack()
                            Notify.toastLong("Success")
                        }

                        override fun onFailure(
                            call: Call<*>?,
                            response: BaseResponse<*>?,
                            tag: Any?
                        ) {
                            Notify.toastLong("Unable get create driver memo")
                        }
                    }).enque(Network.api()?.getDriverMemo(Main.app.getDriverMemo()))
                .execute()
        }
    }

    private fun getDriverMemoDetails() {
        if (memo == null)
            return
    }

}