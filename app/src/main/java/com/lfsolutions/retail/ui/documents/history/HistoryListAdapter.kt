package com.lfsolutions.retail.ui.documents.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.databinding.SaleOrderInvoiceListItemBinding

class HistoryListAdapter(
    private val items: ArrayList<HistoryItemInterface>,
    private val mListener: OnItemClickedListener,
    private val clone: Boolean = false
) :
    RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {


    class ViewHolder(val binding: SaleOrderInvoiceListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        SaleOrderInvoiceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.itemView.tag = item
        holder.binding.clone.tag = item
        holder.binding.title.text = item.getTitle()
        holder.binding.description.text = item.getDescription()
        holder.binding.amount.text = item.getAmount()
        holder.itemView.setOnClickListener {
            mListener.onItemClickedListener(it.tag as HistoryItemInterface)
        }

        holder.binding.clone.visibility = if (clone) View.VISIBLE else View.GONE
        holder.binding.clone.setOnClickListener {
            mListener.onCloneClicked(it.tag as HistoryItemInterface)
        }
    }


    interface OnItemClickedListener {
        fun onItemClickedListener(item: HistoryItemInterface)
        fun onCloneClicked(item: HistoryItemInterface) {}
    }
}