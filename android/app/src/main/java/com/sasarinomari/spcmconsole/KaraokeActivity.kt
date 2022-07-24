package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sasarinomari.spcmconsole.network.APIClient
import kotlinx.android.synthetic.main.activity_karaoke.*
import kotlinx.android.synthetic.main.item_food.view.*

class KaraokeActivity : AppCompatActivity() {
    private val tableName = "karaoke"
    private val adapter =
        sasarinomari.genericdatahelper.DataAdapter(R.layout.item_food) { canvas, item ->
            canvas.food_name.text = item.get("name").asString
            canvas.food_price.text = item.get("number_tj").asInt.toString()
        }

    private val api = object : APIClient() {
        override fun error(message: String) {
            Toast.makeText(this@KaraokeActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_karaoke)

        api.dataApi.list("karaoke") {
            adapter.append(it.toList())
            adapter.notifyDataSetChanged()
        }

        listview.adapter =adapter
    }

}

