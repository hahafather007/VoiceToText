package com.hahafather007.voicetotext.model.pref

import com.chibatching.kotpref.KotprefModel

object VoicePref : KotprefModel() {
    override val kotprefName = "voice_pref"

    /**
     * 是否第一次启动
     */
    var isFirst: Boolean by booleanPref(default = true)
}