package com.catlogservice.model.account


import com.google.gson.annotations.SerializedName

data class PanDetailsN(
    @SerializedName("DocumentContent")
    var documentContent: String? = null,
    @SerializedName("DocumentName")
    var documentName: String? = null,
    @SerializedName("FileType")
    var fileType: String? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("Number")
    var number: String? = null
)