package com.sasarinomari.spcmconsole

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_raspberry_server_panel.view.*

class RaspberryServerFragmentDialog(private val api: APICall) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_raspberry_server_panel, container)

        rootView.button_reboot.setOnClickListener {
            SPCMConsole.confirm(context!!, getString(R.string.Confirm_PiReboot)) { api.reboot_pi() }
            this.dismiss()
        }
        rootView.button_open_logs.setOnClickListener {
            val intent = Intent(context, LogViewActivity::class.java)
            startActivity(intent)
        }

        this.dialog?.setTitle("Server Management")

        return rootView
    }
}