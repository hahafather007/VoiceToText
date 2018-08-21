package com.hahafather007.voicetotext.model.db

import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.model.db.table.Note
import com.hahafather007.voicetotext.utils.asyncSwitch
import com.hahafather007.voicetotext.utils.disposable
import com.hahafather007.voicetotext.utils.log
import com.raizlabs.android.dbflow.sql.language.Select
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.joda.time.LocalDateTime

//语音笔记Note的管理仓库
class NotesHolder private constructor() : RxController {
    override val rxComposite = CompositeDisposable()

    //用于缓存之前查找的结果
    private var cacheNotes = emptyList<Note>()

    //增删改后通知相关联界面进行数据刷新
    val statusChange: Subject<List<Note>> = PublishSubject.create()
    //note被编辑的回调
    val noteEdited: Subject<Note> = PublishSubject.create()
    //note增加的回调
    val noteAdded: Subject<Note> = PublishSubject.create()

    fun addNoteAuto(title: String, content: String, file: String): Completable {
        val time = LocalDateTime.now().toString()
        val note = Note()
        note.content = content
        note.time = time
        note.recordFile = file
        note.title = title

        return Completable.fromAction {
            note.save()
        }.doOnComplete {
            refreshNotes(RefreshType.ADDED, note)

            "Note表增加成功：Title=$title,Content=$content".log()
        }
    }

    fun getNotes(): Single<List<Note>> {
        return if (cacheNotes.isEmpty())
            Single.just(Select().from(Note::class.java).queryList())
                    .map {
                        it.sortByDescending { it.time }
                        it
                    }
                    .doOnSuccess { cacheNotes = it }
        else
            Single.just(cacheNotes)
    }

    fun getNote(id: Long): Single<Note> {
        return Single.just(cacheNotes)
                .map { v -> v.forEach { if (it.id == id) return@map it } }
                .map { it as Note }
    }

    fun deleteNote(note: Note): Completable {
        return Completable.fromAction { note.delete() }
                .doOnComplete {
                    refreshNotes()
                    "Note表删除成功：Title=${note.title},Content=${note.content}".log()
                }

    }

    fun editNote(note: Note): Completable {
        note.time = LocalDateTime.now().toString()

        return Completable.fromAction { note.update() }
                .doOnComplete {
                    refreshNotes(RefreshType.EDITED, note)

                    "Note表编辑成功：Title=${note.title},Content=${note.content}".log()
                }
    }

    private fun refreshNotes(type: RefreshType? = null, note: Note? = null) {
        Single.just(Select().from(Note::class.java).queryList())
                .map {
                    it.sortByDescending { it.time }
                    it
                }
                .asyncSwitch()
                .disposable(this)
                .subscribe { v ->
                    cacheNotes = v
                    statusChange.onNext(v)

                    when (type) {
                        RefreshType.ADDED -> noteAdded.onNext(note ?: Note())
                        RefreshType.EDITED -> noteEdited.onNext(note ?: Note())
                    }
                }
    }

    enum class RefreshType {
        ADDED,
        EDITED
    }

    object Factory {
        val INSTANCE = NotesHolder()
    }
}