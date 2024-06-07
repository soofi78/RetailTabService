package com.lfsolutions.retail.ui.agreementmemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewAgreementMemoViewModel : ViewModel() {

    private val _isSignOn: MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    val isSignOn get() = _isSignOn

    fun toggleSignaturePad(isSignOn: Boolean) {

        _isSignOn.postValue(isSignOn)

    }

}