package com.lfsolutions.retail.ui.saleorder

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
import com.lfsolutions.retail.databinding.FragmentTaxInvoiceSummaryBinding
import com.lfsolutions.retail.model.sale.order.SalesOrderDetail
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.maltaisn.calcdialog.CalcDialog

import com.videotel.digital.util.Notify
import java.math.BigDecimal


class SaleOrderSummaryFragment : Fragment(), CalcDialog.CalcDialogCallback {

    private var discount: Double = 0.0
    private var itemSwipeHelper: ItemTouchHelper? = null
    private var _binding: FragmentTaxInvoiceSummaryBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: SaleOrderSummaryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaxInvoiceSummaryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = SaleOrderSummaryAdapter(Main.app.getSaleOrder()?.SalesOrderDetail)
        mAdapter.setListener(object : SaleOrderSummaryAdapter.OnOrderSummarySelectListener {
            override fun onOrderSummarySelect(salesOrderDetail: SalesOrderDetail) {
                openQuantityUpdateDialog(salesOrderDetail)
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Sale Order Summary")
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
    }

    private fun openQuantityUpdateDialog(salesOrderDetail: SalesOrderDetail) {
        val modal = ProductQuantityUpdateSheet()
        modal.setProductDetails(
            salesOrderDetail.ProductImage.toString(),
            salesOrderDetail.ProductName.toString(),
            salesOrderDetail.Qty,
            salesOrderDetail.CostWithoutTax,
            salesOrderDetail.UnitName.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                val index =
                    Main.app.getSaleOrder()?.SalesOrderDetail?.indexOf(salesOrderDetail) ?: -1
                if (index > -1) {
                    Main.app.getSaleOrder()?.SalesOrderDetail?.get(index)?.apply {
                        val subTotal = (quantity * CostWithoutTax) - discount
                        val taxAmount = subTotal * (TaxRate / 100.0)
                        val netTotal = subTotal + taxAmount
                        val total = subTotal + taxAmount
                        Qty = quantity
                        Price = subTotal
                        NetCost = total
                        SubTotal = subTotal
                        NetTotal = netTotal
                        Tax = taxAmount
                        TotalValue = subTotal
                    }
                    updateSummaryAmountAndQty()
                    mAdapter.notifyItemChanged(index)
                }
            }

            override fun onPriceChanged(price: Double) {
                val index =
                    Main.app.getSaleOrder()?.SalesOrderDetail?.indexOf(salesOrderDetail) ?: -1
                if (index > -1) {
                    Main.app.getSaleOrder()?.SalesOrderDetail?.get(index)?.apply {
                        val subTotal = Qty * price
                        val discount = 0.0
                        val taxAmount = subTotal * (TaxRate / 100.0)
                        val netTotal = (subTotal - discount) + taxAmount
                        val total = (subTotal + taxAmount)
                        Price = subTotal
                        CostWithoutTax = price
                        NetCost = total
                        SubTotal = subTotal
                        NetTotal = netTotal
                        Tax = taxAmount
                        TotalValue = subTotal
                    }
                    updateSummaryAmountAndQty()
                    mAdapter.notifyItemChanged(index)
                }
            }
        })
        requireActivity().supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
    }

    private fun updateSummaryAmountAndQty() {
        Main.app.getSaleOrder()?.updatePriceAndQty(discount)
        Main.app.getSaleOrder()?.serializeItems()
        val currency = Main.app.getSession().currencySymbol
        mBinding.txtQTY.text = Main.app.getSaleOrder()?.SalesOrder?.OrderedQty.toString()
        mBinding.txtTotalAmount.text = "$currency " +
                Main.app.getSaleOrder()?.SalesOrder?.SoTotalValue.toString()
                    .formatDecimalSeparator()
        mBinding.txtDiscounts.text = "$currency " +
                Main.app.getSaleOrder()?.SalesOrder?.SoNetDiscount.toString()
                    .formatDecimalSeparator()
        mBinding.txtSubTotal.text = "$currency " +
                Main.app.getSaleOrder()?.SalesOrder?.SoSubTotal.toString()
                    .formatDecimalSeparator()
        mBinding.txtTaxAmount.text = "$currency " +
                Main.app.getSaleOrder()?.SalesOrder?.SoTax.toString()
                    .formatDecimalSeparator()
        mBinding.txtTotal.text = "$currency " +
                Main.app.getSaleOrder()?.SalesOrder?.SoGrandTotal.toString()
                    .formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = Main.app.getSaleOrder()?.SalesOrder?.OrderedQty != 0.0
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
                if (viewHolder is SaleOrderSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as SaleOrderSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as SaleOrderSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as SaleOrderSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as SaleOrderSummaryAdapter.ViewHolder).getSwipableView(),
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    }

    private fun addOnClickListener() {
        mBinding.checkboxFOC.setOnCheckedChangeListener { _, isChecked ->
            Main.app.getSaleOrder()?.SalesOrder?.Type = if (isChecked) "F" else "A"
        }
        mBinding.flowDiscount.setDebouncedClickListener {
            Calculator.show(this)
        }
        mBinding.btnCancel.setDebouncedClickListener {
            Notify.toastLong("Cleared all items")
            Main.app.getSaleOrder()?.SalesOrderDetail?.clear()
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