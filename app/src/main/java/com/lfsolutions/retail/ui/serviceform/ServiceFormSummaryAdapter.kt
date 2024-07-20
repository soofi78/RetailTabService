package com.lfsolutions.retail.ui.serviceform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.service.ComplaintServiceDetails

class ServiceFormSummaryAdapter(val serviceDetails: ArrayList<ComplaintServiceDetails>?) :
    RecyclerView.Adapter<ServiceFormSummaryAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = serviceDetails?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = serviceDetails?.get(position)?.qty.toString()
        holder.binding.txtPrice.text = serviceDetails?.get(position)?.price.toString()
        holder.binding.txtProductName.text = serviceDetails?.get(position)?.productName
        holder.binding.txtSerials.text = serviceDetails?.get(position)?.getSerialNumbers()
        holder.binding.txtTag.text = serviceDetails?.get(position)?.transTypeDisplayText
        holder.itemView.setOnClickListener {
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