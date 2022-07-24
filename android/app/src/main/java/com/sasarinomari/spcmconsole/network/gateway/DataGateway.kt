package com.sasarinomari.spcmconsole.network.gateway

import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface

internal class DataGateway : GatewayBase() {
    fun list(client: APIClient, tableName: String, callback: ((Array<JsonObject>)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.data_list(token, tableName)
            call.enqueue(object: GeneralHandler<Array<JsonObject>>(client, callback, { list(client, tableName, callback) }) {})
        }
    }

    fun random(client: APIClient, tableName: String, callback: ((Array<JsonObject>)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.data_random(token, tableName)
            call.enqueue(object: GeneralHandler<Array<JsonObject>>(client, callback, { random(client, tableName, callback) }) {})
        }
    }

    fun add(client: APIClient, tableName: String, requestBody: JsonObject, callback: ((JsonObject)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.data_add(token, tableName, requestBody)
            call.enqueue(object: GeneralHandler<JsonObject>(client, callback, { add(client, tableName, requestBody, callback) }) {})
        }
    }

    fun update(client: APIClient, tableName: String, requestBody: JsonObject, callback: ((JsonObject)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.data_update(token, tableName, requestBody)
            call.enqueue(object: GeneralHandler<JsonObject>(client, callback, { update(client, tableName, requestBody, callback) }) {})
        }
    }

    fun delete(client: APIClient, tableName: String, requestBody: JsonObject, callback: ((JsonObject)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.data_delete(token, tableName, requestBody)
            call.enqueue(object: GeneralHandler<JsonObject>(client, callback, { delete(client, tableName, requestBody, callback) }) {})
        }
    }
}