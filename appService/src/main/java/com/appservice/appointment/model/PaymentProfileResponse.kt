package com.appservice.appointment.model

import com.appservice.model.accountDetails.BankAccountDetails
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentProfileResponse(


		@field:SerializedName("StatusCode")
		var statusCode: Int? = null,

		@field:SerializedName("Result")
		var result: PaymentResult? = null,
) : BaseResponse(), Serializable

data class TANDetails(

		@field:SerializedName("VerificationStatus")
		var verificationStatus: String? = null,

		@field:SerializedName("DocumentFile")
		var documentFile: Any? = null,

		@field:SerializedName("Number")
		var number: String? = null,

		@field:SerializedName("DocumentName")
		var documentName: String? = null,
) : Serializable

data class RegisteredBusinessAddress(

		@field:SerializedName("State")
		var state: String? = null,

		@field:SerializedName("Country")
		var country: String? = null,

		@field:SerializedName("City")
		var city: String? = null,

		@field:SerializedName("Line1")
		var line1: String? = null,

		@field:SerializedName("Line2")
		var line2: String? = null,
) : Serializable

data class PANDetails(

		@field:SerializedName("VerificationStatus")
		var verificationStatus: String? = null,

		@field:SerializedName("DocumentFile")
		var documentFile: Any? = null,

		@field:SerializedName("Number")
		var number: String? = null,

		@field:SerializedName("Name")
		var name: String? = null,

		@field:SerializedName("DocumentName")
		var documentName: String? = null,
) : Serializable

data class Error(

		@field:SerializedName("ErrorList")
		var errorList: ErrorList? = null,

		@field:SerializedName("ErrorCode")
		var errorCode: Any? = null,
) : Serializable

data class PaymentResult(

		@field:SerializedName("PaymentGatewayDetails")
		var paymentGatewayDetails: List<Any?>? = null,

		@field:SerializedName("TaxDetails")
		var taxDetails: TaxDetails? = null,

		@field:SerializedName("MerchantSignature")
		var merchantSignature: String? = null,

		@field:SerializedName("RegisteredBusinessContactDetails")
		var registeredBusinessContactDetails: RegisteredBusinessContactDetails? = null,

		@field:SerializedName("BankAccountDetails")
		var bankAccountDetails: BankAccountDetails? = null,

		@field:SerializedName("UPIId")
		var uPIId: String? = null,

		@field:SerializedName("RegisteredBusinessAddress")
		var registeredBusinessAddress: RegisteredBusinessAddress? = null,

		@field:SerializedName("AdditionalKYCDocuments")
		var additionalKYCDocuments: List<AdditionalKYCDocumentsItem?>? = null,

		@field:SerializedName("PaymentConfiguration")
		var paymentConfiguration: PaymentConfiguration? = null,
) : Serializable

data class AdditionalKYCDocumentsItem(

		@field:SerializedName("VerificationStatus")
		var verificationStatus: String? = null,

		@field:SerializedName("DocumentFile")
		var documentFile: Any? = null,

		@field:SerializedName("DocumentName")
		var documentName: String? = null,
) : Serializable


data class PaymentConfiguration(

		@field:SerializedName("AcceptCodForHomeDelivery")
		var acceptCodForHomeDelivery: Boolean? = null,

		@field:SerializedName("AcceptCodForStorePickup")
		var acceptCodForStorePickup: Boolean? = null,
) : Serializable

data class TaxDetails(

		@field:SerializedName("PANDetails")
		var pANDetails: PANDetails? = null,

		@field:SerializedName("TANDetails")
		var tANDetails: TANDetails? = null,

		@field:SerializedName("GSTDetails")
		var gSTDetails: GSTDetails? = null,
) : Serializable

data class RegisteredBusinessContactDetails(

		@field:SerializedName("RegisteredBusinessMobile")
		var registeredBusinessMobile: String? = null,

		@field:SerializedName("RegisteredBusinessCountryCode")
		var registeredBusinessCountryCode: String? = null,

		@field:SerializedName("RegisteredBusinessEmail")
		var registeredBusinessEmail: String? = null,

		@field:SerializedName("MerchantName")
		var merchantName: String? = null,
) : Serializable

data class ErrorList(
		var any: Any? = null,
)
