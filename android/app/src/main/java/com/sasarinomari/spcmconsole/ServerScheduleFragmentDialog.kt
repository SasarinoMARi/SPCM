package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.model.ScheduleModel
import kotlinx.android.synthetic.main.fragment_server_schedule_panel.view.*
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*

class ServerScheduleFragmentDialog (private val api: APIClient) : DialogFragment() {
    private val adapter = ScheduleAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_server_schedule_panel, container)
        rootView.listview.adapter = adapter
        rootView.button_reload_schedules.setOnClickListener {
            api.reloadSchedule { }
        }

        adapter.setOnScheduleClickListener(object: ScheduleAdapter.OnScheduleClickListener {
            override fun onClick(schedule: ScheduleModel) {
                SPCMConsole.confirm(context!!, getString(R.string.Confirm_Toggle_Schedule)) {
                    api.setSchedule(schedule.idx, schedule.active != 1) { fetchSchedule() }
                }
            }
        })

        fetchSchedule()

        this.dialog?.setTitle("Server Schedule")

        return rootView
    }

    private fun fetchSchedule() {
        api.getSchedule {
            adapter.clear()
            adapter.append(it.toList())
            adapter.notifyDataSetChanged()
        }
    }
}
class ScheduleAdapter : BaseAdapter() {
    interface OnScheduleClickListener {
        fun onClick(schedule: ScheduleModel)
    }
    private var onScheduleClickListener : OnScheduleClickListener? = null
    fun setOnScheduleClickListener(listener: OnScheduleClickListener) = listener.also { onScheduleClickListener = it }

    private val items = ArrayList<ScheduleModel>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<ScheduleModel>) {
        items.addAll(new)
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): ScheduleModel = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val convertView = view?: LayoutInflater.from(parent?.context).inflate(R.layout.item_schedule, parent, false)!!

        val item: ScheduleModel = getItem(position)
        val color = if(item.active == 1) enabled else disabled
        convertView.schedule_name.text = item.name
        convertView.schedule_name.setTextColor(color)
        convertView.schedule_cron.text = item.cron
        convertView.schedule_cron.setTextColor(color)
        convertView.schedule_command.text = item.command
        convertView.schedule_command.setTextColor(color)

        convertView.setOnClickListener {
            onScheduleClickListener?.onClick(item)
        }
        return convertView
    }

    companion object Colors {
        val enabled = Color.parseColor("#000000")
        val disabled = Color.parseColor("#aaaaaa")
    }
}