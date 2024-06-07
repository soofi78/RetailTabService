package com.lfsolutions.retail.ui.documents

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lfsolutions.retail.R
import com.lfsolutions.retail.model.Models

class DocumentsViewModel : ViewModel() {

    private val _documentList: MutableLiveData<List<DocumentType>> by lazy { MutableLiveData() }
    val documentList get() = _documentList

    fun loadDocuments() {

        _documentList.postValue(
            listOf(
                DocumentType.OutGoingStockRecord(
                    R.string.label_outgoing_stock_record,
                    R.drawable.outgoing_stock_record
                ),
                DocumentType.DailySalesRecord(
                    R.string.label_daily_sales_record,
                    R.drawable.daily_sales_record
                ),
                DocumentType.DriverMemo(
                    R.string.label_driver_memo,
                    R.drawable.driver_memo
                )
            )
        )


    }

}