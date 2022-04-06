package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_food_dispenser.view.*

class FoodDispenserFragmentDialog(private val api: APICall) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_food_dispenser, container)

        rootView.button_select.setOnClickListener {
            api.foodDispenser { foods ->
                for (food in foods) {

                }
            }
        }

        this.dialog?.setTitle("Food Dispenser")

        return rootView
    }
}