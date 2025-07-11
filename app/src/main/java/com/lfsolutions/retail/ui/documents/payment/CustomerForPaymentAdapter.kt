package com.lfsolutions.retail.ui.documents.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemDeliveryBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.makeTextBold
import com.lfsolutions.retail.util.setDebouncedClickListener

class CustomerForPaymentAdapter(var customers: List<Customer>? = ArrayList()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    class SimpleCustomerHolder(val binding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        SimpleCustomerHolder(
            ItemDeliveryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val customer = customers?.get(position)
        val binding = (holder as SimpleCustomerHolder).binding
        setUergentToVisitData(binding, customer)
    }

    private fun setUergentToVisitData(binding: ItemDeliveryBinding, customer: Customer?) {
        binding.txtGroup.text =
            makeTextBold(
                text = binding.txtGroup.context.getString(
                    R.string.prefix_group,
                    customer?.group
                ), startIndex = 8
            )
        binding.txtName.text = customer?.name
        binding.txtAddress1.text = customer?.address1
        binding.txtAddress2.text = customer?.address2
        binding.txtAddress3.text = customer?.address3
        binding.txtAddress2.visibility = if (customer?.address2.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.txtAddress3.visibility = if (customer?.address3.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.txtAccountNo.text =
            makeTextBold(
                text = binding.txtAccountNo.context.getString(
                    R.string.prefix_account_no,
                    customer?.customerCode
                ), startIndex = 8
            )

        binding.txtArea.text =
            makeTextBold(
                text = binding.txtArea.context.getString(
                    R.string.prefix_balance,
                    customer?.balanceAmount?.formatDecimalSeparator()
                ),
                startIndex = 9
            )

        binding.root.tag = customer
        binding.root.setDebouncedClickListener {
            mListener?.onItemClick(it.tag as Customer)
        }
    }

    override fun getItemCount(): Int = customers?.size ?: 0
    fun setListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(customer: Customer)
    }
}