package com.project.tex.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.project.tex.R

class MsgUtils(
    val context: Context, var msgType: MSG_TYPE = MSG_TYPE.SNACKBAR, var view: View? = null
) : MsgImpl() {
    private val toast by lazy { ToastUtils(context) }
    private val snackbar by lazy { SnackbarUtils(context, view!!) }

    override fun showShortMsg(msg: String) =
        if (msgType == MSG_TYPE.TOAST) toast.showShortMsg(msg) else snackbar.showShortMsg(msg)

    override fun showLongMsg(msg: String) =
        if (msgType == MSG_TYPE.TOAST) toast.showLongMsg(msg) else snackbar.showLongMsg(msg)

    override fun showIndefinteMsg(msg: String, btnText: String?, listener: View.OnClickListener?) =
        if (msgType == MSG_TYPE.TOAST) toast.showShortMsg(msg) else snackbar.showIndefinteMsg(
            msg, btnText, listener
        )

    fun updateSnackbarView(uView: View) {
        this.view = uView
    }
}

internal class ToastUtils(val context: Context) : MsgImpl() {
    override fun showShortMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showLongMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    override fun showIndefinteMsg(msg: String, btnText: String?, listener: View.OnClickListener?) {
        showLongMsg(msg)
    }
}

internal class SnackbarUtils(val context: Context, val view: View) : MsgImpl() {
    override fun showShortMsg(msg: String) {
        Snackbar.make(context, view, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun showLongMsg(msg: String) {
        Snackbar.make(context, view, msg, Snackbar.LENGTH_LONG).show()
    }

    override fun showIndefinteMsg(msg: String, btnText: String?, listener: View.OnClickListener?) {
        Snackbar.make(context, view, msg, Snackbar.LENGTH_INDEFINITE)
            .setAction(btnText ?: context.getString(R.string.ok), listener).show()
    }
}

enum class MSG_TYPE {
    SNACKBAR, TOAST
}

abstract class MsgImpl {
    abstract fun showShortMsg(msg: String)
    abstract fun showLongMsg(msg: String)
    abstract fun showIndefinteMsg(
        msg: String, btnText: String? = null, listener: View.OnClickListener? = null
    )
}