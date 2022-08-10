package com.festive.poster.viewmodels

import android.net.Uri
import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.*
import com.festive.poster.models.response.*
import com.festive.poster.reset.repo.*
import com.framework.BaseApplication
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.rest.NetworkResult
import com.framework.utils.fetchString
import com.framework.utils.getResponse
import com.framework.utils.toArrayList
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PromoUpdatesViewModel: BaseViewModel() {




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


    init {
       refreshTemplates()
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
                    templates = allUiTemplates
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



}