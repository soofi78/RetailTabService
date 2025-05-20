package com.lfsolutions.retail.ui.documents.history

interface HistoryItemInterface {
    fun getTitle(): String
    fun getDescription(): String
    fun getAmount(): String

    fun getAppliedAmount(): String{
        return ""
    }

    fun getMinQty(): String {
        return ""
    }

    fun getVarianceQty(): String{
        return ""
    }

    fun getId(): Int {
        return -1
    }

    fun isSerialEquipment(): Boolean {
        return false
    }

    fun getSerializedNumber(): String {
        return ""
    }

    fun getImageUrl(): String {
        return ""
    }
}