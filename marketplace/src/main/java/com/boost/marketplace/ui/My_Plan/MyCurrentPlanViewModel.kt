package com.boost.marketplace.ui.My_Plan

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.Edgecase.EdgeCases
import com.boost.dbcenterapi.data.api_model.GetFeatureDetails.FeatureDetailsV2Item
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MyCurrentPlanViewModel() : BaseViewModel() {
    var updatesResult: MutableLiveData<ArrayList<FeatureDetailsV2Item>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var activeFreeWidgetList: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var activePremiumWidgetList: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var activeWidgetCount: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var inActiveWidgetCount: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var edgecaseResult: MutableLiveData<EdgeCases> = MutableLiveData()
    val compositeDisposable = CompositeDisposable()
    var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)
    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(
        application: Application,
        lifecycleOwner: LifecycleOwner
    ) {
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

    fun updateResult(): LiveData<ArrayList<FeatureDetailsV2Item>> {
        return updatesResult
    }

    fun getActivePremiumWidgets(): LiveData<List<FeaturesModel>> {
        return activePremiumWidgetList
    }

    fun activeWidgetCount(): LiveData<List<FeaturesModel>> {
        return activeWidgetCount
    }

    fun inActiveWidgetCount(): LiveData<List<FeaturesModel>> {
        return inActiveWidgetCount
    }

    fun edgecaseResult(): LiveData<EdgeCases> {
        return edgecaseResult
    }

    fun loadPurchasedItems(fpid: String, clientId: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.GetFeatureDetails(fpid, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { it1 ->
                        val list = ArrayList<String>()
                        for (singleItem in it1) {
                            list.add(singleItem.featureCode)
                        }
                        compositeDisposable.add(
                            AppDatabase.getInstance(application)!!
                                .featuresDao()
                                .getallActiveFeatures1(list)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess { it2 ->
                                    val listFeaturesModel = it2.map { it3 ->
                                        it1.firstOrNull { it.featureCode.equals(it3.feature_code) }
                                            .apply {
                                                it3.expiryDate = this?.expiryDate
                                                it3.activatedDate = this?.activatedDate
                                                it3.featureState = this?.featureState
                                            };it3
                                    }
                                    Completable.fromAction {
                                        AppDatabase.getInstance(application)!!
                                            .featuresDao()
                                            .insertAllFeatures(listFeaturesModel)
                                    }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnComplete {

                                            compositeDisposable.add(
                                                AppDatabase.getInstance(application)!!
                                                    .featuresDao()
                                                    .getallActivefeatureCount1(list)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .doOnSuccess {
                                                        activeWidgetCount.postValue(it)
                                                    }
                                                    .doOnError {
                                                        updatesError.postValue(it.message)
                                                        updatesLoader.postValue(false)
                                                    }.subscribe()
                                            )
                                            Log.i("insertAllFeatures", "Successfully")
                                            activePremiumWidgetList.postValue(listFeaturesModel)
                                            updatesLoader.postValue(false)

                                        }.doOnError {
                                            updatesError.postValue(it.message)
                                            updatesLoader.postValue(false)
                                        }
                                        .subscribe()

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
                                .getallActiveFeatures1(list)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess { it2 ->
                                    val listFeaturesModel = it2.map { it3 ->
                                        it1.firstOrNull { it.featureCode.equals(it3.feature_code) }
                                            .apply {
                                                it3.expiryDate = this?.expiryDate
                                                it3.activatedDate = this?.activatedDate
                                                it3.featureState = this?.featureState
                                            };it3
                                    }
                                    Completable.fromAction {
                                        AppDatabase.getInstance(application)!!
                                            .featuresDao()
                                            .insertAllFeatures(listFeaturesModel)
                                    }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnComplete {

                                            compositeDisposable.add(
                                                AppDatabase.getInstance(application)!!
                                                    .featuresDao()
                                                    .getallInActivefeatureCount(list)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .doOnSuccess {
                                                        inActiveWidgetCount.postValue(it)
                                                    }
                                                    .doOnError {
                                                        updatesError.postValue(it.message)
                                                        updatesLoader.postValue(false)
                                                    }.subscribe()
                                            )
                                            Log.i("insertAllFeatures", "Successfully")
                                            activePremiumWidgetList.postValue(listFeaturesModel)
                                            updatesLoader.postValue(false)

                                        }.doOnError {
                                            updatesError.postValue(it.message)
                                            updatesLoader.postValue(false)
                                        }
                                        .subscribe()

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

    fun edgecases(fpid: String,clientId:String,featureCode:String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getEdgeCases(fpid,clientId,featureCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        edgecaseResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }
}
