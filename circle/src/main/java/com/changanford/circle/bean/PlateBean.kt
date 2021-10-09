package com.changanford.circle.bean
import com.google.gson.annotations.SerializedName


data class PlateBean(
    @SerializedName("action")
    val action: List<String>,
    @SerializedName("plate")
    val plate: List<Plate>
)

data class Plate(
    @SerializedName("actionCode")
    val actionCode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("plate")
    val plate: Int
)