package com.hahafather007.voicetotext.model

import android.os.Bundle
import android.os.Environment
import cafe.adriel.androidaudioconverter.AndroidAudioConverter
import cafe.adriel.androidaudioconverter.callback.IConvertCallback
import cafe.adriel.androidaudioconverter.model.AudioFormat
import com.hahafather007.voicetotext.app.VoiceApp
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.utils.*
import com.iflytek.cloud.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class VoiceHolder : RxController {
    override val rxComposite = CompositeDisposable()

    /**
     * 用于记录当前说活时长
     */
    private var speakTime = 0
    /**
     * 标记当前是否真正说话
     */
    private var speaking = false
    /**
     * 调用startRecording的次数
     */
    private var times = 0
    /**
     * 转换后的文件名
     */
    private var fileName = ""

    /**
     * 每句话识别的结果
     */
    val resultText: Subject<String> = PublishSubject.create()
    /**
     * 音量大小
     */
    val volume: Subject<Int> = PublishSubject.create()
    val loading: Subject<Boolean> = PublishSubject.create()

    private val mAsr = SpeechRecognizer.createRecognizer(VoiceApp.appContext, null)
    /**
     * 识别监听器
     */
    private var listener: RecognizerListener

    init {
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat")
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin")
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")

        listener = object : RecognizerListener {
            //data表示音频数据
            //音量值0~30
            override fun onVolumeChanged(vol: Int, data: ByteArray?) {
                volume.onNext(vol)

//                Log.i("音量：$vol")
            }

            override fun onResult(result: RecognizerResult?, isLast: Boolean) {
                val text = SpeechJsonParser.parseGrammarResult(result?.resultString)

                if (!text.isNullOrEmpty()) {
                    resultText.onNext(text)
                }

                if (speaking && speakTime >= 40) {
                    mAsr.stopListening()

                    val cacheFile = File("${Environment.getExternalStorageDirectory()}" +
                            "/VoiceToText/录音/缓存/${times - 1}.wav")

                    Observable.interval(16, TimeUnit.MILLISECONDS)
                            .filter { cacheFile.exists() }
                            .disposable(this@VoiceHolder)
                            .computeSwitch()
                            .doOnNext {
                                speakTime = 0

                                if (speaking) {
                                    startRecording()
                                }

                                rxComposite.clear()
                            }
                            .subscribe()
                }
            }

            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            override fun onBeginOfSpeech() {
                "我准备好了".log()
            }

            override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            }

            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入（最大只能识别60秒）
            override fun onEndOfSpeech() {
                "识别时间到".log()

                continueRecording()
            }

            override fun onError(error: SpeechError?) {
                "识别出错：${error?.errorCode}--->${error?.errorDescription}".logError()

                continueRecording()
            }
        }
    }

    fun startRecording() {
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory().toString() + "/VoiceToText/录音/缓存/${times++}.wav")

        speaking = true

        mAsr.startListening(listener)

        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .disposable(this)
                .computeSwitch()
                .doOnNext { speakTime++ }
                .subscribe()
    }

    /**
     * 停止识别，有返回结果
     */
    fun stopRecording() {
        speaking = false

        mAsr.stopListening()

        loading.onNext(true)

        val cacheFile = File("${Environment.getExternalStorageDirectory()}" +
                "/VoiceToText/录音/缓存/${times - 1}.wav")

        Observable.interval(16, TimeUnit.MILLISECONDS)
                .filter { cacheFile.exists() }
                .flatMap { Observable.just(decodeFile()) }
                .disposable(this)
                .computeSwitch()
                .doOnNext {
                    times = 0

                    wavToMp3()
                }
                .doOnError {

                    fileName = ""
                }
                .doFinally { rxComposite.clear() }
                .subscribe()
    }

    fun getFileName() = fileName

    /**
     * 将录音数据合并
     */
    private fun decodeFile() {
        val files = (0 until times)
                .map {
                    File("${Environment.getExternalStorageDirectory()}" +
                            "/VoiceToText/录音/缓存/$it.wav")
                }

        fileName = "${Environment.getExternalStorageDirectory()}" +
                "/VoiceToText/录音/${System.currentTimeMillis()}.wav"

        //合并文件的名字为设备号+系统当前时间
        try {
            WavMergeUtil.mergeWav(files, File(fileName))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        //删除缓存文件
        FileDeleteUtil.deleteDirectory("${Environment.getExternalStorageDirectory()}" +
                "/VoiceToText/录音/缓存/")
    }

    private fun wavToMp3() {
        val callback = object : IConvertCallback {
            override fun onSuccess(convertedFile: File?) {
                //删除缓存文件
                FileDeleteUtil.deleteFile(fileName)
                fileName = convertedFile?.absolutePath ?: ""

                "转换成功：$fileName".log()

                loading.onNext(false)
            }

            override fun onFailure(e: Exception?) {
                e?.printStackTrace()

                loading.onNext(false)
            }
        }
        AndroidAudioConverter.with(VoiceApp.appContext)
                .setFile(File(fileName))
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert()
    }

    /**
     * 取消识别，无返回结果
     */
    private fun cancelRecording() {
        mAsr.cancel()

        speaking = false
    }

    private fun continueRecording() {
        if (speaking) {
            mAsr.stopListening()

            val cacheFile = File("${Environment.getExternalStorageDirectory()}" +
                    "/VoiceToText/录音/缓存/${times - 1}.wav")

            Observable.interval(16, TimeUnit.MILLISECONDS)
                    .filter { cacheFile.exists() }
                    .disposable(this)
                    .computeSwitch()
                    .doOnNext {
                        speakTime = 0

                        if (speaking) {
                            startRecording()
                        }

                        rxComposite.clear()
                    }
                    .subscribe()
        }
    }


    override fun onCleared() {
        super.onCleared()

        cancelRecording()
        mAsr.destroy()
    }
}