package com.sasarinomari.spcmconsole

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), APICall {
    var token : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        loadingCover.visibility = View.VISIBLE
//        lookup(object: APICall.lookupInterface {
//            override fun onDead() {
//                onMessage("서버가 오프라인입니다.")
//                finish()
//            }
//
//            override fun onLive() {
//                establish {}
//            }
//        })
        establish {}
        button_lookup.setOnClickListener {
            lookup(object : APICall.lookupInterface {
                override fun onDead() {
                    onMessage("서버가 오프라인입니다.")
                }

                override fun onLive() {
                    onMessage("서버가 온라인입니다.")
                }
            })
        }

        button_wakeup.setOnClickListener {
            wakeup(token!!)
        }

        button_shutdown.setOnClickListener {
            shutdown(token!!)
        }

        button_sleep.setOnClickListener {
            sleep(token!!)
        }
    }

    private fun establish(callback: () -> Unit) {
        loadingCover.visibility = View.VISIBLE
        establishment {token ->
            this@MainActivity.token = token
            runOnUiThread {
                loadingCover.visibility = View.GONE
                callback()
            }
        }
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onMessage(message: String) {
        onError(message)
    }
}
