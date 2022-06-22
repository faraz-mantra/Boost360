package com.boost.marketplace.ui.details.domain

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.blockingAPI.BlockApi
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.DataLoader
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CustomDomainViewModel() : BaseViewModel() {

    var updatesResult: MutableLiveData<CustomDomains> = MutableLiveData()
    var updateStatus : MutableLiveData<BlockApi> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)
    val compositeDisposable = CompositeDisposable()
    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(application: Application,
                                lifecycleOwner: LifecycleOwner
    ){
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }

    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun updateResult(): LiveData<CustomDomains> {
        return updatesResult
    }

    fun updateStatus():LiveData<BlockApi> {
        return updateStatus
    }

    fun GetSuggestedDomains(domainRequest: DomainRequest) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getDomains(domainRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updatesResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }

    fun addItemToCart1(updatesModel: FeaturesModel, activity: Activity) {
        updatesLoader.postValue(false)
        val discount = 100 - updatesModel.discount_percent
        val paymentPrice = ((discount * updatesModel.price) / 100)
        val mrpPrice =  updatesModel.price
        val cartItem = CartModel(
            updatesModel.feature_id,
            updatesModel.boost_widget_key,
            updatesModel.feature_code,
            updatesModel.name,
            updatesModel.description,
            updatesModel.primary_image,
            paymentPrice,
            mrpPrice,
            updatesModel.discount_percent,
            1,
            1,
            "features",
            updatesModel.extended_properties,
            updatesModel.widget_type?:""
        )

        Completable.fromAction {
            AppDatabase.getInstance(application)!!.cartDao()
                .insertToCart(cartItem)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                updatesLoader.postValue(false)
                //add cartitem to firebase
                DataLoader.updateCartItemsToFirestore(application)
            }
            .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue(false)
            }
            .subscribe()
    }

    fun domainStatus(auth :String,fpid: String,clientId: String,blockedItem:String){
//        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getItemAvailability(auth,fpid,clientId,blockedItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updateStatus.postValue(it)
//                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }
}