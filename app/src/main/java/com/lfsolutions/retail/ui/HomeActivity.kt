package com.lfsolutions.retail.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View.OnClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.BuildConfig
import com.lfsolutions.retail.databinding.ActivityHomeBinding
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.ui.login.ProfileActivity
import com.lfsolutions.retail.ui.theme.RetailThemes
import com.lfsolutions.retail.ui.theme.recreateSmoothly
import com.lfsolutions.retail.ui.widgets.theme.OnThemeSelected
import com.lfsolutions.retail.ui.widgets.theme.ThemeSelectionSheet
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Dialogs
import com.lfsolutions.retail.util.OnOptionDialogItemClicked
import com.videotel.digital.util.Notify

class HomeActivity : BaseActivity() {

    private var optionsClick = OnClickListener {
        Dialogs.optionsDialog(context = this@HomeActivity,
            options = arrayOf(
                Constants.Logout,
                Constants.ViewProfile,
                Constants.AppTheme,
                Constants.Version
            ),
            onOptionDialogItemClicked = object : OnOptionDialogItemClicked {
                override fun onClick(option: String) {
                    when (option) {
                        Constants.Logout -> Main.app.sessionExpired()
                        Constants.ViewProfile -> openProfile()
                        Constants.Version -> Notify.toastLong("Version: " + BuildConfig.VERSION_NAME + " / " + BuildConfig.VERSION_CODE)
                        Constants.AppTheme -> ThemeSelectionSheet.show(supportFragmentManager,
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

    private fun openProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private var _binding: ActivityHomeBinding? = null

    private val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        val navView: BottomNavigationView = mBinding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        navView.setupWithNavController(navController)
        Main.app.getSession().isSupervisor?.let {
            navView.menu.findItem(R.id.navigation_schedule)
                .setVisible(it)
        }
        setData()
        setClickListener()

    }

    private fun setClickListener() {
        mBinding.icoAccount.setOnClickListener(optionsClick)
        mBinding.detailsFlow.setOnClickListener(optionsClick)
    }

    private fun setData() {
        val userSession =
            Gson().fromJson(AppSession[Constants.SESSION], UserSession::class.java)
        mBinding.txtName.text = userSession.userName
        mBinding.txtLocation.text = makeTextBold(
            text = getString(R.string.prefix_location, userSession.locationCode.toString()),
            startIndex = 10
        )
        mBinding.txtVehicleNo.text = makeTextBold(
            text = getString(R.string.prefix_vehicle_no, "SBC 1234"), startIndex = 12
        )
    }

    private fun makeTextBold(
        text: String, startIndex: Int
    ): SpannableStringBuilder = SpannableStringBuilder(text).let { spannable ->

        spannable.setSpan(
            StyleSpan(Typeface.BOLD), startIndex, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable

    }

}