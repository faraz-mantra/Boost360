package com.boost.marketplace.ui.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.framework.models.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FeatureDetailsViewModel(): BaseViewModel() {

    var updatesResult: MutableLiveData<FeaturesModel> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    fun addonsResult(): LiveData<FeaturesModel> {
        return updatesResult
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

    fun loadAddonsFromDB(application: Application, boostKey: String) {
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

    fun addItemToCart1(application: Application, updatesModel: FeaturesModel) {
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

    fun addItemToCart(application: Application, updatesModel: FeaturesModel) {
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

    fun getCartItems(application: Application) {
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