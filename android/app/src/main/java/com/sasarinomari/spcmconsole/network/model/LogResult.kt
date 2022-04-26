package com.sasarinomari.spcmconsole.network.model

import com.google.gson.annotations.SerializedName
import java.util.*

class LogResult {
    @SerializedName("created_at")
    var createdAt: Date = Date(0)
    @SerializedName("level")
    var level: Int = -1
    @SerializedName("subject")
    var subject: String = ""
    @SerializedName("content")
    var content: String = ""
    @SerializedName("from")
    var from: String? = null
}