package com.lfsolutions.retail.ui.agreementmemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.ItemOrderSummaryBinding
import com.lfsolutions.retail.model.memo.AgreementMemoDetail
import com.lfsolutions.retail.util.formatDecimalSeparator
import kotlinx.coroutines.MainScope

class AgreementMemoSummaryAdapter(val agreementMemoDetail: ArrayList<AgreementMemoDetail>?) :
    RecyclerView.Adapter<AgreementMemoSummaryAdapter.ViewHolder>() {

    private var mListener: OnOrderSummarySelectListener? = null

    class ViewHolder(val binding: ItemOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getSwipableView(): View? {
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
        holder.binding.txtSerials.text = agreementMemoDetail?.get(position)?.getSerialNumbers()
        holder.binding.txtTag.text = agreementMemoDetail?.get(position)?.AgreementTypeDisplayText
        holder.itemView.setOnClickListener {
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