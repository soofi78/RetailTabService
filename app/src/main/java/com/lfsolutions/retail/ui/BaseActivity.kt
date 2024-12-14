package com.lfsolutions.retail.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lfsolutions.retail.BuildConfig
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.ui.login.ProfileActivity
import com.lfsolutions.retail.ui.settings.printer.PrinterSettingsActivity
import com.lfsolutions.retail.ui.theme.RetailThemes
import com.lfsolutions.retail.ui.theme.recreateSmoothly
import com.lfsolutions.retail.ui.widgets.theme.OnThemeSelected
import com.lfsolutions.retail.ui.widgets.theme.ThemeSelectionSheet
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Dialogs
import com.lfsolutions.retail.util.OnOptionDialogItemClicked
import com.videotel.digital.util.Notify


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

    var optionsClick = View.OnClickListener {
        Dialogs.optionsDialog(context = this@BaseActivity,
            options = arrayOf(
                Constants.ViewProfile,
                Constants.PrinterSettings,
                Constants.AppTheme,
                Constants.Version,
                Constants.Logout,
            ),
            onOptionDialogItemClicked = object :
                OnOptionDialogItemClicked {
                override fun onClick(option: String) {
                    when (option) {
                        Constants.Logout -> Main.app.sessionExpired()
                        Constants.ViewProfile -> openProfile()
                        Constants.PrinterSettings -> selectPrinter()
                        Constants.Version -> Notify.toastLong("Version: " + BuildConfig.VERSION_NAME + " / " + BuildConfig.VERSION_CODE)
                        Constants.AppTheme -> ThemeSelectionSheet.show(supportFragmentManager,
                            object :
                                OnThemeSelected {
                                override fun onThemeSelected(theme: RetailThemes) {
                                    AppSession.put(Constants.AppTheme, theme.themeName)
                                    recreateSmoothly()
                                    finishAffinity()
                                }
                            })
                    }
                }
            })
    }

    private fun selectPrinter() {
        startActivity(Intent(this, PrinterSettingsActivity::class.java))
    }

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}