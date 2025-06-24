package com.lfsolutions.retail.ui.stocktransfer.outgoing

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
import com.lfsolutions.retail.databinding.FragmentOutGoingStockSummaryBinding
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.Notify


class OutGoingStockSummaryFragment(val openEquipmentList: () -> Unit) : Fragment() {
    private lateinit var mBinding: FragmentOutGoingStockSummaryBinding
    private var itemSwipeHelper: ItemTouchHelper? = null
    private lateinit var mAdapter: OutGoingStockSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentOutGoingStockSummaryBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateView()
    }


    private fun initiateView() {
        println("StockTransferDetails: ${Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails}")
        mAdapter = OutGoingStockSummaryAdapter(
            requireActivity(),
            Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails
        )
        mAdapter.setOnItemUpdateListener(object : OutGoingStockSummaryAdapter.OnItemUpdated {
            override fun OnItemUpdated(stockTransferProduct: StockTransferProduct) {
                updateSummaryAmountAndQty()
            }
        })
        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Back")
        mBinding.summaryTitle.text = "Summary for Stock Transfer"
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
        mBinding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
    }


    private fun updateSummaryAmountAndQty() {
        mBinding.txtQTY.text = getQty().toString()
        mBinding.txtTotal.text = Main.app.getSession().currencySymbol + getTotal().formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.isNotEmpty()
    }

    private fun getTotal(): Double {
        var total = 0.0
        Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.forEach {
            total += it.subTotal
        }
        return total
    }

    private fun getQty(): Double {
        var qty = 0.0
        Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.forEach {
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
                updateSummaryAmountAndQty()
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                super.getMovementFlags(recyclerView, viewHolder)
                if (viewHolder is OutGoingStockSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as OutGoingStockSummaryAdapter.ViewHolder).getSwipableView(),
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

        mBinding.addEquipment.setOnClickListener {
            openEquipmentList.invoke()
        }

        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }

        mBinding.btnComplete.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}