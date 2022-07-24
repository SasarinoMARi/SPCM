package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.model.FoodModel
import kotlinx.android.synthetic.main.activity_food_list.*
import kotlinx.android.synthetic.main.item_food.view.*

class FoodListActivity : AppCompatActivity() {
    private val api = object : APIClient() {
        override fun error(message: String) {
            Toast.makeText(this@FoodListActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private val adapter = FoodAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        listview.adapter = adapter

        api.getFoodList {
            adapter.clear()
            adapter.append(it.toList())
            adapter.notifyDataSetChanged()
        }
    }
}

class FoodAdapter : BaseAdapter() {
    private val items = ArrayList<FoodModel>()

    fun clear() {
        items.clear()
    }

    fun append(new: List<FoodModel>) {
        items.addAll(new)
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): FoodModel = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val convertView = view?: LayoutInflater.from(parent?.context).inflate(R.layout.item_food, parent, false)!!

        val item: FoodModel = getItem(position)
        convertView.food_name.text = item.Name
        convertView.food_price.text = item.Price.toString()
        convertView.food_store.text = item.Store
        return convertView
    }
}