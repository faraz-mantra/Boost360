package com.boost.cart.ui.historydetails

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.cart.base_class.BaseViewModel
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HistoryDetailsViewModel(application: Application) : BaseViewModel(application) {

  val updateResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()

  val compositeDisposable = CompositeDisposable()

  fun getResult(): LiveData<List<FeaturesModel>> {
    return updateResult
  }

  fun getDetailsOfWidgets(list: List<String>) {
    compositeDisposable.add(
      AppDatabase.getInstance(getApplication())!!
        .featuresDao()
        .getallFeaturesInList(list)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess {
          updateResult.postValue(it)
        }
        .doOnError {

        }
        .subscribe())
  }
}
