package com.sasarinomari.spcmconsole

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

abstract interface APICall {
    abstract fun onError(message: String)
    abstract fun onMessage(message: String)

    fun establishment(callback: (token: String) -> Unit) {
        val c = Calendar.getInstance()
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        val key = sha256("s${h + m}")
        // val key = sha256("*Mkgj**oVC_VRb@#pM>iy^4H*T&,o)Gf&vjPN-]+oNufE2V,2Bs+TWqntq,H.8e,")

        // Log.d("MainActivity", "key: $key")
        val call = APIInterface.api.establishment(key.toLowerCase(Locale.getDefault()))
        call.enqueue(object : Callback<TokenModel> {
            override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<TokenModel>, response: Response<TokenModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result.errorCode > 0) {
                        onError(result.message)
                    } else callback(result.token)
                }

            }
        })
    }

    fun shutdown(token: String) {
        val call = APIInterface.api.shutdown(token)
        call.enqueue(object : Callback<ResultModel> {
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result.errorCode > 0) {
                        onError(result.message)
                    } else {
                        onMessage("종료 요청을 보냈습니다.")
                    }
                }
                else {
                    // TODO: Http Response 처리
                    onMessage("권한이 없습니다.")
                }
            }
        })
    }

    fun sleep(token: String) {
        val call = APIInterface.api.sleep(token)
        call.enqueue(object : Callback<ResultModel> {
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result.errorCode > 0) {
                        onError(result.message)
                    } else {
                        onMessage("절전 요청을 보냈습니다.")
                    }
                }
                else {
                    // TODO: Http Response 처리
                    onMessage("권한이 없습니다.")
                }
            }
        })
    }

    fun wakeup(token: String) {
        val call = APIInterface.api.wakeup(token)
        call.enqueue(object : Callback<ResultModel> {
            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result.errorCode > 0) {
                        onError(result.message)
                    } else {
                        onMessage("부팅 요청을 보냈습니다.")
                    }
                }
                else {
                    // TODO: Http Response 처리
                    onMessage("권한이 없습니다.")
                }
            }
        })
    }


    interface lookupInterface {
        fun onDead()
        fun onLive()
    }
    fun lookup(i: lookupInterface) {
        val call = APIInterface.api.lookup()
        call.enqueue(object : Callback<ResultModel> {
            private fun dead() {
                i.onDead()
            }
            private fun live() {
                i.onLive()
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                dead()
            }

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result.errorCode > 0) {
                        dead()
                    } else {
                        live()
                    }
                }
                else {
                    // TODO: Http Response 처리
                    dead()
                }
            }
        })
    }

    private fun sha256(param: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(param.toByteArray())
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }
        return result.toString()
    }
}