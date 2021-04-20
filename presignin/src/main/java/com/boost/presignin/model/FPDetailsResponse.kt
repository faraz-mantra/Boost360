package com.boost.presignin.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FPDetailsResponse(

		@field:SerializedName("ExpiryDate")
		var expiryDate: String? = null,

		@field:SerializedName("Email")
		var email: Any? = null,

		@field:SerializedName("Address")
		var address: String? = null,

		@field:SerializedName("SecondaryTileImages")
		var secondaryTileImages: List<Any?>? = null,

		@field:SerializedName("ExpiryType")
		var expiryType: Int? = null,

		@field:SerializedName("LogoUrl")
		var logoUrl: String? = null,

		@field:SerializedName("Ratings")
		var ratings: Any? = null,

		@field:SerializedName("Name")
		var name: String? = null,

		@field:SerializedName("LanguageCode")
		var languageCode: String? = null,

		@field:SerializedName("WebKeywords")
		var webKeywords: Any? = null,

		@field:SerializedName("ExternalSourceName")
		var externalSourceName: Any? = null,

		@field:SerializedName("UpdatedOn")
		var updatedOn: String? = null,

		@field:SerializedName("OnboardingbyRiaTeamCompletedOn")
		var onboardingbyRiaTeamCompletedOn: String? = null,

		@field:SerializedName("IsVerified")
		var isVerified: Boolean? = null,

		@field:SerializedName("Pwd")
		var pwd: Any? = null,

		@field:SerializedName("lat")
		var lat: Double? = null,

		@field:SerializedName("height")
		var height: Double? = null,

		@field:SerializedName("WhatsAppNumber")
		var whatsAppNumber: Any? = null,

		@field:SerializedName("FaviconUrl")
		var faviconUrl: String? = null,

		@field:SerializedName("ParentPrimaryNumber")
		var parentPrimaryNumber: Any? = null,

		@field:SerializedName("Timings")
		var timings: List<Any?>? = null,

		@field:SerializedName("WhatsppNumber")
		var whatsppNumber: Any? = null,

		@field:SerializedName("PackageIds")
		var packageIds: Any? = null,

		@field:SerializedName("PaymentLevel")
		var paymentLevel: Int? = null,

		@field:SerializedName("lng")
		var lng: Double? = null,

		@field:SerializedName("IsSMSSubscriptionEnabled")
		var isSMSSubscriptionEnabled: Boolean? = null,

		@field:SerializedName("GAToken")
		var gAToken: String? = null,

		@field:SerializedName("TinyLogoUrl")
		var tinyLogoUrl: String? = null,

		@field:SerializedName("PanaromaId")
		var panaromaId: Any? = null,

		@field:SerializedName("FPEngagementIndex")
		var fPEngagementIndex: Double? = null,

		@field:SerializedName("PrimaryNumber")
		var primaryNumber: String? = null,

		@field:SerializedName("City")
		var city: String? = null,

		@field:SerializedName("ImageUri")
		var imageUri: String? = null,

		@field:SerializedName("SubscriberNotificationType")
		var subscriberNotificationType: String? = null,

		@field:SerializedName("EnterpriseName")
		var enterpriseName: Any? = null,

		@field:SerializedName("Country")
		var country: String? = null,

		@field:SerializedName("CountryPhoneCode")
		var countryPhoneCode: String? = null,

		@field:SerializedName("_id")
		var id: String? = null,

		@field:SerializedName("RootAliasUriStatusUpdate")
		var rootAliasUriStatusUpdate: Int? = null,

		@field:SerializedName("WebTemplateId")
		var webTemplateId: String? = null,

		@field:SerializedName("ApplicationId")
		var applicationId: String? = null,

		@field:SerializedName("IsOnboardingbyRiaTeamComplete")
		var isOnboardingbyRiaTeamComplete: Boolean? = null,

		@field:SerializedName("errorRadius")
		var errorRadius: Double? = null,

		@field:SerializedName("AccountManagerLocationId")
		var accountManagerLocationId: Any? = null,

		@field:SerializedName("FPLocalWidgets")
		var fPLocalWidgets: Any? = null,

		@field:SerializedName("RootAliasUriStatusUpdatedOn")
		var rootAliasUriStatusUpdatedOn: String? = null,

		@field:SerializedName("Description")
		var description: Any? = null,

		@field:SerializedName("Category")
		var category: Category? = null,

		@field:SerializedName("PrimaryCategory")
		var primaryCategory: String? = null,

		@field:SerializedName("PaymentState")
		var paymentState: Int? = null,

		@field:SerializedName("AliasTag")
		var aliasTag: Any? = null,

		@field:SerializedName("Uri")
		var uri: Any? = null,

		@field:SerializedName("PinCode")
		var pinCode: String? = null,

		@field:SerializedName("TileImageUri")
		var tileImageUri: String? = null,

		@field:SerializedName("SitemapUrl")
		var sitemapUrl: Any? = null,

		@field:SerializedName("IsSiteHealthCritical")
		var isSiteHealthCritical: Boolean? = null,

		@field:SerializedName("SearchTrafficNotificationType")
		var searchTrafficNotificationType: String? = null,

		@field:SerializedName("ProductCategoryVerb")
		var productCategoryVerb: String? = null,

		@field:SerializedName("IsPrivateForApplication")
		var isPrivateForApplication: Boolean? = null,

		@field:SerializedName("CreatedOn")
		var createdOn: String? = null,

		@field:SerializedName("ReportsNotificationType")
		var reportsNotificationType: String? = null,

		@field:SerializedName("BoostXWebsiteUrl")
		var boostXWebsiteUrl: String? = null,

		@field:SerializedName("FBPageName")
		var fBPageName: Any? = null,

		@field:SerializedName("ExternalUrls")
		var externalUrls: List<Any?>? = null,

		@field:SerializedName("ParentId")
		var parentId: Any? = null,

		@field:SerializedName("OverallRating")
		var overallRating: Double? = null,

		@field:SerializedName("SystemGeneratedCategory")
		var systemGeneratedCategory: Any? = null,

		@field:SerializedName("AppExperienceCode")
		var appExperienceCode: String? = null,

		@field:SerializedName("UriDescription")
		var uriDescription: Any? = null,

		@field:SerializedName("NFXAccessTokens")
		var nFXAccessTokens: Any? = null,

		@field:SerializedName("RIANotificationType")
		var rIANotificationType: String? = null,

		@field:SerializedName("RootAliasUri")
		var rootAliasUri: String? = null,

		@field:SerializedName("Contact")
		var contact: Contact? = null,

		@field:SerializedName("AccountManagerId")
		var accountManagerId: String? = null,

		@field:SerializedName("WebTemplateType")
		var webTemplateType: Double? = null,

		@field:SerializedName("EnterpriseEmailContact")
		var enterpriseEmailContact: Any? = null,

		@field:SerializedName("Contacts")
		var contacts: List<ContactsItem?>? = null,

		@field:SerializedName("FPWebWidgets")
		var fPWebWidgets: List<String?>? = null,

		@field:SerializedName("SystemNotificationType")
		var systemNotificationType: String? = null,

		@field:SerializedName("IsEmailSubscriptionEnabled")
		var isEmailSubscriptionEnabled: Boolean? = null,

		@field:SerializedName("IsBulkSubscription")
		var isBulkSubscription: Boolean? = null,

		@field:SerializedName("SMSGatewayUri")
		var sMSGatewayUri: Any? = null,

		@field:SerializedName("SearchTags")
		var searchTags: Any? = null,

		@field:SerializedName("location")
		var location: Location? = null,

		@field:SerializedName("IsStoreFront")
		var isStoreFront: Boolean? = null,

		@field:SerializedName("UserMessageNotificationType")
		var userMessageNotificationType: String? = null,

		@field:SerializedName("Tag")
		var tag: String? = null,

		@field:SerializedName("SecondaryImages")
		var secondaryImages: List<Any?>? = null,

		@field:SerializedName("ContactName")
		var contactName: Any? = null,

		@field:SerializedName("ExternalSourceId")
		var externalSourceId: Any? = null,

		@field:SerializedName("GADomain")
		var gADomain: String? = null,
) : BaseResponse(), Serializable

data class Contact(

		@field:SerializedName("ContactNumber")
		var contactNumber: String? = null,

		@field:SerializedName("ContactName")
		var contactName: Any? = null,
)

data class ContactsItem(

		@field:SerializedName("ContactNumber")
		var contactNumber: String? = null,

		@field:SerializedName("ContactName")
		var contactName: Any? = null,
)

data class Location(

		@field:SerializedName("latitude")
		var latitude: Double? = null,

		@field:SerializedName("longitude")
		var longitude: Double? = null,
)

data class Category(

		@field:SerializedName("HEALTHCARE - GENERAL")
		var hEALTHCAREGENERAL: String? = null,
)
