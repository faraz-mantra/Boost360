package com.boost.dbcenterapi.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

object AddToCartUtils {

  val compositeDisposable = CompositeDisposable()


  fun addPackageToCart(cartItem: CartModel, application: Application) {
    Completable.fromAction {
      AppDatabase.getInstance(application)!!.cartDao().insertToCart(cartItem)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete {
      }
      .doOnError {
      }
      .subscribe()

  }

  fun addWidgetToCart(updatesModel: FeaturesModel, application: Application) {
    val discount = 100 - updatesModel.discount_percent
    val paymentPrice = (discount * updatesModel.price) / 100.0
    val cartItem = CartModel(
      updatesModel.feature_id,
      updatesModel.boost_widget_key,
      updatesModel.feature_code,
      updatesModel.name,
      updatesModel.description,
      updatesModel.primary_image,
      paymentPrice,
      updatesModel.price.toDouble(),
      updatesModel.discount_percent,
      1,
      1,
      "features",
      updatesModel.extended_properties
    )
    Completable.fromAction {
      AppDatabase.getInstance(application)!!.cartDao()
        .insertToCart(cartItem)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete {
      }
      .doOnError {

      }
      .subscribe()

  }

  fun openCart(session: UserSessionManager,
               isOpenCardFragment: Boolean,
               context: Context
  ){
    val intent = Intent(context, Class.forName("com.boost.cart.CartActivity"))
    intent.putExtra("fpid", session.fPID)
    intent.putExtra("expCode", session.fP_AppExperienceCode)
    intent.putExtra("fpName", session.fPName)
    intent.putExtra("isDeepLink", true)
//    intent.putExtra("deepLinkViewType", deepLinkViewType)
//    intent.putExtra("deepLinkDay", deepLinkDay)
    intent.putExtra("isOpenCardFragment", isOpenCardFragment)
    intent.putExtra(
      "accountType",
      session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)
    )
    intent.putStringArrayListExtra(
      "userPurchsedWidgets",
      session.getStoreWidgets() as ArrayList<String>
    )
    if (session.userProfileEmail != null) {
      intent.putExtra("email", session.userProfileEmail)
    } else {
      intent.putExtra("email", "ria@nowfloats.com")
    }
    if (session.userPrimaryMobile != null) {
      intent.putExtra("mobileNo",session.userPrimaryMobile)
    } else {
      intent.putExtra("mobileNo", "9160004303")
    }
    intent.putExtra("profileUrl", session.fPLogo)
    context.startActivity(intent)

  }

}