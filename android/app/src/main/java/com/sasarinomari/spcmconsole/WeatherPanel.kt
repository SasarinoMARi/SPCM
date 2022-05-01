package com.sasarinomari.spcmconsole

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.gateway.WeatherGateway
import kotlinx.android.synthetic.main.fragment_weather_panel.*

class WeatherPanel : Fragment(R.layout.fragment_weather_panel) {
    private lateinit var api : APIClient
    fun setApiCall(api: APIClient) { this.api = api }
    private lateinit var activity : AppCompatActivity
    fun setActivity(activity: AppCompatActivity) { this.activity = activity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root.visibility = View.GONE
        root.setOnClickListener {
            val f = WeatherDetailFragmentDialog(api)
            f.show(childFragmentManager, "Weather Detail")
        }
    }

    override fun onResume() {
        super.onResume()
        
        // TODO: 어제와의 기온차 보여주기

        api.getWeather { weather ->
            root.visibility = View.VISIBLE
            SPCMConsole.downloadUrlToBitmap(weather.weatherIconUrl) {
                activity.runOnUiThread { weather_icon.setImageBitmap(it) }
            }
            weather_text.text = WeatherGateway.mapWeatherCode(weather.weather)
            temp_current.text = getString(R.string.temp, weather.temp.toString())
            temp_current.setTextColor(WeatherGateway.getTempColor((weather.temp)))
            temp_diff.text = getString(R.string.tempdiff, WeatherGateway.getTempDiff(weather.minTemp, weather.maxTemp),
                weather.minTemp.toInt(), weather.maxTemp.toInt())
        }
    }
}