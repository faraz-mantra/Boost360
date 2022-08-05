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
import com.framework.utils.getResponse
import com.framework.utils.toArrayList
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PromoUpdatesViewModel: BaseViewModel() {


    private var configResponse: GetTemplateViewConfigResult?=null
    private val todaysPickMData=MutableLiveData<ArrayList<PosterPackModel>?>()
    val todaysPickLData:LiveData<ArrayList<PosterPackModel>?> get() =
        todaysPickMData

    private val browseAllMData=MutableLiveData<ArrayList<CategoryUi>>()
    val browseAllLData:LiveData<ArrayList<CategoryUi>> get() =
        browseAllMData


    private val _favData=MutableLiveData<ArrayList<CategoryUi>>()
    val favData:LiveData<ArrayList<CategoryUi>> get() = _favData

    private val _todayPickData=MutableLiveData<ArrayList<CategoryUi>>()
    val todayPickData:LiveData<ArrayList<CategoryUi>> get() = _todayPickData


    init {
        getTodaysPickTemplate()
        getTemplatesUi()
    }
/*
    suspend fun getTemplates(floatingPointId: String?, floatingPointTag: String?,
                             tags:List<String>?,
                             configSection:FestivePosterSectionModel?,
                             @LayoutRes templateViewId:Int,@LayoutRes packViewId:Int)=
        suspendCoroutine<ArrayList<PosterPackModel>?> {cont->
        NowFloatsRepository.getTemplates(floatingPointId,floatingPointTag,tags).getResponse {
            if (it.isSuccess()){
                val templates_response = it as? GetTemplatesResponse
                var todaysPickList: ArrayList<PosterPackModel>? = ArrayList()
                templates_response?.let {
                    configSection?.tags?.forEach { pack_tag ->
                        val templateList = ArrayList<PosterModel>()
                        templates_response.Result.templates?.forEach { template ->
                            var posterTag =
                                template.tags?.find { posterTag -> posterTag == pack_tag.tag }
                            if (posterTag != null && template.active == true) {
                                template.greeting_message = pack_tag.description
                                template.layout_id =templateViewId

                                templateList.add(template.clone()!!)
                            }
                        }
                        *//* val filterdList= ArrayList<PosterModel>()
                        if (templateList.size>=4){
                            filterdList.addAll(
                                templateList.take(4))
                            filterdList.add(PosterModel(layout_id = RecyclerViewItemType.VIEW_MORE_POSTER.getLayout()))
                        }else{
                            filterdList.addAll(templateList)
                        }*//*

                        *//*todaysPickList?.add(
                            PosterPackModel(
                                pack_tag,
                                filterdList,
                                isPurchased = pack_tag.isPurchased==true,
                                list_layout = RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()
                            )
                        )*//*

                        todaysPickList?.add(
                            PosterPackModel(
                                pack_tag,
                                templateList,
                                isPurchased = pack_tag.isPurchased == true,
                                list_layout = packViewId
                            )
                        )

                    }
                }

                cont.resume(todaysPickList)
            }else{
                cont.resume(null)
            }

        }
    }


    fun getTodaysPickData(fKey:String,floatingPointId: String?,floatingPointTag: String?){
        viewModelScope.launch {
            if (configResponse==null){
                configResponse = getTemplateConfig(fKey,floatingPointId,floatingPointTag)
            }
            if (configResponse==null){
                todaysPickMData.postValue(null)
            }
            val tags = prepareTagForApi(configResponse?.todayPick?.tags)
            val todaysPickList = getTemplates(floatingPointId,floatingPointTag,tags,
                configResponse?.todayPick,
                RecyclerViewItemType.TEMPLATE_VIEW_FOR_TODAY_PICK.getLayout(),
                RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()
                )
            todaysPickMData.postValue(todaysPickList)

        }

    }

    fun getBrowseAllData(fKey:String,floatingPointId: String?,floatingPointTag: String?){
        viewModelScope.launch {
            if (configResponse==null){
                configResponse = getTemplateConfig(fKey,floatingPointId,floatingPointTag)
            }
            if (configResponse==null){
                browseAllMData.postValue(null)
            }
            val tags = prepareTagForApi(configResponse?.allTemplates?.tags)
            val browseAllList = getTemplates(floatingPointId,floatingPointTag,tags,configResponse?.allTemplates,-1,RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT.getLayout())
           // browseAllMData.postValue(browseAllList)

        }

    }

    private fun prepareTagForApi(tags: List<PosterPackTagModel>?): ArrayList<String> {
        val list = ArrayList<String>()
        tags?.forEach {
            it.tag?.let { tag -> list.add(tag) }
        }
        return list
    }

    suspend fun getTemplateConfig(fKey:String,floatingPointId: String?,floatingPointTag: String?)=
        suspendCoroutine<GetTemplateViewConfigResult?>{cont->
        NowFloatsRepository.getTemplateConfig(fKey,floatingPointId,floatingPointTag).getResponse {
            if (it.isSuccess()){
                val response = it as? GetTemplateViewConfigResponse
                response?.let {res->
                    cont.resume(res.Result)
                }
            }else{
                cont.resume(null)
            }
        }
    }*/

    fun getTodaysPickTemplate(){
        NowFloatsRepository.getTodaysTemplates().getResponse {
            if (it.isSuccess()){
                val response = it as? GetTodayTemplateResponse
                _todayPickData.postValue(response?.Result?.asDomainModel()?.toArrayList())

            }
        }
    }




    fun templateSaveAction(action: TemplateSaveActionBody.ActionType,
                           isFav:Boolean, templateId:String): LiveData<BaseResponse> {
        return NowFloatsRepository.saveTemplateAction(action,isFav,templateId).toLiveData()
    }

    private suspend fun getCategories()=
        suspendCoroutine<List<CategoryUi>>{cont->
            NowFloatsRepository.getCategories().getResponse {
                if (it.isSuccess()){
                    val response = it as? GetCategoryResponse
                    cont.resume(response!!.Result.asDomainModels())

                }else{
                    cont.resumeWithException(Exception())
                }
            }
        }

    private suspend fun getTemplates(isFav:Boolean?=null)=
        suspendCoroutine<List<GetTemplatesResponseV2Template>>{cont->
            NowFloatsRepository.getTemplatesV2(isFav).getResponse {
                if (it.isSuccess()){
                    val response = it as? GetTemplatesResponseV2
                    cont.resume(response!!.Result.templates)

                }else{
                    cont.resumeWithException(Exception())
                }
            }
        }

    fun getTemplatesUi(){
        viewModelScope.launch {
            try {
                val categories = getCategories()
                val allTemplates = getTemplates()
                categories.forEach {uicat->
                    val dbTemplates = allTemplates.filter { template->
                        template.categories.find {dbcat->
                            dbcat.id==uicat.id
                        }!=null
                    }
                    uicat.setTemplates(dbTemplates.asDomainModels())
                }
                browseAllMData.postValue(categories.toArrayList())
                getFavTemplates()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }


    fun getFavTemplates() {
        viewModelScope.launch {
            try {
                val categories = getCategories().toArrayList()
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
                    name = "All",
                    thumbnailUrl = "https://i.ibb.co/7Xg48cN/Coming-soon-card.png",
                    templates = allUiTemplates
                )

                categories.add(0,allDummyCat)

                _favData.postValue(categories.toArrayList())

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }



}