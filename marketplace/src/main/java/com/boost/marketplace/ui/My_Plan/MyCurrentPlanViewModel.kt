package com.boost.marketplace.ui.My_Plan

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.GetFeatureDetails.FeatureDetailsV2Item
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MyCurrentPlanViewModel(): BaseViewModel() {
    var updatesResult: MutableLiveData <ArrayList<FeatureDetailsV2Item>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    var activeFreeWidgetList:MutableLiveData<List<FeaturesModel>> = MutableLiveData()

    var activePremiumWidgetList: MutableLiveData<List<FeaturesModel>> = MutableLiveData()

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

    fun getActiveFreeWidgets(): LiveData<List<FeaturesModel>> {
        return activeFreeWidgetList
    }


    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun updateResult():LiveData<ArrayList<FeatureDetailsV2Item>>{
        return updatesResult
    }

    fun getActivePremiumWidgets(): LiveData<List<FeaturesModel>> {
        return activePremiumWidgetList
    }

//    fun loadUpdates(auth:String,fpid: String, clientId: String) {
//        updatesLoader.postValue(true)
//        compositeDisposable.add(
//            ApiService.GetFloatingPointWebWidgets(auth,fpid, clientId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
////                                                        activeWidgetList.postValue(it.Result)
//                        Log.i("FloatingPointWebWidgets", ">> " + it.Result)
//                        compositeDisposable.add(
//                            AppDatabase.getInstance(application)!!
//                                .featuresDao()
//                                .getallActiveFeatures(it.Result, true)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .doOnSuccess {
//                                    activePremiumWidgetList.postValue(it)
//                                    updatesLoader.postValue(false)
//                                }
//                                .doOnError {
//                                    updatesError.postValue(it.message)
//                                    updatesLoader.postValue(false)
//                                }
//                                .subscribe()
//
//                        )
//                        compositeDisposable.add(
//                            AppDatabase.getInstance(application)!!
//                                .featuresDao()
//                                .getallActiveFeatures(it.Result, false)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .doOnSuccess {
//                                    updatesResult.postValue(it)
//                                }
//                                .doOnError {
//                                    updatesError.postValue(it.message)
//                                    updatesLoader.postValue(false)
//                                }
//                                .subscribe()
//
//                        )
//                    },
//                    {
//                        updatesError.postValue(it.message)
//                        updatesLoader.postValue(false)
//                    }
//                )
//        )
//    }

    fun disposeElements() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }

    fun loadPurchasedItems(fpid: String, clientId: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.GetFeatureDetails(fpid, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {it1 ->
                        val list= ArrayList<String>()
                        for (singleItem in it1 ) {
                            list.add(singleItem.featureCode)
                        }
                        compositeDisposable.add(
                            AppDatabase.getInstance(application)!!
                                .featuresDao()
                                .getallActiveFeatures(list, true)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess {it2->
                                    val listFeaturesModel=it2.map {it3->
                                         it1.firstOrNull { it.featureCode.equals(it3.feature_code)}.apply {
                                            it3.expiryDate=this?.expiryDate
                                             it3.activatedDate=this?.activatedDate
                                             it3.status=this?.featureState
                                        };it3
                                        }
                                    activePremiumWidgetList.postValue(listFeaturesModel)
                                    updatesLoader.postValue(false)
                                }
                                .doOnError {
                                    updatesError.postValue(it.message)
                                    updatesLoader.postValue(false)
                                }
                                .subscribe()

                        )
                        compositeDisposable.add(
                            AppDatabase.getInstance(application)!!
                                .featuresDao()
                                .getallActiveFeatures(list, false)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess {it2->
                                    val listFeaturesModel=it2.map {it3->
                                        it1.firstOrNull { it.featureCode.equals(it3.feature_code)}.apply {
                                            it3.expiryDate=this?.expiryDate
                                            it3.activatedDate=this?.activatedDate
                                            it3.status=this?.featureState
                                        };it3
                                    }
                                    activeFreeWidgetList.postValue(listFeaturesModel)
                                    updatesLoader.postValue(false)
                                }
                                .doOnError {
                                    updatesError.postValue(it.message)
                                    updatesLoader.postValue(false)
                                }
                                .subscribe()

                        )
//                        updatesResult.postValue(it)
//                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }
}
