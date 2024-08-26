package com.lfsolutions.retail.ui.documents.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.CategoryItemBinding
import com.lfsolutions.retail.databinding.HistoryItemBinding

class HistoryTypeAdapter(
    private val types: ArrayList<HistoryType>,
    private val onHistoryTypeClicked: OnHistoryTypeClicked
) :
    RecyclerView.Adapter<HistoryTypeAdapter.ViewHolder>() {

    var lastSelectedIndex = -1

    init {
        types.forEach {
            if (it.type == HistoryType.Order.type) {
                it.selected = true
                lastSelectedIndex = 0
            } else {
                it.selected = false
            }
        }
    }

    class ViewHolder(val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = types.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (types[position].selected) {
            holder.binding.item.setBackgroundResource(R.drawable.rounded_corner_orange_background)
        } else {
            holder.binding.item.setBackgroundResource(R.drawable.rounded_corner_dark_grey)
        }

        holder.binding.item.text = types[position].type
        holder.binding.item.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(
                holder.binding.item.context,
                types[position].icon
            ), null, null, null
        )
        holder.binding.item.tag = types[position]
        holder.binding.item.setOnClickListener { buttonView ->
            (buttonView.tag as HistoryType).selected = true
            if (lastSelectedIndex > -1 && lastSelectedIndex != position)
                types[lastSelectedIndex].selected = false
            lastSelectedIndex = position
            onHistoryTypeClicked.onHistoryTypeClicked(buttonView.tag as HistoryType)
            try {
                notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSelectedHistoryItem() {

    }
}

interface OnHistoryTypeClicked {
    fun onHistoryTypeClicked(type: HistoryType)
}