package com.hahafather007.voicetotext.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager

class VoiceApp : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        FlowManager.init(FlowConfig.builder(this).build())
    }

    override fun onTerminate() {
        super.onTerminate()

        FlowManager.destroy()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
    }
}