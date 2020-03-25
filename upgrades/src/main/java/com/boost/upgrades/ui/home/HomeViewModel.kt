package com.boost.upgrades.ui.home

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.Cart
import com.boost.upgrades.data.model.UpdatesModel
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.Utils.readJSONFromAsset
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_fragment.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application) : BaseViewModel(application) {
    var updatesResult: MutableLiveData<List<UpdatesModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var cartResult: MutableLiveData<List<Cart>> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()
    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

    fun upgradeResult(): LiveData<List<UpdatesModel>> {
        return updatesResult
    }

    fun cartResult(): LiveData<List<Cart>> {
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
            val data: List<UpdatesModel>? = readJSONFromAsset(getApplication())
                        Completable.fromAction {
                            AppDatabase.getInstance(getApplication())!!.updatesDao().insertAllUPdates(data!!)
                        }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete {
                                updatesResult.postValue(data)
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
