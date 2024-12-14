package com.lfsolutions.retail.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityHomeBinding
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants

class HomeActivity : BaseActivity() {

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