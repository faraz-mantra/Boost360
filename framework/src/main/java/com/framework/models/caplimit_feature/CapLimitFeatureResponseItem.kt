package com.framework.models.caplimit_feature

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

  enum class FeatureType {
    UNLIMITED_CONTENT, LATESTUPDATES, PRODUCTCATALOGUE, CUSTOMPAGES, IMAGEGALLERY, TESTIMONIALS, SUBSCRIBERCOUNT, BROCHURE, OURTOPPERS, UPCOMING_BATCHES, FACULTY
  }

  fun filterProperty(type: PropertiesItem.KeyType): PropertiesItem {
    return properties?.firstOrNull { it.key == type.name }?:PropertiesItem()
  }
}


fun getCapData(): List<CapLimitFeatureResponseItem> {
  return convertStringToList(PreferencesUtils.instance.getData(CAP_LIMIT_PROPERTIES, "") ?: "") ?: arrayListOf()
}

fun List<CapLimitFeatureResponseItem>.saveCapData() {
  PreferencesUtils.instance.saveData(CAP_LIMIT_PROPERTIES, convertListObjToString(this))
}

fun List<CapLimitFeatureResponseItem>.filterFeature(type: CapLimitFeatureResponseItem.FeatureType): CapLimitFeatureResponseItem? {
  return this.firstOrNull { it.featureKey == type.name }
}