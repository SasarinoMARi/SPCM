package com.sasarinomari.spcmconsole.network

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.R
import com.sasarinomari.spcmconsole.network.gateway.*
import com.sasarinomari.spcmconsole.network.gateway.DesktopGateway
import com.sasarinomari.spcmconsole.network.gateway.FoodGateway
import com.sasarinomari.spcmconsole.network.gateway.NotificationGateway
import com.sasarinomari.spcmconsole.network.gateway.ScheduleGateway
import com.sasarinomari.spcmconsole.network.gateway.SystemGateway
import com.sasarinomari.spcmconsole.network.parameter.*
import com.sasarinomari.spcmconsole.network.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class APIClient(val context: Context) {
    abstract fun error(message: String)

    fun establishment(callback: (String) -> Unit) = SystemGateway().establishment(this, callback)
    fun disconnect() = SystemGateway().disconnect()
    fun lookup(callback: (LookupResult) -> Unit) = SystemGateway().lookup(callback)
    fun rebootServer(callback: ((Unit)->Unit)? = null) = SystemGateway().reboot(this, callback)
    fun getHeaderImage(callback:(String)->Unit) = SystemGateway().getHeaderImage(this, callback)

    fun reloadSchedule(callback: ((Unit)->Unit)?) = ScheduleGateway().reload(this, callback)
    fun getSchedule(callback: ((Array<ScheduleModel>)->Unit)?) = ScheduleGateway().get(this, callback)
    fun setSchedule(id: Int, active: Boolean, callback: ((Unit)->Unit)?) = ScheduleGateway().set(ScheduleModel(id, active), this, callback)

    fun wakeup(callback: ((Unit)->Unit)? = null) = DesktopGateway().wakeup(this, callback)
    fun shutdown(callback: ((Unit)->Unit)? = null) = DesktopGateway().shutdown(this, callback)
    fun startFileServer(callback: ((Unit)->Unit)? = null) = DesktopGateway().startFileServer(this, callback)
    fun startTeamviewerServer(callback: ((Unit)->Unit)? = null) = DesktopGateway().startTeamviewerServer(this, callback)
    fun setVolume(amount: Int, callback: ((Unit)->Unit)? = null) = DesktopGateway().setVolume(amount, this, callback)
    fun mute(option: Int = 2, callback: ((Unit)->Unit)? = null) = DesktopGateway().mute(option, this, callback)
    fun openUrl(url: String, callback: ((Unit)->Unit)? = null) = DesktopGateway().openUrl(url, this, callback)

    fun sendFcm(title: String, content: String, callback: ((Unit)->Unit)? = null) = NotificationGateway().sendFcm(NotifyParameter(title, content), this, callback)
    fun updateFcmToken(fcmid: String, callback: ((Unit)->Unit)? = null) = NotificationGateway().updateFcmToken(FcmTokenUpdateParameter(fcmid), this, callback)

    fun foodDispenser(callback:(FoodResult)->Unit) = FoodGateway().dispense(this, callback)

    fun getLogs(logLevel: Int, page: Int, callback:(Array<LogResult>)->Unit) = SystemGateway().getLogs(logLevel, page, this, callback)
    fun log(level: Int, subject: String, content: String, callback: ((Unit)->Unit)? = null) = SystemGateway().log(LogParameter(level, subject, content), this, callback)

    fun createTask(task: TaskModel, callback: ((Unit)->Unit)? = null) = MemoboardGateway().createTask(task, this, callback)
    fun getTasks(options: GetTaskParameter, callback: (Array<TaskModel>)->Unit) = MemoboardGateway().getTasks(options, this, callback)
}