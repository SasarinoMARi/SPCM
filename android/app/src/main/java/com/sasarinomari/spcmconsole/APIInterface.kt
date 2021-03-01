package com.sasarinomari.spcmconsole

import android.database.Observable
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.CookieManager

interface APIInterface {
    @GET("establishment")
    fun establishment(@Header("key") key:String): Call<TokenModel>

    @GET("lookup")
    fun lookup(): Call<ResultModel>

    @GET("shutdown")
    fun shutdown(@Header("token") token:String): Call<ResultModel>

    @GET("sleep")
    fun sleep(@Header("token") token:String): Call<ResultModel>

    @GET("wakeup")
    fun wakeup(@Header("token") token:String): Call<ResultModel>



    companion object {
        private val BASE_URL = "https://spcmc.herokuapp.com"

        private val gson = GsonBuilder()
            .setLenient()
            .create()

        val okHttpClient = OkHttpClient
            .Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        val api: APIInterface by lazy {
            retrofit.create(APIInterface::class.java)
        }
    }
}

class TokenModel(
    @SerializedName("error")
    val errorCode: Long,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String
)

class ResultModel(
    @SerializedName("error")
    val errorCode: Long,

    @SerializedName("message")
    val message: String
)