package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.R
import com.festive.poster.constant.Constants
import com.festive.poster.models.FeaturePurchaseUiModel
import com.festive.poster.models.PosterCustomizationModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.response.UpgradeGetDataFeature
import com.festive.poster.models.response.UpgradeGetDataResponse
import com.festive.poster.models.response.saveCache
import com.festive.poster.reset.repo.AzureWebsiteNewRepository
import com.festive.poster.reset.repo.DevBoostRepository
import com.framework.base.BaseResponse
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.firebaseUtils.caplimit_feature.saveCache
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.rest.NetworkResult
import com.framework.utils.application
import com.framework.utils.fetchString
import com.framework.utils.getResponse
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UpdateStudioPurchaseViewModel: BaseViewModel() {

    var updateStudioFeature: UpgradeGetDataFeature?=null
    private val _featurePurchaseData=MutableLiveData<NetworkResult<
            List<FeaturePurchaseUiModel>>>()
    val featurePurchaseData:LiveData<NetworkResult<
            List<FeaturePurchaseUiModel>>> get() = _featurePurchaseData

    var doesUserHavePurchasedAnything = false
    
    init {
        getPurchaseItem()
    }

    fun getPurchaseItem(){
        _featurePurchaseData.postValue(NetworkResult.Loading())
        viewModelScope.launch {
            try {
                val appFeaturesResponse = getUpgradeData()
                val userPurchasedFeatures = getUserPurchasedFeatures()


                val featureUiList = convertFeatureResponseToUiItem(appFeaturesResponse)

                doesUserHavePurchasedAnything= checkUserHavePurchasedAnything(userPurchasedFeatures,appFeaturesResponse)

                _featurePurchaseData.postValue(NetworkResult.Success(featureUiList))
            }
            catch (e:Exception){
                _featurePurchaseData.postValue(NetworkResult.Error(e.message))
            }


        }
    }


    fun convertFeatureResponseToUiItem(appFeaturesResponse: UpgradeGetDataResponse): ArrayList<FeaturePurchaseUiModel> {
        val featurePurchaseList =  getBundlesWhereUpdateStudioPresent(appFeaturesResponse)

        updateStudioFeature = getFeatureDetails(Constants.UPDATES_STUDIO_WIDGET_KEY,appFeaturesResponse.Data.firstOrNull()?.features)

        val desc = application().getString(
            R.string.pay_placeholder_or_placeholder_gst_extra,
            updateStudioFeature?.price, updateStudioFeature?.price ?: (0 * 12)
        )

        featurePurchaseList.add(
            FeaturePurchaseUiModel(
                title = fetchString(R.string.buy_only_this_feature),
                desc =desc,
                updateStudioFeature?.price
            )
        )

        return featurePurchaseList
    }

    fun getBundlesWhereUpdateStudioPresent(appFeaturesResponse: UpgradeGetDataResponse): ArrayList<FeaturePurchaseUiModel> {
        val featurePurchaseUiList = ArrayList<FeaturePurchaseUiModel>()

        val bundles =  appFeaturesResponse.Data.firstOrNull()?.bundles?.filter { bundle->
            bundle.included_features.find {feature->
                feature.feature_code== Constants.UPDATES_STUDIO_WIDGET_KEY
            }!=null }


        bundles?.forEach {

            val title = application().getString(
                R.string.buy_it_as_part_of_placeholder,
                it.name)

            val desc = application().getString(
                R.string.get_branded_update_templates_placeholder_more_features,
                it.included_features)


            val item = FeaturePurchaseUiModel(
                title,
                desc,
                null,
            )

            featurePurchaseUiList.add(item)

        }
        return featurePurchaseUiList
    }

    fun getFeatureDetails(featureCode: String?, features: List<UpgradeGetDataFeature>?): UpgradeGetDataFeature?{
        return features?.find { it.feature_code==featureCode }
    }

    fun checkUserHavePurchasedAnything(
        userPurchasedFeatures: Array<CapLimitFeatureResponseItem>,
        appFeaturesResponse: UpgradeGetDataResponse
    ): Boolean {
        return  userPurchasedFeatures.filter { purchasedFeature->

            val isPremium = getFeatureDetails(purchasedFeature.featureKey,
                appFeaturesResponse.Data.firstOrNull()?.features)?.is_premium

            purchasedFeature.featureState==1
                    && isPremium!!
        }.isNotEmpty()
    }

    suspend fun getUpgradeData()= suspendCoroutine<UpgradeGetDataResponse> { cont->
        if (UpgradeGetDataResponse.getFromCache()!=null){
            cont.resume(UpgradeGetDataResponse.getFromCache()!!)
        }
        DevBoostRepository.getUpgradeData().getResponse { response->
            if (response.isSuccess()){
                val data = response as UpgradeGetDataResponse
                data.saveCache()
                cont.resume(data)
            }else{
                cont.resumeWithException(Exception(response.message))
            }

        }
    }

    suspend fun getUserPurchasedFeatures()= suspendCoroutine<Array<CapLimitFeatureResponseItem>> { cont->
        if (CapLimitFeatureResponseItem.getFromCache()!=null){
            cont.resume(CapLimitFeatureResponseItem.getFromCache()!!)
        }
        AzureWebsiteNewRepository.getFeatureDetails().getResponse { response->
            if (response.isSuccess()){
                val data = response.arrayResponse as Array<CapLimitFeatureResponseItem>
                data.saveCache()
                cont.resume(data)
            }else{
                cont.resumeWithException(Exception(response.message))
            }

        }
    }
}