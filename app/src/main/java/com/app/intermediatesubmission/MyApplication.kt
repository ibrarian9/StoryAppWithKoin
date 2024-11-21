package com.app.intermediatesubmission

import android.app.Application
import com.app.intermediatesubmission.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        //start koin
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}