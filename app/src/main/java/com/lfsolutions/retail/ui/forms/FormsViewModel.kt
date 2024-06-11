package com.lfsolutions.retail.ui.forms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FormsViewModel : ViewModel() {

    private val _displayEquipmentOrderView: MutableLiveData<Pair<Boolean, Boolean>> by lazy { MutableLiveData() }
    val displayEquipmentOrderView get() = _displayEquipmentOrderView

    fun displayEquipmentOrderView(show: Boolean, isEquipment: Boolean = true) {

        _displayEquipmentOrderView.postValue(Pair(show, isEquipment))

    }
}