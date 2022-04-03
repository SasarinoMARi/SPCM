package com.sasarinomari.spcmconsole.Memoboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.APICall
import com.sasarinomari.spcmconsole.R
import kotlinx.android.synthetic.main.fragment_create_task.view.*

class CreateTaskFragmentDialog(private val api: APICall) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_create_task, container)
        rootView.button_submit.setOnClickListener {
            val task = TaskModel()
            task.Name = rootView.text_name.text.toString()
            task.Description = rootView.text_description.text.toString()
            if(task.Name.isNullOrBlank()) return@setOnClickListener
            api.createTask(task) {
                Toast.makeText(context, getString(R.string.Task_Created), Toast.LENGTH_LONG).show()
                this@CreateTaskFragmentDialog.dismiss()
            }
        }
        rootView.text_name.requestFocus()

        this.dialog?.setTitle("Create Task")

        return rootView
    }
}