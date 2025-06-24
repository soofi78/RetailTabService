package com.lfsolutions.retail.util.multiselect

interface MultiSelectModelInterface {
    fun getId(): Long=0
    fun getText(): String
    fun isSelected(): Boolean=false
    fun setSelected(selected: Boolean)
}