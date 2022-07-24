package sasarinomari.genericdatahelper

import com.google.gson.JsonObject
import retrofit2.Callback

class DataGateway {
    private var _beforeRequest: (()->Unit)? = null

    fun setBaseUrl(url: String) = DataInterface.setBaseUrl(url)
    fun clearCustomHeader() = DataInterface.clearCustomHeader()
    fun addCustomHeader(key: String, value: String) = DataInterface.addCustomHeader(key, value)
    fun beforeRequest(action: ()->Unit) { this._beforeRequest = action }

    fun list(tableName: String, handler: Callback<Array<JsonObject>>) {
        _beforeRequest?.invoke()
        val call = DataInterface.client.list(tableName)
        call.enqueue(handler)
    }

    fun random(tableName: String, handler: Callback<Array<JsonObject>>) {
        _beforeRequest?.invoke()
        val call = DataInterface.client.random(tableName)
        call.enqueue(handler)
    }

    fun add(tableName: String, requestBody: JsonObject, handler: Callback<JsonObject>) {
        _beforeRequest?.invoke()
        val call = DataInterface.client.add(tableName, requestBody)
        call.enqueue(handler)
    }

    fun update(tableName: String, requestBody: JsonObject, handler: Callback<JsonObject>) {
        _beforeRequest?.invoke()
        val call = DataInterface.client.update(tableName, requestBody)
        call.enqueue(handler)
    }

    fun delete(tableName: String, requestBody: JsonObject, handler: Callback<JsonObject>) {
        _beforeRequest?.invoke()
        val call = DataInterface.client.delete(tableName, requestBody)
        call.enqueue(handler)
    }
}