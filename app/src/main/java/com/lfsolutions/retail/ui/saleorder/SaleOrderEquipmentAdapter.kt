package com.lfsolutions.retail.ui.saleorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemProductBinding
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.util.formatDecimalSeparator

class SaleOrderEquipmentAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<SaleOrderEquipmentAdapter.ViewHolder>() {

    private var mListener: OnEquipmentClickListener? = null

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipment = products.get(position)
        holder.itemView.tag = equipment
        holder.binding.txtProductName.text = equipment?.productName
        holder.binding.txtCategory.text =
            """SKU: ${equipment?.inventoryCode} | QTY Available: ${equipment?.qtyOnHand}"""
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + equipment?.cost?.formatDecimalSeparator()

        Glide.with(holder.itemView).load(Main.app.getBaseUrl() + equipment?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.itemView.setOnClickListener {
            mListener?.onEquipmentClick(it.tag as Product)
        }
    }

    fun setListener(onEquipmentClickListener: OnEquipmentClickListener) {
        this.mListener = onEquipmentClickListener
    }


    interface OnEquipmentClickListener {
        fun onEquipmentClick(product: Product)
    }
}