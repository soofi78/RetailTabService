package com.lfsolutions.retail.ui.delivery.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.lfsolutions.retail.util.toAddVisibility
import com.lfsolutions.retail.util.toSerialNumberAdapter

class DeliveryOrderSummaryAdapter(val deliveryOrder: ArrayList<DeliveryOrderDetails>?) :
    RecyclerView.Adapter<DeliveryOrderSummaryAdapter.ViewHolder>() {

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

    override fun getItemCount(): Int = deliveryOrder?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = deliveryOrder?.get(position)?.deliverQty.toString()
        holder.binding.txtPrice.visibility = View.GONE
        holder.binding.txtProductName.text = deliveryOrder?.get(position)?.productName
        Glide.with(holder.binding.imgProduct.context)
            .load(Main.app.getBaseUrl() + deliveryOrder?.get(position)?.productImage)
            .centerCrop()
            .placeholder(R.drawable.no_image)
            .into(holder.binding.imgProduct)
        holder.binding.txtSerials.visibility = View.GONE
        holder.binding.txtTag.visibility = View.GONE
//        holder.binding.txtTag.text =
//            if (deliveryOrder?.get(position)?.IsFOC == true) "FOC" else if (deliveryOrder?.get(
//                    position
//                )?.IsExchange == true
//            ) "Exchange" else if (deliveryOrder?.get(position)?.IsExpire == true) "Expire" else "None"

//        holder.binding.txtTag.visibility =
//            if (holder.binding.txtTag.text.equals("None")) View.GONE else View.VISIBLE

        //holder.binding.txtSerials.text = salesOrderDetails?.get(position)?.getSerialNumbers()
        val batchList = deliveryOrder?.get(position)?.productBatchList ?: emptyList()
        holder.binding.txtSerials.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()

        holder.itemView.tag = deliveryOrder?.get(position)
        holder.itemView.setDebouncedClickListener {
            mListener?.onOrderSummarySelect(it.tag as DeliveryOrderDetails)
        }

    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        deliveryOrder?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect(item: DeliveryOrderDetails)
    }

}