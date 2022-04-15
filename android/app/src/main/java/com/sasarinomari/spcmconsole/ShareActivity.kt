package com.sasarinomari.spcmconsole

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShareActivity : AppCompatActivity() {
    private val api = object : APICall(this) {
        override fun onError(message: String) {
            Toast.makeText(this@ShareActivity, message, Toast.LENGTH_LONG).show()
        }

        override fun onMessage(message: String) {
            onError(message)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Intent.ACTION_SEND == intent.action) {
            /**
             * 공유하기를 통해 접근한 경우 인텐트에서 url을 추출해 작업을 시작합니다.
             */
            if("text/plain" == intent.type) {
                val url = intent.getStringExtra(Intent.EXTRA_TEXT)
                if(url!=null) api.openUrl(url)
                finish()
            }
        }
        else {
            Toast.makeText(this, "잘못된 접근", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}