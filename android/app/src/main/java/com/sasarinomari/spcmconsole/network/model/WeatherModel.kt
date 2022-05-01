package com.sasarinomari.spcmconsole.network.model

import com.google.gson.annotations.SerializedName

class WeatherModel {
    @SerializedName("weather")
    var weather: Int = -1
    @SerializedName("icon")
    var weatherIcon: String = ""
    val weatherIconUrl : String
        get() {
            return "http://openweathermap.org/img/wn/${weatherIcon}@2x.png"
        }
    @SerializedName("date")
    var date: String? = null

    @SerializedName("temp")
    var temp: Float = -1f
    @SerializedName("temp_min")
    var minTemp: Float = -1f
    @SerializedName("temp_max")
    var maxTemp: Float = -1f
}