package com.sasarinomari.spcmconsole.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.parameter.*
import com.sasarinomari.spcmconsole.network.model.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface SPCMInterface {
    @GET("establishment")
    fun establishment(@Header("key") key:String): Call<String>
    @GET("lookup")
    fun lookup(): Call<LookupResult>
    @GET("reboot")
    fun reboot(@Header("token") token:String): Call<Unit>
    @GET("logs")
    fun logs(@Header("token") token:String, @Header("level") level:Int, @Header("page") page:Int): Call<Array<LogResult>>
    @POST("log")
    fun log(@Header("token") token:String, @Body body: LogParameter): Call<Unit>
    @GET("header_image")
    fun header_image(@Header("token") token:String): Call<Array<JsonObject>>

    @GET("schedule/reload")
    fun reloadSchedule(@Header("token") token:String): Call<Unit>
    @GET("schedule/get")
    fun getSchedules(@Header("token") token:String): Call<Array<ScheduleModel>>
    @POST("schedule/set")
    fun setSchedules(@Header("token") token:String, @Body body: ScheduleModel): Call<Unit>

    @GET("power/wakeup")
    fun wakeup(@Header("token") token:String): Call<Unit>
    @GET("power/shutdown")
    fun shutdown(@Header("token") token:String): Call<Unit>

    @GET("file_server/start")
    fun startFileServer(@Header("token") token:String): Call<Unit>
    @GET("file_server/stop")
    fun stopFileServer(@Header("token") token:String): Call<Unit>

    @GET("rdp_server/start")
    fun startRdpServer(@Header("token") token:String): Call<Unit>

    @GET("media/volume")
    fun volume(@Header("token") token:String, @Header("amount") amount:Int): Call<Unit>
    @GET("media/mute")
    fun mute(@Header("token") token:String, @Header("option") amount:Int): Call<Unit>
    @GET("media/play")
    fun play(@Header("token") token:String, @Header("src") src:String): Call<Unit>

    @POST("noti/send_fcm")
    fun sendFcm(@Header("token") token:String, @Body body: NotifyParameter): Call<Unit>
    @POST("noti/update_fcm_token")
    fun updateFcmToken(@Header("token") token:String, @Body body: FcmTokenUpdateParameter): Call<Unit>

    @GET("food_dispenser")
    fun foodDispenser(@Header("token") token:String): Call<FoodResult>


    companion object {
        private val gson = GsonBuilder()
            .setLenient()
            .create()

        private val okHttpClient = OkHttpClient
            .Builder()
            .build()

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Secret.SPCM_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        val api: SPCMInterface by lazy {
            retrofit.create(SPCMInterface::class.java)
        }

        val key: String get() { return Secret.SPCM_KEY }
    }
}