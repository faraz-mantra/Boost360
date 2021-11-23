package com.boost.dbcenterapi.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.multidex.MultiDexApplication
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.framework.BaseApplication
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.Callable

object AddToCartUtils {

  fun addWidgetToCart(cartItem :CartModel,appDatabase: Application) {
    Completable.fromAction {
      AppDatabase.getInstance(appDatabase)?.cartDao()?.insertToCart(cartItem)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete {
      }
      .doOnError {
      }
      .subscribe()

  }

  fun addPackToCart() {

  }

}