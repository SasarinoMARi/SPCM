package com.sasarinomari.spcmconsole.network.parameter

import com.google.gson.annotations.SerializedName

class GetTaskParameter {
    @SerializedName("tags")
    var tags: ArrayList<Int> = ArrayList()
    @SerializedName("method")
    var method: Int = TASK_ONLY_UNDONE
    @SerializedName("limit")
    var limit = 30
    @SerializedName("page")
    var page = 0

    companion object {
        const val TASK_ONLY_UNDONE = 0          // 완료되지 않은 할 일만 불러오기
        const val TASK_ONLY_DONE = 1            // 완료된 할 일만 불러오기
        const val TASK_ALL = 2                  // 전체 할 일 불러오기
    }

    class DateObject {
        @SerializedName("From")
        var From: String? = ""

        @SerializedName("To")
        var To: String? = ""
    }
}