package com.lfsolutions.retail.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.lfsolutions.retail.databinding.HeadersBinding
import com.lfsolutions.retail.databinding.SaleRecordItemViewBinding
import com.lfsolutions.retail.model.dailysale.DailySalesItem
import com.lfsolutions.retail.ui.documents.dailysale.SaleRecordInvoiceItemsAdapter

class SaleRecordItemView : LinearLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    var binding: SaleRecordItemViewBinding =
        SaleRecordItemViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(item: DailySalesItem) {
        binding.saleRecordName.text = item.paymentTerm.toString()
        this.tag = item
        binding.records.adapter = SaleRecordInvoiceItemsAdapter(item.dailySalesInvoices)
    }


}