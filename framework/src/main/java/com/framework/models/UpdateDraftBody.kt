package com.framework.models

import com.google.gson.annotations.SerializedName

data class UpdateDraftBody(
    @SerializedName("clientId")
    val clientId: String?=null,
    @SerializedName("content")
    val content: String?=null,
    @SerializedName("fpTag")
    val fpTag: String?=null,
    @SerializedName("imageUri")
    val imageUri: String?=null
)