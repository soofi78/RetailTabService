package com.lfsolutions.retail.ui.delivery.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding

class DeliveryOrderSummaryAdapter(val items: ArrayList<DeliveryOrderDetails>?) :
    RecyclerView.Adapter<DeliveryOrderSummaryAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = items?.get(position)?.deliverQty.toString()
        holder.binding.txtPrice.visibility = View.GONE
        holder.binding.txtProductName.text = items?.get(position)?.productName
        Glide.with(holder.binding.imgProduct.context)
            .load(Main.app.getBaseUrl() + items?.get(position)?.productImage)
            .centerCrop()
            .placeholder(R.drawable.no_image)
            .into(holder.binding.imgProduct)
        holder.binding.txtSerials.visibility = View.GONE
        holder.binding.txtTag.visibility = View.GONE
//        holder.binding.txtTag.text =
//            if (items?.get(position)?.IsFOC == true) "FOC" else if (items?.get(
//                    position
//                )?.IsExchange == true
//            ) "Exchange" else if (items?.get(position)?.IsExpire == true) "Expire" else "None"

//        holder.binding.txtTag.visibility =
//            if (holder.binding.txtTag.text.equals("None")) View.GONE else View.VISIBLE

        holder.itemView.tag = items?.get(position)
        holder.itemView.setOnClickListener {
            mListener?.onOrderSummarySelect(it.tag as DeliveryOrderDetails)
        }

    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        items?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect(item: DeliveryOrderDetails)
    }

}