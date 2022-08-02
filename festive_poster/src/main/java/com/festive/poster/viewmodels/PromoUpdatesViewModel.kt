package com.festive.poster.viewmodels

import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.models.*
import com.festive.poster.models.response.*
import com.festive.poster.reset.repo.*
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.utils.getResponse
import com.framework.utils.toArrayList
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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
                        /* val filterdList= ArrayList<PosterModel>()
                        if (templateList.size>=4){
                            filterdList.addAll(
                                templateList.take(4))
                            filterdList.add(PosterModel(layout_id = RecyclerViewItemType.VIEW_MORE_POSTER.getLayout()))
                        }else{
                            filterdList.addAll(templateList)
                        }*/

                        /*todaysPickList?.add(
                            PosterPackModel(
                                pack_tag,
                                filterdList,
                                isPurchased = pack_tag.isPurchased==true,
                                list_layout = RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()
                            )
                        )*/

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

    private suspend fun getTemplates()=
        suspendCoroutine<List<GetTemplatesResponseV2Template>>{cont->
            NowFloatsRepository.getTemplatesV2().getResponse {
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
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}