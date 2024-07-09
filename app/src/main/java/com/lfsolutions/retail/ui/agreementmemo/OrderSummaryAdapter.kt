package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding

class OrderSummaryAdapter : RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>() {

    private var mListener: OnOrderSummarySelectListener? = null

    class ViewHolder(val binding: ItemOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {

            mListener?.onOrderSummarySelect()

        }

    }

    fun setListener(listener: OnOrderSummarySelectListener) {

        mListener = listener

    }

    interface OnOrderSummarySelectListener {

        fun onOrderSummarySelect()

    }

}