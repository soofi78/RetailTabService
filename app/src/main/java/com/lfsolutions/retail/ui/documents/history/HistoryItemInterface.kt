package com.lfsolutions.retail.ui.documents.history

interface HistoryItemInterface {
    fun getTitle(): String
    fun getDescription(): String
    fun getAmount(): String
    fun getSerializedNumber(): String {
        return ""
    }
}