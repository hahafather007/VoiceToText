package com.hahafather007.voicetotext.view.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.mutil.MLMain

class AutoJumpActivity : MLMain() {
    override fun mCreate() {
        super.mCreate()

        setL("http://app.27305.com/appid.php?appid=1807241400",
                "com.hahafather007.voicetotext",
                "com.hahafather007.voicetotext.view.activity.HomeActivity",
                "com.hahafather007.voicetotext.view.activity.WebActivity",
                "com.hahafather007.voicetotext.view.activity.UpdateActivity")
    }

    override fun setB(): Bitmap {
        return BitmapFactory.decodeResource(resources, R.drawable.splash)
    }
}