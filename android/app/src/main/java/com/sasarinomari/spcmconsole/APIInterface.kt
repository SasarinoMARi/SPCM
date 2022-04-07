package com.sasarinomari.spcmconsole

import android.database.Observable
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.CookieManager

interface APIInterface {
    @GET("establishment")
    fun establishment(@Header("key") key:String): Call<String>
    @GET("lookup")
    fun lookup(): Call<String>
    @GET("reboot")
    fun reboot_pi(@Header("token") token:String): Call<String>
    @GET("hetzer")
    fun hetzer(@Header("token") token:String): Call<String>

    @GET("power/wakeup")
    fun wakeup(@Header("token") token:String): Call<String>
    @GET("power/shutdown")
    fun shutdown(@Header("token") token:String): Call<String>
    @GET("power/reboot")
    fun reboot(@Header("token") token:String): Call<String>

    @GET("file_server/start")
    fun startFileServer(@Header("token") token:String): Call<String>
    @GET("file_server/stop")
    fun stopFileServer(@Header("token") token:String): Call<String>

    @GET("rdp_server/start")
    fun startRdpServer(@Header("token") token:String): Call<String>

    @GET("media/volume")
    fun volume(@Header("token") token:String, @Header("amount") amount:Int): Call<String>
    @GET("media/mute")
    fun mute(@Header("token") token:String, @Header("option") amount:Int): Call<String>
    @GET("media/play")
    fun play(@Header("token") token:String, @Header("src") src:String): Call<String>

    @POST("noti/send_fcm")
    fun sendFcm(@Header("token") token:String, @Body body: sendFcmParam): Call<String>
    class sendFcmParam(val title: String, val body: String)
    @POST("noti/update_fcm_token")
    fun updateFcmToken(@Header("token") token:String, @Body body: updateFcmTokenParam): Call<String>
    class updateFcmTokenParam(val token: String)

    @GET("food_dispenser")
    fun foodDispenser(@Header("token") token:String): Call<FoodModel>

    companion object {
        private val BASE_URL = ""

        private val gson = GsonBuilder()
            .setLenient()
            .create()

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        val api: APIInterface by lazy {
            retrofit.create(APIInterface::class.java)
        }
    }
}