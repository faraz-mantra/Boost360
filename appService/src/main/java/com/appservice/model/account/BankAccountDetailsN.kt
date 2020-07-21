package com.appservice.model.account


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BankAccountDetailsN(
    @SerializedName("AccountAlias")
    var accountAlias: String? = null,
    @SerializedName("AccountName")
    var accountName: String? = null,
    @SerializedName("AccountNumber")
    var accountNumber: String? = null,
    @SerializedName("BankName")
    var bankName: String? = null,
    @SerializedName("IFSC")
    var iFSC: String? = null,
    @SerializedName("KYCDetails")
    var kYCDetails: KYCDetailsN? = null
) : BaseRequest(), Serializable {

  fun kycObj(): KYCDetailsN {
    return KYCDetailsN("", "", "")
  }
}
