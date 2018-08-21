package com.hahafather007.voicetotext.view

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.databinding.ActivityHomeBinding
import com.hahafather007.voicetotext.databinding.ItemVoiceNoteBinding

import com.hahafather007.voicetotext.model.db.table.Note
import com.hahafather007.voicetotext.utils.DialogUtil
import com.hahafather007.voicetotext.utils.ToastUtil.showToast
import com.hahafather007.voicetotext.utils.disposable
import com.hahafather007.voicetotext.viewmodel.NoteViewModel
import io.reactivex.disposables.CompositeDisposable

class HomeActivity : AppCompatActivity(), RxController {
    override val rxComposite = CompositeDisposable()

    private val viewModel = NoteViewModel()
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.activity = this
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
                .doOnNext { showToast(this, "已删除！！！") }
                .subscribe()
    }

    fun onBindItem(binding: ViewDataBinding, data: Any, position: Int) {
        val noteBinding = binding as ItemVoiceNoteBinding
        noteBinding.activity = this
    }

    fun openNote(id: Long, title: String) {


//            startActivity(NoteCreateActivity.intentOfNote(getContext(), id, title))
    }

    fun readyDelete(note: Note) {
        DialogUtil.showDialog(this, R.string.text_ask_delete,
                R.string.text_cancel, R.string.text_enter, null,
                DialogInterface.OnClickListener { _, _ -> viewModel.deleteNote(note) })
    }

    fun newsNote() {

//            startActivity(Intent(this, NoteCreateActivity::class.java))
    }
}
