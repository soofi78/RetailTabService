package com.lfsolutions.retail.ui.itemdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityItemDetailsBinding

class ItemDetails : AppCompatActivity() {

    private lateinit var mBinding: ActivityItemDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityItemDetailsBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        val navView: BottomNavigationView = mBinding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_item_details)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
       /* val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)*/
        navView.setupWithNavController(navController)

        addOnClickListener()

    }

    private fun addOnClickListener(){

        mBinding.flowBack.setOnClickListener { finish() }

    }

    companion object{

        fun getInstance(context: Context) : Intent = Intent(context, ItemDetails::class.java)

    }

}