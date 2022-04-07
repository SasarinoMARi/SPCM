package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.sasarinomari.spcmconsole.Memoboard.CreateTaskFragmentDialog
import com.sasarinomari.spcmconsole.Memoboard.GetTaskOptions
import com.sasarinomari.spcmconsole.Memoboard.TaskModel
import kotlinx.android.synthetic.main.fragment_payday_dday.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*

class PayDayDday(private val api: APICall, private val activity: AppCompatActivity) {
    private fun calculatePayday(): Long {
        val calendar = Calendar.getInstance()
        val today = calendar.time

        /**
         * 월급일 계산 코드
         * 주말일 경우 직전의 금요일을 월급일로 취급한다.
          */
        if(calendar.get(Calendar.DAY_OF_MONTH) >= 25) calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 25)
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -2)
        }
        val destination = calendar.time

        val diff = destination.time - today.time
        val days = diff / (24*60*60*1000)

        return days
    }

    private lateinit var container : View

    @SuppressLint("SetTextI18n")
    fun init(view: View) {
        val dday = calculatePayday()
        view.text_dday.text = "D-${dday}"
        container = view.layout_memoboard

        view.button_open_memoboard.setOnClickListener {
            val launchIntent = activity.packageManager?.getLaunchIntentForPackage("com.sasarinomari.memoboard")
            launchIntent?.let { activity.startActivity(it) }
        }
        view.button_create_task.setOnClickListener {
            CreateTaskFragmentDialog(api).show(activity.supportFragmentManager, "Create Task")
        }
    }

    fun reload() {
        fetchMemoboard(container as ViewGroup)
    }

    private fun fetchMemoboard(view: ViewGroup) {
        val option = GetTaskOptions()
        option.Limit = 5

        api.getTasks(option) {
            var height = 0
            for(item in it) {
                height += inflateMemoboard(view, item)
            }

            val params = view.layoutParams
            params.height = height
            view.layoutParams = params
            view.requestLayout()
        }
    }

    private fun inflateMemoboard(parent: ViewGroup, item: TaskModel): Int {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_task, parent, false)

        view.text_task_name.text = item.Name
        view.text_task_description.text = item.Description
        if(item.Description.isNullOrEmpty()) view.text_task_description.visibility = View.GONE

        view.setOnClickListener {
            // TODO: idx로 메모보드 호출
        }

        if(item.Date != null) {
            val leftDays = getTaskLeftDays(item)
            view.text_task_left_days.text = getTaskLeftDayText(leftDays)
        }
        else {
            view.text_task_left_days.visibility = View.GONE
        }


        parent.addView(view)

        view.measure(0, 0)
        return view.measuredHeight
    }

    private fun getTaskLeftDayText(leftDays: Long): String {
        return when {
            leftDays == 1.toLong() -> "내일"
            leftDays == 2.toLong() -> "모레"
            leftDays > 0 -> "${leftDays}일 후"
            leftDays == (-1).toLong() -> "어제"
            leftDays == (-2).toLong() -> "그저께"
            leftDays < 0 -> "${-leftDays}일 전"
            else -> "오늘"
        }
    }

    private fun getTaskLeftDays(item: TaskModel): Long {
        if(item.Date == null) return 0
        val calendar = Calendar.getInstance()
        val today = calendar.time

        val dateArray = item.Date!!.split("-")
        calendar.set(Calendar.YEAR, dateArray[0].toInt())
        calendar.set(Calendar.MONTH, dateArray[1].toInt()-1)
        calendar.set(Calendar.DAY_OF_MONTH, dateArray[2].toInt())
        val destination = calendar.time

        val diff = destination.time - today.time
        return diff / (24 * 60 * 60 * 1000)
    }

}