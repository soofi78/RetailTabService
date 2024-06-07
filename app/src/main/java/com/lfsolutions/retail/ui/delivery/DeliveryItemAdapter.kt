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

class DeliveryItemAdapter(private val size: Int) :
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

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.txtGroup.text =
            makeTextBold(
                text = holder.itemView.context.getString(R.string.prefix_group, "BKE"),
                startIndex = 8
            )

        holder.binding.txtAccountNo.text =
            makeTextBold(
                text = holder.itemView.context.getString(
                    R.string.prefix_account_no,
                    "AZ0001"
                ), startIndex = 8
            )

        holder.binding.txtArea.text =
            makeTextBold(
                text = holder.itemView.context.getString(R.string.prefix_area, "W"),
                startIndex = 7
            )

        holder.binding.root.setOnClickListener { mListener?.onItemClick() }

    }

    private fun makeTextBold(
        text: String,
        startIndex: Int
    ): SpannableStringBuilder =
        SpannableStringBuilder(text).let { spannable ->

            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable

        }

    fun setListener(listener: OnItemClickListener){

        mListener = listener

    }

    interface OnItemClickListener {

        fun onItemClick()

    }

}