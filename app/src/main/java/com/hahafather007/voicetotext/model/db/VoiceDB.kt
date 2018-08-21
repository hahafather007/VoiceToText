package com.hahafather007.voicetotext.model.db

import com.hahafather007.voicetotext.model.db.VoiceDB.NAME
import com.hahafather007.voicetotext.model.db.VoiceDB.VERSION
import com.raizlabs.android.dbflow.annotation.Database

/**
 * 本应用主要的数据库
 */
@Database(name = NAME, version = VERSION)
object VoiceDB {
    const val NAME = "voiceDataBase"
    const val VERSION = 1
}