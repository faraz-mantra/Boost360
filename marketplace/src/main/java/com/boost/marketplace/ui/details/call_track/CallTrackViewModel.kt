package com.boost.marketplace.ui.details.call_track

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.gst.Error
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class CallTrackViewModel : BaseViewModel() {

    private var callTrackListResponse: MutableLiveData<CallTrackListResponse> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    val compositeDisposable = CompositeDisposable()


    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner
    var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)

    fun setApplicationLifecycle(
        application: Application,
        lifecycleOwner: LifecycleOwner
    ) {
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }


    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun getCallTrackingDetails(): LiveData<CallTrackListResponse> {
        return callTrackListResponse
    }
    fun loadNumberList(fpid: String, clientId: String) {
        if (com.boost.cart.utils.Utils.isConnectedToInternet(application)) {
            System.out.println("fpid--->" + fpid)
            System.out.println("clientId--->" + clientId)
            CompositeDisposable().add(
                ApiService.getCallTrackDetails(fpid, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("getNumbersforVMN", it.toString())
                            var NumberList = it
                            callTrackListResponse.postValue(NumberList)
                        },
                        {
                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
                            val errorBody: Error =
                                Gson().fromJson(temp, object : TypeToken<Error>() {}.type)
                            Toasty.error(
                                application,
                                "Error in Loading Numbers!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
            )

        }
    }


}