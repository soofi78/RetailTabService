package com.lfsolutions.retail.ui.taxinvoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemCartProductBinding

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

}