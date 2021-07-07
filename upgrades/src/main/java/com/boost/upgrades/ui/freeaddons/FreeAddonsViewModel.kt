package com.boost.upgrades.ui.freeaddons

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.utils.Utils
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FreeAddonsViewModel(application: Application) : BaseViewModel(application) {

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

  fun loadUpdates(fpid: String, clientId: String) {
    updatesLoader.postValue(true)
    compositeDisposable.add(
      ApiService.GetFloatingPointWebWidgets(fpid, clientId)
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
