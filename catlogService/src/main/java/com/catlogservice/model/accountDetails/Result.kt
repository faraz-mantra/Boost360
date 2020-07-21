package com.catlogservice.model.accountDetails


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
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