package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.model.ScheduleModel

internal class ScheduleGateway: GatewayBase() {
    fun reload(client: APIClient, callback: ((String)->Unit)?) {
        client.establishment {
            val call = SPCMInterface.api.reloadSchedule(it)
            call.enqueue(object: GatewayBase.GeneralHandler<String>(client, callback, { reload(client, callback) }) {})
        }
    }

    fun get(client: APIClient, callback: ((Array<ScheduleModel>)->Unit)?) {
        client.establishment {
            val call = SPCMInterface.api.getSchedules(it)
            call.enqueue(object: GatewayBase.GeneralHandler<Array<ScheduleModel>>(client, callback, { get(client, callback) }) {})
        }
    }

    fun set(param: ScheduleModel, client: APIClient, callback: ((String)->Unit)?) {
        client.establishment {
            val call = SPCMInterface.api.setSchedules(it, param)
            call.enqueue(object: GatewayBase.GeneralHandler<String>(client, callback, { set(param, client, callback) }) {})
        }
    }
}