package com.boost.marketplace.ui.details.domain

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CustomDomainViewModel() : BaseViewModel() {

    var updatesResult: MutableLiveData<CustomDomains> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)

    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(application: Application,
                                lifecycleOwner: LifecycleOwner
    ){
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }

    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun updateResult(): LiveData<CustomDomains> {
        return updatesResult
    }

    fun GetSuggestedDomains(domainRequest: DomainRequest) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getDomains(domainRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updatesResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }
}