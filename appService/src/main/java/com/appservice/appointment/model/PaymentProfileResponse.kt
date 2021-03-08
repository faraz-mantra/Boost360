package com.appservice.appointment.model

import com.appservice.model.accountDetails.BankAccountDetails
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class PaymentProfileResponse(


		@field:SerializedName("StatusCode")
		val statusCode: Int? = null,

		@field:SerializedName("Result")
		val result: Result? = null,
) : BaseResponse()

data class TANDetails(

		@field:SerializedName("VerificationStatus")
		val verificationStatus: String? = null,

		@field:SerializedName("DocumentFile")
		val documentFile: Any? = null,

		@field:SerializedName("Number")
		val number: String? = null,

		@field:SerializedName("DocumentName")
		val documentName: String? = null,
) : BaseResponse()

data class RegisteredBusinessAddress(

		@field:SerializedName("State")
		val state: String? = null,

		@field:SerializedName("Country")
		val country: String? = null,

		@field:SerializedName("City")
		val city: String? = null,

		@field:SerializedName("Line1")
		val line1: String? = null,

		@field:SerializedName("Line2")
		val line2: String? = null,
)

data class PANDetails(

		@field:SerializedName("VerificationStatus")
		val verificationStatus: String? = null,

		@field:SerializedName("DocumentFile")
		val documentFile: Any? = null,

		@field:SerializedName("Number")
		val number: String? = null,

		@field:SerializedName("Name")
		val name: String? = null,

		@field:SerializedName("DocumentName")
		val documentName: String? = null,
)

data class Error(

		@field:SerializedName("ErrorList")
		val errorList: ErrorList? = null,

		@field:SerializedName("ErrorCode")
		val errorCode: Any? = null,
)

data class Result(

		@field:SerializedName("PaymentGatewayDetails")
		val paymentGatewayDetails: List<Any?>? = null,

		@field:SerializedName("TaxDetails")
		val taxDetails: TaxDetails? = null,

		@field:SerializedName("MerchantSignature")
		val merchantSignature: Any? = null,

		@field:SerializedName("RegisteredBusinessContactDetails")
		val registeredBusinessContactDetails: RegisteredBusinessContactDetails? = null,

		@field:SerializedName("BankAccountDetails")
		val bankAccountDetails: BankAccountDetails? = null,

		@field:SerializedName("UPIId")
		val uPIId: String? = null,

		@field:SerializedName("RegisteredBusinessAddress")
		val registeredBusinessAddress: RegisteredBusinessAddress? = null,

		@field:SerializedName("AdditionalKYCDocuments")
		val additionalKYCDocuments: List<AdditionalKYCDocumentsItem?>? = null,

		@field:SerializedName("PaymentConfiguration")
		val paymentConfiguration: PaymentConfiguration? = null,
)

data class AdditionalKYCDocumentsItem(

		@field:SerializedName("VerificationStatus")
		val verificationStatus: String? = null,

		@field:SerializedName("DocumentFile")
		val documentFile: Any? = null,

		@field:SerializedName("DocumentName")
		val documentName: String? = null,
)


data class PaymentConfiguration(

		@field:SerializedName("AcceptCodForHomeDelivery")
		val acceptCodForHomeDelivery: Boolean? = null,

		@field:SerializedName("AcceptCodForStorePickup")
		val acceptCodForStorePickup: Boolean? = null,
)

data class TaxDetails(

		@field:SerializedName("PANDetails")
		val pANDetails: PANDetails? = null,

		@field:SerializedName("TANDetails")
		val tANDetails: TANDetails? = null,

		@field:SerializedName("GSTDetails")
		val gSTDetails: GSTDetails? = null,
)

data class RegisteredBusinessContactDetails(

		@field:SerializedName("RegisteredBusinessMobile")
		val registeredBusinessMobile: String? = null,

		@field:SerializedName("RegisteredBusinessCountryCode")
		val registeredBusinessCountryCode: String? = null,

		@field:SerializedName("RegisteredBusinessEmail")
		val registeredBusinessEmail: String? = null,

		@field:SerializedName("MerchantName")
		val merchantName: String? = null,
)

data class ErrorList(
		val any: Any? = null,
)
