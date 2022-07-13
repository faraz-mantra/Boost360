package com.appservice.model.aptsetting

import android.app.Activity
import com.appservice.constant.Constants
import com.appservice.base.getProductType
import com.framework.base.BaseResponse
import com.framework.pref.UserSessionManager
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserFpDetailsResponse(

  @field:SerializedName("ExpiryDate")
  val expiryDate: String? = null,

  @field:SerializedName("Email")
  val email: String? = null,

  @field:SerializedName("Address")
  val address: String? = null,

  @field:SerializedName("SecondaryTileImages")
  val secondaryTileImages: ArrayList<String?>? = null,

  @field:SerializedName("LogoUrl")
  val logoUrl: String? = null,

  @field:SerializedName("Name")
  val name: String? = null,

  @field:SerializedName("Ratings")
  val ratings: Any? = null,

  @field:SerializedName("LanguageCode")
  val languageCode: String? = null,

  @field:SerializedName("WebKeywords")
  val webKeywords: Any? = null,

  @field:SerializedName("ExternalSourceName")
  val externalSourceName: Any? = null,

  @field:SerializedName("IsVerified")
  val isVerified: Boolean? = null,

  @field:SerializedName("Pwd")
  val pwd: Any? = null,

  @field:SerializedName("lat")
  val lat: String? = null,

  @field:SerializedName("height")
  val height: Double? = null,

  @field:SerializedName("WhatsAppNumber")
  val whatsAppNumber: Any? = null,

  @field:SerializedName("Timings")
  val timings: ArrayList<TimingDetailsModel>? = null,

  @field:SerializedName("ParentPrimaryNumber")
  val parentPrimaryNumber: Any? = null,

  @field:SerializedName("FaviconUrl")
  val faviconUrl: String? = null,

  @field:SerializedName("PackageIds")
  val packageIds: ArrayList<String>? = null,

  @field:SerializedName("lng")
  val lng: String? = null,

  @field:SerializedName("PaymentLevel")
  val paymentLevel: String? = null,

  @field:SerializedName("IsSMSSubscriptionEnabled")
  val isSMSSubscriptionEnabled: Boolean? = null,

  @field:SerializedName("PanaromaId")
  val panaromaId: Any? = null,

  @field:SerializedName("TinyLogoUrl")
  val tinyLogoUrl: String? = null,

  @field:SerializedName("GAToken")
  val gAToken: String? = null,

  @field:SerializedName("PrimaryNumber")
  val primaryNumber: String? = null,

  @field:SerializedName("City")
  val city: String? = null,

  @field:SerializedName("ImageUri")
  val imageUri: String? = null,

  @field:SerializedName("EnterpriseName")
  val enterpriseName: Any? = null,

  @field:SerializedName("CountryPhoneCode")
  val countryPhoneCode: String? = null,

  @field:SerializedName("Country")
  val country: String? = null,

  @field:SerializedName("_id")
  val id: String? = null,

  @field:SerializedName("ApplicationId")
  val applicationId: String? = null,

  @field:SerializedName("WebTemplateId")
  val webTemplateId: String? = null,

  @field:SerializedName("errorRadius")
  val errorRadius: Double? = null,

  @field:SerializedName("FPLocalWidgets")
  val fPLocalWidgets: Any? = null,

  @field:SerializedName("Description")
  val description: String? = null,

  @field:SerializedName("Category")
  val category: ArrayList<CategoryItem>? = null,

  @field:SerializedName("PaymentState")
  val paymentState: String? = null,

  @field:SerializedName("AliasTag")
  val aliasTag: String? = null,

  @field:SerializedName("Uri")
  val uri: String? = null,

  @field:SerializedName("TileImageUri")
  val tileImageUri: String? = null,

  @field:SerializedName("PinCode")
  val pinCode: String? = null,

  @field:SerializedName("ProductCategoryVerb")
  val productCategoryVerb: String? = null,

  @field:SerializedName("CreatedOn")
  val createdOn: String? = null,

  @field:SerializedName("FBPageName")
  val fBPageName: String? = null,

  @field:SerializedName("ParentId")
  val parentId: String? = null,

  @field:SerializedName("OverallRating")
  val overallRating: Double? = null,

  @field:SerializedName("AppExperienceCode")
  val appExperienceCode: String? = null,

  @field:SerializedName("UriDescription")
  val uriDescription: String? = null,

  @field:SerializedName("NFXAccessTokens")
  val nFXAccessTokens: Any? = null,

  @field:SerializedName("Contact")
  val contact: Contact? = null,

  @field:SerializedName("RootAliasUri")
  val rootAliasUri: String? = null,

  @field:SerializedName("WebTemplateType")
  val webTemplateType: String? = null,

  @field:SerializedName("AccountManagerId")
  val accountManagerId: String? = null,

  @field:SerializedName("EnterpriseEmailContact")
  val enterpriseEmailContact: Any? = null,

  @field:SerializedName("Contacts")
  val contacts: ArrayList<ContactsItem>? = null,

  @field:SerializedName("FPWebWidgets")
  val fPWebWidgets: ArrayList<String>? = null,

  @field:SerializedName("IsEmailSubscriptionEnabled")
  val isEmailSubscriptionEnabled: Boolean? = null,

  @field:SerializedName("IsBulkSubscription")
  val isBulkSubscription: Boolean? = null,

  @field:SerializedName("SMSGatewayUri")
  val sMSGatewayUri: Any? = null,

  @field:SerializedName("SearchTags")
  val searchTags: Any? = null,

  @field:SerializedName("IsStoreFront")
  val isStoreFront: Boolean? = null,

  @field:SerializedName("Tag")
  val tag: String? = null,

  @field:SerializedName("SecondaryImages")
  val secondaryImages: List<Any?>? = null,

  @field:SerializedName("ContactName")
  val contactName: String? = null,

  @field:SerializedName("ExternalSourceId")
  val externalSourceId: String? = null,

  @field:SerializedName("GADomain")
  val gADomain: String? = null,
) : BaseResponse(), Serializable {

  fun productCategoryVerb(activity: Activity): String {
    return Constants.CATALOG_PREFIX + if (productCategoryVerb.isNullOrEmpty()) getProductType(UserSessionManager(activity).fP_AppExperienceCode) else productCategoryVerb
  }

  fun productCategory(activity: Activity): String {
    return if (productCategoryVerb.isNullOrEmpty()) getProductType(UserSessionManager(activity).fP_AppExperienceCode) else productCategoryVerb
  }
}

data class Contact(

  @field:SerializedName("ContactNumber")
  val contactNumber: String? = null,

  @field:SerializedName("ContactName")
  val contactName: Any? = null
) : Serializable

data class ContactsItem(

  @field:SerializedName("ContactNumber")
  val contactNumber: String? = null,

  @field:SerializedName("ContactName")
  val contactName: String? = null,
) : Serializable

data class CategoryItem(

  @field:SerializedName("Value")
  val value: String? = null,

  @field:SerializedName("Key")
  val key: String? = null,
) : Serializable

data class TimingDetailsModel(

  @field:SerializedName("From")
  val from: String? = null,

  @field:SerializedName("To")
  val to: String? = null,
) : Serializable
