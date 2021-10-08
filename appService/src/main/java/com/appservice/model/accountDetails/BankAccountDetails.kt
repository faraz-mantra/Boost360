package com.appservice.model.accountDetails

import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val BANK_ACCOUNT_SAVE = "BANK_ACCOUNT_SAVE"

data class BankAccountDetails(
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
  var kYCDetails: KYCDetails? = null
) : Serializable {
  var bankBranch: String? = null

  fun isValidAccount(): Boolean {
    return  iFSC.isNullOrEmpty().not() && getAccountNumberN().isNotEmpty() && bankName.isNullOrEmpty().not()
  }

  fun getAccountNumberN():String{
    return if (accountNumber.isNullOrEmpty() ||  accountNumber=="null") "" else accountNumber!!
  }

  fun getVerifyText(): String {
    return if (kYCDetails?.verificationStatus == KYCDetails.Status.PENDING.name) "unverified" else "verified"
  }
}

fun getBankDetail(): BankAccountDetails? {
  return convertStringToObj(PreferencesUtils.instance.getData(BANK_ACCOUNT_SAVE, "") ?: "")
}

fun BankAccountDetails.saveBanKDetail() {
  PreferencesUtils.instance.saveData(BANK_ACCOUNT_SAVE, convertObjToString(this))
}