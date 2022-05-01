package com.sasarinomari.spcmconsole

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.gateway.WeatherGateway
import com.sasarinomari.spcmconsole.network.model.WeatherModel
import kotlinx.android.synthetic.main.fragment_weather_detail.view.*
import kotlinx.android.synthetic.main.fragment_weather_panel.view.*
import kotlinx.android.synthetic.main.fragment_weather_panel.view.weather_icon
import kotlinx.android.synthetic.main.item_weather.view.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherDetailFragmentDialog (private val api: APIClient, private val cur: WeatherModel) : DialogFragment() {
    private val adapter by lazy { WeatherAdapter(context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_weather_detail, container)
        rootView.listview.adapter = adapter
        rootView.listview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val iconResId = WeatherGateway.getWeatherIconIdFromResource(cur.weatherIcon)
        if (iconResId != null) rootView.layout_weather_panel.weather_icon.setImageResource(iconResId)

        rootView.layout_weather_panel.weather_text.text = WeatherGateway.mapWeatherCode(cur.weather)
        rootView.layout_weather_panel.temp_current.text = getString(R.string.temp, cur.temp.toString())
        rootView.layout_weather_panel.temp_current.setTextColor(WeatherGateway.getTempColor((cur.temp)))
        context?.let { rootView.text_recommend_cloth.text = getClothText(it, cur) }
        fetchSchedule()

        this.dialog?.setTitle("Weather Detail")

        return rootView
    }

    private fun getClothText(context: Context, weather: WeatherModel): String {
        return when {
            weather.temp >= 27 -> context.getString(R.string.cloth_recommend_over28)
            weather.temp >= 23 -> context.getString(R.string.cloth_recommend_over23)
            weather.temp >= 20 -> context.getString(R.string.cloth_recommend_over20)
            weather.temp >= 17 -> context.getString(R.string.cloth_recommend_over17)
            weather.temp >= 12 -> context.getString(R.string.cloth_recommend_over12)
            weather.temp >= 9 -> context.getString(R.string.cloth_recommend_over9)
            weather.temp >= 5 -> context.getString(R.string.cloth_recommend_over5)
            else -> context.getString(R.string.cloth_recommend_under4)
        }
    }

    private fun fetchSchedule() {
        api.getForecast {
            /*
            val groupedResult = ArrayList<WeatherModel>()
            val group = it.groupBy { w -> w.date?.substring(10) }
            group.forEach { day ->
                val item = WeatherModel()
                item.date = day.key
                item.minTemp = day.value.minBy { w -> w.minTemp }?.minTemp ?: -99f
                item.maxTemp = day.value.maxBy { w -> w.maxTemp }?.maxTemp ?: -99f
                item.temp = day.value.stream().mapToDouble { w -> w.temp.toDouble() }.summaryStatistics().average.toFloat()
                groupedResult.add(item)
            }
             */

            adapter.clear()
            adapter.append(it.toList())
            adapter.notifyDataSetChanged()
        }
    }
}

class WeatherAdapter(private val context: Context?) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    private val items = ArrayList<WeatherModel>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<WeatherModel>) {
        items.addAll(new)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return ViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val convertView: View,
                     private val context: Context?) : RecyclerView.ViewHolder(convertView) {
        private val df0 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val df1 = SimpleDateFormat("MM월 dd일", Locale.getDefault())
        private val df2 = SimpleDateFormat("EEEE", Locale.getDefault())
        fun onBind(item: WeatherModel) {
            item.date?.let { dateStr ->
                df0.parse(dateStr)?.let { date ->
                    convertView.text_date.text = df1.format(date)
                    val weekday = SPCMConsole.convertWeekday(df2.format(date))
                    convertView.text_weekday.text = weekday
                    convertView.text_weekday.setTextColor(Colors.getWeekdayColor(weekday))
                    if(date.day == Date().day) convertView.image_cursor.visibility = View.VISIBLE
                }
            }

            val iconResId = WeatherGateway.getWeatherIconIdFromResource(item.weatherIcon)
            if (iconResId != null) convertView.weather_icon.setImageResource(iconResId)
            else SPCMConsole.downloadUrlToBitmap(item.weatherIconUrl) {
                // 액티비티 넘기면 되는데 굳이 하기 귀찮음..
                // activity.runOnUiThread { weather_icon.setImageBitmap(it) }
            }

            convertView.text_temp.text = context?.getString(R.string.temp, item.temp.toString())
            convertView.text_temp.setTextColor(WeatherGateway.getTempColor((item.temp)))
            convertView.text_temp_diff.text = context?.getString(R.string.tempdiff,
                WeatherGateway.getTempDiff(item.minTemp, item.maxTemp), item.minTemp.toInt(), item.maxTemp.toInt())
        }

        private class Colors {
            companion object {
                val COLOR_DEFAULT = Color.parseColor("#666666")
                val COLOR_SATURDAY = Color.parseColor("#66eeee")
                val COLOR_SUNDAY = Color.parseColor("#ee6666")

                fun getWeekdayColor(weekday: String) : Int {
                    return when {
                        weekday.toUpperCase(Locale.getDefault()).contains("일요일") -> COLOR_SUNDAY
                        weekday.toUpperCase(Locale.getDefault()).contains("토요일") -> COLOR_SATURDAY
                        else -> COLOR_DEFAULT
                    }
                }
            }
        }
    }
}