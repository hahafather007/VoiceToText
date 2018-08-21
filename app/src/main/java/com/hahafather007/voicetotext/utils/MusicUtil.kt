package com.hahafather007.voicetotext.utils

import android.media.AudioManager
import android.media.MediaPlayer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

object MusicUtil {
    private val player = MediaPlayer()

    @JvmStatic
    fun playMusic(url: String?, disposable: CompositeDisposable) {
        playMusic(url, null, disposable)
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun playMusic(url: String?, listener: MediaListener?, disposable: CompositeDisposable) {
        if (player.isPlaying) {
            player.stop()
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)

        //异步进行音乐播放，以免阻塞线程
        Observable.just(url)
                .map {
                    player.reset()
                    player.setDataSource(it)
                    player.setOnCompletionListener { listener?.complete() }
                    player.setOnErrorListener { _, _, _ ->
                        listener?.error()
                        false
                    }
                    player.prepare()
                    player
                }
                .asyncSwitch()
                .subscribe(MediaPlayer::start)
    }

    @JvmStatic
    fun stopMusic() {//停止播放
        player.stop()
    }

    @JvmStatic
    fun pauseMusic() {//暂停播放
        player.pause()
    }

    @JvmStatic
    fun continueMusic() {//继续播放
        player.start()
    }

    interface MediaListener {
        fun error()

        fun complete()
    }
}