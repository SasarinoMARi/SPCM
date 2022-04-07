package com.sasarinomari.spcmconsole

import android.content.Context
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

abstract class APICall(private val context: Context) {
    abstract fun onError(message: String)
    abstract fun onMessage(message: String)

    private var token: String? = null
    private var mb_token = "8]&Ynz#?)K&3h:"


    fun establishment(callback: () -> Unit) {
        val c = Calendar.getInstance()
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        val key = ""

        // Log.d("MainActivity", "key: $key")
        val call = APIInterface.api.establishment(key)
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

    fun shutdown(callback: ()->Unit) {
        if (token == null) {
            establishment {
                shutdown(callback)
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
                        onMessage(context.getString(R.string.Confirm_Shutdown))
                    } else {
                        onError(context.getString(R.string.server_error))
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            shutdown(callback)
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

        val call = APIInterface.api.reboot(token!!)
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
                        onError(context.getString(R.string.server_error))
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

    fun wakeup(callback: ()->Unit) {
        if (token == null) {
            establishment {
                wakeup(callback)
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
                        onMessage(context.getString(R.string.Confirm_Wakeup))
                    } else {
                        onError(context.getString(R.string.server_error))
                    }
                } else {
                    if (response.code() == 403) {
                        establishment {
                            wakeup(callback)
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }


    interface lookupInterface {
        fun onComputerOffline()
        fun onComputerOnline()
        fun onServerOffline()
    }

    fun lookup(i: lookupInterface) {
        val call = APIInterface.api.lookup()
        call.enqueue(object : Callback<String> {
            private fun dead() {
                i.onComputerOffline()
            }

            private fun live() {
                i.onComputerOnline()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                i.onServerOffline()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "Online") {
                        i.onComputerOnline()
                    } else {
                        i.onComputerOffline()
                    }
                } else {
                    i.onServerOffline()
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

        val call = APIInterface.api.startFileServer(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage(context.getString(R.string.Confirm_FileServer))
                    } else {
                        onError(context.getString(R.string.server_error))
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


    fun start_tv() {
        if (token == null) {
            establishment {
                start_tv()
            }
            return
        }

        val call = APIInterface.api.startRdpServer(token!!)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {
                        onMessage(context.getString(R.string.Confirm_RdpServer))
                    } else {
                        onError(context.getString(R.string.server_error))
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
                reboot_pi()
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
                        onMessage(context.getString(R.string.Confirm_PiReboot))
                    } else {
                        onError(context.getString(R.string.server_error))
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

    fun sendFcm(title: String, content: String, callback: ()->Unit) {
        if (token == null) {
            establishment {
                sendFcm(title, content, callback)
            }
            return
        }

        val data = APIInterface.sendFcmParam(title, content)
        val call = APIInterface.api.sendFcm(token!!, data)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback()
                } else {
                    onMessage("${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }
        })
    }

    fun updateFcmToken(fcmid: String, callback: ()->Unit) {
        if (token == null) {
            establishment {
                updateFcmToken(fcmid, callback)
            }
            return
        }

        val data = APIInterface.updateFcmTokenParam(fcmid)
        val call = APIInterface.api.updateFcmToken(token!!, data)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback()
                } else {
                    onMessage("${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }
        })
    }


    fun volume(amount: Int) {
        if (token == null) {
            establishment { volume(amount) }
            return
        }

        val call = APIInterface.api.volume(token!!, amount)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") {   }
                    else { onError(context.getString(R.string.server_error)) }
                } else {
                    if (response.code() == 403) { establishment { volume(amount) } }
                    else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun play(src: String) {
        if (token == null) {
            establishment { play(src) }
            return
        }

        val call = APIInterface.api.play(token!!, src)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) { onError(t.toString()) }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") { onMessage(context.getString(R.string.Confirm_Play)) }
                    else { onError(context.getString(R.string.server_error)) }
                } else {
                    if (response.code() == 403) { establishment { play(src) } }
                    else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun mute(option: Int = 2) {
        if (token == null) {
            establishment { mute(option) }
            return
        }

        val call = APIInterface.api.mute(token!!, option)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) { onError(t.toString()) }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result == "OK") { onMessage(context.getString(R.string.Confirm_Mute)) }
                    else { onError(context.getString(R.string.server_error)) }
                } else {
                    if (response.code() == 403) { establishment { mute(option) } }
                    else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun foodDispenser(callback:(FoodModel)->Unit) {
        if (token == null) {
            establishment { foodDispenser(callback) }
            return
        }

        val call = APIInterface.api.foodDispenser(token!!)
        call.enqueue(object : Callback<FoodModel> {
            override fun onFailure(call: Call<FoodModel>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<FoodModel>, response: Response<FoodModel>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    callback(result)
                } else {
                    if (response.code() == 403) { establishment { foodDispenser(callback) } }
                    else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    /**
     * 2022-03-31 최종 커밋 기준 복붙
     */
    fun createTask(task: com.sasarinomari.spcmconsole.Memoboard.TaskModel, callback: () -> Unit) {
        val call = com.sasarinomari.spcmconsole.Memoboard.APIInterface.api.task_create(mb_token, task)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    Log.d("API_NEW_TASK", result.toString())
                    callback()
                } else {
                    onMessage("${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                onError(t.toString())
            }
        })
    }

    fun getTasks(options: com.sasarinomari.spcmconsole.Memoboard.GetTaskOptions, callback: (Array<com.sasarinomari.spcmconsole.Memoboard.TaskModel>)->Unit) {
        val call = com.sasarinomari.spcmconsole.Memoboard.APIInterface.api.task_list(mb_token!!, options)
        call.enqueue(object : Callback<Array<com.sasarinomari.spcmconsole.Memoboard.TaskModel>> {
            override fun onResponse(call: Call<Array<com.sasarinomari.spcmconsole.Memoboard.TaskModel>>, response: Response<Array<com.sasarinomari.spcmconsole.Memoboard.TaskModel>>) {
                if (response.isSuccessful) {
                    val tasks = response.body()!!
                    callback(tasks)
                } else {
                    onMessage("${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Array<com.sasarinomari.spcmconsole.Memoboard.TaskModel>>, t: Throwable) {
                onError(t.toString())
            }
        })
    }
}