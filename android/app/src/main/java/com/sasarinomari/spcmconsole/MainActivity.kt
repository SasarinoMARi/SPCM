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

        private val overviewFragment by lazy { server_overview as ServerOverviewFragment }
        private val taskPanelFragment by lazy { task_panel as TaskPanelFragment }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            overviewFragment.setApiCall(api)
            taskPanelFragment.setApiCall(api)

            // 실험적 기능 어댑터 초기화
            buildAdapter()

            // FCM 토큰 갱신
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                api.updateFcmToken(it) { }
            }
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
