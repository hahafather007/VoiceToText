package com.hahafather007.voicetotext.utils

import android.media.MediaMetadataRetriever


object MimeUtil {
    // 根据文件后缀名获得对应的MIME类型。
    @JvmStatic
    fun getMimeType(filePath: String?): String {
        val mmr = MediaMetadataRetriever()
        var mime = "*/*"
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath)
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return mime
    }
}