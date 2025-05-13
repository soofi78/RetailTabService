package com.lfsolutions.retail.ui.delivery.order

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
import com.lfsolutions.retail.databinding.FragmentDeliveryOrderSummaryBinding
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.maltaisn.calcdialog.CalcDialog
import com.videotel.digital.util.Notify
import java.math.BigDecimal


class DeliveryOrderSummaryFragment : Fragment(), CalcDialog.CalcDialogCallback {

    private var discount: Double = 0.0
    private var itemSwipeHelper: ItemTouchHelper? = null
    private var _binding: FragmentDeliveryOrderSummaryBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: DeliveryOrderSummaryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryOrderSummaryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = DeliveryOrderSummaryAdapter(Main.app.getDeliveryOrder()?.deliveryOrderDetail)
        mAdapter.setListener(object : DeliveryOrderSummaryAdapter.OnOrderSummarySelectListener {
            override fun onOrderSummarySelect(item: DeliveryOrderDetails) {
                openQuantityUpdateDialog(item)
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Delivery Order Summary")
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
    }

    private fun openQuantityUpdateDialog(salesInvoiceDetail: DeliveryOrderDetails) {
        val modal = ProductQuantityUpdateSheet()
        modal.setProductDetails(
            salesInvoiceDetail.productImage.toString(),
            salesInvoiceDetail.productName.toString(),
            salesInvoiceDetail.deliverQty ?: 0.0,
            0.0,
            salesInvoiceDetail.uom.toString()
        )
        modal.showPrice(false)
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                val index =
                    Main.app.getDeliveryOrder()?.deliveryOrderDetail?.indexOf(salesInvoiceDetail)
                        ?: -1
                if (index > -1) {
                    Main.app.getDeliveryOrder()?.deliveryOrderDetail?.get(index)?.apply {
                        val subTotal = (quantity * 0.0) - discount
                        val taxAmount = 0.0// subTotal * (TaxRate / 100.0)
                        val netTotal = subTotal + taxAmount
                        val total = subTotal + taxAmount
                        deliverQty = quantity
                        cost = total
                    }
                    updateSummaryAmountAndQty()
                    mAdapter.notifyItemChanged(index)
                }
            }

            override fun onPriceChanged(price: Double) {
                val index =
                    Main.app.getDeliveryOrder()?.deliveryOrderDetail?.indexOf(salesInvoiceDetail)
                        ?: -1
                if (index > -1) {
                    Main.app.getDeliveryOrder()?.deliveryOrderDetail?.get(index)?.apply {
                        val subTotal = (deliverQty ?: 0.0 * price) - discount
                        val taxAmount = 0.0 // subTotal * (TaxRate / 100.0)
                        val netTotal = subTotal + taxAmount
                        val total = subTotal + taxAmount
                        cost = total
                    }
                    updateSummaryAmountAndQty()
                    mAdapter.notifyItemChanged(index)
                }
            }
        })
        requireActivity().supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
    }

    private fun updateSummaryAmountAndQty() {
        Main.app.getDeliveryOrder()?.updatePriceAndQty()
        Main.app.getTaxInvoice()?.serializeItems()
        val currency = Main.app.getSession().currencySymbol
        mBinding.txtQTY.text =
            Main.app.getDeliveryOrder()?.deliveryOrder?.totalDeliveredQty.toString()
        mBinding.btnComplete.isEnabled =
            Main.app.getDeliveryOrder()?.deliveryOrder?.totalDeliveredQty != 0.0
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
                if (viewHolder is DeliveryOrderSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as DeliveryOrderSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as DeliveryOrderSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as DeliveryOrderSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as DeliveryOrderSummaryAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun addOnClickListener() {
//        mBinding.checkboxFOC.setOnCheckedChangeListener { _, isChecked ->
//            Main.app.getDeliveryOrder()?.deliveryOrder?.Type = if (isChecked) "F" else "N"
//        }
        mBinding.flowDiscount.setDebouncedClickListener {
            Calculator.show(this)
        }
        mBinding.btnCancel.setDebouncedClickListener {
            Notify.toastLong("Cleared all items")
            Main.app.getDeliveryOrder()?.deliveryOrderDetail?.clear()
            findNavController().popBackStack()
        }

        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }

        mBinding.btnComplete.setDebouncedClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        discount = value?.toDouble() ?: 0.0
        updateSummaryAmountAndQty()
    }
}