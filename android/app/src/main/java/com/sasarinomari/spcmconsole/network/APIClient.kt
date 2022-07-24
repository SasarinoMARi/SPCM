package com.sasarinomari.spcmconsole.network

import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.gateway.*
import com.sasarinomari.spcmconsole.network.gateway.DesktopGateway
import com.sasarinomari.spcmconsole.network.gateway.FoodGateway
import com.sasarinomari.spcmconsole.network.gateway.NotificationGateway
import com.sasarinomari.spcmconsole.network.gateway.ScheduleGateway
import com.sasarinomari.spcmconsole.network.gateway.SystemGateway
import com.sasarinomari.spcmconsole.network.parameter.*
import com.sasarinomari.spcmconsole.network.model.*
import sasarinomari.genericdatahelper.DataGateway

abstract class APIClient() {
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

    fun pickRandomFood(callback:(Array<FoodModel>)->Unit) = FoodGateway().dispense(this, callback)
    fun getFoodList(callback:(Array<FoodModel>)->Unit) = FoodGateway().getList(this, callback)

    fun getLogs(logLevel: Int, page: Int, callback:(Array<LogResult>)->Unit) = SystemGateway().getLogs(logLevel, page, this, callback)
    fun log(level: Int, subject: String, content: String, callback: ((Unit)->Unit)? = null) = SystemGateway().log(LogParameter(level, subject, content), this, callback)

    fun createTask(task: TaskModel, callback: ((Unit)->Unit)? = null) = MemoboardGateway().createTask(task, this, callback)
    fun getTasks(options: GetTaskParameter, callback: (Array<TaskModel>)->Unit) = MemoboardGateway().getTasks(options, this, callback)

    fun getWeather(callback: (WeatherModel)->Unit) = WeatherGateway().getWeather(this, callback)
    fun getForecast(callback: (Array<WeatherModel>)->Unit) = WeatherGateway().getForecast(this, callback)

    class DataAPIClient internal constructor(private val parent: APIClient) {
        private val instance: DataGateway by lazy {
            val i = DataGateway()
            i.setBaseUrl(SPCMInterface.url)
            i.beforeRequest {
                parent.establishment { token ->
                    i.clearCustomHeader()
                    i.addCustomHeader("token", token)
                }
            }
            i
        }

        fun list(tableName: String, callback:(Array<JsonObject>)->Unit) = instance.list(tableName,
            object: GatewayBase.GeneralHandler<Array<JsonObject>>(parent, callback, {
                // 재귀할 방법 찾기
                // list(tableName, callback)
            }) { }
        )

        fun random(tableName: String, callback:(Array<JsonObject>)->Unit) = instance.random(tableName,
            object: GatewayBase.GeneralHandler<Array<JsonObject>>(parent, callback, {
                // 재귀할 방법 찾기
                // random(tableName, callback)
            }) { }
        )

        fun add(tableName: String, body: JsonObject, callback:(JsonObject)->Unit) = instance.add(tableName, body,
            object: GatewayBase.GeneralHandler<JsonObject>(parent, callback, {
                // 재귀할 방법 찾기
                // add(tableName, body, callback)
            }) { }
        )

        fun update(tableName: String, body: JsonObject, callback:(JsonObject)->Unit) = instance.update(tableName, body,
            object: GatewayBase.GeneralHandler<JsonObject>(parent, callback, {
                // 재귀할 방법 찾기
                // update(tableName, body, callback)
            }) { }
        )

        fun delete(tableName: String, body: JsonObject, callback:(JsonObject)->Unit) = instance.delete(tableName, body,
            object: GatewayBase.GeneralHandler<JsonObject>(parent, callback, {
                // 재귀할 방법 찾기
                // delete(tableName, body, callback)
            }) { }
        )
    }
    public val dataApi : DataAPIClient by lazy { DataAPIClient(this) }
}