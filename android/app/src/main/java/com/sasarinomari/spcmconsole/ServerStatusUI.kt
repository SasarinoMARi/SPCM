package com.sasarinomari.spcmconsole

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class ServerStatusUI(
    private val views: HashMap<String, View>,
    private val strings: HashMap<String, String>
) : APICall.lookupInterface {
    val color_offline = Color.parseColor("#EF3D56")
    val color_online = Color.parseColor("#00A889")
    val color_loading = Color.parseColor("#000000")


    override fun onComputerOffline() {
        (views["computer_text"] as TextView).text = strings["offline"]
        (views["computer_text"] as TextView).setTextColor(color_offline)
        (views["computer_icon"] as ImageView).setColorFilter(color_offline)

        (views["raspberry_text"] as TextView).text =  strings["online"]
        (views["raspberry_text"] as TextView).setTextColor(color_online)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_online)
    }

    override fun onComputerOnline() {
        (views["computer_text"] as TextView).text = strings["online"]
        (views["computer_text"] as TextView).setTextColor(color_online)
        (views["computer_icon"] as ImageView).setColorFilter(color_online)

        (views["raspberry_text"] as TextView).text = strings["online"]
        (views["raspberry_text"] as TextView).setTextColor(color_online)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_online)
    }

    override fun onServerOffline() {
        (views["computer_text"] as TextView).text = strings["offline"]
        (views["computer_text"] as TextView).setTextColor(color_offline)
        (views["computer_icon"] as ImageView).setColorFilter(color_offline)

        (views["raspberry_text"] as TextView).text = strings["offline"]
        (views["raspberry_text"] as TextView).setTextColor(color_offline)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_offline)
    }

    fun setStatusToLoading() {
        (views["computer_text"] as TextView).text = strings["loading"]
        (views["computer_text"] as TextView).setTextColor(color_loading)
        (views["computer_icon"] as ImageView).setColorFilter(color_loading)

        (views["raspberry_text"] as TextView).text = strings["loading"]
        (views["raspberry_text"] as TextView).setTextColor(color_loading)
        (views["raspberry_icon"] as ImageView).setColorFilter(color_loading)
    }
}