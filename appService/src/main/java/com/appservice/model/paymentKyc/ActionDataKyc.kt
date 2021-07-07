package com.appservice.model.paymentKyc


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ActionDataKyc(
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
  @SerializedName("nameOfBank")
  var nameOfBank: String? = null,
  @SerializedName("nameOfBankAccountHolder")
  var nameOfBankAccountHolder: String? = null,
  @SerializedName("nameOfPanHolder")
  var nameOfPanHolder: String? = null,
  @SerializedName("panCardDocument")
  var panCardDocument: String? = null,
  @SerializedName("panNumber")
  var panNumber: String? = null,
  @SerializedName("fpTag")
  var fpTag: String? = null,
  @SerializedName("isverified")
  var isVerified: String? = null
) : Serializable