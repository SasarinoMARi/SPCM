package com.sasarinomari.spcmconsole

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity(), APICall.lookupInterface {
    private val api = object : APICall() {
        override fun onError(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }

        override fun onMessage(message: String) {
            onError(message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        setContentView(R.layout.activity_main)


        button_wakeup.setOnClickListener {
            api.wakeup()
        }

        button_shutdown.setOnClickListener {
            api.shutdown()
        }

        button_more.setOnClickListener {
            val info = arrayOf<CharSequence>(
                getString(R.string.Sleep),
                getString(R.string.StartFileServer),
                getString(R.string.StopFileServer)
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.MoreContextTitle))
            builder.setItems(info) { dialog, which ->
                when (which) {
                    0 -> api.sleep()
                    1 -> api.start_fs()
                    2 -> api.stop_fs()
                }
                dialog.dismiss()
            }

            builder.show()

        }
    }

    private fun startStatusChecker() {
        Thread {
            api.lookup(this@MainActivity)
            Thread.sleep(5000)
            if(focused) startStatusChecker()
        }.start()
    }

    var focused = true
    override fun onResume() {
        super.onResume()
        setStatusToLoading()
        focused = true
        startStatusChecker()
    }

    override fun onPause() {
        super.onPause()
        focused = false
    }
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window ?: return
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    override fun onDead() {
        status_text.text = getString(R.string.Offline)
        val c = Color.parseColor("#EF3D56")
        status_text.setTextColor(c)
        status_icon.setColorFilter(c)
    }

    override fun onLive() {
        status_text.text = getString(R.string.Online)
        val c = Color.parseColor("#00A889")
        status_text.setTextColor(c)
        status_icon.setColorFilter(c)
    }

    fun setStatusToLoading() {
        status_text.text = getString(R.string.Loading)
        val c = Color.parseColor("#ffffff")
        status_text.setTextColor(c)
        status_icon.setColorFilter(c)
    }

}
