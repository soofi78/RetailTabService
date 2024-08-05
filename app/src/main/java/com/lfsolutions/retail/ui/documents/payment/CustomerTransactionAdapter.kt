package com.lfsolutions.retail.ui.documents.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ItemSaleTransactionBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.CustomerSaleTransaction
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.makeTextBold

class CustomerTransactionAdapter(var transactions: List<CustomerSaleTransaction>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnTransactionCallback? = null

    class TransactionHolder(val binding: ItemSaleTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        TransactionHolder(
            ItemSaleTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = transactions?.get(position)
        val binding = (holder as TransactionHolder).binding
        transaction?.let { setData(binding, it) }
    }

    private fun setData(binding: ItemSaleTransactionBinding, transaction: CustomerSaleTransaction) {
        binding.applied.isChecked = transaction.applied == true
        binding.applied.setOnCheckedChangeListener { _, isChecked ->
            val transaction = binding.root.tag as CustomerSaleTransaction
            transaction.applied = isChecked
            mListener?.onTransactionSelected(binding.root.tag as CustomerSaleTransaction)
        }
        binding.transactionNumber.text = transaction.transactionNo
        binding.transactionDate.text = transaction.transactionDate
        binding.transactionAmount.text =
            Main.app.getSession().currencySymbol + transaction.amount?.formatDecimalSeparator()
        binding.appliedAmount.text =
            if (transaction.appliedAmount == null || transaction.appliedAmount == 0.0)
                binding.transactionAmount.text
            else
                Main.app.getSession().currencySymbol + transaction.appliedAmount?.formatDecimalSeparator()

        if (transaction.appliedAmount == null || transaction.appliedAmount == 0.0) {
            binding.editIcon.visibility = View.INVISIBLE
        } else {
            binding.editIcon.visibility = View.VISIBLE
        }
        binding.root.tag = transaction
        binding.root.setOnClickListener {
            mListener?.onAmountEdit(binding.root.tag as CustomerSaleTransaction)
        }
    }

    override fun getItemCount(): Int = transactions.size ?: 0
    fun setListener(listener: OnTransactionCallback) {
        mListener = listener
    }

    interface OnTransactionCallback {
        fun onAmountEdit(transaction: CustomerSaleTransaction)
        fun onTransactionSelected(transaction: CustomerSaleTransaction)
    }
}