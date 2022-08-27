package com.boost.payment.ui.confirmation

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.boost.dbcenterapi.data.api_model.Domain.DomainBookingRequest
import com.boost.dbcenterapi.data.remote.ApiInterface
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.utils.Constants.Companion.clientid
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.Utils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OrderConfirmationViewModel : ViewModel() {
  var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)

  fun emptyCurrentCart(app: Application, activity: Activity) {
    CompositeDisposable().add(
      AppDatabase.getInstance(app)!!
        .cartDao()
        .getCartItems()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess {
          for(singleItem in it){
            if(singleItem.feature_code!!.equals("DOMAINPURCHASE") && !singleItem.addon_title.isNullOrEmpty()){
              val pref = app.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
              val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
              val splitStr: ArrayList<String> = singleItem.addon_title!!.split(".")?.toCollection(ArrayList())!!
              var domainName = ""
              var domainType = "."
              val prefs = SharedPrefs(activity)
              val validityInYears = "1"
              if (prefs.getYearPricing()){
                //do the yearly calculation here
              }
              splitStr.forEachIndexed { index, element ->
                if(splitStr.size > 2){
                  if(index == splitStr.size-1 ){
                    domainType = domainType + element
                  }else{
                    domainName = domainName +"."+ element
                  }
                }else{
                  if(index==0)
                  domainName = element
                  else if(index==1)
                    domainType = domainType + element
                }
              }
              val bookingRequest = DomainBookingRequest(
                0,
                0,
                clientid,
                1,
                domainName,
                domainType,
                fpTag!!,
                validityInYears
              )
              ApiService.buyDomainBooking(bookingRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                  {
                    Log.i("buyDomainBooking","Success >>>"+it)
                  },{
                    Log.i("buyDomainBooking", "Failure >>>$it")
                  }
                )
            }
          }
          Completable.fromAction {
            AppDatabase.getInstance(app)!!.cartDao().emptyCart()
          }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
              //if any action post empty_cart
            }
            .doOnError {
              //in case of error
            }
            .subscribe()
        }
        .doOnError {
          Log.e("getCartItems","Failed to get item -> "+it.message)
        }
        .subscribe()
    )
  }

//  fun getFpDetails(fpId: String, map: Map<String, String>): LiveData<BaseResponse> {
//    return WithFloatTwoRepository.getFpDetails(fpId, map).toLiveData()
//  }
}
