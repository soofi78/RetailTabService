package com.lfsolutions.retail.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lfsolutions.retail.BuildConfig
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.ui.login.ProfileViewSheet
import com.lfsolutions.retail.ui.settings.printer.PrinterSettingsSheet
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
        registerConnectivityCallback(this)
    }

    fun registerConnectivityCallback(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                println("Internet is connected")
                internetDialog(true)
            }

            override fun onLost(network: android.net.Network) {
                println("Internet is disconnected")
                internetDialog(false)
            }

            override fun onCapabilitiesChanged(
                network: android.net.Network, networkCapabilities: NetworkCapabilities
            ) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    println("Network capabilities changed: Internet available")
                    internetDialog(true)
                }
            }

            override fun onUnavailable() {
                println("No network available")
                internetDialog(false)
            }
        }

        // Register the callback
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun internetDialog(availale: Boolean) {
        Handler(Looper.getMainLooper()).post {
            if (dialog == null && this@BaseActivity.isDestroyed.not()) {
                val builder = AlertDialog.Builder(this@BaseActivity)
                builder.setTitle("Internet!")
                builder.setCancelable(false)
                builder.setMessage("Please check your internet connection.")
                builder.setPositiveButtonIcon(
                    ContextCompat.getDrawable(
                        this@BaseActivity, R.drawable.ic_settings_dialog
                    )
                )
                builder.setPositiveButton("Settings") { dialog, which ->
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    startActivity(intent)
                    dialog.dismiss()
                }
                builder.setNegativeButtonIcon(
                    ContextCompat.getDrawable(
                        this@BaseActivity, R.drawable.ic_cancel_dialog
                    )
                )
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                dialog = builder.create()
            }
            if (availale) {
                dialog?.dismiss()
                dialog = null
            } else {
                dialog?.show()
            }
        }
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
        Dialogs.optionsDialog(context = this@BaseActivity, options = arrayOf(
            Constants.ViewProfile,
            Constants.PrinterSettings,
            Constants.AppTheme,
            Constants.Version,
            Constants.Logout,
        ), onOptionDialogItemClicked = object : OnOptionDialogItemClicked {
            override fun onClick(option: String) {
                when (option) {
                    Constants.Logout -> Main.app.sessionExpired()
                    Constants.ViewProfile -> openProfile()
                    Constants.PrinterSettings -> selectPrinter()
                    Constants.Version -> Notify.toastLong("Version: " + BuildConfig.VERSION_NAME + " / " + BuildConfig.VERSION_CODE)
                    Constants.AppTheme -> ThemeSelectionSheet.show(
                        supportFragmentManager,
                        object : OnThemeSelected {
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

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun selectPrinter() {
        PrinterSettingsSheet.show(this)
    }

    private fun openProfile() {
        ProfileViewSheet.show(this)
    }

    companion object {
        var dialog: AlertDialog? = null
    }
}