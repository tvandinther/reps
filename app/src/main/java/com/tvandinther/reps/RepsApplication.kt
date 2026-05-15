package com.tvandinther.reps

import android.app.Application
import com.tvandinther.reps.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RepsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RepsApplication)
            modules(appModule)
        }
    }
}
