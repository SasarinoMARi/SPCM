package com.sasarinomari.spcmconsole

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), APICall.lookupInterface {
    private val api = object : APICall() {
        override fun onError(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }

        override fun onMessage(message: String) {
            onError(message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_wakeup.setOnClickListener {
            api.wakeup()
        }

        button_shutdown.setOnClickListener {
            api.shutdown()
        }

        button_sleep.setOnClickListener {
            api.sleep()
        }
    }

    private fun startStatusChecker() {
        Thread {
            api.lookup(this@MainActivity)
            Thread.sleep(5000)
            if(focused) startStatusChecker()
        }.start()
    }

    var focused = true
    override fun onResume() {
        super.onResume()
        focused = true
        startStatusChecker()
    }

    override fun onPause() {
        super.onPause()
        focused = false
    }


    override fun onDead() {
        status_text.text = getString(R.string.Offline)
        val c = Color.parseColor("#EF3D56")
        status_text.setTextColor(c)
        status_icon.setColorFilter(c)
    }

    override fun onLive() {
        status_text.text = getString(R.string.Online)
        val c = Color.parseColor("#00A889")
        status_text.setTextColor(c)
        status_icon.setColorFilter(c)
    }
}
