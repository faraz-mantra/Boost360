package com.appservice.model.paymentKyc.update


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KycSet(
  @SerializedName("additionalDocument")
  var additionalDocument: String? = null,
  @SerializedName("bankAccountNumber")
  var bankAccountNumber: String? = null,
  @SerializedName("bankAccountStatement")
  var bankAccountStatement: String? = null,
  @SerializedName("bankBranchName")
  var bankBranchName: String? = null,
  @SerializedName("hasexisistinginstamojoaccount?")
  var hasexisistinginstamojoaccount: String? = null,
  @SerializedName("ifsc")
  var ifsc: String? = null,
  @SerializedName("instamojoEmail")
  var instamojoEmail: String? = null,
  @SerializedName("instamojoPassword")
  var instamojoPassword: String? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("nameOfBank")
  var nameOfBank: String? = null,
  @SerializedName("nameOfBankAccountHolder")
  var nameOfBankAccountHolder: String? = null,
  @SerializedName("nameOfPanHolder")
  var nameOfPanHolder: String? = null,
  @SerializedName("panCardDocument")
  var panCardDocument: String? = null,
  @SerializedName("panNumber")
  var panNumber: String? = null
) : Serializable