package com.sasarinomari.spcmconsole.Memoboard

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.APICall
import com.sasarinomari.spcmconsole.DatePickerFragmentDialog
import com.sasarinomari.spcmconsole.R
import kotlinx.android.synthetic.main.fragment_create_task.view.*

class CreateTaskFragmentDialog(private val api: APICall) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_create_task, container)
        rootView.button_submit.setOnClickListener {
            val task = TaskModel()
            task.Name = rootView.text_name.text.toString()
            task.Description = rootView.text_description.text.toString()
            task.Date = DateTimeConvertor.buildDate(year, month, date)
            if(task.Name.isNullOrBlank()) return@setOnClickListener

            api.createTask(task) {
                Toast.makeText(context, getString(R.string.Task_Created), Toast.LENGTH_LONG).show()
                this@CreateTaskFragmentDialog.dismiss()
            }
        }

        rootView.button_calendar.setOnClickListener {
            val f = DatePickerFragmentDialog(this)
            f.show(fragmentManager!!, "DatePicker")
        }

        rootView.text_name.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        this.dialog?.setTitle("Create Task")

        return rootView
    }


    private var year : Int? = null
    private var month : Int? = null
    private var date : Int? = null
    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, date: Int) {
        this.year = year
        this.month = month+1
        this.date = date
    }
}