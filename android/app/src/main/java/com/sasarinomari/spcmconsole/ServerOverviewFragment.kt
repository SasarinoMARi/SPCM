package com.sasarinomari.spcmconsole

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.model.LookupContent
import com.sasarinomari.spcmconsole.network.model.LookupResult
import kotlinx.android.synthetic.main.fragment_server_overview.*
import kotlin.math.roundToInt

class ServerOverviewFragment : Fragment(R.layout.fragment_server_overview) {
    private lateinit var api : APIClient
    fun setApiCall(api: APIClient) { this.api = api }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout_pi_status.setOnClickListener {
            val d = RaspberryServerFragmentDialog(api)
            d.afterCreateView {
                serverOnline?.let {
                    serverStateChangeHandler?.onChangedComputerState(it)
                }
            }
            serverStateChangeHandler = d.getServerStateChangeHandler()
            d.show(childFragmentManager, "Server Management")
        }
        layout_computer_status.setOnClickListener {
            val d = RemoteComputerFragmentDialog(api)
            d.afterCreateView {
                computerOnline?.let {
                    serverStateChangeHandler?.onChangedComputerState(it)
                }
            }
            serverStateChangeHandler = d.getServerStateChangeHandler()
            d.show(childFragmentManager, "Remote Power Management")
        }
    }

    // region 서버 상태 갱신 코드

    private var active = true
    override fun onResume() {
        super.onResume()
        setStatusToLoading()
        active = true
        startLookupThread()
    }

    override fun onPause() {
        active = false
        super.onPause()
    }

    private var serverOnline: Boolean? = null
    private var computerOnline: Boolean? = null
    private fun startLookupThread() {
        Thread {
            api.lookup {
                when (it.Server.Status) {
                    LookupContent.STATUS_ONLINE -> onServerOnline(it)
                    else -> onServerOffline()
                }
                when (it.PC.Status) {
                    LookupContent.STATUS_ONLINE -> onComputerOnline(it)
                    else -> onComputerOffline()
                }

                val serverStatus = it.Server.Status == LookupContent.STATUS_ONLINE
                if(serverStatus != serverOnline) serverStateChangeHandler?.onChangedServerState(serverStatus)
                serverOnline = serverStatus

                val computerStatus = it.PC.Status == LookupContent.STATUS_ONLINE
                if(computerStatus != computerOnline) serverStateChangeHandler?.onChangedComputerState(computerStatus)
                computerOnline = computerStatus
            }
            Thread.sleep(5000)
            if (active) startLookupThread()
        }.start()
    }

    private var serverStateChangeHandler : ServerStateChangeHandler? = null
    // endregion

    // region 패널 뷰 설정 코드

    private class TextColors {
        val black = Color.parseColor("#000000")
        val gray = Color.parseColor("#999999")
        val magenta = Color.parseColor("#EF3D56")
        val green = Color.parseColor("#00A889")
        val red = Color.parseColor("#ff3333")
    }
    private val colors = TextColors()

    private fun onComputerOffline() {
        computer_status.text = getString(R.string.Offline)
        computer_status.setTextColor(colors.magenta)
        computer_status_icon.setColorFilter(colors.magenta)

        computer_temperature.text = getString(R.string.TouchToDetail)
        computer_temperature.setTextColor(colors.gray)
    }

    private fun onComputerOnline(result : LookupResult) {
        computer_status.text = getString(R.string.Online)
        computer_status.setTextColor(colors.green)
        computer_status_icon.setColorFilter(colors.green)

        if(result.PC.Temoerature != null) {
            val temp = getRoundedTemperature(result.PC.Temoerature!!)
            computer_temperature.text = getString(R.string.CurrentTemperature, temp)
            computer_temperature.setTextColor(if(temp.toInt() > 90) colors.red else colors.gray)
        }
        else {
            computer_temperature.text = getString(R.string.TouchToDetail)
            computer_temperature.setTextColor(colors.gray)
        }
    }

    private fun onServerOffline() {
        server_status.text = getString(R.string.Offline)
        server_status.setTextColor(colors.magenta)
        server_status_icon.setColorFilter(colors.magenta)

        val tempView = server_temperature
        tempView.text = getString(R.string.TouchToDetail)
        tempView.setTextColor(colors.gray)
    }
    
    private fun onServerOnline(result : LookupResult) {
        server_status.text = getString(R.string.Online)
        server_status.setTextColor(colors.green)
        server_status_icon.setColorFilter(colors.green)

        if(result.Server.Temoerature != null) {
            val temp = getRoundedTemperature(result.Server.Temoerature!!)
            server_temperature.text = getString(R.string.CurrentTemperature, temp)
            server_temperature.setTextColor(if(temp.toInt() > 90) colors.red else colors.gray)
        }
        else {
            server_temperature.text = getString(R.string.TouchToDetail)
            server_temperature.setTextColor(colors.gray)
        }
    }

    private fun setStatusToLoading() {
        computer_status.text = getString(R.string.Loading)
        computer_status.setTextColor(colors.black)
        computer_status_icon.setColorFilter(colors.black)

        server_status.text = getString(R.string.Loading)
        server_status.setTextColor(colors.black)
        server_status_icon.setColorFilter(colors.black)

        val cTempView = computer_temperature
        cTempView.text = getString(R.string.TouchToDetail)
        cTempView.setTextColor(colors.gray)
        val rTempView = server_temperature
        rTempView.text = getString(R.string.TouchToDetail)
        rTempView.setTextColor(colors.gray)
    }

    private fun getRoundedTemperature(temperature: String): String {
        var result = ""
        try {
            result = temperature.toFloat().roundToInt().toString()
        } catch (e: Exception) {

        }
        return result
    }

    // endregion
}

interface ServerStateChangeHandler {
    fun onChangedServerState(online: Boolean)
    fun onChangedComputerState(online: Boolean)
}