package com.sasarinomari.spcmconsole

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

abstract class APICall {
    abstract fun onError(message: String)
    abstract fun onMessage(message: String)

    private var token: String? = null

    fun establishment(callback: () -> Unit) {
        val c = Calendar.getInstance()
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        val key = sha256("s${h + m}")
        // val key = sha256("*Mkgj**oVC_VRb@#pM>iy^4H*T&,o)Gf&vjPN-]+oNufE2V,2Bs+TWqntq,H.8e,")

        // Log.d("MainActivity", "key: $key")
        val call = APIInterface.api.establishment(key.toLowerCase(Locale.getDefault()))
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    token = result
                    callback()
                }

            }
        })
    }

    fun shutdown() {
        if (token == null) {
            establishment {
                shutdown()
            }
            return
        }

        val call = APIInterface.api.shutdown(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("시스템 종료 요청을 보냈습니다.")
                    } else {
                        onError("시스템 종료 요청 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            shutdown()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun sleep() {
        if (token == null) {
            establishment {
                sleep()
            }
            return
        }

        val call = APIInterface.api.sleep(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("시스템 절전 요청을 보냈습니다.")
                    } else {
                        onError("시스템 절전 요청 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            sleep()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun wakeup() {
        if (token == null) {
            establishment {
                wakeup()
            }
            return
        }

        val call = APIInterface.api.wakeup(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("시스템 부팅 요청을 보냈습니다.")
                    } else {
                        onError("시스템 부팅 요청 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            wakeup()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
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
        call.enqueue(object : Callback<String> {
            private fun dead() {
                i.onDead()
            }

            private fun live() {
                i.onLive()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                dead()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "Online") {
                        live()
                    } else {
                        dead()
                    }
                } else {
                    // TODO: Http Response 처리
                    dead()
                }
            }
        })
    }

    fun start_fs() {
        if (token == null) {
            establishment {
                start_fs()
            }
            return
        }

        val call = APIInterface.api.start_fs(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("파일 서버 시작 요청을 보냈습니다.")
                    } else {
                        onError("파일 서버 중단 요청 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            start_fs()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun stop_fs() {
        if (token == null) {
            establishment {
                stop_fs()
            }
            return
        }

        val call = APIInterface.api.stop_fs(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("파일 서버 중단 요청을 보냈습니다.")
                    } else {
                        onError("파일 서버 중단 요청 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            stop_fs()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }



    fun start_tv() {
        if (token == null) {
            establishment {
                start_tv()
            }
            return
        }

        val call = APIInterface.api.start_tv(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("Teamviewer 시작 요청을 보냈습니다.")
                    } else {
                        onError("Teamviewr 시작 요청 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            start_fs()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }
    
    fun reboot_pi() {
        if (token == null) {
            establishment {
                start_tv()
            }
            return
        }

        val call = APIInterface.api.reboot_pi(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("pi 서버 리부트")
                    } else {
                        onError("pi 서버 리부트 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            reboot_pi()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }
    
    fun hetzer() {
        if (token == null) {
            establishment {
                hetzer()
            }
            return
        }

        val call = APIInterface.api.hetzer(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage("트윗 청소기 시작")
                    } else {
                        onError("트윗 청소기 시작 실패")
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            hetzer()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
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