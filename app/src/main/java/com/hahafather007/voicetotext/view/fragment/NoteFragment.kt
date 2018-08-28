package com.hahafather007.voicetotext.view.fragment

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.databinding.FragmentNoteBinding
import com.hahafather007.voicetotext.databinding.ItemVoiceNoteBinding
import com.hahafather007.voicetotext.model.db.table.Note
import com.hahafather007.voicetotext.utils.DialogUtil
import com.hahafather007.voicetotext.utils.ToastUtil.showToast
import com.hahafather007.voicetotext.utils.disposable
import com.hahafather007.voicetotext.view.activity.NoteCreateActivity
import com.hahafather007.voicetotext.viewmodel.NoteViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable

class NoteFragment : Fragment(), RxController {
    override val rxComposite = CompositeDisposable()

    private val viewModel = NoteViewModel()
    private lateinit var binding: FragmentNoteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)!!
        binding.fragment = this
        binding.viewModel = viewModel

        addChangeListener()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.onCleared()
        onCleared()
    }

    private fun addChangeListener() {
        viewModel.deleteOver
                .disposable(this)
                .doOnNext { showToast(context, "已删除！！！") }
                .subscribe()
    }

    fun onBindItem(binding: ViewDataBinding, data: Any, position: Int) {
        val noteBinding = binding as ItemVoiceNoteBinding
        noteBinding.fragment = this
    }

    fun openNote(id: Long, title: String) {
        startActivity(NoteCreateActivity.intentOfNote(context!!, id, title))
    }

    fun readyDelete(note: Note) {
        DialogUtil.showDialog(context!!, R.string.text_ask_delete,
                R.string.text_cancel, R.string.text_enter, null,
                DialogInterface.OnClickListener { _, _ -> viewModel.deleteNote(note) })
    }

    fun newsNote() {
        RxPermissions(activity!!)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .doOnNext {
                    startActivity(Intent(context, NoteCreateActivity::class.java))
                }
                .subscribe()
    }
}
