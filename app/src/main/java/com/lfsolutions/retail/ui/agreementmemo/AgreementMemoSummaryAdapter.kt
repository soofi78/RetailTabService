package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.memo.AgreementMemoDetail
import com.lfsolutions.retail.util.disableQtyBox
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.lfsolutions.retail.util.toAddVisibility
import com.lfsolutions.retail.util.toSerialNumberAdapter

class AgreementMemoSummaryAdapter(val agreementMemoDetail: ArrayList<AgreementMemoDetail>?) :
    RecyclerView.Adapter<AgreementMemoSummaryAdapter.ViewHolder>() {

    private var mListener: OnOrderSummarySelectListener? = null

    class ViewHolder(val binding: ItemOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View {
            return binding.swipeAble
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = agreementMemoDetail?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtQty.text = agreementMemoDetail?.get(position)?.Qty.toString()
        holder.binding.txtPrice.text =
            Main.app.getSession().currencySymbol + agreementMemoDetail?.get(position)?.TotalCost?.formatDecimalSeparator()
        holder.binding.txtProductName.text = agreementMemoDetail?.get(position)?.ProductName
        holder.binding.txtTag.text = agreementMemoDetail?.get(position)?.AgreementTypeDisplayText

        val batchList = agreementMemoDetail?.get(position)?.ProductBatchList ?: emptyList()
        holder.binding.serialNoView.toAddVisibility(batchList)
        holder.binding.serialNumberRV.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.binding.serialNumberRV.adapter = batchList.toSerialNumberAdapter()
        batchList.disableQtyBox(
            holder.binding.txtQty,
            holder.itemView
        )

        holder.itemView.setDebouncedClickListener {
            mListener?.onOrderSummarySelect()
        }

        holder.binding.txtQty.setDebouncedClickListener {
            mListener?.onOrderSummarySelect()
        }
    }

    fun setListener(listener: OnOrderSummarySelectListener) {
        mListener = listener
    }

    fun remove(position: Int) {
        agreementMemoDetail?.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnOrderSummarySelectListener {
        fun onOrderSummarySelect()
    }

}