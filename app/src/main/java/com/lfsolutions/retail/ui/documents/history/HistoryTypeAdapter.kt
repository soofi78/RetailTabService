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

    private var lastCheckedButton: ToggleButton? = null

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
        holder.binding.item.textOn = types[position].type
        holder.binding.item.textOff = types[position].type
        holder.binding.item.text = types[position].type
        holder.binding.item.setCompoundDrawables(
            ContextCompat.getDrawable(
                holder.binding.item.context,
                types[position].icon
            ), null, null, null
        )
        holder.binding.item.tag = types[position]
        holder.binding.item.isChecked = types[position].selected
        if (position == 0 && types[0].selected) {
            lastCheckedButton = holder.binding.item
        }
        holder.binding.item.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                lastCheckedButton?.isChecked = false
                lastCheckedButton?.let {
                    (it.tag as HistoryType).selected = false
                }
                (buttonView.tag as HistoryType).selected = true
                lastCheckedButton = buttonView as ToggleButton?
                onHistoryTypeClicked.onHistoryTypeClicked(buttonView.tag as HistoryType)
                try {
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getSelectedItem(): HistoryType {
        return if (lastCheckedButton == null) return HistoryType.Order else lastCheckedButton?.tag as HistoryType
    }
}

interface OnHistoryTypeClicked {
    fun onHistoryTypeClicked(type: HistoryType)
}