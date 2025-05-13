package com.lfsolutions.retail.ui.documents.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.SaleOrderInvoiceListItemBinding
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.sale.order.SaleOrderListItem
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener

class HistoryListAdapter(
    private val items: ArrayList<HistoryItemInterface>,
    private val mListener: OnItemClickedListener,
    private val onChecked: OnCheckedChangeListener? = null,
    private val clone: Boolean = false,
    private val serialNumber: Boolean = false,
) : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {


    class ViewHolder(val binding: SaleOrderInvoiceListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        SaleOrderInvoiceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.tag = item
        holder.binding.clone.tag = item
        holder.binding.title.text = item.getTitle()
        holder.binding.description.text = item.getDescription()
        holder.binding.amount.text = item.getAmount()
        //set minimum qty
        if (item is Product){
            holder.binding.minimumQty.visibility=View.VISIBLE
            holder.binding.minimumQty.text=item.getMinQty()
        }else{
            holder.binding.minimumQty.visibility=View.GONE
        }

        if (item is SaleOrderListItem) {
            holder.binding.check.visibility = View.VISIBLE
        } else {
            holder.binding.check.visibility = View.GONE
        }
        if (item is SaleOrderListItem && item.isStockTransfer == true) {
            holder.binding.check.visibility = View.GONE
            holder.binding.parentss.setBackgroundColor(holder.binding.parentss.resources.getColor(R.color.light_red))
        } else {
            holder.binding.parentss.setBackgroundColor(holder.binding.parentss.resources.getColor(R.color.white))
        }
        holder.binding.image.visibility =
            if (item.getImageUrl().isEmpty()) View.GONE else View.VISIBLE
        Glide.with(holder.itemView).load(item.getImageUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(holder.binding.image)
        holder.binding.parent.tag = item
        holder.binding.parent.setDebouncedClickListener {
            mListener.onItemClickedListener(it.tag as HistoryItemInterface)
        }
        holder.binding.check.tag = item
        holder.binding.check.setOnCheckedChangeListener(onChecked)
        holder.binding.clone.visibility = if (clone) View.VISIBLE else View.GONE
        holder.binding.clone.setDebouncedClickListener {
            mListener.onCloneClicked(it.tag as HistoryItemInterface)
        }

        if (serialNumber && item.isSerialEquipment()) {
            holder.binding.description.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_serial_number, 0
            )

            holder.binding.description.setDebouncedClickListener {
                mListener.onShowSerialNumberClick(item)
            }
        } else {
            holder.binding.description.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            holder.binding.description.setDebouncedClickListener {}
        }
    }


    interface OnItemClickedListener {
        fun onItemClickedListener(item: HistoryItemInterface)
        fun onCloneClicked(item: HistoryItemInterface) {}
        fun onShowSerialNumberClick(item: HistoryItemInterface) {}
    }
}