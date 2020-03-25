package com.boost.upgrades.ui.details

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.Cart
import com.boost.upgrades.data.model.UpdatesModel
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailsViewModel(application: Application) : BaseViewModel(application) {

    var updatesResult: MutableLiveData<List<UpdatesModel>> = MutableLiveData()
    var cartResult: MutableLiveData<List<Cart>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    fun addonsResult(): LiveData<List<UpdatesModel>> {
        return updatesResult
    }

    fun cartResult(): LiveData<List<Cart>> {
        return cartResult
    }

    fun addonsError(): LiveData<String> {
        return updatesError
    }

    fun addonsLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun loadAddonsFromDB() {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            AppDatabase.getInstance(getApplication())!!
                .updatesDao()
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

    fun addItemToCart(updatesModel: UpdatesModel) {
        val cartItem = Cart()
        cartItem.item_id = updatesModel.id
        cartItem.item_name = updatesModel.name
        cartItem.link = updatesModel.image
        cartItem.price = updatesModel.price
        cartItem.quantity = 1


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

    fun disposeElements() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }
}
