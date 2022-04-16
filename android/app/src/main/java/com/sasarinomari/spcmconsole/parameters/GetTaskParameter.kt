package com.sasarinomari.spcmconsole.parameters

import com.google.gson.annotations.SerializedName

class GetTaskParameter {
    @SerializedName("Tags")
    var Tags: ArrayList<Int> = ArrayList()
    @SerializedName("Method")
    var Method: String = "U"

    @SerializedName("Date")
    var Date: DateObject = DateObject()

    @SerializedName("Limit")
    var Limit: Int? = null

    companion object {
        const val GET_TASK_METHOD_ALL = "A"     // 전체 할 일 불러오기
        const val GET_TASK_METHOD_UNDONE = "U"  // 완료되지 않은 할 일만 불러오기
        const val GET_TASK_METHOD_DONE = "D"    // 완료된 할 일만 불러오기
    }

    class DateObject {
        @SerializedName("From")
        var From: String? = ""

        @SerializedName("To")
        var To: String? = ""
    }

}