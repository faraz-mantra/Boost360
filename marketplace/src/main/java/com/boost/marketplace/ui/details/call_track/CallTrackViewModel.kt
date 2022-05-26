package com.boost.marketplace.ui.details.call_track

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.cart.base_class.BaseViewModel
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.data.api_model.cart.RecommendedAddonsResponse
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.model.coupon.CouponServiceModel
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CallTrackViewModel(application: Application) : BaseViewModel(application) {

    private var callTrackListResponse: MutableLiveData<CallTrackListResponse> = MutableLiveData()
    fun getCallTrackingDetails(): LiveData<CallTrackListResponse> {
        return callTrackListResponse
    }


}