package com.sasarinomari.spcmconsole.network.model

import com.google.gson.annotations.SerializedName

class FoodModel {
    @SerializedName("FoodId")
    var FoodId: Int = -1
    @SerializedName("Name")
    var Name: String = ""
    @SerializedName("Store")
    var Store: String = ""
    @SerializedName("Price")
    var Price: Int = -1
    @SerializedName("Phone")
    var Phone: Int? = null
    @SerializedName("Description")
    var Description: String? = null
    @SerializedName("Disabled")
    var Disabled: Int = 0
}