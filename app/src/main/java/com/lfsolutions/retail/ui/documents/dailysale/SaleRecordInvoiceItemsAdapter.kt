package com.lfsolutions.retail.ui.documents.dailysale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.CategoryItemBinding
import com.lfsolutions.retail.databinding.SaleRecordInvoiceItemBinding
import com.lfsolutions.retail.model.dailysale.DailySalesInvoices
import com.lfsolutions.retail.util.formatDecimalSeparator

class SaleRecordInvoiceItemsAdapter(
    private val invoices: ArrayList<DailySalesInvoices>,
) :
    RecyclerView.Adapter<SaleRecordInvoiceItemsAdapter.ViewHolder>() {


    class ViewHolder(val binding: SaleRecordInvoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        SaleRecordInvoiceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = invoices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = invoices.get(position)
        holder.binding.itemName.text = item.invoiceNo.toString()
        holder.binding.price.text =
            Main.app.getSession().currencySymbol + item.netTotal?.formatDecimalSeparator()
    }
}