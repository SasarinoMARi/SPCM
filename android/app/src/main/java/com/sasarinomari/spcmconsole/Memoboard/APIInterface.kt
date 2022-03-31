package com.sasarinomari.spcmconsole.Memoboard

/**
 * 2022-03-31 최종 커밋 기준 복붙
 */
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIInterface {
    @POST("task/list")
    fun task_list(@Header("key") token:String, @Body body:GetTaskOptions): Call<Array<TaskModel>>
    @POST("task/create")
    fun task_create(@Header("key") token:String, @Body body: TaskModel): Call<JsonObject>
    @POST("task/delete")
    fun task_delete(@Header("key") token:String, @Body body: TaskModel): Call<JsonObject>
    @POST("task/modify")
    fun task_modify(@Header("key") token:String, @Body body: TaskModel): Call<JsonObject>
    @POST("task/set_tag")
    fun task_set_tag(@Header("key") token:String, @Body body: set_tag_DTO): Call<JsonObject>
    class set_tag_DTO(val TaskId: Int, val TagIds: Array<Int>)


    @GET("tag/list")
    fun tag_list(@Header("key") token:String): Call<Array<TagModel>>
    @POST("tag/create")
    fun tag_create(@Header("key") token:String, @Body body: create_tag_DTO): Call<JsonObject>
    class create_tag_DTO(val Name: String)
    @POST("tag/delete")
    fun tag_delete(@Header("key") token:String, @Body body: TagModel): Call<JsonObject>
    @POST("tag/modify")
    fun tag_modify(@Header("key") token:String, @Body body: TagModel): Call<JsonObject>


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

        public val api: APIInterface by lazy {
            retrofit.create(APIInterface::class.java)
        }
    }
}