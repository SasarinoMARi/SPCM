package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.sasarinomari.spcmconsole.Memoboard.CreateTaskFragmentDialog
import kotlinx.android.synthetic.main.fragment_memoboard_panel.view.*

class MemoboardPanelFragmentDialog(
    private val api: APICall,
    private val supportFragmentManager: FragmentManager
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_memoboard_panel, container)

        rootView.button_open_memoboard.setOnClickListener {
            val launchIntent =
                context?.packageManager?.getLaunchIntentForPackage("com.sasarinomari.memoboard")
            launchIntent?.let { startActivity(it) }
            this.dismiss()
        }

        rootView.button_create_task.setOnClickListener {
            CreateTaskFragmentDialog(api).show(supportFragmentManager, "Create Task")
        }

        this.dialog?.setTitle("Memoboard Panel")

        return rootView
    }
}