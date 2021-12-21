package com.boost.payment.ui.confirmation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
//import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
//import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.toLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OrderConfirmationViewModel : ViewModel() {
  fun emptyCurrentCart(app: Application) {
    Completable.fromAction {
      AppDatabase.getInstance(app)!!.cartDao().emptyCart()
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnError {
        //in case of error
      }
      .doOnComplete {
        //if any action post empty_cart
      }
      .subscribe()
  }
}
