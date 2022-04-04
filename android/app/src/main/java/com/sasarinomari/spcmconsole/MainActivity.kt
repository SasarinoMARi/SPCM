package com.sasarinomari.spcmconsole

import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
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

        layout_computer_status.setOnClickListener {
            RemoteComputerFragmentDialog(api).show(
                supportFragmentManager,
                "Remote Power Management"
            )
        }
        layout_pi_status.setOnClickListener {
            RaspberryServerFragmentDialog(api).show(
                supportFragmentManager,
                "Server Management"
            )
        }

        buildAdapter()

        // FCM 토큰 갱신
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            api.updateFcmToken(it) { }
        }
    }

    private fun startStatusChecker() {
        Thread {
            api.lookup(this@MainActivity)
            Thread.sleep(5000)
            if (focused) startStatusChecker()
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

    // region Monitor status
    val color_offline = Color.parseColor("#EF3D56")
    val color_online = Color.parseColor("#00A889")
    val color_loading = Color.parseColor("#000000")
    override fun onComputerOffline() {
        status_text_computer.text = getString(R.string.Offline)
        status_text_computer.setTextColor(color_offline)
        status_icon_computer.setColorFilter(color_offline)

        status_text_pi.text = getString(R.string.Online)
        status_text_pi.setTextColor(color_online)
        status_icon_pi.setColorFilter(color_online)
    }
    override fun onComputerOnline() {
        status_text_computer.text = getString(R.string.Online)
        status_text_computer.setTextColor(color_online)
        status_icon_computer.setColorFilter(color_online)

        status_text_pi.text = getString(R.string.Online)
        status_text_pi.setTextColor(color_online)
        status_icon_pi.setColorFilter(color_online)
    }
    override fun onServerOffline() {
        status_text_computer.text = getString(R.string.Offline)
        status_text_computer.setTextColor(color_offline)
        status_icon_computer.setColorFilter(color_offline)

        status_text_pi.text = getString(R.string.Offline)
        status_text_pi.setTextColor(color_offline)
        status_icon_pi.setColorFilter(color_offline)
    }
    fun setStatusToLoading() {
        status_text_computer.text = getString(R.string.Loading)
        status_text_computer.setTextColor(color_loading)
        status_icon_computer.setColorFilter(color_loading)

        status_text_pi.text = getString(R.string.Loading)
        status_text_pi.setTextColor(color_loading)
        status_icon_pi.setColorFilter(color_loading)
    }
    // endregion


    private fun buildAdapter(): ListAdapter? {
        val commandList = arrayOf(
            getString(R.string.Run_MemoboardPanel)
        )
        val arrayList: ArrayList<HashMap<String, String>> = ArrayList()
        for (i in commandList.indices) {
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["text"] = commandList[i]
            arrayList.add(hashMap)
        }
        val from = arrayOf("text")
        val to = intArrayOf(R.id.text_command_name)
        val adapter = SimpleAdapter(this, arrayList, R.layout.item_command, from, to)
        listview.adapter = adapter
        listview.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            when (i) {
                0 -> {
                    MemoboardPanelFragmentDialog(api, supportFragmentManager)
                        .show(supportFragmentManager, "Create Task")
                }
            }
        }
        return adapter
    }

    private fun fetchTasks() {
        val option = com.sasarinomari.spcmconsole.Memoboard.GetTaskOptions()
        Thread { api.getTasks(option) {

        }}.start()
    }

}
