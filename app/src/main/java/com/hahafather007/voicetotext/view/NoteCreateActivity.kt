package com.hahafather007.voicetotext.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.EXTRA_TITLE
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.nfc.NfcAdapter.EXTRA_ID
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.common.RxController
import com.hahafather007.voicetotext.databinding.ActivityNoteCreateBinding
import com.hahafather007.voicetotext.databinding.DialogChooseShareBinding
import com.hahafather007.voicetotext.utils.*
import com.hahafather007.voicetotext.utils.DialogUtil.showLoadingDialog
import com.hahafather007.voicetotext.utils.MusicUtil.continueMusic
import com.hahafather007.voicetotext.utils.MusicUtil.pauseMusic
import com.hahafather007.voicetotext.utils.MusicUtil.playMusic
import com.hahafather007.voicetotext.utils.MusicUtil.stopMusic
import com.hahafather007.voicetotext.viewmodel.NoteCreateViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_note_create.*
import java.io.File
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import com.tbruyelle.rxpermissions2.RxPermissions


class NoteCreateActivity : AppCompatActivity(), RxController {
    override val rxComposite = CompositeDisposable()

    private lateinit var binding: ActivityNoteCreateBinding
    //录音是否正在播放的状态
    private var recordPlaying: Boolean = false
    //录音是否播放过了
    private var hasPlayed: Boolean = false
    private var hasSave = true
    private val viewModel = NoteCreateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_create)
        binding.activity = this
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        addChangeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_create_menu, menu)
        val title = intent.getStringExtra(EXTRA_TITLE)

        if (!title.isNullOrEmpty()) {
            supportActionBar?.title = title
            menu?.getItem(3)?.isVisible = false
        } else {//如果是查看之前的note，则显示分享按钮，否者不显示
            (0..2).map {
                menu?.getItem(it)?.isVisible = false
            }
        }

        menu?.getItem(0)?.isVisible = false

        viewModel.initNote(intent.getLongExtra(EXTRA_ID, -1))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.nav_share -> showShareView()
            R.id.nav_save -> saveNote()
            R.id.nav_play -> {
                playOrPauseMusic()
                if (!recordPlaying) {
                    item.setIcon(R.mipmap.ic_menu_pause)
                } else {
                    item.setIcon(R.mipmap.ic_menu_play)
                }
                recordPlaying = !recordPlaying
            }
            R.id.nav_voice -> {
                item.isVisible = false

                startOrStopRecord()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.onCleared()
        onCleared()
//        stopMusic()

    }

    override fun onBackPressed() {
        if (viewModel.recording.get()) {
            DialogUtil.showDialog(this, R.string.text_diaolg_stop_recording,
                    R.string.text_cancel, R.string.text_enter,
                    null, DialogInterface.OnClickListener { _, _ ->
                viewModel.cancelRecord()
                hasSave = true

                super.onBackPressed()
            })
        } else {
            //如果修改的内容保存了就直接退出，否则提醒
            if (hasSave) {
                stopMusic()

                super.onBackPressed()
            } else {
                DialogUtil.showDialog(this, R.string.text_give_up_save,
                        R.string.text_cancel, R.string.text_enter, null,
                        DialogInterface.OnClickListener { _, _ ->
                            stopMusic()

                            super.onBackPressed()
                        })
            }
        }
    }

    private fun saveNote() {
        val editText = EditText(this)
        val padding = DimensionUtil.dp2px(this, 24f)
        if (!intent.getStringExtra(EXTRA_TITLE).isNullOrEmpty()) {
            editText.setText(viewModel.getNoteTitle())
        }
        editText.setHint(R.string.text_title_save_hint)
        editText.setHintTextColor(Color.argb(0x66, 0x00, 0x00, 0x00))

        DialogUtil.showViewDialog(this, R.string.text_title_save, editText,
                R.string.text_cancel, R.string.text_enter, null,
                DialogInterface.OnClickListener { _, _ ->
                    if (editText.text.toString().isEmpty()) {
                        ToastUtil.showToast(this, R.string.text_no_title)
                    } else {
                        if (!intent.getStringExtra(EXTRA_TITLE).isNullOrEmpty()) {
                            viewModel.setNoteTitle(editText.text.toString())
                            viewModel.saveNote()
                        } else {
                            viewModel.addNote(editText.text.toString(),
                                    viewModel.noteText.get() ?: "")
                        }
                    }
                })

        //让View与title对齐
        (editText.layoutParams as ViewGroup.MarginLayoutParams).setMargins(padding, 0, padding, 0)
    }

    private fun showShareView() {
        val intent = Intent(Intent.ACTION_SEND)

        val shareBinding = DataBindingUtil.inflate<DialogChooseShareBinding>(LayoutInflater.from(this),
                R.layout.dialog_choose_share, null, false)

        DialogUtil.showViewDialog(this, R.string.title_dialog, shareBinding.root,
                R.string.text_cancel, R.string.text_enter, null,
                DialogInterface.OnClickListener { _, _ ->
                    //分享文字
                    if (shareBinding.radioText.isChecked) {
                        intent.putExtra(Intent.EXTRA_TEXT, viewModel.noteText.get())
                        intent.type = "text/plain"
                    } else if (shareBinding.radioFile.isChecked) {
                        if (viewModel.fileName.get().isNullOrEmpty()) return@OnClickListener

                        intent.putExtra(Intent.EXTRA_STREAM,
                                Uri.fromFile(File(viewModel.fileName.get())))
                        intent.type = MimeUtil.getMimeType(viewModel.fileName.get())
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        //android7.0以上
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            val builder = StrictMode.VmPolicy.Builder()
                            StrictMode.setVmPolicy(builder.build())
                        }
                    }//分享录音文件

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()

                        ToastUtil.showToast(this, R.string.text_fail_to_share)
                    }
                })
    }

    private fun addChangeListener() {
        viewModel.saveOver
                .disposable(this)
                .doOnNext {
                    supportActionBar?.title = viewModel.getNoteTitle()
                    intent.putExtra(EXTRA_TITLE, viewModel.getNoteTitle())
                    ToastUtil.showToast(this, R.string.text_save_over)

                    hasSave = true
                }
                .subscribe()

        //实时更新音频图的振幅
        RxField.of(viewModel.volume)
                .disposable(this)
                .skip(0)
                .doOnNext { binding.waveView.updateAmplitude(it / 30f) }
                .subscribe()

        RxField.ofNonNull(viewModel.fileName)
                .disposable(this)
                .filter { it.isNotEmpty() }
                .filter { File(it).exists() }
                .doOnNext { binding.toolbar.menu.findItem(R.id.nav_play).isVisible = true }
                .subscribe()

        RxField.of(viewModel.loading)
                .disposable(this)
                .doOnNext { showLoadingDialog(this, it) }
                .subscribe()

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            var firstChange = !intent.getStringExtra(EXTRA_TITLE).isNullOrEmpty()
            override fun afterTextChanged(p0: Editable?) {
                if (!firstChange) {
                    hasSave = false
                }

                firstChange = false
            }
        })
    }

    private fun playOrPauseMusic() {
        if (!recordPlaying) {
            if (hasPlayed) {
                continueMusic()
            } else {
                playMusic(viewModel.fileName.get(), object : MusicUtil.MediaListener {
                    override fun error() {
                        ToastUtil.showToast(this@NoteCreateActivity, R.string.test_network_error)

                        hasPlayed = false
                    }

                    override fun complete() {
                        stopMusic()

                        binding.toolbar.menu.findItem(R.id.nav_play).setIcon(R.mipmap.ic_menu_play)
                        recordPlaying = false

                        hasPlayed = false
                    }
                }, rxComposite)

                hasPlayed = true
            }
        } else {
            pauseMusic()
        }
    }

    fun startOrStopRecord() {
        if (!viewModel.recording.get()) {
            RxPermissions(this)
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO)
                    .doOnNext {
                        if (it) {
                            viewModel.startRecord()

                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
                            binding.editText.clearFocus()

                            DialogUtil.showDialog(this, R.string.text_keep_screen_on, null,
                                    R.string.text_enter, null, null)
                        } else {
                            ToastUtil.showToast(this, "请检查是否授予了录音和读写权限！")
                        }
                    }
                    .subscribe()
        } else {
            DialogUtil.showDialog(this, R.string.text_stop_recording,
                    R.string.text_cancel, R.string.text_enter, null, DialogInterface.OnClickListener { _, _ ->
                viewModel.stopRecord()
                (0..2).map {
                    binding.toolbar.menu.getItem(it).isVisible = true
                }

                if (viewModel.noteText.get().isNullOrEmpty()) {
                    ToastUtil.showToast(this, R.string.text_say_nothing)
                }
            })
        }
    }

    companion object {
        fun intentOfNote(context: Context, id: Long, title: String): Intent {
            val intent = Intent(context, NoteCreateActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_TITLE, title)
            return intent
        }
    }
}