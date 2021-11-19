package com.appservice.model.aptsetting

import com.google.gson.annotations.SerializedName

data class AddBankAccountRequest(

	@field:SerializedName("BankName")
	var bankName: String? = null,

	@field:SerializedName("KYCDetails")
    var kYCDetails: KYCDetails? = null,

	@field:SerializedName("AccountAlias")
    var accountAlias: String? = null,

	@field:SerializedName("AccountNumber")
    var accountNumber: String? = null,

	@field:SerializedName("IFSC")
    var iFSC: String? = null,

	@field:SerializedName("AccountName")
    var accountName: String? = null
)

data class KYCDetails(

	@field:SerializedName("FileType")
    var fileType: String? = null,

	@field:SerializedName("DocumentContent")
    var documentContent: String? = null,

	@field:SerializedName("DocumentName")
    var documentName: String? = null
)
