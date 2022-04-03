package com.sasarinomari.spcmconsole

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_remote_computer_panel.view.*

class RemoteComputerFragmentDialog(private val api: APICall) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_remote_computer_panel, container)

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

        rootView.seek_volume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, amount: Int, p2: Boolean) { if(amount%4==0) api.volume(amount) }
            override fun onStartTrackingTouch(seekbar: SeekBar?) { }
            override fun onStopTrackingTouch(seekbar: SeekBar?) { seekbar.let { api.volume(it!!.progress) } }
        })
        rootView.button_mute.setOnClickListener {
            api.mute()
        }

        rootView.button_advanced_control.setOnClickListener {
            val i = Intent(context, RemoteComputerActivity::class.java)
            context?.startActivity(i)
            this.dismiss()
        }

        this.dialog?.setTitle("Remote Power Management")

        return rootView
    }
}