package com.sasarinomari.spcmconsole

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), APICall.lookupInterface {
    private val api = object : APICall(this) {
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

        layout_remote_status.setOnClickListener {
            RemoteComputerFragmentDialog(api).show(supportFragmentManager, "Remote Power Management")
        }

        buildAdapter()

        // FCM 토큰 갱신
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            api.updateFcmToken(it) { }
        }
    }

    private fun confirm(text: String, action: ()-> Unit) {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(text)
            .setCancelable(true)
            .setPositiveButton(android.R.string.yes) { d, id ->
                action()
            }
            .setNegativeButton(android.R.string.no) { d, id ->
                d.dismiss()
            }
        val alert = builder.create()
        alert.show()
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
        setStatusToLoading()
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

    fun setStatusToLoading() {
        status_text.text = getString(R.string.Loading)
        val c = Color.parseColor("#000000")
        status_text.setTextColor(c)
        status_icon.setColorFilter(c)
    }

    private fun buildAdapter(): ListAdapter? {
        val commandList = arrayOf(
            getString(R.string.Run_MemoboardPanel),
            "일기 작성",
            getString(R.string.Run_PiReboot)
        )
        val arrayList: ArrayList<HashMap<String, String>> = ArrayList()
        for (i in commandList.indices) {
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["text"] =commandList[i]
            arrayList.add(hashMap)
        }
        val from = arrayOf("text")
        val to = intArrayOf(R.id.text_command_name)
        val adapter = SimpleAdapter(this, arrayList, R.layout.item_command, from, to)
        listview.adapter = adapter
        listview.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            when(i) {
                0 -> {
                    MemoboardPanelFragmentDialog(api, supportFragmentManager)
                        .show(supportFragmentManager, "Create Task")
                }
                1 -> {
                    val intent = Intent("com.sasarinomari.diary.write")
                    startActivity(intent)
                }
                2 -> { confirm(getString(R.string.Confirm_PiReboot)) { api.reboot_pi() } }

            }
        }
        return adapter
    }

}
