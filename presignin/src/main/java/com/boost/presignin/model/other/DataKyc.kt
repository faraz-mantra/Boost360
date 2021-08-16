package com.boost.presignin.model.other

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class DataKyc(
  @SerializedName("ActionId")
  var actionId: String? = null,
  @SerializedName("additionalDocument")
  var additionalDocument: String? = null,
  @SerializedName("bankAccountNumber")
  var bankAccountNumber: String? = null,
  @SerializedName("bankAccountStatement")
  var bankAccountStatement: String? = null,
  @SerializedName("bankBranchName")
  var bankBranchName: String? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("fpTag")
  var fpTag: String? = null,
  @SerializedName("hasexisistinginstamojoaccount?")
  var hasexisistinginstamojoaccount: String? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("ifsc")
  var ifsc: String? = null,
  @SerializedName("instamojoEmail")
  var instamojoEmail: String? = null,
  @SerializedName("instamojoPassword")
  var instamojoPassword: String? = null,
  @SerializedName("IsArchived")
  var isArchived: Boolean? = null,
  @SerializedName("isverified")
  var isVerified: String? = null,
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
  @SerializedName("UpdatedOn")
  var updatedOn: String? = null,
  @SerializedName("UserId")
  var userId: String? = null,
  @SerializedName("WebsiteId")
  var websiteId: String? = null,
) : Serializable {

  enum class HasInginstaMojo {
    YES, NO, UNPAID, PAID
  }

  enum class Verify {
    YES, NO, ALLOW_EDIT
  }
}