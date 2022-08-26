package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.R
import com.festive.poster.constant.Constants
import com.festive.poster.models.*
import com.festive.poster.models.response.*
import com.festive.poster.reset.repo.*
import com.framework.base.BaseResponse
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.rest.NetworkResult
import com.framework.utils.application
import com.framework.utils.fetchString
import com.framework.utils.getResponse
import com.framework.utils.toArrayList
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PromoUpdatesViewModel: BaseViewModel() {


    private  val TAG = "PromoUpdatesViewModel"

    private val browseAllMData=MutableLiveData<NetworkResult<
            ArrayList<CategoryUi>>>()
    val browseAllLData:LiveData<NetworkResult<
            ArrayList<CategoryUi>>> get() =
        browseAllMData


    private val _favData=MutableLiveData<NetworkResult<
            ArrayList<CategoryUi>>>()
    val favData:LiveData<NetworkResult<
            ArrayList<CategoryUi>>> get() = _favData

    private val _todayPickData=MutableLiveData<NetworkResult<
            ArrayList<CategoryUi>>>()
    val todayPickData:LiveData<NetworkResult<
            ArrayList<CategoryUi>>> get() = _todayPickData

    private val _favStatus=MutableLiveData<NetworkResult<
            BaseResponse>>()
    val favStatus:LiveData<NetworkResult<
            BaseResponse>> get() = _favStatus


    var doesUserHavePurchasedAnything = false

    private val _featurePurchaseData=MutableLiveData<NetworkResult<
            List<FeaturePurchaseUiModel>>>()
    val featurePurchaseData:LiveData<NetworkResult<
            List<FeaturePurchaseUiModel>>> get() = _featurePurchaseData

    init {
       refreshTemplates()
        getPurchaseItem()
    }

    fun refreshTemplates(){
        getTodaysPickTemplate()
        getTemplatesUi()
    }


    fun getTodaysPickTemplate(){
        _todayPickData.postValue(NetworkResult.Loading())
        try {
            NowFloatsRepository.getTodaysTemplates().getResponse {
                if (it.isSuccess()){
                    val response = it as? GetTodayTemplateResponse
                    _todayPickData.postValue(
                        NetworkResult.Success(data =
                        response?.Result?.asDomainModel()?.toArrayList())
                    )
                }else{
                    throw Exception(it.errorMessage())
                }
            }
        }catch (e:Exception){
            _todayPickData.postValue(NetworkResult.Error(msg = fetchString(com.framework.R.string.something_went_wrong)))
        }

    }


    fun markAsFav(isFav:Boolean, templateId:String) {
        _favStatus.postValue(NetworkResult.Loading())
        NowFloatsRepository.saveTemplateAction(
            TemplateSaveActionBody.ActionType.FAVOURITE,isFav,templateId).getResponse {
                if (it.isSuccess()){
                    refreshTemplates()
                    _favStatus.postValue(NetworkResult.Success(it))
                }else{
                    _favStatus.postValue(NetworkResult.Error(it.errorMessage()))
                }
        }
    }


    fun templateSaveAction(action: TemplateSaveActionBody.ActionType,
                           isFav:Boolean, templateId:String): LiveData<BaseResponse> {
        return NowFloatsRepository.saveTemplateAction(action,isFav,templateId).toLiveData()
    }



    private suspend fun getTemplates(isFav:Boolean?=null)=
        suspendCoroutine<List<GetTemplatesResponseTemplate>>{cont->
            NowFloatsRepository.getTemplates(isFav).getResponse {
                if (it.isSuccess()){
                    val response = it as? GetTemplatesResponse
                    cont.resume(response!!.Result.templates)

                }else{
                    cont.resumeWithException(Exception())
                }
            }
        }

    fun getTemplatesUi(){
        browseAllMData.postValue(NetworkResult.Loading())
        viewModelScope.launch {
            try {
                val categories = NowFloatsRepository.getCategoriesUI()
                val allTemplates = getTemplates()
                categories.forEach {uicat->
                    val dbTemplates = allTemplates.filter { template->
                        template.categories.find {dbcat->
                            dbcat.id==uicat.id
                        }!=null
                    }

                    uicat.setTemplates(dbTemplates.asDomainModels())
                }
                browseAllMData.postValue(
                    NetworkResult.Success(categories.toArrayList()))
                getFavTemplates()
            }catch (e:Exception){
                e.printStackTrace()
                browseAllMData.postValue(NetworkResult.Error(msg = fetchString(com.framework.R.string.something_went_wrong)))

            }
        }
    }


    fun getFavTemplates() {
        _favData.postValue(NetworkResult.Loading())
        viewModelScope.launch {
            try {
                val categories = NowFloatsRepository.getCategoriesUI().toArrayList()
                val allTemplates = getTemplates(isFav=true)

                val allUiTemplates = ArrayList<TemplateUi>()

                categories.forEach {uicat->
                    val dbTemplates = allTemplates.filter { template->
                        template.categories.find {dbcat->
                            dbcat.id==uicat.id
                        }!=null
                    }
                    val domainModels =dbTemplates.asDomainModels()
                    uicat.setTemplates(domainModels)
                    allUiTemplates.addAll(domainModels)
                }


                val allDummyCat = CategoryUi(
                    iconUrl = "",
                    id="",
                    name = fetchString(R.string.all_favs),
                    thumbnailUrl = "https://i.ibb.co/BfrgDmq/My-project-1.png",
                    templates = allUiTemplates,
                    description = ""
                )

                categories.add(0,allDummyCat)

                _favData.postValue(
                    NetworkResult.Success(
                    categories.toArrayList()))

            }catch (e:Exception){
                e.printStackTrace()
                _favData.postValue(NetworkResult.Error(msg = fetchString(com.framework.R.string.something_went_wrong)))

            }
        }
    }


    suspend fun getUpgradeData()= suspendCoroutine<UpgradeGetDataResponse> {cont->
        DevBoostRepository.getUpgradeData().getResponse { response->
            if (response.isSuccess()){
                cont.resume(response as UpgradeGetDataResponse)
            }else{
                cont.resumeWithException(Exception(response.message))
            }

        }
    }

    suspend fun getUserPurchasedFeatures()= suspendCoroutine<Array<CapLimitFeatureResponseItem>> {cont->
        AzureWebsiteNewRepository.getFeatureDetails().getResponse { response->
            if (response.isSuccess()){
                cont.resume(response.arrayResponse as Array<CapLimitFeatureResponseItem>)
            }else{
                cont.resumeWithException(Exception(response.message))
            }

        }
    }

    fun getPurchaseItem(){
        _featurePurchaseData.postValue(NetworkResult.Loading())
        viewModelScope.launch {
            try {
                val appFeaturesResponse = getUpgradeData()
                val userPurchasedFeatures = getUserPurchasedFeatures()


                val featureUiList =convertFeatureResponseToUiItem(appFeaturesResponse)

                doesUserHavePurchasedAnything=checkUserHavePurchasedAnything(userPurchasedFeatures,appFeaturesResponse)

                _featurePurchaseData.postValue(NetworkResult.Success(featureUiList))
            }
            catch (e:Exception){
                _featurePurchaseData.postValue(NetworkResult.Error(e.message))
            }


        }
    }

    private fun checkUserHavePurchasedAnything(
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

    private fun convertFeatureResponseToUiItem(appFeaturesResponse: UpgradeGetDataResponse): ArrayList<FeaturePurchaseUiModel> {
        val featurePurchaseList =  getBundlesWhereUpdateStudioPresent(appFeaturesResponse)

        val updateStudioFeature = getFeatureDetails(Constants.UPDATES_STUDIO_WIDGET_KEY,appFeaturesResponse.Data.firstOrNull()?.features)

        val desc = application().getString(R.string.pay_placeholder_or_placeholder_gst_extra,
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

    private fun getBundlesWhereUpdateStudioPresent(appFeaturesResponse: UpgradeGetDataResponse): ArrayList<FeaturePurchaseUiModel> {
       val featurePurchaseUiList = ArrayList<FeaturePurchaseUiModel>()

        val bundles =  appFeaturesResponse.Data.firstOrNull()?.bundles?.filter { bundle->
            bundle.included_features.find {feature->
                feature.feature_code==Constants.UPDATES_STUDIO_WIDGET_KEY
            }!=null }


        bundles?.forEach {

            val title = application().getString(R.string.buy_it_as_part_of_placeholder,
                it.name)

            val desc = application().getString(R.string.get_branded_update_templates_placeholder_more_features,
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

    fun getFeatureDetails(featureCode: String?, features: List<UpgradeGetDataFeature>?):UpgradeGetDataFeature?{
       return features?.find { it.feature_code==featureCode }
    }
}