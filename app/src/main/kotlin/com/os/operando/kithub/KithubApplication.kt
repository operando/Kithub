package com.os.operando.kithub

import android.app.Application
import com.facebook.stetho.Stetho
import timber.log.Timber

class KithubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
    }
}