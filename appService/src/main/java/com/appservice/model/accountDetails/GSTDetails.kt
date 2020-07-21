package com.appservice.model.accountDetails


import com.google.gson.annotations.SerializedName

data class GSTDetails(
    @SerializedName("DocumentFile")
    var documentFile: Any? = null,
    @SerializedName("DocumentName")
    var documentName: String? = null,
    @SerializedName("GSTIN")
    var gSTIN: String? = null,
    @SerializedName("VerificationStatus")
    var verificationStatus: String? = null
)