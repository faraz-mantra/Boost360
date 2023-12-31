package com.appservice.model.accountDetails


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdditionalKYCDocument(
  @SerializedName("DocumentFile")
  var documentFile: Any? = null,
  @SerializedName("DocumentName")
  var documentName: String? = null,
  @SerializedName("VerificationStatus")
  var verificationStatus: String? = null
) : Serializable