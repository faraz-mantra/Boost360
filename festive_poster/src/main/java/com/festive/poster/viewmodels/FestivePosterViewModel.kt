package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.reset.repo.DevBoostRepository
import com.festive.poster.reset.repo.FeatureProcessorRepository
import com.festive.poster.reset.repo.NowFloatsRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
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

class FestivePosterViewModel: BaseViewModel() {

    fun getTemplates(floatingPointId: String?,floatingPointTag: String?,tags:List<String>?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplates(floatingPointId,floatingPointTag,tags).toLiveData()
    }

    fun getTemplateConfig(floatingPointId: String?,floatingPointTag: String?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplateConfig(floatingPointId,floatingPointTag).toLiveData()
    }

    fun getFeatureDetails(fpId:String?,clientId:String?): LiveData<BaseResponse> {
        return FeatureProcessorRepository.getFeatureDetails(fpId,clientId).toLiveData()
    }

    fun getUpgradeData(): LiveData<BaseResponse> {
        return DevBoostRepository.getUpgradeData().toLiveData()
    }

    fun uploadProfileImage(floatingPointId: String?,floatingPointTag: String?,fileName:String,file: RequestBody?):LiveData<BaseResponse> {
        return NowFloatsRepository.uploadProfileImage(floatingPointId,floatingPointTag,fileName,file).toLiveData()
    }

    fun saveKeyValue(floatingPointId: String?,fpTag: String?,templateIds:List<String>,map:HashMap<String,String?>):LiveData<BaseResponse>{

        val lData = MutableLiveData<BaseResponse>()
        val observableList = ArrayList<Observable<BaseResponse>>()
        templateIds.forEach {
            observableList.add(NowFloatsRepository.saveKeyValue(floatingPointId,fpTag,it,map))
        }
        Observable.merge(observableList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :Observer<BaseResponse>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: BaseResponse) {
                    lData.value = t
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {

                }

            })

        return lData
    }
    fun prepareTemplatePackList(floatingPointId: String?,floatingPointTag: String?,tags: List<PosterPackTagModel>?){
        val list = ArrayList<PosterPackModel>()
        viewModelScope.launch {
            tags?.forEach {

               val response =  NowFloatsRepository.getTemplates(floatingPointId,floatingPointTag, arrayListOf(it.tag))

              //  val posterPackModel = PosterPackModel(it,response.Result.Templates.toArrayList())

            }
        }
    }

}