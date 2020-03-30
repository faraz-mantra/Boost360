package com.boost.upgrades.ui.myaddons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.WidgetModel
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MyAddonsViewModel(application: Application) : BaseViewModel(application) {

    var updatesResult: MutableLiveData<List<WidgetModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()

    fun upgradeResult(): LiveData<List<WidgetModel>> {
        return updatesResult
    }


    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun loadUpdates() {
        updatesLoader.postValue(true)
            compositeDisposable.add(
                AppDatabase.getInstance(getApplication())!!
                    .widgetDao()
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

    fun disposeElements() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
    }
}
