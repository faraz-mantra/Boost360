package com.boost.cart.ui.myaddons

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.cart.base_class.BaseViewModel
import com.framework.upgradeDB.local.AppDatabase
import com.framework.upgradeDB.model.*
import com.boost.cart.data.remote.ApiInterface
import com.boost.cart.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MyAddonsViewModel(application: Application) : BaseViewModel(application) {

  var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

  var activePremiumWidgetList: MutableLiveData<List<FeaturesModel>> = MutableLiveData()

  val compositeDisposable = CompositeDisposable()

  var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

  fun getActiveFreeWidgets(): LiveData<List<FeaturesModel>> {
    return updatesResult
  }


  fun updatesError(): LiveData<String> {
    return updatesError
  }

  fun updatesLoader(): LiveData<Boolean> {
    return updatesLoader
  }

  fun getActivePremiumWidgets(): LiveData<List<FeaturesModel>> {
    return activePremiumWidgetList
  }

  fun loadUpdates(auth:String,fpid: String, clientId: String) {
    updatesLoader.postValue(true)
    compositeDisposable.add(
      ApiService.GetFloatingPointWebWidgets(auth,fpid, clientId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
//                                                        activeWidgetList.postValue(it.Result)
            Log.i("FloatingPointWebWidgets", ">> " + it.Result)
            compositeDisposable.add(
              AppDatabase.getInstance(getApplication())!!
                .featuresDao()
                .getallActiveFeatures(it.Result, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                  activePremiumWidgetList.postValue(it)
                  updatesLoader.postValue(false)
                }
                .doOnError {
                  updatesError.postValue(it.message)
                  updatesLoader.postValue(false)
                }
                .subscribe()

            )
            compositeDisposable.add(
              AppDatabase.getInstance(getApplication())!!
                .featuresDao()
                .getallActiveFeatures(it.Result, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                  updatesResult.postValue(it)
                }
                .doOnError {
                  updatesError.postValue(it.message)
                  updatesLoader.postValue(false)
                }
                .subscribe()

            )
          },
          {
            updatesError.postValue(it.message)
            updatesLoader.postValue(false)
          }
        )
    )
  }

  fun disposeElements() {
    if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
  }
}
