package com.lfsolutions.retail.ui.serviceform

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemEquipmentBinding
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener

class EquipmentAdapter(val productList: List<Product>?) :
    RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    private var mListener: OnEquipmentClickListener? = null

    class ViewHolder(val binding: ItemEquipmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder = ViewHolder(
        ItemEquipmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = productList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipment = productList?.get(position)
        holder.itemView.tag = equipment
        holder.binding.txtProductName.text = equipment?.productName
        holder.binding.txtCategory.text =
            """SKU: ${equipment?.inventoryCode} | QTY Available: ${equipment?.qtyOnHand}"""
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + equipment?.cost?.formatDecimalSeparator()

        Glide.with(holder.itemView).load(Main.app.getBaseUrl() + equipment?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.itemView.setDebouncedClickListener {
            mListener?.onEquipmentClick(it.tag as Product)
        }
    }

    fun setListener(listener: OnEquipmentClickListener) {
        mListener = listener
    }

    interface OnEquipmentClickListener {
        fun onEquipmentClick(product: Product)
    }
}