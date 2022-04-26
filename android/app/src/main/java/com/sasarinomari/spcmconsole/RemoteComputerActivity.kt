package com.sasarinomari.spcmconsole

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import com.sasarinomari.spcmconsole.network.APIClient
import kotlinx.android.synthetic.main.activity_remote_computer.*

class RemoteComputerActivity : AppCompatActivity() {
    private val api = object : APIClient(this) {
        override fun error(message: String) {
            Toast.makeText(this@RemoteComputerActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_computer)

        buildAdapter()
    }

    private fun buildAdapter(): ListAdapter? {
        val commandList = arrayOf(
            getString(R.string.Run_RdpServer),
            getString(R.string.Run_FileServer)
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
                0 -> { SPCMConsole.confirm(this, getString(R.string.Confirm_RdpServer)) { api.startTeamviewerServer() } }
                1 -> { SPCMConsole.confirm(this,  getString(R.string.Confirm_FileServer)) { api.startFileServer() } }

            }
        }
        return adapter
    }
}