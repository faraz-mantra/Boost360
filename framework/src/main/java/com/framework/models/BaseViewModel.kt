package com.framework.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.framework.base.BaseResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

open class BaseViewModel : ViewModel()

fun Observable<BaseResponse>.toLiveData(strategy: BackpressureStrategy = BackpressureStrategy.BUFFER): LiveData<BaseResponse> {
//    return LiveDataReactiveStreams.fromPublisher(
//        this.map { it }
//            .onErrorReturn {
//                it.printStackTrace()
//                val response = BaseResponse()
//                response.error = it
//                response
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .toFlowable(strategy))

  return LiveDataReactiveStreams.fromPublisher(this.toFlowable(strategy))
}