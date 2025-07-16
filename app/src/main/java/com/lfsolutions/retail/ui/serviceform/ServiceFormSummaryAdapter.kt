package com.lfsolutions.retail.ui.serviceform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.service.ComplaintServiceDetails
import com.lfsolutions.retail.util.disableQtyBox
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.lfsolutions.retail.util.toAddVisibility
import com.lfsolutions.retail.util.toSerialNumberAdapter

class ServiceFormSummaryAdapter(val serviceDetails: ArrayList<ComplaintServiceDetails>?) :
    RecyclerView.Adapter<ServiceFormSummaryAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = serviceDetails?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = serviceDetails?.get(position)?.qty.toString()
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + serviceDetails?.get(position)?.price?.formatDecimalSeparator()
        holder.binding.txtProductName.text = serviceDetails?.get(position)?.productName
        holder.binding.txtTag.text = serviceDetails?.get(position)?.transTypeDisplayText

        val batchList = serviceDetails?.get(position)?.productBatchList ?: emptyList()
        holder.binding.serialNoView.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()

        batchList.disableQtyBox(
            holder.binding.txtQty,
            holder.itemView
        )

        holder.itemView.setDebouncedClickListener {
            mListener?.onOrderSummarySelect()
        }

    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        serviceDetails?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect()
    }

}