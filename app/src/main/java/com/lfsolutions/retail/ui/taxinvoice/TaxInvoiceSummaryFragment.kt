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
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.serialBatchVerified
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.maltaisn.calcdialog.CalcDialog

import com.videotel.digital.util.Notify
import java.math.BigDecimal


class TaxInvoiceSummaryFragment : Fragment(), CalcDialog.CalcDialogCallback {

    private var discount: Double = 0.0
    private var itemSwipeHelper: ItemTouchHelper? = null
    private var userSession: UserSession? = null
    private var _binding: FragmentTaxInvoiceSummaryBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: TaxInvoiceSummaryAdapter
    private var roundingAmountClickCount= 0
    // Add original grandtotal
    private var originalGrandTotal: Double? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaxInvoiceSummaryBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userSession=Main.app.getSession()
        mAdapter = TaxInvoiceSummaryAdapter(Main.app.getTaxInvoice()?.salesInvoiceDetail,requireActivity())
        mAdapter.setOnItemUpdateListener(object : TaxInvoiceSummaryAdapter.OnItemUpdated {
            override fun OnItemUpdated(salesInvoiceDetail: SalesInvoiceDetail) {
                updateSummaryAmountAndQty()
            }
        })
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
        userSession?.userName?.let { mBinding.header.setName(it) }
        addOnClickListener()
        mBinding.checkboxFOC.isChecked = Main.app.getTaxInvoice()?.salesInvoice?.type.equals("F")
        mBinding.flowRoundingAmount.visibility =
            userSession?.roundingAmount?.takeIf { it > 0.0 }?.let { View.VISIBLE } ?: View.GONE

        mBinding.flowNetTotal.visibility =
            userSession?.roundingAmount?.takeIf { it > 0.0 }?.let { View.VISIBLE } ?: View.GONE
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
                        this.price = costWithoutTax
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
        println("Summery: ${Main.app.getTaxInvoice()?.salesInvoice}")
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
        mBinding.txtNetTotal.text =
            "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceNetTotal.toString()
                .formatDecimalSeparator()

        mBinding.txtTotal.text = "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal.toString().formatDecimalSeparator()
        mBinding.btnRoundingAmount.text = "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceRoundingAmount.toString().formatDecimalSeparator()
        mBinding.checkboxFOC.isChecked = Main.app.getTaxInvoice()?.salesInvoice?.type=="F"
        //mBinding.btnComplete.isEnabled = Main.app.getTaxInvoice()?.salesInvoice?.invoiceQty != 0.0
        mBinding.btnComplete.isEnabled = !Main.app.getTaxInvoice()?.salesInvoiceDetail.isNullOrEmpty()
        if (Main.app.getTaxInvoice()?.salesInvoiceDetail.isNullOrEmpty()) {
            clearAllItems()
        }
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
        mBinding.btnRoundingAmount.setDebouncedClickListener {
            val applyRounding = roundingAmountClickCount % 2 == 0
            Main.app.getTaxInvoice()?.salesInvoice?.isRoundingApplied = applyRounding
            Main.app.getTaxInvoice()?.salesInvoice?.roundDown = userSession?.roundDown?:false
            Main.app.getTaxInvoice()?.salesInvoice?.roundingAmount = userSession?.roundingAmount?:0.0
            updateSummaryAmountAndQty()
            roundingAmountClickCount++
        }

        mBinding.btnCancel.setDebouncedClickListener {
            clearAllItems()
            findNavController().popBackStack()
        }

        mBinding.header.setOnBackClick {
            findNavController().popBackStack()
        }

        mBinding.btnComplete.setDebouncedClickListener {
            if (serialBatchVerified(Main.app.getTaxInvoice()?.salesInvoiceDetail).not()) {
                Notify.toastLong("Please add serial numbers")
                return@setDebouncedClickListener
            }

            findNavController().popBackStack()
        }
    }


   fun demoRounding(){
        /* if (roundingAmountClickCount % 2 == 0) {
                updateSummaryAmountAndQty(isRoundingApplied = true)
                */
        /*val result = getRoundOffValue(
                totalPrice = Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal ?: 0.0,
                roundOff = userSession?.roundingAmount ?: 0.0,
                roundDown = userSession?.roundDown ?: false
            )

            Main.app.getTaxInvoice()?.updateGrandTotalAndRoundingAmount(invoiceGrandTotal = result.first, invoiceRoundingAmount = result.second)

            Main.app.getTaxInvoice()?.salesInvoice?.apply {
                invoiceGrandTotal = result.first
                balance = result.first
                invoiceRoundingAmount = result.second.formatPriceForApi()
            }*//*
            }
            else {
                updateSummaryAmountAndQty(isRoundingApplied = false)
                // Reset to original
               *//* Main.app.getTaxInvoice()?.salesInvoice?.apply {
                    invoiceGrandTotal = invoiceNetTotal
                    balance = originalGrandTotal ?: invoiceNetTotal
                    invoiceRoundingAmount = 0.0
                }
                originalGrandTotal = null*//*
            }*/
        //val currency = userSession?.currencySymbol
        //mBinding.txtTotal.text = "$currency ${Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal.toString().formatDecimalSeparator()}"
        //mBinding.btnRoundingAmount.text = "$currency ${Main.app.getTaxInvoice()?.salesInvoice?.invoiceRoundingAmount.toString().formatDecimalSeparator()}"

       /*mBinding.btnRoundingAmount.setDebouncedClickListener {
           */
       /* val roundedAmount = Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal?.getRoundOffValue(dRounding = BigDecimal(userSession?.roundingAmount?:0.0), roundDown = userSession?.roundDown?:false)*//*
            if(roundingAmountClickCount>1){
                return@setDebouncedClickListener
            }
            roundingAmountClickCount++
            val roundedAmount = getRoundOffValue(totalPrice = Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal?:0.0, roundOff = userSession?.roundingAmount?:0.0, roundDown = userSession?.roundDown?:false)
            println("RoundedTotal: $roundedAmount")
            //Notify.toastLong("RoundedTotal:$roundedAmount")
            Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal=roundedAmount.first
            Main.app.getTaxInvoice()?.salesInvoice?.balance=roundedAmount.first
            Main.app.getTaxInvoice()?.salesInvoice?.invoiceRoundingAmount=roundedAmount.second.formatPriceForApi()
            val currency = userSession?.currencySymbol
            mBinding.txtTotal.text = "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceGrandTotal.toString().formatDecimalSeparator()
            mBinding.btnRoundingAmount.text = "$currency " + Main.app.getTaxInvoice()?.salesInvoice?.invoiceRoundingAmount.toString()
                    .formatDecimalSeparator()

        }*/
    }

    private fun clearAllItems() {
        Notify.toastLong("Cleared all items")
        Main.app.getTaxInvoice()?.clear()
        Main.app.getTaxInvoice()?.salesInvoiceDetail?.clear()
    }
    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        discount = value?.toDouble() ?: 0.0
        updateSummaryAmountAndQty()
    }
}