package com.example.campusvibe

import android.app.Application
import com.example.campusvibe.util.RemoteConfig

class CampusVibeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RemoteConfig.initialize()
    }
}
