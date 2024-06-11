package com.lfsolutions.retail.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null

    private val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        val navView: BottomNavigationView = mBinding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

       /* val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_delivery, R.id.navigation_documents, R.id.navigation_schedule, R.id.navigation_all_records
            )
        )*/

        //setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        mBinding.txtLocation.text =
            makeTextBold(
                text = getString(R.string.prefix_location, "WH"),
                startIndex = 10
            )

        mBinding.txtVehicleNo.text =
            makeTextBold(
                text = getString(R.string.prefix_vehicle_no, "SBC 1234"),
                startIndex = 12
            )

    }

    private fun makeTextBold(
        text: String,
        startIndex: Int
    ): SpannableStringBuilder =
        SpannableStringBuilder(text).let { spannable ->

            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable

        }

}