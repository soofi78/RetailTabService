package com.lfsolutions.retail.ui.widgets.theme

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ThemeItemBinding
import com.lfsolutions.retail.ui.theme.RetailThemes
import com.lfsolutions.retail.util.setDebouncedClickListener

class ThemeAdapter(val options: ArrayList<RetailThemes>, val onThemeSelected: OnThemeSelected) :
    RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {

    var lastChecked: ViewHolder? = null

    class ViewHolder(val binding: ThemeItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ThemeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = options[position]
        holder.itemView.tag = theme
        val binding = holder.binding
        binding.colors.visibility = if (theme.selected) View.VISIBLE else View.GONE
        if (theme.selected) lastChecked = holder
        binding.name.text = theme.themeName
        binding.primary.setCardBackgroundColor(Color.parseColor(theme.primary))
        binding.secondary.setCardBackgroundColor(Color.parseColor(theme.secondary))
        binding.tertiary.setCardBackgroundColor(Color.parseColor(theme.tertiary))
        binding.parent.setDebouncedClickListener { _ ->
            binding.colors.visibility = View.VISIBLE
            lastChecked?.let {
                it.binding.colors.visibility = View.GONE
                (it.itemView.tag as RetailThemes).selected = false
            }
            lastChecked = holder
            onThemeSelected.onThemeSelected(holder.itemView.tag as RetailThemes)
        }
    }
}
