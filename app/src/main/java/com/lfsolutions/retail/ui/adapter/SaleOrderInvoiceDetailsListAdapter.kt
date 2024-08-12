package com.lfsolutions.retail.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.SaleOrderInvoiceProductListItemBinding
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface

class SaleOrderInvoiceDetailsListAdapter(
    private val items: ArrayList<HistoryItemInterface>,
    private val mListener: OnItemClickedListener
) :
    RecyclerView.Adapter<SaleOrderInvoiceDetailsListAdapter.ViewHolder>() {

    class ViewHolder(val binding: SaleOrderInvoiceProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        SaleOrderInvoiceProductListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.itemView.tag = item
        holder.binding.serial.text = item.getSerializedNumber()
        holder.binding.title.text = item.getTitle()
        holder.binding.description.text = item.getDescription()
        holder.binding.amount.text = item.getAmount()
        holder.itemView.setOnClickListener {
            mListener.onItemClickedListener(it.tag as HistoryItemInterface)
        }
    }


    interface OnItemClickedListener {
        fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface)
    }
}