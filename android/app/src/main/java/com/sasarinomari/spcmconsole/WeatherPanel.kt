package com.sasarinomari.spcmconsole

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sasarinomari.spcmconsole.network.APIClient
import kotlinx.android.synthetic.main.fragment_weather_panel.*

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
        
        // TODO: 어제와의 기온차 보여주기

        api.getWeather { weather ->
            root.visibility = View.VISIBLE
            SPCMConsole.downloadUrlToBitmap(weather.weatherIconUrl) {
                activity.runOnUiThread { weather_icon.setImageBitmap(it) }
            }
            weather_text.text = mapWeatherCode(weather.weather)
            temp_current.text = getString(R.string.temp, weather.temp.toString())
            temp_current.setTextColor(getTempColor((weather.temp)))
            temp_diff.text = getString(R.string.tempdiff, getTempDiff(weather.minTemp, weather.maxTemp),
                weather.minTemp.toInt(), weather.maxTemp.toInt())
        }
    }

    private class Colors {
        companion object {
            val COLOR_COLD = Color.parseColor("#81d4fa")
            val COLOR_CHILLY = Color.parseColor("#424242")
            val COLOR_WARM = Color.parseColor("#ffd54f")
            val COLOR_HOT = Color.parseColor("#d81b60")
        }
    }

    private fun mapWeatherCode(code: Int): String {
        return when (code) {
            in 200 .. 299 -> "뇌우"
            in 300 .. 309 -> "이슬비"
            in 310 .. 399 -> "가랑비"
            in 500 .. 504 -> "비"
            511 -> "우박"
            in 520 .. 599 -> "소나기"
            in 600 .. 612 -> "눈"
            in 613 .. 616 -> "진눈개비"
            in 620 .. 621 -> "소낙눈"
            622 -> "폭설"
            701 -> "옅은 안개"
            711 -> "안개"
            721 -> "짙은 안개"
            in 731 .. 761 -> "먼지"
            762 -> "화산재"
            in 771 .. 781 -> "태풍"
            800 -> "쾌청"
            801 -> "살짝 흐림"
            802 -> "흐림"
            803 -> "꽤 흐림"
            804 -> "매우 흐림"
            else -> "알 수 없는 날씨 $code"
        }
    }

    private fun getTempDiff(min: Float, max: Float) : String {
        val diff = max - min
        return when {
            diff < 5 -> "적음"
            diff < 10 -> "있음"
            else -> "심함"
        }
    }

    private fun getTempColor(temp: Float) : Int {
        return when {
            temp < 10 -> Colors.COLOR_COLD
            temp < 20 -> Colors.COLOR_CHILLY
            temp < 30 -> Colors.COLOR_WARM
            else -> Colors.COLOR_HOT
        }
    }
}