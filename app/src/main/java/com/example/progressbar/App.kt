package com.example.progressbar

import android.app.Application
import com.example.progressbar.datastore.DataStoreManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        DataStoreManager.init(appContext)
    }
    companion object{
        @JvmStatic
        lateinit var appContext: App
            private set // Prevents external modification
    }
}