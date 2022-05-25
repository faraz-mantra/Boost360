package com.nowfloats.instagram.graph

import com.google.gson.annotations.SerializedName

data class IGUserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String
)