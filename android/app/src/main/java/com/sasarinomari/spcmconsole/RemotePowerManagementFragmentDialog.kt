package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_remote_power_management.view.*
import kotlinx.android.synthetic.main.fragment_volume.view.*

class RemotePowerManagementFragmentDialog(private val api: APICall) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_remote_power_management, container)

        rootView.button_power_on.setOnClickListener {
            api.wakeup {
                Toast.makeText(context, getString(R.string.Confirm_Wakeup), Toast.LENGTH_LONG).show()
            }
            this.dismiss()
        }

        rootView.button_power_off.setOnClickListener {
            api.shutdown {
                Toast.makeText(context, getString(R.string.Confirm_Shutdown), Toast.LENGTH_LONG).show()
            }
            this.dismiss()
        }

        this.dialog?.setTitle("Remote Power Management")

        return rootView
    }
}