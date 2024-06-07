package com.lfsolutions.retail.ui.taxinvoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemProductBinding

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var mListener : OnProductSelectListener? = null

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.root.setOnClickListener {

            mListener?.onProductSelect()

        }

    }

    fun setListener(listener: OnProductSelectListener){

        mListener = listener

    }

    interface OnProductSelectListener{

        fun onProductSelect()

    }
}