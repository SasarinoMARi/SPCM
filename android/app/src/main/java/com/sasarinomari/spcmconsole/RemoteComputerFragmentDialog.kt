package com.sasarinomari.spcmconsole

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.network.APIClient
import kotlinx.android.synthetic.main.fragment_remote_computer_panel.view.*

class RemoteComputerFragmentDialog(private val api: APIClient) : DialogFragment() {
    var callbackAfterCreateView: (()->Unit)? = null
    fun afterCreateView(callback: ()->Unit) : RemoteComputerFragmentDialog {
        callbackAfterCreateView = callback
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_remote_computer_panel, container)

        rootView.button_power_on.setOnClickListener {
            api.wakeup {
                Toast.makeText(requireContext(), getString(R.string.Confirm_Wakeup), Toast.LENGTH_LONG).show()
            }
            this.dismiss()
        }

        rootView.button_power_off.setOnClickListener {
            api.shutdown {
                Toast.makeText(requireContext(), getString(R.string.Confirm_Shutdown), Toast.LENGTH_LONG).show()
            }
            this.dismiss()
        }

        rootView.seek_volume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, amount: Int, p2: Boolean) { if(amount%4==0) api.setVolume(amount) }
            override fun onStartTrackingTouch(seekbar: SeekBar?) { }
            override fun onStopTrackingTouch(seekbar: SeekBar?) { seekbar.let { api.setVolume(it!!.progress) } }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackAfterCreateView?.let { it() }
    }


    fun getServerStateChangeHandler(): ServerStateChangeHandler {
        return object: ServerStateChangeHandler{
            override fun onChangedServerState(online: Boolean) {

            }

            override fun onChangedComputerState(online: Boolean) {
                if(online) {
                    view?.button_power_off?.children?.first()?.background = null
                    // view?.button_power_off?.isEnabled = true

                    view?.container_volume?.background = null
                    // view?.seek_volume?.isEnabled = true
                    // view?.button_mute?.isEnabled = true

                    view?.button_advanced_control?.children?.first()?.background = null
                    // view?.button_advanced_control?.isEnabled = true
                } else {
                    view?.button_power_off?.children?.first()?.setBackgroundColor(context!!.getColor(R.color.button_disabled))
                    // view?.button_power_off?.isEnabled = false

                    view?.container_volume?.setBackgroundColor(context!!.getColor(R.color.button_disabled))
                    // view?.seek_volume?.isEnabled = false
                    // view?.button_mute?.isEnabled = false

                    view?.button_advanced_control?.children?.first()?.setBackgroundColor(context!!.getColor(R.color.button_disabled))
                    // view?.button_advanced_control?.isEnabled = false
                }
            }
        }
    }
}