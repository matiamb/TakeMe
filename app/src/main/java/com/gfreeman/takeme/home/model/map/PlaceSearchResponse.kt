package com.gfreeman.takeme.home.model.map

import com.google.gson.annotations.SerializedName


data class PlaceSearchResponse(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val long: String
)
