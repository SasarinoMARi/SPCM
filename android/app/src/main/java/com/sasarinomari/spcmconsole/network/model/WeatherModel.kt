package com.sasarinomari.spcmconsole.network.model

import com.google.gson.annotations.SerializedName

class WeatherModel {
    @SerializedName("weather")
    var weather: Int = -1
    @SerializedName("weather_icon")
    var weatherIconUrl: String = ""
    @SerializedName("date")
    var date: String? = null

    @SerializedName("temp")
    var temp: Float = -1f
    @SerializedName("temp_min")
    var minTemp: Float = -1f
    @SerializedName("temp_max")
    var maxTemp: Float = -1f
}