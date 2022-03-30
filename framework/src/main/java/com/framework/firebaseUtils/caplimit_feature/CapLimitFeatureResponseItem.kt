package com.framework.firebaseUtils.caplimit_feature

import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val CAP_LIMIT_PROPERTIES = "cap_limit_properties"

data class CapLimitFeatureResponseItem(
  @SerializedName("activatedDate")
  var activatedDate: String? = null,
  @SerializedName("createdDate")
  var createdDate: String? = null,
  @SerializedName("expiryDate")
  var expiryDate: String? = null,
  @SerializedName("featureKey")
  var featureKey: String? = null,
  @SerializedName("featureState")
  var featureState: Int? = null,
  @SerializedName("featureType")
  var featureType: Int? = null,
  @SerializedName("properties")
  var properties: ArrayList<PropertiesItem>? = null
) : BaseResponse(), Serializable {

  enum class FeatureKey {
    UNLIMITED_CONTENT, LATESTUPDATES, PRODUCTCATALOGUE, CUSTOMPAGES, IMAGEGALLERY, TESTIMONIALS,
    SUBSCRIBERCOUNT, BROCHURE, OURTOPPERS, UPCOMING_BATCHES, FACULTY, APPOINTMENTENGINE, VISITORCOUNT
  }

  enum class FeatureState(var state: Int) {
    Initial(0),
    Activated(1),
    Paused(2),
    PendingAtMerchant(3),
    PendingAtThirdParty(4),
    PendingAtVendor(5),
    PendingPayment(6),
    Deactivated(7),
    Reset(8),
    Parked(9)
  }

  enum class FeatureType(var type: Int) {
    Widget(0), DomainPurchase(1), WildFire(2), FacebookLeads(3),
    RiaSupportTeam(4), PackageProperties(5), IVR(6), CallTracker(7),
    MarketplaceApp(8), EmailAccount(9), PackageReset(10), CompositeFeature(11)
  }

  fun filterProperty(type: PropertiesItem.KeyType): PropertiesItem {
    return properties?.firstOrNull { it.key == type.name } ?: PropertiesItem()
  }
}

fun getCapData(): List<CapLimitFeatureResponseItem> {
  return convertStringToList(PreferencesUtils.instance.getData(CAP_LIMIT_PROPERTIES, "") ?: "") ?: arrayListOf()
}

fun List<CapLimitFeatureResponseItem>.saveCapData() {
  PreferencesUtils.instance.saveData(CAP_LIMIT_PROPERTIES, convertListObjToString(this))
}

fun List<CapLimitFeatureResponseItem>.filterFeature(type: CapLimitFeatureResponseItem.FeatureKey): CapLimitFeatureResponseItem? {
  val unlimited = this.firstOrNull { it.featureKey == CapLimitFeatureResponseItem.FeatureKey.UNLIMITED_CONTENT.name }
  val capLimitData = unlimited ?: this.firstOrNull { it.featureKey == type.name }
  val isActiveLimit = capLimitData?.featureState != null && capLimitData.featureState!! == CapLimitFeatureResponseItem.FeatureState.Activated.state
  return if (isActiveLimit) capLimitData else null
}