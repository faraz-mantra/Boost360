package com.boost.upgrades.ui.confirmation

import android.app.Application
import androidx.lifecycle.ViewModel
import com.framework.upgradeDB.local.AppDatabase
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
