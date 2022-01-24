package com.boost.marketplace.ui.coupons

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponse
import com.boost.dbcenterapi.data.api_model.gst.Error
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class OfferCouponViewModel() : BaseViewModel() {

  lateinit var application: Application
  lateinit var lifecycleOwner: LifecycleOwner
  private var couponApiInfo: MutableLiveData<GetCouponResponse> = MutableLiveData()
  private var apiService = com.boost.dbcenterapi.utils.Utils.getRetrofit().create(NewApiInterface::class.java)


  fun setApplicationLifecycle(
    application: Application,
    lifecycleOwner: LifecycleOwner
  ) {
    this.application = application
    this.lifecycleOwner = lifecycleOwner
  }

  fun getCouponApiResult(): LiveData<GetCouponResponse> {
    return couponApiInfo
  }

  fun getCouponRedeem(couponRequest: CouponRequest) {
    if (Utils.isConnectedToInternet(application)) {
      CompositeDisposable().add(
        apiService.getOfferCoupons(couponRequest)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("getOfferCouponDetails", it.toString())
              var couponResponse = GetCouponResponse()
              couponApiInfo.postValue(couponResponse)
            },
            {
              val temp = (it as HttpException).response()!!.errorBody()!!.string()
              val errorBody: Error = Gson().fromJson(temp, object : TypeToken<Error>() {}.type)
              Toasty.error(application, "Error in Loading Coupons!!", Toast.LENGTH_LONG).show()
            }
          )
      )

    }
  }

}