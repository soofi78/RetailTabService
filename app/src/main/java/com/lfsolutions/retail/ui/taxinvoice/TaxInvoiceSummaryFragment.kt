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
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaxInvoiceSummaryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = TaxInvoiceSummaryAdapter(Main.app.getTaxInvoice()?.salesInvoiceDetail)
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
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
    }

    private fun openQuantityUpdateDialog(salesInvoiceDetail: SalesInvoiceDetail) {
        val modal = ProductQuantityUpdateSheet()
        modal.setProductDetails(
            salesInvoiceDetail.productImage.toString(),
            salesInvoiceDetail.productName.toString(),
            salesInvoiceDetail.qty ?: 0.0,
            salesInvoiceDetail.costWithoutTax,
            salesInvoiceDetail.unitName.toString()
        )
        modal.allowNegativeQuantity(salesInvoiceDetail.isExpire == true)
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                val index =
                    Main.app.getTaxInvoice()?.salesInvoiceDetail?.indexOf(salesInvoiceDetail) ?: -1
                if (index > -1) {
                    Main.app.getTaxInvoice()?.salesInvoiceDetail?.get(index)?.apply {
                        val subTotal = (quantity * costWithoutTax) - discount
                        val taxAmount = subTotal * (taxRate / 100.0)
                        val netTotal = subTotal + taxAmount
                        val total = subTotal + taxAmount
                        this.qty = quantity
                        this.price = subTotal
                        this.netCost = total
                        this.subTotal = subTotal
                        this.netTotal = netTotal
                        this.tax = taxAmount
                        this.totalValue = subTotal
                    }
                    updateSummaryAmountAndQty()
                    mAdapter.notifyItemChanged(index)
                }
            }

            override fun onPriceChanged(price: Double) {
                val index =
                    Main.app.getTaxInvoice()?.salesInvoiceDetail?.indexOf(salesInvoiceDetail) ?: -1
                if (index > -1) {
                    Main.app.getTaxInvoice()?.salesInvoiceDetail?.get(index)?.apply {
                        val subTotal = ((qty ?: 0.0) * price) - discount
                        val taxAmount = subTotal * (taxRate / 100.0)
                        val netTotal = subTotal + taxAmount
                        val total = subTotal + taxAmount
                        this.price = subTotal
                        this.costWithoutTax = price
                        this.netCost = total
                        this.subTotal = subTotal
                        this.netTotal = netTotal
                        this.tax = taxAmount
                        this.totalValue = subTotal
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
        mBinding.txtQTY.text = Main.app.getTaxInvoice()?.salesInvoice?.invoiceQty.toString()
        mBinding.txtTotalAmount.text =
            "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceTotalValue.toString()
                .formatDecimalSeparator()
        mBinding.txtDiscounts.text =
            "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceNetDiscount.toString()
                .formatDecimalSeparator()
        mBinding.txtSubTotal.text =
            "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceSubTotal.toString()
                .formatDecimalSeparator()
        mBinding.txtTaxAmount.text =
            "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceTax.toString()
                .formatDecimalSeparator()
        mBinding.txtTotal.text =
            "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal.toString()
                .formatDecimalSeparator()
        mBinding.btnComplete.isEnabled = Main.app.getTaxInvoice()?.salesInvoice?.invoiceQty != 0.0
    }

    private fun getSwipeToDeleteListener(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ): Int {
                super.getMovementFlags(recyclerView, viewHolder)
                if (viewHolder is TaxInvoiceSummaryAdapter.ViewHolder) {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                } else return 0
            }

            override fun clearView(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
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
            Main.app.getTaxInvoice()?.salesInvoice?.type = if (isChecked) "F" else "N"
        }
        mBinding.flowDiscount.setDebouncedClickListener {
            Calculator.show(this)
        }
        mBinding.btnCancel.setDebouncedClickListener {
            Notify.toastLong("Cleared all items")
            Main.app.getTaxInvoice()?.clear()
            Main.app.getTaxInvoice()?.salesInvoiceDetail?.clear()
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