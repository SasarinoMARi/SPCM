package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sasarinomari.spcmconsole.Results.LookupResult
import java.util.*
import kotlin.math.roundToInt

class ServerStatusUI(
    private val views: HashMap<String, View>,
    private val strings: HashMap<String, String>
) {
    private val color_offline = Color.parseColor("#EF3D56")
    private val color_online = Color.parseColor("#00A889")
    private val color_default = Color.parseColor("#000000")
    private val color_danger = Color.parseColor("#ff3333")
    private val color_gray = Color.parseColor("#999999")

    fun onComputerOffline() {
        (views["computer_text"] as TextView).text = strings["offline"]
        (views["computer_text"] as TextView).setTextColor(color_offline)
        (views["computer_icon"] as ImageView).setColorFilter(color_offline)

        val tempView = (views["computer_temp"] as TextView)
        tempView.text = "클릭하여 자세히"
        tempView.setTextColor(color_gray)
    }

    @SuppressLint("SetTextI18n")
    fun onComputerOnline(result : LookupResult) {
        (views["computer_text"] as TextView).text = strings["online"]
        (views["computer_text"] as TextView).setTextColor(color_online)
        (views["computer_icon"] as ImageView).setColorFilter(color_online)

        val tempView = (views["computer_temp"] as TextView)
        if(result.PC.Temoerature != null) {
            val temp = getRoundedTemperature(result.PC.Temoerature!!)
            tempView.text = "현재 온도 : ${temp}˚C"
            tempView.setTextColor(if(temp.toInt() > 90) color_danger else color_gray)
        }
        else {
            tempView.text = "클릭하여 자세히"
            tempView.setTextColor(color_gray)
        }

        onComputerOffline()
    }

    @SuppressLint("SetTextI18n")
    fun onServerOnline(result : LookupResult) {
        (views["raspberry_text"] as TextView).text = strings["online"]
        (views["raspberry_text"] as TextView).setTextColor(color_online)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_online)


        val tempView = (views["raspberry_temp"] as TextView)
        if(result.Server.Temoerature != null) {
            val temp = getRoundedTemperature(result.Server.Temoerature!!)
            tempView.text = "현재 온도 : ${temp}˚C"
            tempView.setTextColor(if(temp.toInt() > 90) color_danger else color_gray)
        }
        else {
            tempView.text = "클릭하여 자세히"
            tempView.setTextColor(color_gray)
        }
    }

    fun onServerOffline() {
        (views["raspberry_text"] as TextView).text = strings["offline"]
        (views["raspberry_text"] as TextView).setTextColor(color_offline)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_offline)

        val tempView = (views["raspberry_temp"] as TextView)
        tempView.text = "클릭하여 자세히"
        tempView.setTextColor(color_gray)
    }

    fun setStatusToLoading() {
        (views["computer_text"] as TextView).text = strings["loading"]
        (views["computer_text"] as TextView).setTextColor(color_default)
        (views["computer_icon"] as ImageView).setColorFilter(color_default)

        (views["raspberry_text"] as TextView).text = strings["loading"]
        (views["raspberry_text"] as TextView).setTextColor(color_default)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_default)
    }

    fun getRoundedTemperature(temperature: String): String {
        var result = ""
        try {
            result = temperature.toFloat().roundToInt().toString()
        } catch (e: Exception) {

        }
        return result
    }

}