package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.model.FoodResult

internal class FoodGateway : GatewayBase() {
    fun dispense (client: APIClient, callback: ((FoodResult)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.pickRandomFood(token)
            call.enqueue(object: GeneralHandler<FoodResult>(client, callback, { dispense(client, callback) }) {})
        }
    }

    fun getList (client: APIClient, callback: ((Array<FoodResult>)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.getFoodList(token)
            call.enqueue(object: GeneralHandler<Array<FoodResult>>(client, callback, { getList(client, callback) }) {})
        }
    }
}