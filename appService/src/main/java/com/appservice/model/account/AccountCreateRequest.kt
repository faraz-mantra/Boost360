package com.appservice.model.account


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountCreateRequest(
    @SerializedName("AdditionalKYCDocuments")
    var additionalKYCDocuments: ArrayList<AdditionalKYCDocumentN>? = null,
    @SerializedName("BankAccountDetails")
    var bankAccountDetails: BankAccountDetailsN? = null,
    @SerializedName("ClientId")
    var clientId: String? = null,
    @SerializedName("FloatingPointId")
    var floatingPointId: String? = null,
    @SerializedName("RegisteredBusinessAddress")
    var registeredBusinessAddress: RegisteredBusinessAddressN? = null,
    @SerializedName("RegisteredBusinessContactDetails")
    var registeredBusinessContactDetails: RegisteredBusinessContactDetailsN? = null,
    @SerializedName("TaxDetails")
    var taxDetails: TaxDetailsN? = null
) : BaseRequest(), Serializable {


  fun setKYCBlankValue(): ArrayList<AdditionalKYCDocumentN> {
    val data = ArrayList<AdditionalKYCDocumentN>()
    data.add(AdditionalKYCDocumentN("", "", ""))
    return data
  }

  fun setAddressBlankValue(): RegisteredBusinessAddressN {
    return RegisteredBusinessAddressN("", "", "", "", "")
  }

  fun setContactDetailBlankValue(): RegisteredBusinessContactDetailsN {
    return RegisteredBusinessContactDetailsN("", "", "", "")
  }

  fun setTaxBlankValue(): TaxDetailsN {
    return TaxDetailsN().taxOb()
  }
}