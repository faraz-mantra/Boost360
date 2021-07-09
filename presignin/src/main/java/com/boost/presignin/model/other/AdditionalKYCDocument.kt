package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName

data class AdditionalKYCDocument(
        @SerializedName("DocumentFile")
        var documentFile: Any? = null,
        @SerializedName("DocumentName")
        var documentName: String? = null,
        @SerializedName("VerificationStatus")
        var verificationStatus: String? = null
)