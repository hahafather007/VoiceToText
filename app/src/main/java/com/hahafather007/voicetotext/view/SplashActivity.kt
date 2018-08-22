package com.hahafather007.voicetotext.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.model.pref.VoicePref
import com.hahafather007.voicetotext.utils.asyncSwitch
import com.hahafather007.voicetotext.utils.disposable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class SplashActivity : Activity(), RxController {
    override val rxComposite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //去除标题栏
        requestWindowFeature(FEATURE_NO_TITLE)
        //去除状态栏
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .asyncSwitch()
                .disposable(this)
                .map {
                    if (VoicePref.isFirst)
                        GuideActivity::class.java
                    else
                        AutoJumpActivity::class.java
                }
                .doOnNext { startActivity(Intent(this, it)) }
                .subscribe()

    }

    override fun onDestroy() {
        super.onDestroy()

        onCleared()
    }
}
