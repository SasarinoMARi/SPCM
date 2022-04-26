package com.sasarinomari.spcmconsole.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.model.TaskModel
import com.sasarinomari.spcmconsole.network.parameter.GetTaskParameter
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MEMOInterface {
    @POST("task/list")
    fun task_list(@Header("key") token:String, @Body body: GetTaskParameter): Call<Array<TaskModel>>
    @POST("task/create")
    fun task_create(@Header("key") token:String, @Body body: TaskModel): Call<JsonObject>

    companion object {
        private val gson = GsonBuilder()
            .setLenient()
            .create()

        private val okHttpClient = OkHttpClient
            .Builder()
            .build()

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Secret.MEMO_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        val api: MEMOInterface by lazy {
            retrofit.create(MEMOInterface::class.java)
        }

        val key: String = Secret.MEMO_KEY
    }
}