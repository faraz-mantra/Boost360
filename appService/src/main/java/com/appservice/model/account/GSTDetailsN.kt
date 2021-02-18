package com.appservice.model.account


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GSTDetailsN(
    @SerializedName("DocumentContent")
    var documentContent: String? = null,
    @SerializedName("DocumentName")
    var documentName: String? = null,
    @SerializedName("FileType")
    var fileType: String? = null,
    @SerializedName("GSTIN")
    var gSTIN: String? = null
): Serializable