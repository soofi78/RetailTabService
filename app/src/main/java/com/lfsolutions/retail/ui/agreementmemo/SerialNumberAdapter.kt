package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemSerialNumberBinding

class SerialNumberAdapter : RecyclerView.Adapter<SerialNumberAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemSerialNumberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemSerialNumberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

}