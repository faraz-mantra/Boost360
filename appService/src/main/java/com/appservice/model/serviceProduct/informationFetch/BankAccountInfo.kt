package com.appservice.model.serviceProduct.informationFetch


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BankAccountInfo(
    @SerializedName("BankAddress")
    var bankAddress: Any? = null,
    @SerializedName("BankBranchName")
    var bankBranchName: Any? = null,
    @SerializedName("BankName")
    var bankName: Any? = null,
    @SerializedName("CancelledCheque")
    var cancelledCheque: Any? = null,
    @SerializedName("HolderName")
    var holderName: Any? = null,
    @SerializedName("IFSC")
    var iFSC: String? = null,
    @SerializedName("IsVerfied")
    var isVerfied: Boolean? = null,
    @SerializedName("Number")
    var number: String? = null
): Serializable