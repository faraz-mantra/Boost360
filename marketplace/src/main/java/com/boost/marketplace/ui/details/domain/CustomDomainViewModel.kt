package com.boost.marketplace.ui.details.domain

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.Domain.DomainBookingRequest
import com.boost.dbcenterapi.data.api_model.blockingAPI.BlockApi
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.DataLoader
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import es.dmoral.toasty.Toasty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CustomDomainViewModel() : BaseViewModel() {

    var updatesResult: MutableLiveData<CustomDomains> = MutableLiveData()
    var updateStatus: MutableLiveData<BlockApi> = MutableLiveData()
    var domainBookingStatus: MutableLiveData<Boolean> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<String> = MutableLiveData()
    var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)
    val compositeDisposable = CompositeDisposable()
    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

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

    fun updatesLoader(): LiveData<String> {
        return updatesLoader
    }

    fun updateResult(): LiveData<CustomDomains> {
        return updatesResult
    }

    fun updateStatus(): LiveData<BlockApi> {
        return updateStatus
    }

    fun domainBookingStatus(): LiveData<Boolean> {
        return domainBookingStatus
    }

    fun GetSuggestedDomains(domainRequest: DomainRequest) {
        updatesLoader.postValue("Loading.. Please wait")
        compositeDisposable.add(
            ApiService.getDomains(domainRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updatesResult.postValue(it)
                        updatesLoader.postValue("")
                    }, {
                        updatesLoader.postValue("")
                        updatesError.postValue(it.message)
                    })
        )
    }

    fun addItemToCart1(updatesModel: FeaturesModel, title: String) {
        updatesLoader.postValue("")
        val discount = 100 - updatesModel.discount_percent
        val paymentPrice = ((discount * updatesModel.price) / 100)
        val mrpPrice = updatesModel.price
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
            updatesModel.widget_type ?: "",
            title

        )

        Completable.fromAction {
            AppDatabase.getInstance(application)!!.cartDao()
                .insertToCart(cartItem)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                updatesLoader.postValue("")
                //add cartitem to firebase
                DataLoader.updateCartItemsToFirestore(application)
            }
            .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue("")
            }
            .subscribe()
    }

    fun domainStatus(auth: String, fpid: String, clientId: String, blockedItem: String) {
//        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getItemAvailability(auth, fpid, clientId, blockedItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updateStatus.postValue(it)
//                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue("")
                        updatesError.postValue(it.message)
                    })
        )
    }

    fun bookDomainActivation(blockedItem: String, app: Application, activity: Activity) {

        updatesLoader.postValue("Domain Activation Is in Progress")
        val pref = app.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        val splitStr: ArrayList<String> =
            blockedItem.split(".")?.toCollection(ArrayList())!!
        var domainName = ""
        var domainType = "."
        val prefs = SharedPrefs(activity)
        var validityInYears = "1"
        var months: Double = 0.0
        if (prefs.getYearPricing()) {
            //do the yearly calculation here
            months = (if (prefs.getCartValidityMonths() != null) prefs.getCartValidityMonths()!!.toDouble() else 1.toDouble() * 12).toDouble()
        } else {
            months = if (prefs.getCartValidityMonths() != null) prefs.getCartValidityMonths()!!.toDouble() else 1.toDouble()
        }
        val tempMonth: Double = months / 12.0
        if (tempMonth.toInt() < tempMonth) { //example 1 < 1.2
            validityInYears = (tempMonth.toInt() + 1).toString()
        } else {
            validityInYears = (tempMonth.toInt()).toString()
        }
        splitStr.forEachIndexed { index, element ->
            if (splitStr.size > 2) {
                if (index == splitStr.size - 1) {
                    domainType = domainType + element
                } else {
                    domainName = domainName + "." + element
                }
            } else {
                if (index == 0)
                    domainName = element
                else if (index == 1)
                    domainType = domainType + element
            }
        }
        val bookingRequest = DomainBookingRequest(
            0,
            0,
            Constants.clientid,
            1,
            domainName,
            domainType,
            fpTag!!,
            validityInYears
        )
        val auth = UserSessionManager(activity.applicationContext).getAccessTokenAuth()?.barrierToken() ?: ""
        compositeDisposable.add(
        ApiService.buyDomainBooking(auth, bookingRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.i("buyDomainBooking", "Success >>>" + it)
                    updatesLoader.postValue("")
                    Toasty.success(
                        activity.applicationContext,
                        "Successfully Booked Your Domain. It Takes 24 to 48 hours to activate",
                        Toast.LENGTH_LONG, true
                    ).show()
                    domainBookingStatus.postValue(true)
                }, {
                    Log.i("buyDomainBooking", "Failure >>>$it")
                    updatesLoader.postValue("")
                    Toasty.error(
                        activity.applicationContext,
                        "Payment is successsfull. But Failed to Booked Your Domain. Try after 24 hours in [My Current Plan]",
                        Toast.LENGTH_LONG, true
                    ).show()
                    domainBookingStatus.postValue(false)
                }
            )
        )
    }
}