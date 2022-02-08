package com.framework.models

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateDraftBody(
    @SerializedName("clientId")
    var clientId: String?=null,
    @SerializedName("content")
    var content: String?=null,
    @SerializedName("fpTag")
    var fpTag: String?=null,
    @SerializedName("imageUri")
    var imageUri: String?=null
):Serializable