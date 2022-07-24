package com.sasarinomari.spcmconsole

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.sasarinomari.spcmconsole.network.APIClient
import kotlinx.android.synthetic.main.fragment_food_dispenser.view.*
import java.text.DecimalFormat

class FoodDispenserFragmentDialog(private val api: APIClient) : DialogFragment() {
    private val df = DecimalFormat("###,###")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_food_dispenser, container)

        rootView.text_name.text = ""
        rootView.text_price.text = ""
        rootView.text_store.text = ""

        rootView.button_reroll.setOnClickListener {
            foodDispenser(rootView)
        }
        rootView.button_open_list.setOnClickListener {
            val i = Intent(context, FoodListActivity::class.java)
            context?.startActivity(i)
        }

        foodDispenser(rootView)

        this.dialog?.setTitle("Food Dispenser")

        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun foodDispenser(view: View) {
        api.pickRandomFood { foods ->
            if (foods.isEmpty())
                return@pickRandomFood

            val food = foods[0]
            view.text_name.text = food.Name
            view.text_price.text = "₩${df.format(food.Price)}"
            view.text_store.text = food.Store
            if(food.Phone != null) {
                view.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${food.Phone}")
                    startActivity(intent)
                }
            }
            else {
                view.setOnClickListener(null)
            }
        }
    }

    private fun setCallButton(btn: CardView, enabled: Boolean) {
        if(!enabled) btn.children.elementAt(0).setBackgroundColor(Color.parseColor("#30000000"))
        else btn.children.elementAt(0).setBackgroundColor(Color.parseColor("#ffffff"))
    }
}