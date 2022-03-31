package com.sasarinomari.spcmconsole.Memoboard

import com.google.gson.annotations.SerializedName


class TagModel {
    @SerializedName("TagId")
    var TagId: Int? = null
    @SerializedName("Name")
    var Name: String? = null
}