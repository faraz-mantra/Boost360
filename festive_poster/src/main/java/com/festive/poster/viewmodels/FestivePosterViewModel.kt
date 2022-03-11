package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.reset.repo.*
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

    fun getFavTemplates(floatingPointId: String?,floatingPointTag: String?,featureKey:String): LiveData<BaseResponse> {
        return NowFloatsRepository.getFavTemplates(floatingPointId,floatingPointTag,featureKey).toLiveData()
    }

    fun makeTemplateFav(floatingPointId: String?,floatingPointTag: String?,templateId:String): LiveData<BaseResponse> {
        return NowFloatsRepository.getFavTemplates(floatingPointId,floatingPointTag,templateId).toLiveData()
    }


    fun getTemplateConfig(fKey:String,floatingPointId: String?,floatingPointTag: String?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplateConfig(fKey,floatingPointId,floatingPointTag).toLiveData()
    }

    fun getFeatureDetails(fpId:String?,clientId:String?): LiveData<BaseResponse> {
        return FeatureProcessorRepository.getFeatureDetails(fpId,clientId).toLiveData()
    }

    fun getUserDetails(fpTag: String?, clientId: String): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getUserDetails(fpTag,clientId).toLiveData()
    }
    fun getUpgradeData(): LiveData<BaseResponse> {
        return DevBoostRepository.getUpgradeData().toLiveData()
    }

 /*   fun uploadProfileImage(floatingPointId: String?,floatingPointTag: String?,fileName:String,file: RequestBody?):LiveData<BaseResponse> {
        return NowFloatsRepository.uploadProfileImage(floatingPointId,floatingPointTag,fileName,file).toLiveData()
    }
*/
    fun uploadProfileImage( clientId: String?,loginId:String?,fileName:String, file: RequestBody?): LiveData<BaseResponse> {
        return WithFloatsRepository.uploadUserProfileImage(clientId,loginId,fileName,file).toLiveData()
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

    fun updatePurchaseStatus(floatingPointId: String?,fpTag: String?,posterTag:String?,templateIds:List<String>): LiveData<BaseResponse> {
        return NowFloatsRepository.updatePurchaseStatus(floatingPointId,fpTag,posterTag,templateIds).toLiveData()
    }

    fun favPoster(floatingPointId: String?,posterId:String?,templateId: String?): LiveData<BaseResponse> {
        return NowFloatsRepository.makeTempFav(floatingPointId,posterId,templateId).toLiveData()
    }

}