package com.sagernet.singbox

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SingBoxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any required components here
    }
}
