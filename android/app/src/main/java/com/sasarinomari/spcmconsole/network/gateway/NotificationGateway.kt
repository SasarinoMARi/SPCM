package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.parameter.FcmTokenUpdateParameter
import com.sasarinomari.spcmconsole.network.parameter.NotifyParameter

internal class NotificationGateway : GatewayBase() {
    fun updateFcmToken(param: FcmTokenUpdateParameter, client: APIClient, callback: ((String)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.updateFcmToken(token, param)
            call.enqueue(object: GeneralHandler<String>(client, callback, { updateFcmToken(param, client, callback) }) {})
        }
    }

    fun sendFcm(param: NotifyParameter, client: APIClient, callback: ((String)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.sendFcm(token, param)
            call.enqueue(object: GeneralHandler<String>(client, callback, { sendFcm(param, client, callback) }) {})
        }
    }
}