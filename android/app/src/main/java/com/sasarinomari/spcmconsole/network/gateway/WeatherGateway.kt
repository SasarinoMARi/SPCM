package com.sasarinomari.spcmconsole.network.gateway

import android.graphics.Color
import com.sasarinomari.spcmconsole.R
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.model.WeatherModel

internal class WeatherGateway : GatewayBase() {
    fun getWeather (client: APIClient, callback: ((WeatherModel)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.getWeather(token)
            call.enqueue(object: GeneralHandler<WeatherModel>(client, callback, { getWeather(client, callback) }) {})
        }
    }

    fun getForecast (client: APIClient, callback: ((Array<WeatherModel>)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.getForecast(token)
            call.enqueue(object: GeneralHandler<Array<WeatherModel>>(client, callback, { getForecast(client, callback) }) {})
        }
    }

    companion object {
        class Colors {
            companion object {
                val COLOR_COLD = Color.parseColor("#81d4fa")
                val COLOR_CHILLY = Color.parseColor("#424242")
                val COLOR_WARM = Color.parseColor("#ffd54f")
                val COLOR_HOT = Color.parseColor("#d81b60")
            }
        }

        fun getWeatherIconIdFromResource(code: String): Int? {
            return when {
                code.startsWith("01") -> R.drawable.weather_icon_01d
                code.startsWith("02") -> R.drawable.weather_icon_02d
                code.startsWith("03") -> R.drawable.weather_icon_03d
                code.startsWith("04") -> R.drawable.weather_icon_04d
                code.startsWith("09") -> R.drawable.weather_icon_09d
                code.startsWith("10") -> R.drawable.weather_icon_10d
                code.startsWith("11") -> R.drawable.weather_icon_11d
                code.startsWith("13") -> R.drawable.weather_icon_13d
                code.startsWith("50") -> R.drawable.weather_icon_50d
                else -> null
            }
        }

        fun mapWeatherCode(code: Int): String {
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

        fun getTempDiff(min: Float, max: Float) : String {
            val diff = max - min
            return when {
                diff < 5 -> "적음"
                diff < 10 -> "있음"
                else -> "심함"
            }
        }

        fun getTempColor(temp: Float) : Int {
            return when {
                temp < 10 -> Colors.COLOR_COLD
                temp < 20 -> Colors.COLOR_CHILLY
                temp < 30 -> Colors.COLOR_WARM
                else -> Colors.COLOR_HOT
            }
        }
    }
}