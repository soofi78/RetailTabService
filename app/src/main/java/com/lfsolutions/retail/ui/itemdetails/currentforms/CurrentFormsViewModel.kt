package com.lfsolutions.retail.ui.itemdetails.currentforms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lfsolutions.retail.ui.itemdetails.FormType

class CurrentFormsViewModel : ViewModel() {

    private val _formTypeLiveData: MutableLiveData<List<FormType>> by lazy { MutableLiveData() }
    val formTypeLiveData get() = _formTypeLiveData

    fun getData() {

        _formTypeLiveData.postValue(
            listOf(
                FormType.AgreementMemo,
                FormType.ServiceForm,
                FormType.InvoiceForm
            )
        )

    }
}