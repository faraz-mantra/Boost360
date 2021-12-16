package com.boost.upgrades.ui.marketplace_offers

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MarketPlaceOfferViewModel(application: Application) : BaseViewModel(application) {
  var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
  var marketOffersCouponResult: MutableLiveData<MarketOfferModel> = MutableLiveData()

  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

  fun getUpgradeResult(): LiveData<List<FeaturesModel>> {
    return updatesResult
  }

  fun cartResult(): LiveData<List<CartModel>> {
    return cartResult
  }

  fun marketOffersCouponResult(): LiveData<MarketOfferModel> {
    return marketOffersCouponResult
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

  fun getOffersByCouponId(couponId: String) {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .marketOffersDao()
        .getMarketOffersByCouponCode(couponId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess {
          marketOffersCouponResult.postValue(it)
        }
        .doOnError {
          updatesError.postValue(it.message)
          updatesLoader.postValue(false)
        }
        .subscribe()
    )
  }
}
