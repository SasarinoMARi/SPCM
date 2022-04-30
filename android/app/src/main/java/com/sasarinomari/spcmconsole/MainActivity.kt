package com.sasarinomari.spcmconsole

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.Secret
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    private val api = object : APIClient(this) {
        override fun error(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private val overviewFragment by lazy { server_overview as ServerOverviewFragment }
    private val taskPanelFragment by lazy { task_panel as TaskPanelFragment }
    private val weatherPanelFragment by lazy { weather_panel as WeatherPanel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        overviewFragment.setApiCall(api)
        taskPanelFragment.setApiCall(api)
        weatherPanelFragment.setApiCall(api)
        weatherPanelFragment.setActivity(this)

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
            api.updateFcmToken(it) { }
        }

        loadHeader()
    }

    private fun loadHeader() {
        var image : Bitmap? = null

        // 저장된 헤더 있는지 확인
        val pref = getSharedPreferences(Constants.PREFERENCE_DEFAULT, MODE_PRIVATE)
        pref.getString(Constants.NEXT_HEADER_IMAGE_ID, null)?.let { b64str ->
            if(b64str.isEmpty()) return

            image = try {
                val imageAsBytes: ByteArray = Base64.decode(b64str.toByteArray(), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        // 저장된 헤더 없으면 다운받기
        if(image == null) {
            api.getHeaderImage { path ->
                val url = "${Secret.SPCM_URL}/header/$path"
                SPCMConsole.downloadUrlToBitmap(url) { image ->
                    runOnUiThread {
                        image_header.setImageBitmap(image)
                        image_header.visibility = View.VISIBLE
                        loading_header.visibility = View.INVISIBLE
                    }
                    downloadNextHeader()
                }
            }
        } else {
            image_header.setImageBitmap(image)
            image_header.visibility = View.VISIBLE
            loading_header.visibility = View.VISIBLE
            downloadNextHeader()
        }
    }

    private fun downloadNextHeader() {
        api.getHeaderImage { path ->
            val url = "${Secret.SPCM_URL}/header/$path"
            SPCMConsole.downloadUrlToBitmap(url) { image ->
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, stream) //bm is the bitmap object
                val ba: ByteArray = stream.toByteArray()
                val b64str = Base64.encodeToString(ba, Base64.DEFAULT)

                val pref = getSharedPreferences(Constants.PREFERENCE_DEFAULT, MODE_PRIVATE).edit()
                pref.putString(Constants.NEXT_HEADER_IMAGE_ID, b64str)
                pref.apply()
            }
        }
    }

}
