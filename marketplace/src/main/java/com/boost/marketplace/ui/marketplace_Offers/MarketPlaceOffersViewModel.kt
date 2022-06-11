package com.boost.marketplace.ui.marketplace_Offers

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.MarketOfferModel
import com.framework.models.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MarketPlaceOffersViewModel:BaseViewModel() {

    var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var marketOffersCouponResult: MutableLiveData<MarketOfferModel> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    fun marketOffersCouponResult(): LiveData<MarketOfferModel> {
        return marketOffersCouponResult
    }

    fun getOffersByCouponId(couponId: String) {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
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
