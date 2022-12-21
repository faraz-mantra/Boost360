package com.boost.marketplace.ui.My_Plan

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.Edgecase.EdgeCases
import com.boost.dbcenterapi.data.api_model.GetFeatureDetails.FeatureDetailsV2Item
import com.boost.dbcenterapi.data.api_model.mycurrentPlanV3.MyPlanV3
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MyCurrentPlanViewModel() : BaseViewModel() {
    var updatesResult: MutableLiveData<ArrayList<FeatureDetailsV2Item>> = MutableLiveData()
    var myplanV3Result: MutableLiveData<MyPlanV3> = MutableLiveData()
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

    fun myplanResultV3(): LiveData<MyPlanV3> {
        return myplanV3Result
    }

    //new implementation with expiry date
    fun loadPurchasedItems(fpid: String, clientId: String, activity: Activity) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.GetMyPlanV3(fpid, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { it1 ->

                        var inActiveList =
                            ArrayList<String>()
                        var activeList =
                            ArrayList<String>()

                        for (singleItem in it1.Result) {
                            if (singleItem.ActionNeeded != null && singleItem.FeatureDetails != null) {
                                if (singleItem.ActionNeeded.ActionNeeded != 0 && singleItem.FeatureDetails.FeatureState != 7) {
                                    inActiveList.add(singleItem.FeatureDetails.FeatureKey)
                                } else if (singleItem.ActionNeeded.ActionNeeded == 0 && (singleItem.FeatureDetails.FeatureState == 3
                                            || singleItem.FeatureDetails.FeatureState == 4 || singleItem.FeatureDetails.FeatureState == 5
                                            || singleItem.FeatureDetails.FeatureState == 6)
                                ) {
                                    inActiveList.add(singleItem.FeatureDetails.FeatureKey)
                                } else if (singleItem.ActionNeeded.ActionNeeded == 0 && singleItem.FeatureDetails.FeatureState == 1) {
                                    activeList.add(singleItem.FeatureDetails.FeatureKey)
                                }
                            }
                        }

                        val pref = SharedPrefs(activity)
                        if (pref.getBoostKeyboardActivateState()) {
                            activeList.add("BOOSTKEYBOARD")
                            inActiveList.removeIf { it.contains("BOOSTKEYBOARD") }
                        }

//                        val list = ArrayList<String>()
//                        for (singleItem in it1) {
//                            list.add(singleItem.featureCode)
//                        }
                        compositeDisposable.add(
                            AppDatabase.getInstance(application)!!
                                .featuresDao()
                                .getallActiveFeatures1(inActiveList)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess { it2 ->
                                    val listFeaturesModel = it2.map { it3 ->
                                        it1.Result.firstOrNull {
                                            it.FeatureDetails.FeatureKey.equals(
                                                it3.feature_code
                                            )
                                        }
                                            .apply {
                                                it3.expiryDate = this?.FeatureDetails?.ExpiryDate
                                                it3.activatedDate =
                                                    this?.FeatureDetails?.ActivatedDate
                                                it3.featureState =
                                                    this?.FeatureDetails?.FeatureState
                                                it3.createdon = this?.FeatureDetails?.CreatedDate
                                                it3.actionNeeded =
                                                    this?.ActionNeeded?.ActionNeeded.toString()
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
                                                    .getallActiveFeatures1(inActiveList)
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
                                            //    activePremiumWidgetList.postValue(listFeaturesModel)
                                            inActiveWidgetCount.postValue(listFeaturesModel)
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
                                .getallActiveFeatures1(activeList)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSuccess { it2 ->
                                    val listFeaturesModel = it2.map { it3 ->
                                        it1.Result.firstOrNull {
                                            it.FeatureDetails.FeatureKey.equals(
                                                it3.feature_code
                                            )
                                        }
                                            .apply {
                                                it3.expiryDate = this?.FeatureDetails?.ExpiryDate
                                                it3.activatedDate =
                                                    this?.FeatureDetails?.ActivatedDate
                                                it3.featureState =
                                                    this?.FeatureDetails?.FeatureState
                                                it3.createdon = this?.FeatureDetails?.CreatedDate
                                                it3.actionNeeded =
                                                    this?.ActionNeeded?.ActionNeeded.toString()
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
                                                    .getallActiveFeatures1(activeList)
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
                                            //activePremiumWidgetList.postValue(listFeaturesModel)
                                            activeWidgetCount.postValue(listFeaturesModel)
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

    fun edgecases(fpid: String, clientId: String, featureCode: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getEdgeCases(fpid, clientId, featureCode)
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

    //old implementation without expiry date
    fun myPlanV3Status(fpid: String, clientId: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.GetMyPlanV3(fpid, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        myplanV3Result.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }


}
