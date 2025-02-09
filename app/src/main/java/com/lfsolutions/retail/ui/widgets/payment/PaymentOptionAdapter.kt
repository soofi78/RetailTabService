package com.lfsolutions.retail.ui.widgets.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.ItemPaymentBinding
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.util.setDebouncedClickListener

class PaymentOptionAdapter(
    private val options: ArrayList<PaymentType>,
    private val onPaymentOptionSelected: OnPaymentOptionSelected
) : RecyclerView.Adapter<PaymentOptionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder = ViewHolder(
        ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtName.text = options[position].displayText
        holder.binding.root.tag = options[position]
        holder.binding.root.setDebouncedClickListener {
            onPaymentOptionSelected.onPaymentOptionSelected(it.tag as PaymentType)
        }
    }

}