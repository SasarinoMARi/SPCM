package com.sasarinomari.spcmconsole.network.gateway

import android.util.Log
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.Token
import com.sasarinomari.spcmconsole.network.model.LogResult
import com.sasarinomari.spcmconsole.network.model.LookupResult
import com.sasarinomari.spcmconsole.network.parameter.LogParameter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class SystemGateway : GatewayBase() {
    companion object {
        private var token: Token? = null
        private val lock = ReentrantLock()
        private var isEstablishing = false;
    }

    /**
     * 연결 수립 및 api 토큰 등록
     */
    fun disconnect() = null.also { token = it }
    fun establishment(client: APIClient, callback: (String) -> Unit) {
        lock.lock() // isLocked 걸려있는데도 아래 코드 실행됨.. 이렇게 쓰는게 아닌가?
        if (token == null || !token!!.isValid()) {
            val call = SPCMInterface.api.establishment(SPCMInterface.key)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val result = response.body()!!
                        token = Token(result)
                        lock.unlock()
                        callback(result)
                    } else {
                        lock.unlock()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    lock.unlock()
                    client.error(t.toString())
                }
            })
        } else {
            lock.unlock()
            callback(token?.token!!)
        }
    }

    fun lookup(callback: (LookupResult) -> Unit) {
        val call = SPCMInterface.api.lookup()
        call.enqueue(object : Callback<LookupResult> {
            override fun onResponse(call: Call<LookupResult>, response: Response<LookupResult>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    callback(result)
                } else {
                    onFailure(call, Exception("Response is not successful"))
                }
            }

            override fun onFailure(call: Call<LookupResult>, t: Throwable) {
                val result = LookupResult()
                result.Server.Status = 0
                callback(result)
            }
        })
    }

    fun reboot(client: APIClient, callback: ((Unit)->Unit)?) {
        establishment(client) { token ->
            val call = SPCMInterface.api.reboot(token)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { reboot(client, callback) }) {})
        }
    }

    fun getLogs(level: Int, page: Int, client: APIClient, callback: ((Array<LogResult>)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.logs(token, level, page)
            call.enqueue(object: GeneralHandler<Array<LogResult>>(client, callback, { getLogs(level, page, client, callback) }) {})
        }
    }

    fun log(param: LogParameter, client: APIClient, callback: ((Unit)->Unit)?) {
        establishment(client) { token ->
            val call = SPCMInterface.api.log(token, param)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { log(param, client, callback) }) {})
        }
    }

    fun getHeaderImage(client: APIClient, callback: ((String)->Unit)?) {
        val _callback = object : ((Array<JsonObject>)->Unit) {
            override fun invoke(p1: Array<JsonObject>) {
                try{
                    callback?.let { it(p1[0].get("url").asString) }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        establishment(client) { token ->
            val call = SPCMInterface.api.header_image(token)
            call.enqueue(object: GeneralHandler<Array<JsonObject>>(client, _callback, { getHeaderImage(client, callback) }) {})
        }
    }


}