package com.sasarinomari.spcmconsole

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.sasarinomari.spcmconsole.network.APICall
import com.sasarinomari.spcmconsole.network.APIInterface
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val api = object : APICall(this) {
        override fun onError(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }

        override fun onMessage(message: String) {
            onError(message)
        }
    }

    private val overviewFragment by lazy { server_overview as ServerOverviewFragment }
    private val taskPanelFragment by lazy { task_panel as TaskPanelFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        overviewFragment.setApiCall(api)
        taskPanelFragment.setApiCall(api)

        button_write_diary.setOnClickListener {
            val intent = Intent("com.sasarinomari.diary.write")
            startActivity(intent)
        }
        button_food_dispenser.setOnClickListener {
            FoodDispenserFragmentDialog(api).show(supportFragmentManager, "Food Dispenser")
        }
        button_random_correct.setOnClickListener {
            val intent = Intent("com.sasarinomari.diary.random_correcting")
            startActivity(intent)
        }

        // FCM 토큰 갱신
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            // api.updateFcmToken(it) { }
        }
        
        api.getHeaderImage { path ->
            val url = "${APIInterface.BASE_URL}/header/$path"
            SPCMConsole.downloadUrlToBitmap(url) { image ->
                runOnUiThread {
                    image_header.setImageBitmap(image)
                    image_header.visibility = View.VISIBLE
                    loading_header.visibility = View.INVISIBLE
                }
            }
        }
    }

}
