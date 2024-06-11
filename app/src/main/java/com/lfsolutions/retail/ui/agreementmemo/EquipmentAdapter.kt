package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemEquipmentBinding

class EquipmentAdapter : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    private var mListener: OnEquipmentClickListener? = null

    class ViewHolder(val binding: ItemEquipmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemEquipmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {

            mListener?.onEquipmentClick()

        }

    }

    fun setListener(listener: OnEquipmentClickListener) {

        mListener = listener

    }

    interface OnEquipmentClickListener {

        fun onEquipmentClick()

    }

}