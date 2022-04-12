package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sasarinomari.spcmconsole.Results.LookupResult
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.roundToInt

class ServerStatusUI(
    private val views: HashMap<String, View>,
    private val strings: HashMap<String, String>
) {
    private val color_offline = Color.parseColor("#EF3D56")
    private val color_online = Color.parseColor("#00A889")
    private val color_loading = Color.parseColor("#000000")
    private val color_danger = Color.parseColor("#ff3333")

    fun onComputerOffline() {
        (views["computer_text"] as TextView).text = strings["offline"]
        (views["computer_text"] as TextView).setTextColor(color_offline)
        (views["computer_icon"] as ImageView).setColorFilter(color_offline)

        (views["computer_temp"] as TextView).visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun onComputerOnline(result : LookupResult) {
        (views["computer_text"] as TextView).text = strings["online"]
        (views["computer_text"] as TextView).setTextColor(color_online)
        (views["computer_icon"] as ImageView).setColorFilter(color_online)

        if(result.PC.Temoerature != null) {
            val temp = getRoundedTemperature(result.PC.Temoerature!!)
            (views["computer_temp"] as TextView).visibility = View.VISIBLE
            (views["computer_temp"] as TextView).text = "현재 온도 : ${temp}˚C"
            if(temp.toInt() > 90) {
                (views["computer_temp"] as TextView).setTextColor(color_danger)
            }
        }
        else {
            (views["computer_temp"] as TextView).visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    fun onServerOnline(result : LookupResult) {
        (views["raspberry_text"] as TextView).text = strings["online"]
        (views["raspberry_text"] as TextView).setTextColor(color_online)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_online)


        if(result.PC.Temoerature != null) {
            val temp = getRoundedTemperature(result.Server.Temoerature!!)
            (views["raspberry_temp"] as TextView).visibility = View.VISIBLE
            (views["raspberry_temp"] as TextView).text = "현재 온도 : ${temp}˚C"
            if(temp.toInt() > 90) {
                (views["raspberry_temp"] as TextView).setTextColor(color_danger)
            }
        }
        else {
            (views["raspberry_temp"] as TextView).visibility = View.GONE
        }
    }

    fun onServerOffline() {
        (views["raspberry_text"] as TextView).text = strings["offline"]
        (views["raspberry_text"] as TextView).setTextColor(color_offline)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_offline)

        (views["raspberry_temp"] as TextView).visibility = View.GONE
    }

    fun setStatusToLoading() {
        (views["computer_text"] as TextView).text = strings["loading"]
        (views["computer_text"] as TextView).setTextColor(color_loading)
        (views["computer_icon"] as ImageView).setColorFilter(color_loading)

        (views["raspberry_text"] as TextView).text = strings["loading"]
        (views["raspberry_text"] as TextView).setTextColor(color_loading)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_loading)

        (views["computer_temp"] as TextView).visibility = View.GONE
        (views["raspberry_temp"] as TextView).visibility = View.GONE
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