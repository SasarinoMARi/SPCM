package com.sasarinomari.spcmconsole

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.model.TaskModel
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import java.util.*

class CreateTaskFragmentDialog(private val api: APIClient) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var onTaskChangedListener: OnTaskChangedListener? = null
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_create_task, container)
        rootView.button_submit.setOnClickListener {
            val task = TaskModel()
            task.name = rootView.text_name.text.toString()
            task.description = rootView.text_description.text.toString()
            task.date = DateTimeConvertor.buildDate(year, month, date)
            if(task.name.isNullOrBlank()) return@setOnClickListener

            api.createTask(task) {
                Toast.makeText(context, getString(R.string.Task_Created), Toast.LENGTH_LONG).show()
                onTaskChangedListener?.onTaskChanged()
                this@CreateTaskFragmentDialog.dismiss()
            }
        }

        rootView.button_calendar.setOnClickListener {
            val f = DatePickerFragmentDialog(this)
            f.show(fragmentManager!!, "DatePicker")
        }

        rootView.button_today.setOnClickListener {
            val calendar = Calendar.getInstance()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH) + 1
            date = calendar.get(Calendar.DAY_OF_MONTH)
        }
        rootView.button_tommorow.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH) + 1
            date = calendar.get(Calendar.DAY_OF_MONTH)
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

    fun setOnTaskChangedListener(listener: OnTaskChangedListener) {
        this.onTaskChangedListener = listener
    }

    interface OnTaskChangedListener {
        fun onTaskChanged()
    }
}