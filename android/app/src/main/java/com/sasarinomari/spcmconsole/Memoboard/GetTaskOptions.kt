package com.sasarinomari.spcmconsole.Memoboard

import com.google.gson.annotations.SerializedName

class GetTaskOptions {
    @SerializedName("Tags")
    var Tags: ArrayList<Int> = ArrayList()
    @SerializedName("Method")
    var Method: String = "U"

    companion object {
        const val GET_TASK_METHOD_ALL = "A"     // 전체 할 일 불러오기
        const val GET_TASK_METHOD_UNDONE = "U"  // 완료되지 않은 할 일만 불러오기
        const val GET_TASK_METHOD_DONE = "D"    // 완료된 할 일만 불러오기
    }
}