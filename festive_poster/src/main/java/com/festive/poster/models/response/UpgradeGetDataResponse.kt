package com.festive.poster.models.response

import com.framework.base.BaseResponse

data class UpgradeGetDataResponse(
    val Data: List<UpgradeGetDataData>,
):BaseResponse()
data class UpgradeGetDataData(
    val _kid: String,
    val createdon: String,
    val feature_deals: List<Any>,
    val features: List<UpgradeGetDataFeature>,
    val feedback_link: String,
    val isarchived: Boolean,
    val schemaid: String,
    val updatedon: String,
    val userid: String,
    val websiteid: String
)
data class UpgradeGetDataFeature(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val boost_widget_key: String,
    val createdon: String,
    val description: String,
    val description_title: String,
    val discount_percent: Double,
    val exclusive_to_categories: List<String>,
    val feature_code: String,
    val feature_importance: Double,
    val is_premium: Boolean,
    val isarchived: Boolean,
    val name: String,
    val price: Double,
    val target_business_usecase: String,
    val time_to_activation: Double,
    val total_installs: String,
    val updatedon: String,
    val usecase_importance: String,
    val websiteid: String
)

