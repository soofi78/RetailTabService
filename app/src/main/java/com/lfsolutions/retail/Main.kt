package com.lfsolutions.retail

import android.app.Application

class Main : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
    }


    companion object {
        lateinit var app: Main
    }
}