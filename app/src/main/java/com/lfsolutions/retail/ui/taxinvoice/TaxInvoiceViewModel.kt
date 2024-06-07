package com.lfsolutions.retail.ui.taxinvoice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaxInvoiceViewModel : ViewModel() {

    private val _isSignOn : MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    val isSignOn get() = _isSignOn

    fun toggleSignaturePad(isSignOn : Boolean){

        _isSignOn.postValue(isSignOn)

    }

}