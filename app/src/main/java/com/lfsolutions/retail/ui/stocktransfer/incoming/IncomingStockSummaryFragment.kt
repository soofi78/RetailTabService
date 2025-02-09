package com.lfsolutions.retail.ui.stocktransfer.incoming

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentIncomingStockSummaryBinding
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class IncomingStockSummaryFragment : Fragment() {
    private lateinit var mBinding: FragmentIncomingStockSummaryBinding
    private var itemSwipeHelper: ItemTouchHelper? = null
    private lateinit var mAdapter: InComingStockSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentIncomingStockSummaryBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateView()
    }

    private fun initiateView() {
        mAdapter = InComingStockSummaryAdapter(
            requireActivity(),
            Main.app.getInComingStockTransferRequestObject().stockTransferDetails
        )
        mAdapter.setOnItemUpdateListener(object : InComingStockSummaryAdapter.OnItemUpdated {
            override fun OnItemUpdated(stockTransferProduct: StockTransferProduct) {
                updateSummaryAmountAndQty()
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Stock Receive")
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
        mBinding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        Main.app.getInComingStockTransferRequestObject().date =
            mBinding.date.text.toString() + "T00:00:00Z"
        mBinding.dateSelectionView.setDebouncedClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    mBinding.date.text = "$day-$month-$year"
                    Main.app.getInComingStockTransferRequestObject().date =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }

        mBinding.addEquipment.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_in_coming_stock_to_product_listing)
        }
    }


    private fun updateSummaryAmountAndQty() {
        mBinding.txtQTY.text = getQty().toString()
        mBinding.txtTotal.text =
            Main.app.getSession().currencySymbol + getTotal().formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = getQty() != 0.0
    }

    private fun getTotal(): Double {
        var total = 0.0
        Main.app.getInComingStockTransferRequestObject().stockTransferDetails.forEach {
            total += it.subTotal
        }
        return total
    }

    private fun getQty(): Double {
        var qty = 0.0
        Main.app.getInComingStockTransferRequestObject().stockTransferDetails.forEach {
            qty += it.qty
        }
        return qty
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
                mAdapter.remove(position)
                mAdapter.notifyDataSetChanged()
                updateSummaryAmountAndQty()
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                super.getMovementFlags(recyclerView, viewHolder)
                if (viewHolder is InComingStockSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as InComingStockSummaryAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun addOnClickListener() {
        mBinding.btnCancel.setOnClickListener {
            Notify.toastLong("Cleared all items")
            requireActivity().finish()
        }

        mBinding.header.setOnBackClick {
            requireActivity().finish()
        }

        mBinding.btnComplete.setOnClickListener {
            if (serialBatchVerified().not()) {
                Notify.toastLong("Please add serial numbers")
                return@setOnClickListener
            }


            if (Main.app.getInComingStockTransferRequestObject().date == null ||
                Main.app.getInComingStockTransferRequestObject().date?.isBlank() == true
            ) {
                Notify.toastLong("Please select date")
                return@setOnClickListener
            }

            NetworkCall.make()
                .autoLoadigCancel(Loading().forApi(requireActivity(), "Requesting Stock Transfer"))
                .setCallback(object : OnNetworkResponse {
                    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                        Notify.toastLong("Success")
                        requireActivity().finish()
                    }

                    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                        Notify.toastLong("Stock transfer failed")
                    }
                }).enque(
                    Network.api()
                        ?.createUpdateInComingStockTransfer(Main.app.getInComingStockTransferRequestObject())
                ).execute()
        }
    }

    private fun serialBatchVerified(): Boolean {
        var verified = true
        Main.app.getInComingStockTransferRequestObject().stockTransferDetails.forEach {
            val serial = it.isAsset == true || it.type.equals("S")
            val notBatched = it.productBatchList == null || it.productBatchList.size == 0
            val batchedAndQtyNotMatch =
                it.productBatchList != null && it.qty.toInt() != it.productBatchList.size
            if (serial && (notBatched || batchedAndQtyNotMatch)) {
                verified = false
            }
        }
        return verified
    }

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        Main.app.clearInComingStockTransfer()
    }
}