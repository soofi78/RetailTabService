package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemEquipmentBinding
import com.lfsolutions.retail.model.Equipment

class EquipmentAdapter(val equipmentList: List<Equipment>?) :
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

    override fun getItemCount(): Int = equipmentList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipment = equipmentList?.get(position)
        holder.itemView.tag = equipment
        holder.binding.txtProductName.text = equipment?.productName
        holder.binding.txtCategory.text =
            """${equipment?.categoryName} | QTY Available: ${equipment?.qtyOnHand}"""
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + equipment?.price.toString()

        Glide.with(holder.itemView).load(Main.app.getBaseUrl() + equipment?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.imgProduct)

        holder.itemView.setOnClickListener {
            mListener?.onEquipmentClick(it.tag as Equipment)
        }
    }

    fun setListener(listener: OnEquipmentClickListener) {
        mListener = listener
    }

    interface OnEquipmentClickListener {
        fun onEquipmentClick(equipment: Equipment)
    }

}