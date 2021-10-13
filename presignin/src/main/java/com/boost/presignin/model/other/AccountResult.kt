package com.boost.presignin.model.other

import com.appservice.model.accountDetails.BankAccountDetails
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountResult(
  @SerializedName("AdditionalKYCDocuments")
  var additionalKYCDocuments: List<AdditionalKYCDocument>? = null,
  @SerializedName("BankAccountDetails")
  var bankAccountDetails: BankAccountDetails? = null,
  @SerializedName("RegisteredBusinessAddress")
  var registeredBusinessAddress: RegisteredBusinessAddress? = null,
  @SerializedName("RegisteredBusinessContactDetails")
  var registeredBusinessContactDetails: RegisteredBusinessContactDetails? = null,
  @SerializedName("TaxDetails")
  var taxDetails: TaxDetails? = null
) : Serializable