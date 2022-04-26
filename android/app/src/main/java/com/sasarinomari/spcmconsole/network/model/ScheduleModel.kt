package com.sasarinomari.spcmconsole.network.model

import com.google.gson.annotations.SerializedName

class ScheduleModel() {
    @SerializedName("idx")
    var idx: Int = -1
    @SerializedName("active")
    var active: Int = 0
    @SerializedName("name")
    var name: String = ""
    @SerializedName("cron")
    var cron: String = ""
    @SerializedName("command")
    var command: String = ""
    @SerializedName("created_at")
    var created_at: String = ""

    constructor(id: Int, active: Boolean) : this() {
        this.idx = id
        this.active = if(active) 1 else 0
    }
}