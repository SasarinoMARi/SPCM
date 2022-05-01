package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.gateway.WeatherGateway
import com.sasarinomari.spcmconsole.network.model.WeatherModel
import kotlinx.android.synthetic.main.fragment_weather_detail.view.*
import kotlinx.android.synthetic.main.item_weather.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherDetailFragmentDialog (private val api: APIClient) : DialogFragment() {
    private val df = SimpleDateFormat("yyyy-MM-dd EEEE hh:mm:ss", Locale.getDefault())
    private val adapter = WeatherAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_weather_detail, container)
        rootView.listview.adapter = adapter

        fetchSchedule()

        this.dialog?.setTitle("Weather Detail")

        return rootView
    }

    private fun fetchSchedule() {
        api.getForecast {
            val groupedResult = ArrayList<WeatherModel>()
            val group = it.groupBy { w -> w.date?.substring(0, 10) }
            group.forEach { day ->
                val item = WeatherModel()
                item.date = day.key
                item.minTemp = day.value.minBy { w -> w.minTemp }?.minTemp ?: -99f
                item.maxTemp = day.value.maxBy { w -> w.maxTemp }?.maxTemp ?: -99f
                item.temp = day.value.stream().mapToDouble { w -> w.temp.toDouble() }.summaryStatistics().average.toFloat()
                groupedResult.add(item)
            }

            adapter.clear()
            adapter.append(groupedResult)
            adapter.notifyDataSetChanged()
        }
    }
}

class WeatherAdapter : BaseAdapter() {
    private val items = ArrayList<WeatherModel>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<WeatherModel>) {
        items.addAll(new)
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): WeatherModel = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val convertView = view?: LayoutInflater.from(parent?.context).inflate(R.layout.item_weather, parent, false)!!

        val item: WeatherModel = getItem(position)
        convertView.text_weather.text = item.date
        convertView.text_temp.text = parent?.context?.getString(R.string.temp, item.temp.toString())
        convertView.text_temp.setTextColor(WeatherGateway.getTempColor((item.temp)))
        convertView.text_temp_diff.text = parent?.context?.getString(R.string.tempdiff,
            WeatherGateway.getTempDiff(item.minTemp, item.maxTemp), item.minTemp.toInt(), item.maxTemp.toInt())
        return convertView
    }
}