package com.boost.marketplace.ui.details

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.Edgecase.EdgeCases
import com.boost.dbcenterapi.data.api_model.blockingAPI.BlockApi
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.data.api_model.gst.Error
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.DataLoader
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class FeatureDetailsViewModel : BaseViewModel() {

    var updatesResult: MutableLiveData<FeaturesModel> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var findingNumberLoader: MutableLiveData<Boolean> = MutableLiveData()
    var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()
    private var callTrackListResponse: MutableLiveData<CallTrackListResponse> = MutableLiveData()
    var updateStatus: MutableLiveData<BlockApi> = MutableLiveData()
    var edgecaseResult: MutableLiveData<EdgeCases> = MutableLiveData()
    var customDomainsResult: MutableLiveData<CustomDomains> = MutableLiveData()

    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner
    var ApiService = com.boost.dbcenterapi.utils.Utils.getRetrofit().create(NewApiInterface::class.java)

    val compositeDisposable = CompositeDisposable()

    fun addonsResult(): LiveData<FeaturesModel> {
        return updatesResult
    }

    fun bundleResult(): LiveData<List<BundlesModel>> {
        return allBundleResult
    }

    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun addonsError(): LiveData<String> {
        return updatesError
    }

    fun addonsLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun numberLoader(): LiveData<Boolean> {
        return findingNumberLoader
    }

    fun updateStatus(): LiveData<BlockApi> {
        return updateStatus
    }

    fun getCallTrackingDetails(): LiveData<CallTrackListResponse> {
        return callTrackListResponse
    }


    fun edgecaseResult(): LiveData<EdgeCases> {
        return edgecaseResult
    }

    fun setApplicationLifecycle(
        application: Application,
        lifecycleOwner: LifecycleOwner
    ) {
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }

    fun updateCustomDomainsResultResult(): LiveData<CustomDomains> {
        return customDomainsResult
    }

    fun GetSuggestedDomains(domainRequest: DomainRequest) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getDomains(domainRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        customDomainsResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }

    fun loadNumberList(fpid: String, clientId: String) {
        findingNumberLoader.postValue(true)
        if (Utils.isConnectedToInternet(application)) {

            CompositeDisposable().add(
                ApiService.getCallTrackDetails(fpid, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            findingNumberLoader.postValue(false)
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

    fun loadAddonsFromDB(boostKey: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getFeaturesItemByFeatureCode(boostKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    updatesResult.postValue(it)
                    updatesLoader.postValue(false)
                }
                .doOnError {
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                }
                .subscribe({}, { e -> Log.e("TAG", "Empty DB") }
                )
        )
    }

    fun getAllPackages() {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .bundlesDao()
                .getBundleItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    Log.e("getAssociatedPackages", it.toString())
                    allBundleResult.postValue(it)
                }
                .doOnError {
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                }
                .subscribe()
        )
    }


    fun addItemToCart1(updatesModel: FeaturesModel, activity: Activity, title: String?) {
        updatesLoader.postValue(false)
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
            updatesModel.widget_type ?: "",title
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

    fun getCartItems() {
        updatesLoader.postValue(false)
        compositeDisposable.add(
            AppDatabase.getInstance(application)!!
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

    fun addItemToCartPackage1(cartItem: CartModel) {
        updatesLoader.postValue(false)
        Completable.fromAction {
            AppDatabase.getInstance(application)!!.cartDao()
                .insertToCart(cartItem)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                updatesLoader.postValue(false)
            }
            .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue(false)
            }
            .subscribe()
    }

    fun blockNumberStatus(auth: String, fpid: String, clientId: String, blockedItem: String) {
        compositeDisposable.add(
            ApiService.getItemAvailability(auth, fpid, clientId, blockedItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updateStatus.postValue(it)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }

    fun edgecases(fpid: String,clientId:String,featureCode:String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getEdgeCases(fpid,clientId,featureCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        edgecaseResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }
}