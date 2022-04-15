package com.sasarinomari.spcmconsole

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.parameters.*
import com.sasarinomari.spcmconsole.results.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class APICall(private val context: Context) {
    abstract fun onError(message: String)
    abstract fun onMessage(message: String)

    private var token: String? = null

    fun establishment(callback: () -> Unit) {
        val call = APIInterface.api.establishment(APIInterface.getToken())
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

    fun lookup(callback: (LookupResult) -> Unit) {
        val call = APIInterface.api.lookup()
        call.enqueue(object : Callback<LookupResult> {
            override fun onFailure(call: Call<LookupResult>, t: Throwable) {
                val result = LookupResult()
                result.Server.Status = 0
                callback(result)
            }

            override fun onResponse(call: Call<LookupResult>, response: Response<LookupResult>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    callback(result)
                } else {
                    onFailure(call, Exception("Response is not successful"))
                }
            }
        })
    }

    fun startFileServer() {
        if (token == null) {
            establishment {
                startFileServer()
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
                            startFileServer()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun startTeamviewerServer() {
        if (token == null) {
            establishment {
                startTeamviewerServer()
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
                            startFileServer()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }
    
    fun rebootServer() {
        if (token == null) {
            establishment {
                rebootServer()
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
                            rebootServer()
                        }
                    } else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun sendFcm(title: String, content: String, callback: ()->Unit) {
        if (token == null) {
            establishment {
                sendFcm(title, content, callback)
            }
            return
        }

        val data = NotifyParameter(title, content)
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

        val data = FcmTokenUpdateParameter(fcmid)
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

    fun setVolume(amount: Int) {
        if (token == null) {
            establishment { setVolume(amount) }
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
                    if (response.code() == 403) { establishment { setVolume(amount) } }
                    else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun openUrl(src: String) {
        if (token == null) {
            establishment { openUrl(src) }
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
                    if (response.code() == 403) { establishment { openUrl(src) } }
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

    fun foodDispenser(callback:(FoodResult)->Unit) {
        if (token == null) {
            establishment { foodDispenser(callback) }
            return
        }

        val call = APIInterface.api.foodDispenser(token!!)
        call.enqueue(object : Callback<FoodResult> {
            override fun onFailure(call: Call<FoodResult>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<FoodResult>, response: Response<FoodResult>) {
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

    fun getLogs(logLevel: Int, page: Int, callback:(Array<LogResult>)->Unit) {
        if (token == null) {
            establishment { getLogs(logLevel, page, callback) }
            return
        }

        val call = APIInterface.api.logs(token!!, logLevel, page)
        call.enqueue(object : Callback<Array<LogResult>> {
            override fun onFailure(call: Call<Array<LogResult>>, t: Throwable) {
                onError(t.toString())
            }

            override fun onResponse(call: Call<Array<LogResult>>, response: Response<Array<LogResult>>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    callback(result)
                } else {
                    if (response.code() == 403) { establishment { getLogs(logLevel, page, callback) } }
                    else onMessage("${response.code()} : ${response.message()}")
                }
            }
        })
    }

    fun log(level: Int, subject: String, content:String, callback: ()->Unit) {
        if (token == null) {
            establishment {
                log(level, subject, content, callback)
            }
            return
        }

        val data = LogParameter(level, subject, content)
        val call = APIInterface.api.log(token!!, data)
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

    fun createTask(task: TaskModel, callback: () -> Unit) {
        val call = APIInterface.memoApi.task_create(APIInterface.getMemoToken(), task)
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

    fun getTasks(options: GetTaskParameter, callback: (Array<TaskModel>)->Unit) {
        val call = APIInterface.memoApi.task_list(APIInterface.getMemoToken(), options)
        call.enqueue(object : Callback<Array<TaskModel>> {
            override fun onResponse(call: Call<Array<TaskModel>>, response: Response<Array<TaskModel>>) {
                if (response.isSuccessful) {
                    val tasks = response.body()!!
                    callback(tasks)
                } else {
                    onMessage("${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Array<TaskModel>>, t: Throwable) {
                onError(t.toString())
            }
        })
    }
}