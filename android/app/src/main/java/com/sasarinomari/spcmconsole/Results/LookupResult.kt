package com.sasarinomari.spcmconsole.Results

import com.google.gson.annotations.SerializedName

class LookupResult {
    @SerializedName("server")
    var Server: LookupContent = LookupContent()

    @SerializedName("pc")
    var PC: LookupContent = LookupContent()
}

class LookupContent {
    @SerializedName("status")
    var Status: Int = -1

    @SerializedName("temp")
    var Temoerature: String? = null

    companion object {
        const val STATUS_NOT_INITIALIZED = -1
        const val STATUS_OFFLINE = 0
        const val STATUS_ONLINE = 1
    }
}

