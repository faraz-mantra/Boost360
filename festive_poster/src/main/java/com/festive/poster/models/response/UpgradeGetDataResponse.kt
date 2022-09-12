package com.festive.poster.models.response

import com.festive.poster.constant.PreferenceConstant
import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils
import com.framework.utils.convertJsonToObj
import com.framework.utils.getData
import com.framework.utils.saveData
import com.google.gson.Gson

data class UpgradeGetDataResponse(
    val Data: List<UpgradeGetDataData>,
):BaseResponse(){

    companion object{
        fun getFromCache():UpgradeGetDataResponse?{
            val json =  PreferencesUtils.instance.getData(
                PreferenceConstant.UPGRADE_DATA_RESPONSE,
                null)

            return if (json!=null){
                convertJsonToObj(json)
            }else{
                null
            }
        }
    }
}
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
    val websiteid: String,
    val bundles: List<UpgradeGetDataBundle>

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


data class UpgradeGetDataBundle(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val benefits: List<String>,
    val createdon: String,
    val desc: String,
    val exclusive_for_customers: List<Any>,
    val exclusive_to_categories: List<String>,
    val frequently_asked_questions: List<FeatureFrequentlyAskedQuestion>,
    val how_to_activate: List<FeatureHowToActivate>,
    val included_features: List<BundleIncludedFeature>,
    val isarchived: Boolean,
    val min_purchase_months: Double,
    val name: String,
    val overall_discount_percent: Double,
    val primary_image: FeaturePrimaryImage,
    val testimonials: List<FeatureTestimonial>,
    val updatedon: String,
    val websiteid: String,
    val what_is_include: List<Any>
)

data class BundleIncludedFeature(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val feature_code: String,
    val feature_price_discount_percent: Double,
    val feature_unit_count: Double,
    val isarchived: Boolean,
    val updatedon: String,
    val websiteid: String
)

data class FeatureFrequentlyAskedQuestion(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val answer: String,
    val createdon: String,
    val isarchived: Boolean,
    val question: String,
    val updatedon: String,
    val websiteid: String
)

data class FeatureHowToActivate(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val answer: String,
    val createdon: String,
    val isarchived: Boolean,
    val question: String,
    val updatedon: String,
    val websiteid: String
)

data class FeaturePrimaryImage(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val isarchived: Boolean,
    val updatedon: String,
    val url: String,
    val websiteid: String
)

data class FeatureTestimonial(
    val _kid: String,
    val _parentClassId: String,
    val _parentClassName: String,
    val _propertyName: String,
    val createdon: String,
    val isarchived: Boolean,
    val name: String,
    val text: String,
    val title: String,
    val updatedon: String,
    val websiteid: String
)
fun UpgradeGetDataResponse.saveCache(){
    PreferencesUtils.instance.saveData(PreferenceConstant.UPGRADE_DATA_RESPONSE,Gson().toJson(this))
}

