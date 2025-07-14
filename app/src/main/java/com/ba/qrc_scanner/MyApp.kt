package com.ba.qrc_scanner

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        com.jakewharton.threetenabp.AndroidThreeTen.init(this)
    }
}