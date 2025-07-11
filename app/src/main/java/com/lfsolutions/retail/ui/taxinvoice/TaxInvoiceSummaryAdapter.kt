package com.lfsolutions.retail.ui.taxinvoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.sale.invoice.SalesInvoiceDetail
import com.lfsolutions.retail.util.disableQtyBox
import com.lfsolutions.retail.util.disableQtyFields
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.lfsolutions.retail.util.toAddVisibility
import com.lfsolutions.retail.util.toSerialNumberAdapter

class TaxInvoiceSummaryAdapter(val salveInvoiceDetails: ArrayList<SalesInvoiceDetail>?) :
    RecyclerView.Adapter<TaxInvoiceSummaryAdapter.ViewHolder>() {

    private var mListener: OnOrderSummarySelectListener? = null

    class ViewHolder(val binding: ItemOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
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
        holder.binding.txtQty.text = salveInvoiceDetails?.get(position)?.qty.toString()
        if (salveInvoiceDetails?.get(position)?.isFOC == true)
            holder.binding.txtPrice.text =
                """${Main.app.getSession().currencySymbol}0"""
        else
            holder.binding.txtPrice.text =
                """${Main.app.getSession().currencySymbol}${salveInvoiceDetails?.get(position)?.totalValue?.formatDecimalSeparator()}"""
        holder.binding.txtProductName.text = salveInvoiceDetails?.get(position)?.productName
        Glide.with(holder.binding.imgProduct.context)
            .load(Main.app.getBaseUrl() + salveInvoiceDetails?.get(position)?.productImage)
            .centerCrop()
            .placeholder(R.drawable.no_image)
            .into(holder.binding.imgProduct)
        holder.binding.txtTag.text =
            if (salveInvoiceDetails?.get(position)?.isFOC == true) "FOC" else if (salveInvoiceDetails?.get(
                    position
                )?.isExchange == true
            ) "Exchange" else if (salveInvoiceDetails?.get(position)?.isExpire == true) "Return" else "None"

        holder.binding.txtTag.visibility =
            if (holder.binding.txtTag.text.equals("None")) View.GONE else View.VISIBLE

        holder.itemView.tag = salveInvoiceDetails?.get(position)
        holder.itemView.setDebouncedClickListener {
            mListener?.onOrderSummarySelect(it.tag as SalesInvoiceDetail)
        }
        val batchList = salveInvoiceDetails?.get(position)?.productBatchList ?: emptyList()
        //holder.binding.txtSerials.text = salveInvoiceDetails?.get(position)?.getSerialNumbers()
        holder.binding.txtSerials.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()
        batchList.disableQtyBox(
            holder.binding.txtQty,
            holder.itemView
        )
    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        salveInvoiceDetails?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect(salesInvoiceDetail: SalesInvoiceDetail)
    }

}