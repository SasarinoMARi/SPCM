package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.sasarinomari.spcmconsole.Memoboard.GetTaskOptions
import com.sasarinomari.spcmconsole.Memoboard.TaskModel
import kotlinx.android.synthetic.main.fragment_payday_dday.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import java.time.LocalDate
import java.util.*

class PayDayDday(private val api: APICall, private val context: Context) {
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

    @SuppressLint("SetTextI18n")
    fun setDdayView(view: View) {
        val dday = calculatePayday()
        view.text_dday.text = "D-${dday}"
        fetchMemoboard(view.layout_memoboard as ViewGroup)
    }


    fun fetchMemoboard(view: ViewGroup) {
        val option = GetTaskOptions()
        option.Limit = 3

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
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_task, parent, false)

        view.text_task_name.text = item.Name
        view.text_task_description.text = item.Description
        if(item.Description.isNullOrEmpty()) view.text_task_description.visibility = View.GONE

        view.setOnClickListener {
            // TODO: idx로 메모보드 호출
        }

        parent.addView(view)

        view.measure(0, 0)
        return view.measuredHeight
    }

}