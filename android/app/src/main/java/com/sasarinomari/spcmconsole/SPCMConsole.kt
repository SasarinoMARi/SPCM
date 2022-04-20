package com.sasarinomari.spcmconsole

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.lang.Exception
import java.net.URL

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

        fun downloadUrlToBitmap(url: String, callback: (Bitmap)-> Unit) {
            object: Thread(){
                override fun run() {
                    try {
                        val bitmap = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
                        callback(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }
    }
}