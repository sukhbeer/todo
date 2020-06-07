package com.github.sukhbeer.todo

import android.content.Context
import android.support.v7.app.AlertDialog

fun showDialog(context: Context, title: String, message: String) {
    AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create().show()
}
