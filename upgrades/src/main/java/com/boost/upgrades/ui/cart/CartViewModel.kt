package com.boost.upgrades.ui.cart

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.PurchaseOrder.request.CreatePurchaseOrderRequest
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.create.CustomerIDRequest
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luminaire.apolloar.base_class.BaseViewModel
import es.dmoral.toasty.Toasty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class CartViewModel(application: Application) : BaseViewModel(application){

    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var _initiatePurchaseOrder: MutableLiveData<CreatePurchaseOrderResponse> = MutableLiveData()
    private var customerId: String? = null
    private var APIRequestStatus: String? = null

    var _updateGSTIN: MutableLiveData<String> = MutableLiveData()
    var _updateTAN: MutableLiveData<String> = MutableLiveData()

    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

    val compositeDisposable = CompositeDisposable()


    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun getCustomerId(): String? {
        return customerId
    }

    fun getAPIRequestStatus(): String?{
        return APIRequestStatus
    }

    fun updateGSTIN(gstinValue: String){
        _updateGSTIN.postValue(gstinValue)
    }

    fun getGSTIN(): LiveData<String>{
        return _updateGSTIN
    }

    fun updateTAN(gstinValue: String){
        _updateTAN.postValue(gstinValue)
    }

    fun getTAN(): LiveData<String>{
        return _updateTAN
    }

    fun getLoaderStatus(): LiveData<Boolean>{
        return updatesLoader
    }

    fun getPurchaseOrderResponse(): LiveData<CreatePurchaseOrderResponse> {
        return _initiatePurchaseOrder
    }

    fun InitiatePurchaseOrder(createPurchaseOrderRequest: CreatePurchaseOrderRequest) {
        if (Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            APIRequestStatus = "Order registration in progress..."
            compositeDisposable.add(
                    ApiService.CreatePurchaseOrder(createPurchaseOrderRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        Log.i("InitiatePurchaseOrder>>", it.toString())
                                        _initiatePurchaseOrder.postValue(it)
                                        updatesLoader.postValue(false)
                                    },
                                    {
                                        Toasty.error(getApplication(), "Error occurred while registering your order - " + it.message, Toast.LENGTH_LONG).show()
                                        updatesError.postValue(it.message)
                                        updatesLoader.postValue(false)
                                    }
                            )
            )
        }
    }

    fun requestCustomerId(customerIDRequest: CustomerIDRequest) {
        if (Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            APIRequestStatus = "Retrieving your payment profile..."
            compositeDisposable.add(
                    ApiService.getCustomerId(customerIDRequest.InternalSourceId, customerIDRequest.ClientId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        Log.i("getCustomerId>>", it.Result.ExternalSourceId)
                                        customerId = it.Result.ExternalSourceId
                                        updatesLoader.postValue(false)
                                    },
                                    {
                                        val temp = (it as HttpException).response()!!.errorBody()!!.string()
                                        val errorBody: CreateCustomerIDResponse = Gson().fromJson(
                                                temp, object : TypeToken<CreateCustomerIDResponse>() {}.type
                                        )
                                        if (errorBody != null && errorBody.Error.ErrorCode.equals("INVALID CUSTOMER") && errorBody.StatusCode == 400) {
                                            APIRequestStatus = "Creating a new payment profile..."
                                            compositeDisposable.add(
                                                    ApiService.createCustomerId(customerIDRequest)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(
                                                                    {
                                                                        Log.i("CreateCustomerId>>", it.toString())
                                                                        customerId = it.Result.CustomerId
                                                                        updatesLoader.postValue(false)
                                                                    },
                                                                    {
                                                                        Toasty.error(getApplication(), "Failed to create new payment profile for your account - " + it.message, Toast.LENGTH_LONG).show()
                                                                        updatesError.postValue(it.message)
                                                                        updatesLoader.postValue(false)
                                                                    }
                                                            )
                                            )
                                        }
                                    }
                            )
            )
        }
    }

    fun getCartItems(){
        updatesLoader.postValue(true)
        APIRequestStatus = "Get Cart Details"
        compositeDisposable.add(
            AppDatabase.getInstance(getApplication())!!
                .cartDao()
                .getCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    cartResult.postValue(it)
                     updatesLoader.postValue(false)
                }
                .doOnError {
                    updatesError.postValue(it.message)
                     updatesLoader.postValue(false)
                }
                .subscribe()
        )
    }

    fun deleteCartItems(itemID: String){
         updatesLoader.postValue(true)
        APIRequestStatus = "Delete Cart items."
        Completable.fromAction {
            AppDatabase.getInstance(getApplication())!!.cartDao().deleteCartItem(itemID)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.add(
                    AppDatabase.getInstance(getApplication())!!.cartDao().getCartItems()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        cartResult.postValue(it)
                         updatesLoader.postValue(false)
                    }
                    .doOnError {
                        updatesError.postValue(it.message)
                         updatesLoader.postValue(false)
                    }
                    .subscribe()
                )
                 updatesLoader.postValue(false)
            }
            .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue(false)
            }
            .subscribe()
    }
}
