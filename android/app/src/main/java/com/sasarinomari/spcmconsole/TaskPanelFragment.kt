package com.sasarinomari.spcmconsole

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasarinomari.spcmconsole.network.APICall
import com.sasarinomari.spcmconsole.network.parameter.GetTaskParameter
import com.sasarinomari.spcmconsole.network.model.TaskModel
import kotlinx.android.synthetic.main.fragment_task_panel.*
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*

class TaskPanelFragment : Fragment(R.layout.fragment_task_panel) {
    private lateinit var api : APICall
    fun setApiCall(api: APICall) { this.api = api }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dday = calculatePayday()
        text_dday.text = getString(R.string.Dday, dday)

        button_open_memoboard.setOnClickListener {
            val launchIntent = context?.packageManager?.getLaunchIntentForPackage("com.sasarinomari.memoboard")
            launchIntent?.let { context?.startActivity(it) }
        }
        button_create_task.setOnClickListener {
            CreateTaskFragmentDialog(api).show(childFragmentManager, "Create Task")
        }

    }

    override fun onResume() {
        super.onResume()
        fetchMemoboard(layout_memoboard as ViewGroup)
    }


    /**
     * 월급일 계산 코드
     * 주말일 경우 직전의 금요일을 월급일로 취급한다.
     */
    private fun calculatePayday(): Long {
        val calendar = Calendar.getInstance()
        val today = calendar.time

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

    // region 할 일 가져오는 코드

    private fun fetchMemoboard(view: ViewGroup) {
        val option = GetTaskParameter()
        option.limit = 5

        api.getTasks(option) { tasks ->
            view.removeAllViews()

            var height = 0
            for(item in tasks) {
                height += inflateMemoboard(view, item) + 10 // Magic..
            }

            val params = view.layoutParams
            params.height = height
            view.layoutParams = params
            view.requestLayout()
        }
    }

    private fun inflateMemoboard(parent: ViewGroup, item: TaskModel): Int {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val view = inflater?.inflate(R.layout.item_task, parent, false) ?: return 0

        view.text_task_name.text = item.name
        view.text_task_description.text = item.description
        if(item.description.isNullOrEmpty()) view.text_task_description.visibility = View.GONE

        if(item.date != null) {
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
        if(item.date == null) return 0
        val calendar = Calendar.getInstance()
        val today = calendar.time

        val dateArray = item.date!!.split("-")
        calendar.set(Calendar.YEAR, dateArray[0].toInt())
        calendar.set(Calendar.MONTH, dateArray[1].toInt()-1)
        calendar.set(Calendar.DAY_OF_MONTH, dateArray[2].toInt())
        val destination = calendar.time

        val diff = destination.time - today.time
        return diff / (24 * 60 * 60 * 1000)
    }

    // endregion
}