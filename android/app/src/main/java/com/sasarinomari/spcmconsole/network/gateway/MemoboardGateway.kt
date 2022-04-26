package com.sasarinomari.spcmconsole.network.gateway

import com.google.gson.JsonObject
import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.MEMOInterface
import com.sasarinomari.spcmconsole.network.model.TaskModel
import com.sasarinomari.spcmconsole.network.parameter.GetTaskParameter

internal class MemoboardGateway : GatewayBase() {
    fun createTask (task: TaskModel, client: APIClient, callback: ((JsonObject)->Unit)?) {
        val call = MEMOInterface.api.task_create(MEMOInterface.key, task)
        call.enqueue(object: GeneralHandler<JsonObject>(client, callback, { createTask(task, client, callback) }) {})
    }

    fun getTasks (options: GetTaskParameter, client: APIClient, callback: ((Array<TaskModel>)->Unit)?) {
        val call = MEMOInterface.api.task_list(MEMOInterface.key, options)
        call.enqueue(object: GeneralHandler<Array<TaskModel>>(client, callback, { getTasks(options, client, callback) }) {})
    }
}