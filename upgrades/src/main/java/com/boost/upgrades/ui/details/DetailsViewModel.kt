package com.boost.upgrades.ui.details

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.WidgetModel
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailsViewModel(application: Application) : BaseViewModel(application) {

    var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    fun addonsResult(): LiveData<List<FeaturesModel>> {
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

    fun loadAddonsFromDB() {
//        updatesLoader.postValue(true)
//        compositeDisposable.add(
//            AppDatabase.getInstance(getApplication())!!
//                .widgetDao()
//                .queryUpdates()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSuccess {
//                    updatesResult.postValue(it)
//                    updatesLoader.postValue(false)
//                }
//                .doOnError {
//                    updatesError.postValue(it.message)
//                    updatesLoader.postValue(false)
//                }
//                .subscribe()
//        )
        updatesLoader.postValue(true)
        compositeDisposable.add(
                AppDatabase.getInstance(getApplication())!!
                        .featuresDao()
                        .getFeaturesItems(true)
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

    fun addItemToCart(updatesModel: FeaturesModel) {
        val discount = 100 - updatesModel.discount_percent
        val paymentPrice = (discount * updatesModel.price) / 100
        val cartItem = CartModel(
                updatesModel.boost_widget_key,
                updatesModel.name,
                updatesModel.primary_image,
                paymentPrice,
                updatesModel.price,
                updatesModel.discount_percent,
                1
        )


        Completable.fromAction {
            AppDatabase.getInstance(getApplication())!!.cartDao()
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
