package com.sasarinomari.spcmconsole

import android.app.AlertDialog
import android.content.Context

class SPCMConsole {
    companion object {
        fun confirm(context: Context, text: String, action: () -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(text)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes) { d, id ->
                    action()
                }
                .setNegativeButton(android.R.string.no) { d, id ->
                    d.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }
}