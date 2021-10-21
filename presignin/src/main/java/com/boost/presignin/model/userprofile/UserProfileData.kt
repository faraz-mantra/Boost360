package com.boost.presignin.model.userprofile

import com.framework.base.BaseResponse
import com.framework.utils.*
import java.io.Serializable

const val USER_MERCHANT_DETAIL = "USER_MERCHANT_DETAIL"

data class UserProfileData(
  val Error: Error? = null,
  val Result: UserProfileDataResult? = null,
  val StatusCode: Int? = null
) : BaseResponse()

data class UserProfileDataResult(
  val Email: String? = null,
  val FloatingPointDetails: List<FloatingPointDetail>? = null,
  val ImageUrl: String? = null,
  val IsEmailVerfied: Boolean? = null,
  val IsMobileVerified: Boolean? = null,
  val LoginId: String? = null,
  val MobileNo: String? = null,
  val UserName: String? = null
) : Serializable {

  fun getUserNameN(): String? {
    return if (UserName.isNullOrEmpty()) null else UserName
  }

  companion object {
    fun saveMerchantProfileDetails(user: UserProfileDataResult) {
      PreferencesUtils.instance.saveData(USER_MERCHANT_DETAIL, convertObjToString(user) ?: "")
    }

    fun getMerchantProfileDetails(): UserProfileDataResult? {
      return convertStringToObj(PreferencesUtils.instance.getData(USER_MERCHANT_DETAIL, "") ?: "")
    }
  }
}

data class FloatingPointDetail(
  val AccountManagerId: String? = null,
  val AccountManagerLocationId: String? = null,
  val Address: String? = null,
  val AliasTag: String? = null,
  val AppExperienceCode: String? = null,
  val ApplicationId: String? = null,
  val BoostXWebsiteUrl: String? = null,
  val Category: HashMap<String, String>? = null,
  val City: String? = null,
  val Contact: UserProfileContact? = null,
  val ContactName: String? = null,
  val Contacts: List<UserProfileContact>? = null,
  val Country: String? = null,
  val CountryPhoneCode: String? = null,
  val CreatedOn: String? = null,
  val Description: String? = null,
  val Email: String? = null,
  val EnterpriseEmailContact: String? = null,
  val EnterpriseName: String? = null,
  val ExpiryDate: String? = null,
  val ExpiryType: Int? = null,
  val ExternalSourceId: String? = null,
  val ExternalSourceName: String? = null,
  val ExternalUrls: List<UserProfileExternalUrl>? = null,
  val FBPageName: String? = null,
  val FPEngagementIndex: Double? = null,
  val FPLocalWidgets: List<UserProfileFPLocalWidget>? = null,
  val FPWebWidgets: List<String>? = null,
  val FaviconUrl: String? = null,
  val GADomain: String? = null,
  val GAToken: String? = null,
  val ImageUri: String? = null,
  val IsBulkSubscription: Boolean? = null,
  val IsEmailSubscriptionEnabled: Boolean? = null,
  val IsOnboardingbyRiaTeamComplete: Boolean? = null,
  val IsPrivateForApplication: Boolean? = null,
  val IsSMSSubscriptionEnabled: Boolean? = null,
  val IsSiteHealthCritical: Boolean? = null,
  val IsStoreFront: Boolean? = null,
  val IsVerified: Boolean? = null,
  val LanguageCode: String? = null,
  val LogoUrl: String? = null,
  val NFXAccessTokens: String? = null,
  val Name: String? = null,
  val OnboardingbyRiaTeamCompletedOn: String? = null,
  val OverallRating: Double,
  val PackageIds: List<String>,
  val PanaromaId: String? = null,
  val ParentId: String? = null,
  val ParentPrimaryNumber: String? = null,
  val PaymentLevel: Int? = null,
  val PaymentState: Int? = null,
  val PinCode: String? = null,
  val PrimaryCategory: String? = null,
  val PrimaryNumber: String? = null,
  val ProductCategoryVerb: String? = null,
  val Pwd: String? = null,
  val RIANotificationType: String? = null,
  val Ratings: String? = null,
  val ReportsNotificationType: String? = null,
  val RootAliasUri: String? = null,
  val RootAliasUriStatusUpdate: Int? = null,
  val RootAliasUriStatusUpdatedOn: String? = null,
  val SMSGatewayUri: String? = null,
  val SearchTags: String? = null,
  val SearchTrafficNotificationType: String? = null,
  val SecondaryImages: List<String>? = null,
  val SecondaryTileImages: List<String>? = null,
  val SitemapUrl: String? = null,
  val SubscriberNotificationType: String? = null,
  val SystemGeneratedCategory: String? = null,
  val SystemNotificationType: String? = null,
  val Tag: String? = null,
  val TileImageUri: String? = null,
  val Timings: List<UserProfileTiming>,
  val TinyLogoUrl: String? = null,
  val UpdatedOn: String? = null,
  val Uri: String? = null,
  val UriDescription: String? = null,
  val UserMessageNotificationType: String? = null,
  val WebKeywords: List<String>? = null,
  val WebTemplateId: String? = null,
  val WebTemplateType: Double? = null,
  val WhatsAppNumber: String? = null,
  val WhatsppNumber: String? = null,
  val _id: String? = null,
  val errorRadius: Double? = null,
  val height: Double? = null,
  val lat: Double? = null,
  val lng: Double? = null,
  val location: UserProfileLocation? = null,
)

data class UserProfileFPLocalWidget(
  val _key: String? = null,
  val html: String? = null,
  val type: String? = null,
)

data class UserProfileExternalUrl(
  val Uri: String? = null,
  val UriDescription: Any? = null,
  val UriFaviconUrl: Any? = null
)

data class UserProfileContact(
  val ContactName: String? = null,
  val ContactNumber: String? = null
)

data class UserProfileTiming(
  val From: String? = null,
  val To: String
)

data class UserProfileLocation(
  val latitude: Double? = null,
  val longitude: Double? = null,
)

data class UserProfileError(
  val ErrorCode: Any? = null,
  val ErrorList: Any? = null
)