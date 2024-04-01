package com.lulin.wmcamera

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.lulin.wmcamera.inject.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
class WMApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        private const val TAG = "WMCameraApp"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startKoin {
            androidLogger()
            androidContext(this@WMApplication)
            modules(AppModules)
        }
    }
}