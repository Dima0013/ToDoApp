package com.altun.dimasnotebook

import android.app.Application
import android.content.Context

class App:Application() {

    companion object{
        lateinit var instance:App
        var context:Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
    }
}