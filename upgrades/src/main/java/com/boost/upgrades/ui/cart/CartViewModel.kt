package com.boost.upgrades.ui.cart

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.PurchaseOrder.CreatePurchaseOrderRequest
import com.boost.upgrades.data.api_model.PurchaseOrder.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.create.CustomerIDRequest
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.model.Cart
import com.boost.upgrades.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class CartViewModel(application: Application) : BaseViewModel(application){

    var cartResult: MutableLiveData<List<Cart>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var _initiatePurchaseOrder: MutableLiveData<CreatePurchaseOrderResponse> = MutableLiveData()
    private var customerId: String? = null
    private var APIRequestStatus: String? = null

    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

    val compositeDisposable = CompositeDisposable()


    fun cartResult(): LiveData<List<Cart>> {
        return cartResult
    }

    fun getCustomerId(): String? {
        return customerId
    }

    fun getAPIRequestStatus(): String?{
        return APIRequestStatus
    }

    fun getLoaderStatus(): LiveData<Boolean>{
        return updatesLoader
    }

    fun getPurchaseOrderResponse(): LiveData<CreatePurchaseOrderResponse> {
        return _initiatePurchaseOrder
    }

    fun InitiatePurchaseOrder(createPurchaseOrderRequest: CreatePurchaseOrderRequest){
        updatesLoader.postValue(true)
        APIRequestStatus = "InitiatePurchaseOrder"
        compositeDisposable.add(
            ApiService.CreatePurchaseOrder(createPurchaseOrderRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.i("InitiatePurchaseOrder>>",it.toString())
                        _initiatePurchaseOrder.postValue(it)
//                        customerId = it.CustomerId
                        updatesLoader.postValue(false)
                    },
                    {
                        updatesError.postValue(it.message)
                         updatesLoader.postValue(false)
                    }
                )
        )
    }

    fun requestCustomerId(customerIDRequest: CustomerIDRequest){
        updatesLoader.postValue(true)
        APIRequestStatus = "Get Customer Info"
        compositeDisposable.add(
            ApiService.getCustomerId(customerIDRequest.InternalSourceId, customerIDRequest.ClientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.i("getCustomerId>>",it.Result.ExternalSourceId)
                        customerId = it.Result.ExternalSourceId
                         updatesLoader.postValue(false)
                    },
                    {
                        val errorBody: CreateCustomerIDResponse = Gson().fromJson(
                                (it as HttpException).response()!!.errorBody()!!.string(), object : TypeToken<CreateCustomerIDResponse>() {}.type
                        )
                        if(errorBody!=null && errorBody.StatusCode == 400) {
                            compositeDisposable.add(
                                    ApiService.createCustomerId(customerIDRequest)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    {
                                                        Log.i("CreateCustomerId>>",it.toString())
                                                        customerId = it.Result.CustomerId
                                                        updatesLoader.postValue(false)
                                                    },
                                                    {
                                                        Toast.makeText(getApplication(),it.message, Toast.LENGTH_SHORT).show()
                                                        updatesError.postValue(it.message)
                                                        updatesLoader.postValue(false)
                                                    }
                                            )
                            )
                        }
//                        updatesError.postValue(it.message)
//                        updatesLoader.postValue(false)
                    }
                )
        )
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
