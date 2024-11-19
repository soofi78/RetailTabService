package com.lfsolutions.retail.ui.documents.history

interface HistoryItemInterface {
    fun getTitle(): String
    fun getDescription(): String
    fun getAmount(): String
    fun getId(): Int {
        return -1
    }

    fun getSerializedNumber(): String {
        return ""
    }
}