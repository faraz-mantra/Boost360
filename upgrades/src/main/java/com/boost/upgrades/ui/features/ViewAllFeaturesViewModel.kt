package com.boost.upgrades.ui.features

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.FeaturesModel
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ViewAllFeaturesViewModel(application: Application) : BaseViewModel(application) {

  var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

  val compositeDisposable = CompositeDisposable()

  fun addonsResult(): LiveData<List<FeaturesModel>> {
    return updatesResult
  }

  fun addonsError(): LiveData<String> {
    return updatesError
  }

  fun addonsLoader(): LiveData<Boolean> {
    return updatesLoader
  }

  fun loadAddonsByTypeFromDB(categoryType: String) {
    updatesLoader.postValue(true)
    compositeDisposable.add(
      AppDatabase.getInstance(getApplication())!!
        .featuresDao()
        .getFeaturesItemsByType(categoryType, "MERCHANT_TRAINING")
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

  fun loadAddonsFromDB() {
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

  fun disposeElements() {
    if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
  }
}
