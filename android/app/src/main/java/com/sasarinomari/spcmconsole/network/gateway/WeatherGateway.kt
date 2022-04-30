package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface
import com.sasarinomari.spcmconsole.network.model.WeatherModel

internal class WeatherGateway : GatewayBase() {
    fun getWeather (client: APIClient, callback: ((WeatherModel)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.getWeather(token)
            call.enqueue(object: GeneralHandler<WeatherModel>(client, callback, { getWeather(client, callback) }) {})
        }
    }
}