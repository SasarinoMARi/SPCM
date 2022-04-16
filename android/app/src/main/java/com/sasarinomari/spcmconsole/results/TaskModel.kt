package com.sasarinomari.spcmconsole.results

import com.google.gson.annotations.SerializedName

class TaskModel {
    @SerializedName("TaskId")
    var TaskId: Int? = null
    @SerializedName("Name")
    var Name: String? = null
    @SerializedName("Description")
    var Description: String? = null
    @SerializedName("Date")
    var Date: String? = null
    @SerializedName("Time")
    var Time: String? = null
    @SerializedName("Priority")
    var Priority: Short? = null
    @SerializedName("Done")
    var Done: Int? = null
    val Status: Boolean
        get() {return Done==1}
    @SerializedName("Tags")
    var Tags: String? = null
    val TagList: List<String>
        get() {
            return if(Tags==null) ArrayList()
            else Tags!!.split(",")
        }
}