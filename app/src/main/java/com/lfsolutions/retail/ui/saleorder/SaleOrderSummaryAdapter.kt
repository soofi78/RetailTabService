package com.lfsolutions.retail.ui.saleorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.sale.order.SalesOrderDetail
import com.lfsolutions.retail.util.disableQtyBox
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.lfsolutions.retail.util.toAddVisibility
import com.lfsolutions.retail.util.toSerialNumberAdapter

class SaleOrderSummaryAdapter(val salesOrderDetails: ArrayList<SalesOrderDetail>?) :
    RecyclerView.Adapter<SaleOrderSummaryAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = salesOrderDetails?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = salesOrderDetails?.get(position)?.Qty.toString()
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + salesOrderDetails?.get(position)?.SubTotal?.formatDecimalSeparator()
        holder.binding.txtProductName.text = salesOrderDetails?.get(position)?.ProductName
        Glide.with(holder.binding.imgProduct.context)
            .load(Main.app.getBaseUrl() + salesOrderDetails?.get(position)?.ProductImage)
            .centerCrop()
            .placeholder(R.drawable.no_image)
            .into(holder.binding.imgProduct)
        holder.binding.txtTag.visibility = View.GONE
        holder.itemView.tag = salesOrderDetails?.get(position)

        //holder.binding.txtSerials.text = salesOrderDetails?.get(position)?.getSerialNumbers()
        val batchList = salesOrderDetails?.get(position)?.productBatchList ?: emptyList()
        holder.binding.txtSerials.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()
        batchList.disableQtyBox(
            holder.binding.txtQty,
            holder.itemView
        )

        holder.itemView.setDebouncedClickListener {
            mListener?.onOrderSummarySelect(it.tag as SalesOrderDetail)
        }

    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        salesOrderDetails?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect(salesOrderDetail: SalesOrderDetail)
    }

}