package com.lfsolutions.retail.ui.taxinvoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.util.formatDecimalSeparator

class TaxInvoiceSummaryAdapter(val salveInvoiceDetails: ArrayList<SalesInvoiceDetail>?) :
    RecyclerView.Adapter<TaxInvoiceSummaryAdapter.ViewHolder>() {

    private var mListener: OnOrderSummarySelectListener? = null

    class ViewHolder(val binding: ItemOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View? {
            return binding.swipeAble
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = salveInvoiceDetails?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = salveInvoiceDetails?.get(position)?.Qty.toString()
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + salveInvoiceDetails?.get(position)?.NetTotal?.formatDecimalSeparator()
        holder.binding.txtProductName.text = salveInvoiceDetails?.get(position)?.ProductName
        holder.binding.txtSerials.text = salveInvoiceDetails?.get(position)?.getSerialNumbers()
        holder.binding.txtTag.text =
            if (salveInvoiceDetails?.get(position)?.IsFOC == true) "FOC" else if (salveInvoiceDetails?.get(
                    position
                )?.IsExchange == true
            ) "Exchange" else if (salveInvoiceDetails?.get(position)?.IsExpire == true) "Expire" else "None"
        holder.itemView.setOnClickListener {
            mListener?.onOrderSummarySelect()
        }

    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        salveInvoiceDetails?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect()
    }

}