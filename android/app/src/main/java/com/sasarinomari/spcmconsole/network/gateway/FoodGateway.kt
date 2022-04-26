package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.model.FoodResult

internal class FoodGateway : GatewayBase() {
    fun dispense (client: APIClient, callback: ((FoodResult)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.foodDispenser(token)
            call.enqueue(object: GeneralHandler<FoodResult>(client, callback, { dispense(client, callback) }) {})
        }
    }
}