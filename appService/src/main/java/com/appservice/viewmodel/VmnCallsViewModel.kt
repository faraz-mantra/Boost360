package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.appservice.rest.TaskCode
import com.appservice.rest.repository.RiaMemoryWithFloatsTwoRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.QueryMap

class VmnCallsViewModel: BaseViewModel() {

    fun trackerCalls(
        @QueryMap data: Map<*, *>?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.trackerCalls(
                data
            ).toLiveData()

    }


  /*  fun getVmnSummary(
        @Query("clientId") clientId: String?,
        @Query("fpid") fpId: String?,
        @Query("identifierType") type: String?,
    ): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getVmnSummary(
                clientId,
                fpId,
                type
            ).toLiveData()

    }*/

    fun getCallCountByType(
        @Query("fptag") fptag: String?,
        @Query("eventType") eventType: String?,
        @Query("eventChannel") eventChannel: String?,
    ): LiveData<BaseResponse> {
        return RiaMemoryWithFloatsTwoRepository.getCallCountByType(
                fptag,
                eventType,
                eventChannel
            ).toLiveData()

    }

 /*   fun requestVmn(
        @Body bodyMap: Map<String?, String?>?,
        @Query("authClientId") clientId: String?,
        @Query("fpTag") fpTag: String?,
    ): LiveData<BaseResponse> {
        return WithFloatTwoRepository.requestVmn(
                bodyMap,
                clientId,
                fpTag
            ).toLiveData()

    }*/
}