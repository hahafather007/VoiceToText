package com.hahafather007.voicetotext.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.utils.asyncSwitch
import com.hahafather007.voicetotext.utils.disposable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class SplashActivity : Activity(), RxController {
    override val rxComposite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .asyncSwitch()
                .disposable(this)
                .doOnNext {
                    startActivity(Intent(this, AutoJumpActivity::class.java))
                }
                .subscribe()

    }

    override fun onDestroy() {
        super.onDestroy()

        onCleared()
    }
}
