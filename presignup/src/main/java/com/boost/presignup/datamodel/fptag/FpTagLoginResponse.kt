package com.boost.presignup.datamodel.fptag


import com.google.gson.annotations.SerializedName

data class FpTagLoginResponse(
  @SerializedName("AccountManagerId")
  var accountManagerId: String? = null,
  @SerializedName("Address")
  var address: String? = null,
  @SerializedName("AliasTag")
  var aliasTag: Any? = null,
  @SerializedName("AppExperienceCode")
  var appExperienceCode: String? = null,
  @SerializedName("ApplicationId")
  var applicationId: String? = null,
  @SerializedName("BoostXWebsiteUrl")
  var boostXWebsiteUrl: Any? = null,
  @SerializedName("Category")
  var category: List<String>? = null,
  @SerializedName("City")
  var city: String? = null,
  @SerializedName("Contact")
  var contact: Contact? = null,
  @SerializedName("ContactName")
  var contactName: String? = null,
  @SerializedName("Contacts")
  var contacts: List<ContactX>? = null,
  @SerializedName("Country")
  var country: String? = null,
  @SerializedName("CountryPhoneCode")
  var countryPhoneCode: String? = null,
  @SerializedName("CreatedOn")
  var createdOn: String? = null,
  @SerializedName("Description")
  var description: String? = null,
  @SerializedName("Email")
  var email: String? = null,
  @SerializedName("EnterpriseEmailContact")
  var enterpriseEmailContact: Any? = null,
  @SerializedName("EnterpriseName")
  var enterpriseName: Any? = null,
  @SerializedName("errorRadius")
  var errorRadius: Double? = null,
  @SerializedName("ExpiryDate")
  var expiryDate: String? = null,
  @SerializedName("ExternalSourceId")
  var externalSourceId: Any? = null,
  @SerializedName("ExternalSourceName")
  var externalSourceName: Any? = null,
  @SerializedName("FBPageName")
  var fBPageName: String? = null,
  @SerializedName("FPLocalWidgets")
  var fPLocalWidgets: Any? = null,
  @SerializedName("FPWebWidgets")
  var fPWebWidgets: List<String>? = null,
  @SerializedName("FaviconUrl")
  var faviconUrl: String? = null,
  @SerializedName("GADomain")
  var gADomain: String? = null,
  @SerializedName("GAToken")
  var gAToken: String? = null,
  @SerializedName("height")
  var height: Double? = null,
  @SerializedName("_id")
  var id: String? = null,
  @SerializedName("ImageUri")
  var imageUri: String? = null,
  @SerializedName("IsBulkSubscription")
  var isBulkSubscription: Boolean? = null,
  @SerializedName("IsEmailSubscriptionEnabled")
  var isEmailSubscriptionEnabled: Boolean? = null,
  @SerializedName("IsSMSSubscriptionEnabled")
  var isSMSSubscriptionEnabled: Boolean? = null,
  @SerializedName("IsStoreFront")
  var isStoreFront: Boolean? = null,
  @SerializedName("IsVerified")
  var isVerified: Boolean? = null,
  @SerializedName("LanguageCode")
  var languageCode: String? = null,
  @SerializedName("lat")
  var lat: Double? = null,
  @SerializedName("lng")
  var lng: Double? = null,
  @SerializedName("LogoUrl")
  var logoUrl: String? = null,
  @SerializedName("NFXAccessTokens")
  var nFXAccessTokens: Any? = null,
  @SerializedName("Name")
  var name: String? = null,
  @SerializedName("OverallRating")
  var overallRating: Double? = null,
  @SerializedName("PackageIds")
  var packageIds: Any? = null,
  @SerializedName("PanaromaId")
  var panaromaId: Any? = null,
  @SerializedName("ParentId")
  var parentId: String? = null,
  @SerializedName("ParentPrimaryNumber")
  var parentPrimaryNumber: Any? = null,
  @SerializedName("PaymentLevel")
  var paymentLevel: Int? = null,
  @SerializedName("PaymentState")
  var paymentState: Int? = null,
  @SerializedName("PinCode")
  var pinCode: String? = null,
  @SerializedName("PrimaryNumber")
  var primaryNumber: String? = null,
  @SerializedName("ProductCategoryVerb")
  var productCategoryVerb: String? = null,
  @SerializedName("Pwd")
  var pwd: Any? = null,
  @SerializedName("Ratings")
  var ratings: Any? = null,
  @SerializedName("RootAliasUri")
  var rootAliasUri: String? = null,
  @SerializedName("SMSGatewayUri")
  var sMSGatewayUri: Any? = null,
  @SerializedName("SearchTags")
  var searchTags: Any? = null,
  @SerializedName("SecondaryImages")
  var secondaryImages: List<String>? = null,
  @SerializedName("SecondaryTileImages")
  var secondaryTileImages: List<String>? = null,
  @SerializedName("Tag")
  var tag: String? = null,
  @SerializedName("TileImageUri")
  var tileImageUri: String? = null,
  @SerializedName("Timings")
  var timings: List<Timing>? = null,
  @SerializedName("TinyLogoUrl")
  var tinyLogoUrl: String? = null,
  @SerializedName("Uri")
  var uri: String? = null,
  @SerializedName("UriDescription")
  var uriDescription: Any? = null,
  @SerializedName("WebKeywords")
  var webKeywords: Any? = null,
  @SerializedName("WebTemplateId")
  var webTemplateId: String? = null,
  @SerializedName("WebTemplateType")
  var webTemplateType: Double? = null,
  @SerializedName("WhatsAppNumber")
  var whatsAppNumber: Any? = null
)