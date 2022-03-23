package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_volume.view.*

class VolumeFragmentDialog(private val api: APICall) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_volume, container)

        rootView.seek_volume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, amount: Int, p2: Boolean) { if(amount%4==0) api.volume(amount) }
            override fun onStartTrackingTouch(seekbar: SeekBar?) { }
            override fun onStopTrackingTouch(seekbar: SeekBar?) { seekbar.let { api.volume(it!!.progress) } }
        })
        rootView.button_mute.setOnClickListener {
            api.mute()
        }

        this.dialog?.setTitle("Volume Control")

        return rootView
    }
}