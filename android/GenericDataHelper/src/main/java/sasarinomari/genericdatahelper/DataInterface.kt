package sasarinomari.genericdatahelper

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

internal interface DataInterface {
    @GET("{table_name}/list")
    fun list(@Path(value = "table_name", encoded = true) tableName: String) : Call<Array<JsonObject>>

    @GET("{table_name}/random")
    fun random(@Path(value = "table_name", encoded = true) tableName: String,
               @Header("pick_count") pickCount: Int = 1) : Call<Array<JsonObject>>

    @POST("{table_name}/add")
    fun add(@Path(value = "table_name", encoded = true) tableName: String,
            @Body body: JsonObject) : Call<JsonObject>

    @POST("{table_name}/update")
    fun update(@Path(value = "table_name", encoded = true) tableName: String,
               @Body body: JsonObject) : Call<JsonObject>

    @POST("{table_name}/delete")
    fun delete(@Path(value = "table_name", encoded = true) tableName: String,
               @Body body: JsonObject) : Call<JsonObject>


    companion object {
        private val gson = GsonBuilder()
            .setLenient()
            .create()

        private val okHttpClient : OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                    for (header in customHeaders) {
                        request.addHeader(header.key, header.value)
                    }
                    return@addInterceptor chain.proceed(request.build())
                }
                .build()
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        internal val client: DataInterface by lazy {
            retrofit.create(DataInterface::class.java)
        }

        private lateinit var url: String
        private val customHeaders = HashMap<String, String>()


        /**
         * Http 요청에 사용할 url을 초기화합니다.
         */
        internal fun setBaseUrl(url: String) {  this.url = url }

        /**
         * Http 요청에 커스텀 헤더를 추가합니다.
         */
        internal fun addCustomHeader(key: String, value: String) = customHeaders.put(key, value)

        internal fun clearCustomHeader() = customHeaders.clear()
    }
}