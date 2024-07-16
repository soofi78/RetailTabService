package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.SerialNumberItemBinding
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface

class SerialNumberAdapter(private val serialNumbers: ArrayList<MultiSelectModelInterface>) :
    RecyclerView.Adapter<SerialNumberAdapter.ViewHolder>() {

    class ViewHolder(val binding: SerialNumberItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        SerialNumberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = serialNumbers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.serialNumberText.setText(serialNumbers.get(position).getText())
    }

}