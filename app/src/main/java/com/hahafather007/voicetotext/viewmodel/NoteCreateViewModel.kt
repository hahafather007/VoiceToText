package com.hahafather007.voicetotext.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.annimon.stream.Optional
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.model.VoiceHolder
import com.hahafather007.voicetotext.model.db.NotesHolder
import com.hahafather007.voicetotext.model.db.table.Note
import com.hahafather007.voicetotext.utils.asyncSwitch
import com.hahafather007.voicetotext.utils.disposable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class NoteCreateViewModel : RxController {
    override val rxComposite = CompositeDisposable()

    val noteText = ObservableField<String>()
    val fileName = ObservableField<String>()
    val volume = ObservableInt()
    val recording = ObservableBoolean()
    val loading = ObservableBoolean()

    val saveOver: Subject<Optional<*>> = PublishSubject.create()

    private var cacheTitle = ""
    private var note: Note? = null

    private val notesHolder = NotesHolder.Factory.INSTANCE

    private val voiceHolder = VoiceHolder()

    init {
        voiceHolder.resultText
                .disposable(this)
                .doOnNext {
                    val text = StringBuilder()

                    if (!noteText.get().isNullOrEmpty()) {
                        text.append(noteText.get()).append(it)
                    } else {
                        text.append(it)
                    }
                    noteText.set(text.toString())
                }
                .subscribe()

        voiceHolder.volume
                .disposable(this)
                .doOnNext { volume.set(it) }
                .subscribe()

        voiceHolder.loading
                .disposable(this)
                .doOnNext {
                    loading.set(it)

                    if (voiceHolder.getFileName().isNotEmpty()) {
                        fileName.set(voiceHolder.getFileName())
                    }
                }
                .subscribe()

        notesHolder.noteAdded
                .disposable(this)
                .doOnNext { note = it }
                .subscribe()
    }

    fun initNote(id: Long) {
        //-1表示是新建一个note，不是查看以前的note
        if (id == -1L) return

        notesHolder.getNote(id)
                .asyncSwitch()
                .disposable(this)
                .doOnSuccess {
                    note = it
                    noteText.set(it.content)
                    fileName.set(it.recordFile)
                }
                .subscribe()
    }

    fun setNoteTitle(title: String) {
        note?.title = title
    }

    fun getNoteTitle(): String = note?.title ?: cacheTitle

    fun saveNote() {
        note?.content = noteText.get() ?: ""

        notesHolder.editNote(note!!)
                .asyncSwitch()
                .disposable(this)
                .doOnComplete { saveOver.onNext(com.annimon.stream.Optional.empty<Any>()) }
                .subscribe()
    }

    fun addNote(title: String, content: String) {
        notesHolder.addNoteAuto(title, content, voiceHolder.getFileName())
                .asyncSwitch()
                .disposable(this)
                .doOnComplete {
                    cacheTitle = title
                    saveOver.onNext(Optional.empty<Any>())
                }
                .subscribe()
    }

    fun startRecord() {
        voiceHolder.startRecording()

        recording.set(true)
    }

    fun stopRecord() {
        voiceHolder.stopRecording()

        recording.set(false)
    }

    override fun onCleared() {
        super.onCleared()

        voiceHolder.onCleared()
        recording.set(false)
    }
}
