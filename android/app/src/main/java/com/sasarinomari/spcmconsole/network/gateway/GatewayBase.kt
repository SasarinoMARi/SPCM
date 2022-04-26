package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal abstract class GatewayBase {
    abstract class GeneralHandler<T> (private val client: APIClient,
                         private val callback: ((T)->Unit)?,
                         private val recursive: () -> Unit): Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            when {
                response.isSuccessful -> callback?.let { it(response.body()!!) }
                response.code() == 403 -> recursive()
                else -> client.error("요청 실패(${response.code()}) : ${response.message()}")
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            client.error("네트워크 오류: ${t.message}")
        }
    }
}