package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.APIClient
import kotlinx.android.synthetic.main.activity_karaoke.*
import kotlinx.android.synthetic.main.item_food.view.*
import java.util.*

class KaraokeActivity : AppCompatActivity() {
    private val tableName = "karaoke"
    private val adapter = DataAdapter(R.layout.item_food) { canvas, item ->
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

/**
 * viewResourceID : 어떤 뷰 리소스를 사용할지 그 아이디를 설정합니다.
 * draw : 데이터를 뷰에 어떻게 그릴지 정의하는 함수입니다.
 */
class DataAdapter(private val viewResourceID :Int,
                  private val draw:(View, JsonObject)->Unit) : BaseAdapter() {
    private val items = ArrayList<JsonObject>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<JsonObject>) {
        items.addAll(new)
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): JsonObject = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val convertView = view?: LayoutInflater.from(parent?.context).inflate(viewResourceID, parent, false) ?: return null
        val item = getItem(position)
        draw(convertView, item)
        return convertView
    }
}