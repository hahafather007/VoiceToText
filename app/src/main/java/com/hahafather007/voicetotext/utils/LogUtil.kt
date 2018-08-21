package com.hahafather007.voicetotext.utils

import android.util.Log

private const val tag = "===========>"

fun Any?.log() {
    Log.i(tag, if (this == null) "null" else "$this")
}

fun Any?.logError() {
    Log.e(tag, if (this == null) "null" else "$this")
}