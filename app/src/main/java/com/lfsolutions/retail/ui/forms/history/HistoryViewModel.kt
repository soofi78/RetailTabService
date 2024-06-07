package com.lfsolutions.retail.ui.forms.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lfsolutions.retail.ui.forms.FormType

class HistoryViewModel : ViewModel() {

    private val _formTypeLiveData: MutableLiveData<List<FormType>> by lazy { MutableLiveData() }
    val formTypeLiveData get() = _formTypeLiveData

    fun getData() {

        _formTypeLiveData.postValue(
            listOf(
                FormType.AgreementMemo,
                FormType.ServiceForm,
                FormType.ServiceForm
            )
        )

    }
}