package com.lfsolutions.retail.ui.taxinvoice

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
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.maltaisn.calcdialog.CalcDialog

import com.videotel.digital.util.Notify
import java.math.BigDecimal


class TaxInvoiceSummaryFragment : Fragment(), CalcDialog.CalcDialogCallback {

    private var discount: Double = 0.0
    private var itemSwipeHelper: ItemTouchHelper? = null
    private var _binding: FragmentTaxInvoiceSummaryBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: TaxInvoiceSummaryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaxInvoiceSummaryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = TaxInvoiceSummaryAdapter(Main.app.getTaxInvoice()?.SalesInvoiceDetail)
        mAdapter.setListener(object : TaxInvoiceSummaryAdapter.OnOrderSummarySelectListener {
            override fun onOrderSummarySelect(salesInvoiceDetail: SalesInvoiceDetail) {
                openQuantityUpdateDialog(salesInvoiceDetail)
            }
        })

        itemSwipeHelper = ItemTouchHelper(getSwipeToDeleteListener())
        itemSwipeHelper?.attachToRecyclerView(mBinding.recyclerView)
        mBinding.recyclerView.adapter = mAdapter
        updateSummaryAmountAndQty()
        mBinding.header.setBackText("Sale Invoice Summary")
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
    }

    private fun openQuantityUpdateDialog(salesInvoiceDetail: SalesInvoiceDetail) {
        val modal = ProductQuantityUpdateSheet()
        modal.setProductDetails(
            salesInvoiceDetail.ProductImage.toString(),
            salesInvoiceDetail.ProductName.toString(),
            salesInvoiceDetail.Qty,
            salesInvoiceDetail.NetTotal / salesInvoiceDetail.Qty,
            salesInvoiceDetail.UnitName.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                val index =
                    Main.app.getTaxInvoice()?.SalesInvoiceDetail?.indexOf(salesInvoiceDetail) ?: -1
                if (index > -1) {
                    Main.app.getTaxInvoice()?.SalesInvoiceDetail?.get(index)?.apply {
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
                    Main.app.getTaxInvoice()?.SalesInvoiceDetail?.indexOf(salesInvoiceDetail) ?: -1
                if (index > -1) {
                    Main.app.getTaxInvoice()?.SalesInvoiceDetail?.get(index)?.apply {
                        val subTotal = (Qty * price) - discount
                        val taxAmount = subTotal * (TaxRate / 100.0)
                        val netTotal = subTotal + taxAmount
                        val total = subTotal + taxAmount
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
        Main.app.getTaxInvoice()?.updatePriceAndQty(discount)
        Main.app.getTaxInvoice()?.serializeItems()
        val currency = Main.app.getSession().currencySymbol
        mBinding.txtQTY.text = Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceQty.toString()
        mBinding.txtTotalAmount.text = "$currency " +
                Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceTotalValue.toString()
                    .formatDecimalSeparator()
        mBinding.txtDiscounts.text = "$currency " +
                Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceNetDiscount.toString()
                    .formatDecimalSeparator()
        mBinding.txtSubTotal.text = "$currency " +
                Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceSubTotal.toString()
                    .formatDecimalSeparator()
        mBinding.txtTaxAmount.text = "$currency " +
                Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceTax.toString()
                    .formatDecimalSeparator()
        mBinding.txtTotal.text = "$currency " +
                Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceGrandTotal.toString()
                    .formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = Main.app.getTaxInvoice()?.SalesInvoice?.InvoiceQty != 0.0
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
                if (viewHolder is TaxInvoiceSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                getDefaultUIUtil().clearView((viewHolder as TaxInvoiceSummaryAdapter.ViewHolder).getSwipableView())
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected((viewHolder as TaxInvoiceSummaryAdapter.ViewHolder).getSwipableView())
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
                    (viewHolder as TaxInvoiceSummaryAdapter.ViewHolder).getSwipableView(),
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
                    (viewHolder as TaxInvoiceSummaryAdapter.ViewHolder).getSwipableView(),
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
            Main.app.getTaxInvoice()?.SalesInvoice?.Type = if (isChecked) "F" else "N"
        }
        mBinding.flowDiscount.setOnClickListener {
            Calculator.show(this)
        }
        mBinding.btnCancel.setOnClickListener {
            Notify.toastLong("Cleared all items")
            Main.app.getTaxInvoice()?.SalesInvoiceDetail?.clear()
            findNavController().popBackStack()
        }

        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }

        mBinding.btnComplete.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        discount = value?.toDouble() ?: 0.0
        updateSummaryAmountAndQty()
    }
}