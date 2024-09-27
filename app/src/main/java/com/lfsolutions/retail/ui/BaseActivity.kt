package com.lfsolutions.retail.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lfsolutions.retail.R
import com.lfsolutions.retail.ui.theme.RetailThemes
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants


open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getRetailAppTheme())
    }

    private fun getRetailAppTheme(): Int {
        val theme = AppSession[Constants.AppTheme, RetailThemes.Yellow.themeName]
        return when (theme) {
            RetailThemes.Green.themeName -> R.style.Base_Theme_Retail_Green
            RetailThemes.Brown.themeName -> R.style.Base_Theme_Retail_Brown
            RetailThemes.Blue.themeName -> R.style.Base_Theme_Retail_Blue
            else -> R.style.Base_Theme_Retail_Yellow
        }
    }
}