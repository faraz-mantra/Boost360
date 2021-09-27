package com.dashboard.model.live.user_profile

import com.framework.base.BaseResponse
import java.io.Serializable

data class UserProfileData(
    val Error: Error,
    val Result: UserProfileDataResult,
    val StatusCode: Int
):BaseResponse()

data class UserProfileDataResult(
    val Email: String,
    val FloatingPointDetails: List<FloatingPointDetail>,
    val ImageUrl: String,
    val IsEmailVerfied: Boolean,
    val IsMobileVerified: Boolean,
    val LoginId: String,
    val MobileNo: String,
    val UserName: String
)

data class FloatingPointDetail(
    val AccountManagerId: String,
    val AccountManagerLocationId: String,
    val Address: String,
    val AliasTag: String,
    val AppExperienceCode: String,
    val ApplicationId: String,
    val BoostXWebsiteUrl: String,
    val Category: HashMap<String,String>,
    val City: String,
    val Contact: UserProfileContact,
    val ContactName: String,
    val Contacts: List<UserProfileContact>,
    val Country: String,
    val CountryPhoneCode: String,
    val CreatedOn: String,
    val Description: String,
    val Email: String,
    val EnterpriseEmailContact: String,
    val EnterpriseName: String,
    val ExpiryDate: String,
    val ExpiryType: Int,
    val ExternalSourceId: String,
    val ExternalSourceName: String,
    val ExternalUrls: List<UserProfileExternalUrl>,
    val FBPageName: String,
    val FPEngagementIndex: Double,
    val FPLocalWidgets: List<UserProfileFPLocalWidget>,
    val FPWebWidgets: List<String>,
    val FaviconUrl: String,
    val GADomain: String,
    val GAToken: String,
    val ImageUri: String,
    val IsBulkSubscription: Boolean,
    val IsEmailSubscriptionEnabled: Boolean,
    val IsOnboardingbyRiaTeamComplete: Boolean,
    val IsPrivateForApplication: Boolean,
    val IsSMSSubscriptionEnabled: Boolean,
    val IsSiteHealthCritical: Boolean,
    val IsStoreFront: Boolean,
    val IsVerified: Boolean,
    val LanguageCode: String,
    val LogoUrl: String,
    val NFXAccessTokens: String,
    val Name: String,
    val OnboardingbyRiaTeamCompletedOn: String,
    val OverallRating: Double,
    val PackageIds: List<String>,
    val PanaromaId: String,
    val ParentId: String,
    val ParentPrimaryNumber: String,
    val PaymentLevel: Int,
    val PaymentState: Int,
    val PinCode: String,
    val PrimaryCategory: String,
    val PrimaryNumber: String,
    val ProductCategoryVerb: String,
    val Pwd: String,
    val RIANotificationType: String,
    val Ratings: String,
    val ReportsNotificationType: String,
    val RootAliasUri: String,
    val RootAliasUriStatusUpdate: Int,
    val RootAliasUriStatusUpdatedOn: String,
    val SMSGatewayUri: String,
    val SearchTags: String,
    val SearchTrafficNotificationType: String,
    val SecondaryImages: List<String>,
    val SecondaryTileImages: List<String>,
    val SitemapUrl: String,
    val SubscriberNotificationType: String,
    val SystemGeneratedCategory: String,
    val SystemNotificationType: String,
    val Tag: String,
    val TileImageUri: String,
    val Timings: List<UserProfileTiming>,
    val TinyLogoUrl: String,
    val UpdatedOn: String,
    val Uri: String,
    val UriDescription: String,
    val UserMessageNotificationType: String,
    val WebKeywords: List<String>,
    val WebTemplateId: String,
    val WebTemplateType: Double,
    val WhatsAppNumber: String,
    val WhatsppNumber: String,
    val _id: String,
    val errorRadius: Double,
    val height: Double,
    val lat: Double,
    val lng: Double,
    val location: UserProfileLocation
)

data class UserProfileFPLocalWidget(
    val _key: String,
    val html: String,
    val type: String
)

data class UserProfileExternalUrl(
    val Uri: String,
    val UriDescription: Any,
    val UriFaviconUrl: Any
)

data class UserProfileContact(
    val ContactName: String,
    val ContactNumber: String
)

data class UserProfileTiming(
    val From: String,
    val To: String
)

data class UserProfileLocation(
    val latitude: Double,
    val longitude: Double
)

data class UserProfileError(
    val ErrorCode: Any,
    val ErrorList: Any
)