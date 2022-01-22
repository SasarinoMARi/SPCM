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
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity(), APICall.lookupInterface {
    private val api = object : APICall(this) {
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
            confirm(getString(R.string.pc_start)) { api.wakeup() }
        }

        button_shutdown.setOnClickListener {
            confirm(getString(R.string.pc_stop)) { api.shutdown() }
        }

        button_more.setOnClickListener {
            val popupMenu = PopupMenu(this@MainActivity, it)
            menuInflater.inflate(R.menu.menu_main_actions, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.action_fserver_start -> {
                        confirm(getString(R.string.fserver_start)) { api.start_fs() }
                    }
                    R.id.action_rdpserver_start -> {
                        confirm(getString(R.string.rdpserver_start)) { api.start_tv() }
                    }
                    R.id.action_raspi_reboot -> {
                        confirm(getString(R.string.raspi_reboot)) { api.reboot_pi() }
                    }
                    R.id.action_hetzer_start -> {
                        confirm(getString(R.string.hetzer_start)) {api.hetzer()}
                    }
                    else -> {

                    }
                }
                return@setOnMenuItemClickListener false
            }
            popupMenu.show()
        }
    }

    private fun confirm(text: String, action: ()-> Unit) {
        val builder = AlertDialog.Builder(this@MainActivity)
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
