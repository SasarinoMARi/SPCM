package com.sasarinomari.spcmconsole

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.parameters.*
import com.sasarinomari.spcmconsole.results.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.MessageDigest
import java.util.*

interface APIInterface {
    @GET("establishment")
    fun establishment(@Header("key") key:String): Call<String>
    @GET("lookup")
    fun lookup(): Call<LookupResult>
    @GET("reboot")
    fun reboot_pi(@Header("token") token:String): Call<String>
    @GET("logs")
    fun logs(@Header("token") token:String, @Header("level") level:Int, @Header("page") page:Int): Call<Array<LogResult>>
    @POST("log")
    fun log(@Header("token") token:String, @Body body: LogParameter): Call<String>
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
    fun sendFcm(@Header("token") token:String, @Body body: NotifyParameter): Call<String>
    @POST("noti/update_fcm_token")
    fun updateFcmToken(@Header("token") token:String, @Body body: FcmTokenUpdateParameter): Call<String>

    @GET("food_dispenser")
    fun foodDispenser(@Header("token") token:String): Call<FoodResult>


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

        val api: APIInterface by lazy {
            retrofit.create(APIInterface::class.java)
        }

        fun getToken() : String {
            val c = Calendar.getInstance()
            val h = c.get(Calendar.HOUR_OF_DAY)
            val m = c.get(Calendar.MINUTE)
            return ""
        }

        val memoApi: MemoboardAPIInterface
            get() {
                return MemoboardAPIInterface.api
            }

        fun getMemoToken() : String {
            return ""
        }
    }

    interface MemoboardAPIInterface {
        @POST("task/list")
        fun task_list(@Header("key") token:String, @Body body: GetTaskParameter): Call<Array<TaskModel>>
        @POST("task/create")
        fun task_create(@Header("key") token:String, @Body body: TaskModel): Call<JsonObject>

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

            val api: MemoboardAPIInterface by lazy {
                retrofit.create(MemoboardAPIInterface::class.java)
            }
        }
    }
}