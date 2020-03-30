package com.boost.upgrades.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.GetAllWidgets.GetAllWidgets
import com.boost.upgrades.data.model.CartModel
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
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()
    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

    fun upgradeResult(): LiveData<List<WidgetModel>> {
        return updatesResult
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
//            compositeDisposable.add(
//            ApiService.getUpdates(Constants.START_ZERO_VALUE)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        Timber.e(it.size.toString())
            val data: List<GetAllWidgets>? = readJSONFromAsset(getApplication())
            val widgetsData = arrayListOf<WidgetModel>()
            data!!.forEachIndexed { index, getAllWidgets ->
                widgetsData.add(WidgetModel(
                        getAllWidgets.id,
                        getAllWidgets.title,
                        getAllWidgets.name,
                        getAllWidgets.price,
                        getAllWidgets.MRPPrice,
                        getAllWidgets.discount,
                        getAllWidgets.image,
                        Gson().toJson(getAllWidgets.featureDetails)
                ))
            }
            Completable.fromAction {
                AppDatabase.getInstance(getApplication())!!.widgetDao().insertAllUPdates(widgetsData!!)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        updatesResult.postValue(widgetsData)
                        updatesLoader.postValue(false)
                    }
                    .doOnError {
                        updatesError.postValue(it.message)
                        updatesLoader.postValue(false)
                    }
                    .subscribe()
//                    },
//                    {
//                        updatesError.postValue(it.message)
//                        updatesLoader.postValue(false)
//                    }
//                )
//            )
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
