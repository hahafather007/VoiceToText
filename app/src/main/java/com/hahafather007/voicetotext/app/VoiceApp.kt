package com.hahafather007.voicetotext.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cafe.adriel.androidaudioconverter.AndroidAudioConverter
import cafe.adriel.androidaudioconverter.callback.ILoadCallback
import com.chibatching.kotpref.Kotpref
import com.hahafather007.voicetotext.utils.log
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins

class VoiceApp : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        FlowManager.init(FlowConfig.builder(this).build())

        SpeechUtility.createUtility(this, "${SpeechConstant.APPID}=5a9e6792")

        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())

        AndroidAudioConverter.load(this, object : ILoadCallback {
            override fun onSuccess() {
                "load成功！！！".log()
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
            }
        })

        Kotpref.init(this)
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