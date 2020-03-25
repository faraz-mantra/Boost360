package com.boost.upgrades.ui.removeaddons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.Cart
import com.boost.upgrades.data.model.UpdatesModel
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Utils
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RemoveAddonsViewModel(application: Application) : BaseViewModel(application) {

    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var cartResult: MutableLiveData<List<Cart>> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    fun cartResult(): LiveData<List<Cart>> {
        return cartResult
    }

    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun getCartItems(){
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

    fun updateCartItems(item: List<Cart>){
        Completable.fromAction {
            AppDatabase.getInstance(getApplication())!!.cartDao().emptyCart()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Completable.fromAction {
                    AppDatabase.getInstance(getApplication())!!.cartDao().insertAllUPdates(item)
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
                updatesLoader.postValue(false)
            }
            .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue(false)
            }
            .subscribe()
//        compositeDisposable.add(
//            AppDatabase.getInstance(getApplication())!!
//                .cartDao()
//                .emptyCart()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSuccess {
//                    cartResult.postValue(it)
//                    updatesLoader.postValue(false)
//                }
//                .doOnError {
//                    updatesError.postValue(it.message)
//                    updatesLoader.postValue(false)
//                }
//                .subscribe()
//        )
    }

    fun disposeElements() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }
}
