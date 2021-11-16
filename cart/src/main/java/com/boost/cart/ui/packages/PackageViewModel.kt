package com.boost.cart.ui.packages

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.cart.base_class.BaseViewModel
import com.framework.upgradeDB.local.AppDatabase
import com.framework.upgradeDB.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PackageViewModel(application: Application) : BaseViewModel(application) {
  var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
  var bundleKeysResult: MutableLiveData<List<String>> = MutableLiveData()

  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

  fun getUpgradeResult(): LiveData<List<FeaturesModel>> {
    return updatesResult
  }

  fun cartResult(): LiveData<List<CartModel>> {
    return cartResult
  }

  fun getBundleWidgetKeys(): LiveData<List<String>> {
    return bundleKeysResult
  }

  fun loadUpdates(list: List<String>) {
    Log.v("loadUpdates", " " + list);
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .featuresDao()
        .getallFeaturesInList(list)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            updatesResult.postValue(it)
            updatesLoader.postValue(false)
          },
          {
            it.printStackTrace()
            updatesError.postValue(it.message)
            updatesLoader.postValue(false)
          }
        )
    )
  }

  fun addItemToCart(cartItem: CartModel) {
    updatesLoader.postValue(true)
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
    updatesLoader.postValue(true)
    CompositeDisposable().add(
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

  fun getAssociatedWidgetKeys(bundleId: String) {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .bundlesDao()
        .getIncludedKeysInBundle(bundleId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess {
          var keys = Gson().fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
          bundleKeysResult.postValue(keys)
        }
        .doOnError {
          updatesError.postValue(it.message)
          updatesLoader.postValue(false)
        }
        .subscribe()
    )
  }
}
