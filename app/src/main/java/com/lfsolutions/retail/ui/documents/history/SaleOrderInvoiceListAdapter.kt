package com.lfsolutions.retail.ui.documents.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.SaleOrderInvoiceListItemBinding

class SaleOrderInvoiceListAdapter(
    private val items: ArrayList<SaleOrderInvoiceItem>,
    private val mListener: OnItemClickedListener
) :
    RecyclerView.Adapter<SaleOrderInvoiceListAdapter.ViewHolder>() {


    class ViewHolder(val binding: SaleOrderInvoiceListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        SaleOrderInvoiceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.itemView.tag = item
        holder.binding.title.text = item.getTitle()
        holder.binding.description.text = item.getDescription()
        holder.binding.amount.text = item.getAmount()
        holder.itemView.setOnClickListener {
            mListener.onItemClickedListener(it.tag as SaleOrderInvoiceItem)
        }
    }


    interface OnItemClickedListener {
        fun onItemClickedListener(saleOrderInvoiceItem: SaleOrderInvoiceItem)
    }
}