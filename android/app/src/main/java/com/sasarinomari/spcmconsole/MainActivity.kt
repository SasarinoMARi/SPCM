    package com.sasarinomari.spcmconsole

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.*
    import androidx.appcompat.app.AppCompatActivity
    import com.google.firebase.messaging.FirebaseMessaging
    import com.sasarinomari.spcmconsole.results.LookupContent
    import kotlinx.android.synthetic.main.activity_main.*
    import kotlin.collections.ArrayList
    import kotlin.collections.HashMap


    class MainActivity : AppCompatActivity() {
        private val api = object : APICall(this) {
            override fun onError(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }

            override fun onMessage(message: String) {
                onError(message)
            }
        }
        private lateinit var serverStatusUI: ServerStatusUI
        private val panel_payday = PayDayDday(api, this)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            initServerStatusUI()
            panel_payday.init(layout_dday)

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

        private fun initServerStatusUI() {
            val views = HashMap<String, View>()
            views["computer_text"] = status_text_computer
            views["computer_icon"] = status_icon_computer
            views["computer_temp"] = temp_text_computer
            views["raspberry_text"] = status_text_pi
            views["raspberry_icon"] = status_icon_pi
            views["raspberry_temp"] = temp_text_pi
            val strings = HashMap<String, String>()
            strings["offline"] = getString(R.string.Offline)
            strings["online"] = getString(R.string.Online)
            strings["loading"] = getString(R.string.Loading)
            serverStatusUI = ServerStatusUI(views, strings)
        }

        private fun startStatusChecker() {
            Thread {
                api.lookup {
                    when (it.Server.Status) {
                        LookupContent.STATUS_ONLINE -> serverStatusUI.onServerOnline(it)
                        else -> serverStatusUI.onServerOffline()
                    }
                    when (it.PC.Status) {
                        LookupContent.STATUS_ONLINE -> serverStatusUI.onComputerOnline(it)
                        else -> serverStatusUI.onComputerOffline()
                    }
                }
                Thread.sleep(5000)
                if (focused) startStatusChecker()
            }.start()
        }

        var focused = true
        override fun onResume() {
            super.onResume()
            serverStatusUI.setStatusToLoading()
            focused = true
            startStatusChecker()
            panel_payday.reload()
        }

        override fun onPause() {
            super.onPause()
            focused = false
        }


        private fun buildAdapter(): ListAdapter? {
            val commandList = arrayOf(
                getString(R.string.WriteDiary),
                "오늘의 메뉴 추천",
                "로그 기록 api 테스트"
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
                        val intent = Intent("com.sasarinomari.diary.write")
                        startActivity(intent)
                    }
                    1 -> {
                        FoodDispenserFragmentDialog(api).show(supportFragmentManager, "Food Dispenser")
                    }
                    2 -> {
                        api.log(1, "MainActivity.kt", "안드로이드에서 기록된 로그입니다") {
                            Toast.makeText(this@MainActivity, "OK!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            return adapter
        }
    }
