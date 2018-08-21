package com.hahafather007.voicetotext.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.support.v7.app.AlertDialog
import android.view.View

@Suppress("DEPRECATION")
object DialogUtil {
    private var dialog: AlertDialog? = null
    private var loadingDialog: ProgressDialog? = null

    //显示两个按钮都有的dialog
    //cancelText表示"取消"按键的文字，可为空
    //enterText同理
    @JvmStatic
    fun showDialog(context: Context, msg: Int, cancelText: Int?, enterText: Int?,
                   cancelListener: OnClickListener?, enterListener: OnClickListener?) {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("提示")
                .setMessage(msg)

        show(builder, cancelText, enterText, cancelListener, enterListener)
    }

    //带title需要重新定义的dialog
    @JvmStatic
    fun showDialog(context: Context, title: String, msg: String, cancelText: Int?, enterText: Int?,
                   cancelListener: OnClickListener?, enterListener: OnClickListener?) {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
                .setMessage(msg)

        show(builder, cancelText, enterText, cancelListener, enterListener)
    }

    //带有自定义View的dialog
    @JvmStatic
    fun showViewDialog(context: Context, title: Int, view: View, cancelText: Int?, enterText: Int?,
                       cancelListener: OnClickListener?, enterListener: OnClickListener?) {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
                .setView(view)

        show(builder, cancelText, enterText, cancelListener, enterListener)
    }

    private fun show(builder: AlertDialog.Builder, cancelText: Int?, enterText: Int?,
                     cancelListener: OnClickListener?, enterListener: OnClickListener?) {
        if (cancelText != null) {
            builder.setNegativeButton(cancelText, cancelListener)
        }
        if (enterText != null) {
            builder.setPositiveButton(enterText, enterListener)
        }

        dialog = builder.create()

        dialog?.show()
    }

    //加载处理画面的dialog
    @JvmStatic
    fun showLoadingDialog(context: Context, loading: Boolean) {
        loadingDialog?.dismiss()

        if (loading) {
            loadingDialog = ProgressDialog(context)
            loadingDialog?.setCancelable(false)
            loadingDialog?.setMessage("处理中...")

            loadingDialog?.show()
        }
    }
}