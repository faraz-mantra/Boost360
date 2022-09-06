package com.boost.marketplace.ui.browse

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
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel: BaseViewModel()  {

    var allFeaturesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(application: Application,
                                lifecycleOwner: LifecycleOwner
    ){
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }


    fun addonsResult(): LiveData<List<FeaturesModel>> {
        return allFeaturesResult
    }

    fun bundleResult(): LiveData<List<BundlesModel>> {
        return allBundleResult
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
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getAllFeatures()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    allFeaturesResult.postValue(it)
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

    fun loadAllPackagesFromDB() {
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
                    updatesLoader.postValue(false)
                }
                .doOnError {
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                }
                .subscribe()
        )
    }

    fun addItemToCartPackage1(cartItem: CartModel) {
        updatesLoader.postValue(false)
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
}