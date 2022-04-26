package com.sasarinomari.spcmconsole.network.model

import com.google.gson.annotations.SerializedName

class ScheduleModel {
    @SerializedName("idx")
    var idx: Int = -1
    @SerializedName("active")
    var active: Boolean = false
    @SerializedName("name")
    var name: String = ""
    @SerializedName("cron")
    var cron: String = ""
    @SerializedName("command")
    var command: String = ""
    @SerializedName("created_at")
    var created_at: String = ""
}