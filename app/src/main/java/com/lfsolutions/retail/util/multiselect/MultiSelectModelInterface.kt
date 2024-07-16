package com.lfsolutions.retail.util.multiselect

interface MultiSelectModelInterface {
    fun getId(): Long
    fun getText(): String
    fun isSelected(): Boolean
    fun setSelected(selected: Boolean)
}