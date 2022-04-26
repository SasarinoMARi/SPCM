package com.sasarinomari.spcmconsole.network.gateway

import com.sasarinomari.spcmconsole.network.APIClient
import com.sasarinomari.spcmconsole.network.SPCMInterface

internal class DesktopGateway : GatewayBase() {
    fun wakeup(client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.wakeup(token)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { wakeup(client, callback) }) {})
        }
    }

    fun shutdown(client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.shutdown(token)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { shutdown(client, callback) }) {})
        }
    }

    fun startFileServer(client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.startFileServer(token)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { startFileServer(client, callback) }) {})
        }
    }

    fun startTeamviewerServer(client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.startRdpServer(token)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { startTeamviewerServer(client, callback) }) {})
        }
    }

    fun setVolume(amount: Int, client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.volume(token, amount)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { setVolume(amount, client, callback) }) {})
        }
    }

    fun openUrl(url: String, client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.play(token, url)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { openUrl(url, client, callback) }) {})
        }
    }

    fun mute(option: Int, client: APIClient, callback: ((Unit)->Unit)?) {
        client.establishment { token ->
            val call = SPCMInterface.api.mute(token, option)
            call.enqueue(object: GeneralHandler<Unit>(client, callback, { mute(option, client, callback) }) {})
        }
    }
}