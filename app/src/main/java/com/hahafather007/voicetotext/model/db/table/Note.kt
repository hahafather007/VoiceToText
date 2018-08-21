package com.hahafather007.voicetotext.model.db.table

import com.hahafather007.voicetotext.model.db.VoiceDB
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * 语音笔记的表
 */
@Table(database = VoiceDB::class)

class Note : BaseModel() {
    @Column
    @PrimaryKey(autoincrement = true)
    var id: Long = 0
    @Column
    var title: String = ""
    @Column
    var content: String = ""
    @Column
    var time: String = ""
    @Column
    var recordFile: String = ""
}