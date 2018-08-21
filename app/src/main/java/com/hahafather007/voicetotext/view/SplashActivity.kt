package com.hahafather007.voicetotext.view

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable

class SplashActivity : Activity(), RxController {
    override val rxComposite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        RxPermissions(this)
                .request(WRITE_EXTERNAL_STORAGE,
                        RECORD_AUDIO)
                .doOnNext {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                .subscribe()

    }

    override fun onDestroy() {
        super.onDestroy()

        onCleared()
    }
}
