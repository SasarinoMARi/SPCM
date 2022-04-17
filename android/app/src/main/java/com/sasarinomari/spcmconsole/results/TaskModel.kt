package com.sasarinomari.spcmconsole.results

import com.google.gson.annotations.SerializedName


class TaskModel {
    @SerializedName("idx")
    var idx: Int = -1
    @SerializedName("name")
    var name: String = ""
    @SerializedName("description")
    var description: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("time")
    var time: String? = null
    @SerializedName("priority")
    var priority: Short? = null
    @SerializedName("done")
    var done: Int? = null
    @SerializedName("tags")
    var tags: String? = null

    fun getTagList() : List<String> {
        return if(tags==null) ArrayList()
        else tags!!.split(",")
    }
}