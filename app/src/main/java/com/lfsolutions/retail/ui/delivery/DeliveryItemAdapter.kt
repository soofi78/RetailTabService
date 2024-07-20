package com.lfsolutions.retail.ui.delivery

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemDeliveryBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.makeTextBold

class DeliveryItemAdapter(var customers: ArrayList<Customer>? = ArrayList()) :

    RecyclerView.Adapter<DeliveryItemAdapter.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    class ViewHolder(val binding: ItemDeliveryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            ItemDeliveryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = customers?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = customers?.get(position)
        holder.binding.txtGroup.text =
            makeTextBold(
                text = holder.itemView.context.getString(R.string.prefix_group, customer?.group),
                startIndex = 8
            )
        holder.binding.txtName.text = customer?.name
        holder.binding.txtAddress.text = customer?.address1
        holder.binding.txtAccountNo.text =
            makeTextBold(
                text = holder.itemView.context.getString(
                    R.string.prefix_account_no,
                    customer?.customerCode
                ), startIndex = 8
            )

        holder.binding.txtArea.text =
            makeTextBold(
                text = holder.itemView.context.getString(R.string.prefix_area, customer?.area),
                startIndex = 7
            )

        holder.binding.root.tag = customer
        holder.binding.root.setOnClickListener {
            mListener?.onItemClick(it.tag as Customer)
        }
    }

    fun setListener(listener: OnItemClickListener) {

        mListener = listener

    }

    interface OnItemClickListener {

        fun onItemClick(customer: Customer)

    }

}