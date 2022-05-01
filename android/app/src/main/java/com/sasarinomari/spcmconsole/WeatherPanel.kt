package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.gateway.WeatherGateway
import kotlinx.android.synthetic.main.fragment_weather_panel.*
import kotlin.math.roundToInt

class WeatherPanel : Fragment(R.layout.fragment_weather_panel) {
    private lateinit var api : APIClient
    fun setApiCall(api: APIClient) { this.api = api }
    private lateinit var activity : AppCompatActivity
    fun setActivity(activity: AppCompatActivity) { this.activity = activity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()

        api.getWeather { weather ->
            root.visibility = View.VISIBLE
            val iconResId = WeatherGateway.getWeatherIconIdFromResource(weather.weatherIcon)
            if (iconResId != null) weather_icon.setImageResource(iconResId)
            else SPCMConsole.downloadUrlToBitmap(weather.weatherIconUrl) {
                activity.runOnUiThread { weather_icon.setImageBitmap(it) }
            }

            weather_text.text = WeatherGateway.mapWeatherCode(weather.weather)
            temp_current.text = getString(R.string.temp, weather.temp.toString())
            temp_current.setTextColor(WeatherGateway.getTempColor((weather.temp)))
            weather.yesterdayTempAverage?.let { yesterday ->
                val diff = yesterday - weather.temp
                when {
                    diff in -2f .. 2f -> temp_diff2.text = getString(R.string.tempdiff2_similar)
                    diff > 0 -> temp_diff2.text = getString(R.string.tempdiff2_cold, diff.roundToInt().toString())
                    else -> temp_diff2.text = getString(R.string.tempdiff2_cold, diff.roundToInt().toString())
                }
            }

            /*
            temp_diff.text = getString(R.string.tempdiff, WeatherGateway.getTempDiff(weather.minTemp, weather.maxTemp),
                weather.minTemp.toInt(), weather.maxTemp.toInt())
            */

            root.setOnClickListener {
                val f = WeatherDetailFragmentDialog(api, weather)
                f.show(childFragmentManager, "Weather Detail")
            }
        }
    }
}