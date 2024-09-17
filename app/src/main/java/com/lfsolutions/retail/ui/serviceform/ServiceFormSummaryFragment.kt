package com.lfsolutions.retail.ui.serviceform

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
import com.lfsolutions.retail.databinding.FragmentServiceFormSummaryBinding
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.Notify


class ServiceFormSummaryFragment : Fragment() {

    private var itemSwipeHelper: ItemTouchHelper? = null
    private var _binding: FragmentServiceFormSummaryBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: ServiceFormSummaryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceFormSummaryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter =
            ServiceFormSummaryAdapter(Main.app.getComplaintService()?.complaintServiceDetails)
        mAdapter.setListener(object : ServiceFormSummaryAdapter.OnOrderSummarySelectListener {
            override fun onOrderSummarySelect() {
//                findNavController()
//                    .navigate(R.id.action_navigation_agreement_memo_bottom_navigation_to_navigation_add_equipment)
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Order Summary")
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
    }

    private fun updateSummaryAmountAndQty() {
        Main.app.getComplaintService()?.updatePriceAndQty()
        Main.app.getComplaintService()?.serializeItems()
        val total =
            Main.app.getComplaintService()?.complaintService?.totalPrice?.formatDecimalSeparator()
        val totalQty = Main.app.getComplaintService()?.complaintService?.totalQty
        mBinding.txtQTY.text = totalQty.toString()
        mBinding.txtTotal.text = Main.app.getSession().currencySymbol + total.toString()
        mBinding.btnComplete.isEnabled = totalQty?.toInt() != 0
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
                if (viewHolder is ServiceFormSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as ServiceFormSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as ServiceFormSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as ServiceFormSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as ServiceFormSummaryAdapter.ViewHolder).getSwipableView(),
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
            Main.app.getComplaintService()?.complaintServiceDetails?.clear()
            findNavController().popBackStack()
        }

        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }

        mBinding.btnComplete.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}