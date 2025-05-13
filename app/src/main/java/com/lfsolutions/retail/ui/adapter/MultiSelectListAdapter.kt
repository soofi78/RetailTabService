package com.lfsolutions.retail.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.MultiSelectRecyclerItemBinding
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface

class MultiSelectListAdapter(private val serialNumbers: ArrayList<MultiSelectModelInterface>) :
    RecyclerView.Adapter<MultiSelectListAdapter.ViewHolder>() {

    class ViewHolder(val binding: MultiSelectRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        MultiSelectRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = serialNumbers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serialNumberText.setText(serialNumbers[position].getText())
    }

}