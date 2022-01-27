package com.boost.marketplace.ui.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FeatureDetailsViewModel: BaseViewModel() {

    var updatesResult: MutableLiveData<FeaturesModel> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    fun addonsResult(): LiveData<FeaturesModel> {
        return updatesResult
    }

    fun bundleResult(): LiveData<List<BundlesModel>> {
        return allBundleResult
    }

    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun addonsError(): LiveData<String> {
        return updatesError
    }

    fun addonsLoader(): LiveData<Boolean> {
        return updatesLoader
    }
    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(application: Application,
                                lifecycleOwner: LifecycleOwner
    ){
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }

    fun loadAddonsFromDB(boostKey: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getFeaturesItemByFeatureCode(boostKey)
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
//                        .subscribe(),
                .subscribe({}, {e -> Log.e("TAG", "Empty DB")}
                )
        )
    }

    fun getAllPackages() {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .bundlesDao()
                .getBundleItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    Log.e("getAssociatedPackages", it.toString())
                    allBundleResult.postValue(it)
                }
                .doOnError {
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                }
                .subscribe()
        )
    }



    fun addItemToCart1(updatesModel: FeaturesModel) {
        updatesLoader.postValue(false)
        val discount = 100 - updatesModel.discount_percent
        val paymentPrice = (discount * updatesModel.price) / 100.0
        val cartItem = CartModel(
            updatesModel.feature_id,
            updatesModel.boost_widget_key,
            updatesModel.feature_code,
            updatesModel.name,
            updatesModel.description,
            updatesModel.primary_image,
            paymentPrice,
            updatesModel.price.toDouble(),
            updatesModel.discount_percent,
            1,
            1,
            "features",
            updatesModel.extended_properties
        )


        Completable.fromAction {
            AppDatabase.getInstance(application)!!.cartDao()
                .insertToCart(cartItem)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                updatesLoader.postValue(false)
            }
            .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue(false)
            }
            .subscribe()
    }

    fun addItemToCart(updatesModel: FeaturesModel) {
        updatesLoader.postValue(false)
        val discount = 100 - updatesModel.discount_percent
        val paymentPrice = (discount * updatesModel.price) / 100.0
        val cartItem = CartModel(
            updatesModel.feature_id,
            updatesModel.boost_widget_key,
            updatesModel.feature_code,
            updatesModel.name,
            updatesModel.description,
            updatesModel.primary_image,
            paymentPrice,
            updatesModel.price.toDouble(),
            updatesModel.discount_percent,
            1,
            1,
            "features",
            updatesModel.extended_properties
        )

        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .cartDao()
                .checkCartFeatureTableKeyExist()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it == 1) {
                        Completable.fromAction {
                            AppDatabase.getInstance(application)!!.cartDao().emptyCart()
                        }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnError {
                                //in case of error
                            }
                            .doOnComplete {
                                Completable.fromAction {
                                    AppDatabase.getInstance(application)!!.cartDao()
                                        .insertToCart(cartItem)
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnComplete {
//                                                        getCartItems()
                                        updatesLoader.postValue(false)
                                    }
                                    .doOnError {
                                        updatesError.postValue(it.message)
                                        updatesLoader.postValue(false)
                                    }
                                    .subscribe()
                            }
                            .subscribe()
                    }else{
                        Completable.fromAction {
                            AppDatabase.getInstance(application)!!.cartDao()
                                .insertToCart(cartItem)
                        }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete {
//                                            getCartItems()
                                updatesLoader.postValue(false)
                            }
                            .doOnError {
                                updatesError.postValue(it.message)
                                updatesLoader.postValue(false)
                            }
                            .subscribe()
                    }
                }, {
//                            Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG).show()
                })
        )


    }

    fun getCartItems() {
        updatesLoader.postValue(false)
        compositeDisposable.add(
            AppDatabase.getInstance(application)!!
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