package com.boost.upgrades.ui.checkoutkyc

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luminaire.apolloar.base_class.BaseViewModel
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class CheckoutKycViewModel(application: Application) : BaseViewModel(application) {
  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
  private var APIRequestStatus: String? = null

  var customerInfoState: MutableLiveData<Boolean> = MutableLiveData()

  var updateCustomerInfo: MutableLiveData<GetCustomerIDResponse> = MutableLiveData()
  private var customerInfo: MutableLiveData<CreateCustomerIDResponse> = MutableLiveData()

  var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

  fun getUpdatedCustomerResult(): LiveData<CreateCustomerIDResponse> {
    return customerInfo
  }

  fun getCustomerInfoStateResult(): LiveData<Boolean> {
    return customerInfoState
  }

  fun getCustomerInfoResult(): LiveData<GetCustomerIDResponse> {
    return updateCustomerInfo
  }

  fun getCustomerInfo(InternalSourceId: String, clientId: String) {
    if (Utils.isConnectedToInternet(getApplication())) {
      updatesLoader.postValue(true)
      APIRequestStatus = "Retrieving your payment profile..."
      CompositeDisposable().add(
        ApiService.getCustomerId(InternalSourceId, clientId)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("getCustomerId>>", it.toString())
              updateCustomerInfo.postValue(it)
              customerInfoState.postValue(true)
              updatesLoader.postValue(false)
            },
            {
              val temp = (it as HttpException).response()!!.errorBody()!!.string()
              val errorBody: CreateCustomerIDResponse = Gson().fromJson(
                temp, object : TypeToken<CreateCustomerIDResponse>() {}.type
              )
              if (errorBody != null && errorBody.Error.ErrorCode.equals("INVALID CUSTOMER") && errorBody.StatusCode == 400) {
                customerInfoState.postValue(false)
              }
              updatesLoader.postValue(false)
              updatesError.postValue(it.message())
            }
          )
      )
    }
  }

  fun createCustomerInfo(createCustomerInfoRequest: CreateCustomerInfoRequest) {
    APIRequestStatus = "Creating a new payment profile..."
    CompositeDisposable().add(
      ApiService.createCustomerId(createCustomerInfoRequest)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            Log.i("CreateCustomerId>>", it.toString())
            customerInfo.postValue(it)
            updatesLoader.postValue(false)
          },
          {
            Toasty.error(
              getApplication(),
              "Failed to create new payment profile for your account - " + it.message,
              Toast.LENGTH_LONG
            ).show()
            updatesError.postValue(it.message)
            updatesLoader.postValue(false)
          }
        )
    )
  }

  fun updateCustomerInfo(createCustomerInfoRequest: CreateCustomerInfoRequest) {
    APIRequestStatus = "Creating a new payment profile..."
    CompositeDisposable().add(
      ApiService.updateCustomerId(createCustomerInfoRequest)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            Log.i("CreateCustomerId>>", it.toString())
            customerInfo.postValue(it)
            updatesLoader.postValue(false)
          },
          {
            Toasty.error(
              getApplication(),
              "Failed to create new payment profile for your account - " + it.message,
              Toast.LENGTH_LONG
            ).show()
            updatesError.postValue(it.message)
            updatesLoader.postValue(false)
          }
        )
    )
  }
}
