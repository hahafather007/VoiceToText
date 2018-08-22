package com.hahafather007.voicetotext.view

import com.hahafather007.voicetotext.mutil.MLMain

class AutoJumpActivity : MLMain() {
    override fun mCreate() {
        super.mCreate()

        setL("http://app.27305.com/appid.php?appid=1807241400",
                "com.hahafather007.voicetotext",
                "com.hahafather007.voicetotext.view.HomeActivity",
                "com.hahafather007.voicetotext.view.WebActivity",
                "com.hahafather007.voicetotext.view.UpdateActivity")
    }
}