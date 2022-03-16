package com.sasarinomari.spcmconsole

import android.database.Observable
import com.google.gson.GsonBuilder
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

    @GET("shutdown")
    fun shutdown(@Header("token") token:String): Call<String>

    @GET("sleep")
    fun sleep(@Header("token") token:String): Call<String>

    @GET("wakeup")
    fun wakeup(@Header("token") token:String): Call<String>

    @GET("start-fs")
    fun start_fs(@Header("token") token:String): Call<String>

    @GET("stop-fs")
    fun stop_fs(@Header("token") token:String): Call<String>

    @GET("start-tv")
    fun start_tv(@Header("token") token:String): Call<String>

    @GET("reboot-pi")
    fun reboot_pi(@Header("token") token:String): Call<String>

    @GET("hetzer")
    fun hetzer(@Header("token") token:String): Call<String>

    @POST("fcm_send")
    fun sendFcm(@Header("token") token:String, @Body body: sendFcmParam): Call<String>
    class sendFcmParam(val title: String, val body: String)
    @POST("fcm_update_token")
    fun updateFcmToken(@Header("token") token:String, @Body body: updateFcmTokenParam): Call<String>
    class updateFcmTokenParam(val token: String)

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