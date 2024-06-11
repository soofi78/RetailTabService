package com.lfsolutions.retail.ui.agreementmemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AgreementMemoBottomNavigationViewModel : ViewModel() {

    private val _isOrderCompleted: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    val isOrderCompleted get() = _isOrderCompleted


    fun setOrderCompleted(isOrderCompleted: Boolean) {

        _isOrderCompleted.postValue(isOrderCompleted)

    }

}