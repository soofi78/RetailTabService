package com.lfsolutions.retail.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.CategoryItemBinding
import com.lfsolutions.retail.model.CategoryItem

class ProductCategoryAdapter(
    private val categories: ArrayList<CategoryItem>,
    private val onCategoryItemClicked: OnCategoryItemClicked
) :
    RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder>() {

    private var lastCheckedButton: ToggleButton? = null

    class ViewHolder(val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item.textOn = categories[position].name
        holder.binding.item.textOff = categories[position].name
        holder.binding.item.text = categories[position].name
        holder.binding.item.tag = categories[position]
        holder.binding.item.isChecked = categories[position].isSelected
        if (position == 0 && categories[0].isSelected) {
            lastCheckedButton = holder.binding.item
        }
        holder.binding.item.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                lastCheckedButton?.isChecked = false
                lastCheckedButton?.let {
                    (it.tag as CategoryItem).isSelected = false
                }
                (buttonView.tag as CategoryItem).isSelected = true
                lastCheckedButton = buttonView as ToggleButton?
                onCategoryItemClicked.onCategoryItemClicked(buttonView.tag as CategoryItem)
                try {
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

interface OnCategoryItemClicked {
    fun onCategoryItemClicked(categoryItem: CategoryItem)
}