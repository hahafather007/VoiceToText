package com.hahafather007.voicetotext.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.annimon.stream.Optional
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.model.db.NotesHolder
import com.hahafather007.voicetotext.model.db.table.Note
import com.hahafather007.voicetotext.utils.FileDeleteUtil
import com.hahafather007.voicetotext.utils.asyncSwitch
import com.hahafather007.voicetotext.utils.disposable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File

class NoteViewModel : RxController {
    override val rxComposite = CompositeDisposable()

    val notes = ObservableArrayList<Note>()
    val deleteOver = PublishSubject.create<Optional<*>>()

    private val notesHolder = NotesHolder.Factory.INSTANCE

    init {
        notesHolder.statusChange
                .disposable(this)
                .doOnNext {
                    notes.clear()
                    notes.addAll(it)
                }
                .subscribe()

        getNotes()
    }

    fun deleteNote(note: Note) {
        notesHolder.deleteNote(note)
                .asyncSwitch()
                .disposable(this)
                .doOnComplete {
                    if (File(note.recordFile).exists()) {
                        FileDeleteUtil.deleteFile(note.recordFile)
                    }

                    deleteOver.onNext(Optional.empty<Any>())
                }
                .subscribe()
    }

    private fun getNotes() {
        notesHolder.getNotes()
                .asyncSwitch()
                .disposable(this)
                .doOnSuccess { notes.addAll(it) }
                .subscribe()
    }

    override fun onCleared() {
        super.onCleared()

        notesHolder.onCleared()
    }
}
