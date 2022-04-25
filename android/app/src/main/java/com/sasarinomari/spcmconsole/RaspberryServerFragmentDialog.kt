package com.sasarinomari.spcmconsole

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_raspberry_server_panel.view.*

class RaspberryServerFragmentDialog(private val api: APICall) : DialogFragment() {
    var callbackAfterCreateView: (()->Unit)? = null
    fun afterCreateView(callback: ()->Unit) : RaspberryServerFragmentDialog {
        callbackAfterCreateView = callback
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_raspberry_server_panel, container)

        rootView.button_reboot.setOnClickListener {
            SPCMConsole.confirm(context!!, getString(R.string.Confirm_PiReboot)) { api.rebootServer() }
            this.dismiss()
        }
        rootView.button_open_logs.setOnClickListener {
            val intent = Intent(context, LogViewActivity::class.java)
            startActivity(intent)
        }
        rootView.button_reload_schedules.setOnClickListener {
            api.reloadSchedule {  }
        }

        this.dialog?.setTitle("Server Management")

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackAfterCreateView?.let { it() }
    }

    fun getServerStateChangeHandler(): ServerStateChangeHandler {
        return object: ServerStateChangeHandler{
            override fun onChangedServerState(online: Boolean) {
                if(online) {
                    view?.button_reboot?.children?.first()?.background = null
                    // view?.button_reboot?.isEnabled = true

                    view?.button_open_logs?.children?.first()?.background = null
                    // view?.button_open_logs?.isEnabled = true
                } else {
                    view?.button_reboot?.children?.first()?.setBackgroundColor(context!!.getColor(R.color.button_disabled))
                    // view?.button_reboot?.isEnabled = false

                    view?.button_open_logs?.children?.first()?.setBackgroundColor(context!!.getColor(R.color.button_disabled))
                    // view?.button_open_logs?.isEnabled = false
                }
            }

            override fun onChangedComputerState(online: Boolean) {

            }
        }
    }
}