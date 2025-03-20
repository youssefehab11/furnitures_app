package com.example.furnitures_app

import android.app.Application
import android.content.Context

class ARApplication: Application() {
    init {
        application = this
    }
    companion object{
        private lateinit var application: ARApplication

        fun getApplicationContext(): Context {
            return application.applicationContext
        }
    }
}