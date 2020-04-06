package com.boost.upgrades.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.GetAllWidgets.GetAllWidgets
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.Utils.readJSONFromAsset
import com.google.gson.Gson
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(application: Application) : BaseViewModel(application) {
    var updatesResult: MutableLiveData<List<WidgetModel>> = MutableLiveData()
    var initialResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var _freeAddonsCount: MutableLiveData<Int> = MutableLiveData()

    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()
    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

    fun upgradeResult(): LiveData<List<WidgetModel>> {
        return updatesResult
    }

    fun initialResult(): LiveData<List<FeaturesModel>> {
        return initialResult
    }

    fun getFreeAddonsCount(): LiveData<Int> {
        return _freeAddonsCount
    }

    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun loadUpdates() {
        updatesLoader.postValue(true)

        if (Utils.isConnectedToInternet(getApplication())) {
//            val data: List<GetAllWidgets>? = readJSONFromAsset(getApplication())
//            val widgetsData = arrayListOf<WidgetModel>()
//            data!!.forEachIndexed { index, getAllWidgets ->
//                widgetsData.add(WidgetModel(
//                        getAllWidgets.id,
//                        getAllWidgets.title,
//                        getAllWidgets.name,
//                        getAllWidgets.price,
//                        getAllWidgets.MRPPrice,
//                        getAllWidgets.discount,
//                        getAllWidgets.image,
//                        Gson().toJson(getAllWidgets.featureDetails)
//                ))
//            }
//            Completable.fromAction {
//                AppDatabase.getInstance(getApplication())!!.widgetDao().insertAllUPdates(widgetsData!!)
//            }
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnComplete {
//                        updatesResult.postValue(widgetsData)
//                        updatesLoader.postValue(false)
                        compositeDisposable.add(
                                ApiService.GetAllFeatures()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                {
                                                    Log.e("GetAllFeatures",it.toString())
                                                    var data = arrayListOf<FeaturesModel>()
                                                    for (item in it.Data[0].features){
                                                        val primaryImage = if(item.primary_image==null) null else item.primary_image.url
                                                        val secondaryImage = arrayListOf<String>()
                                                        if (item.secondary_images != null){
                                                        for(sec_images in item.secondary_images){
                                                            if(sec_images.url !=null) {
                                                                secondaryImage.add(sec_images.url)
                                                            }
                                                        }
                                                        }
                                                        data.add(FeaturesModel(
                                                                item.boost_widget_key,
                                                                item.name,
                                                                item.feature_code,
                                                                item.description,
                                                                item.description_title,
                                                                item.createdon,
                                                                item.updatedon,
                                                                item.websiteid,
                                                                item.isarchived,
                                                                item.is_premium,
                                                                item.target_business_usecase,
                                                                item.feature_importance,
                                                                item.discount_percent,
                                                                item.price,
                                                                item.time_to_activation,
                                                                primaryImage,
                                                                if(item.feature_banner==null) null else item.feature_banner.url,
                                                                if(secondaryImage.size==0) null else secondaryImage.toString(),
                                                                Gson().toJson(item.learn_more_link)

                                                        ))
                                                    }
                                                    Completable.fromAction {
                                                        AppDatabase.getInstance(getApplication())!!
                                                                .featuresDao()
                                                                .insertAllFeatures(data)
                                                    }
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .doOnComplete {
                                                                Log.i("insertAllFeatures","Successfully")
                                                                compositeDisposable.add(
                                                                        AppDatabase.getInstance(getApplication())!!
                                                                                .featuresDao()
                                                                                .getFeaturesItems(true)
                                                                                .subscribeOn(Schedulers.io())
                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                .doOnSuccess {
                                                                                    initialResult.postValue(it)
                                                                                    compositeDisposable.add(
                                                                                            AppDatabase.getInstance(getApplication())!!
                                                                                                    .featuresDao()
                                                                                                    .countFeaturesItems(false)
                                                                                                    .subscribeOn(Schedulers.io())
                                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                                    .doOnSuccess {
                                                                                                        _freeAddonsCount.postValue(it)
                                                                                                    }
                                                                                                    .doOnError {
                                                                                                        updatesError.postValue(it.message)
                                                                                                        updatesLoader.postValue(false)
                                                                                                    }
                                                                                                    .subscribe()
                                                                                    )
                                                                                }
                                                                                .doOnError {
                                                                                    updatesError.postValue(it.message)
                                                                                    updatesLoader.postValue(false)
                                                                                }
                                                                                .subscribe()
                                                                )
                                                            }
                                                            .doOnError {
                                                                updatesError.postValue(it.message)
                                                                updatesLoader.postValue(false)
                                                            }
                                                            .subscribe()

                                                },
                                                {
                                                    Log.e("GetAllFeatures", "error"+it.message)
                                                }
                                        )
                        )
//                    }
//                    .doOnError {
//                        updatesError.postValue(it.message)
//                        updatesLoader.postValue(false)
//                    }
//                    .subscribe()
        } else {
            compositeDisposable.add(
                    AppDatabase.getInstance(getApplication())!!
                            .widgetDao()
                            .queryUpdates()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess {
                                updatesResult.postValue(it)
                                updatesLoader.postValue(false)
                            }
                            .doOnError {
                                updatesError.postValue(it.message)
                                updatesLoader.postValue(false)
                            }
                            .subscribe()
            )
        }
    }

    fun getCartItems() {
        compositeDisposable.add(
                AppDatabase.getInstance(getApplication())!!
                        .cartDao()
                        .getCartItems()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess {
                            cartResult.postValue(it)
                            updatesLoader.postValue(false)
                        }
                        .doOnError {
                            updatesError.postValue(it.message)
                            updatesLoader.postValue(false)
                        }
                        .subscribe()
        )
    }

    fun disposeElements() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }
}
