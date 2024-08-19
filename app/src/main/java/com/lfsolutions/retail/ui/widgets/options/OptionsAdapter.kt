package com.lfsolutions.retail.ui.widgets.options

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.OptionItemBinding
import com.lfsolutions.retail.model.Form
import java.lang.StackWalker.Option

class OptionsAdapter(val options: ArrayList<OptionItem>?, val optionItemClick: OnOptionItemClick) :
    RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    class ViewHolder(val binding: OptionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        OptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = options?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options?.get(position)
        option?.icon?.let { holder.binding.icon.setImageResource(it) }
        option?.title?.let { holder.binding.text.text = it }
        holder.itemView.tag = option
        holder.itemView.setOnClickListener { optionItemClick.onOptionItemClick(holder.itemView.tag as OptionItem) }
    }
}
