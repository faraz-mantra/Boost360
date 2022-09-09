package com.boost.marketplace.ui.Compare_Plans

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.data.api_model.gst.Error
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.DataLoader
import com.boost.dbcenterapi.utils.Utils
import com.facebook.FacebookSdk.getApplicationContext
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class ComparePacksViewModel: BaseViewModel() {

    var featureResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var NewApiService = Utils.getRetrofit(true).create(NewApiInterface::class.java)
    var experienceCode: String = "SVC"
    var _fpTag: String = "ABC"
    var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()
    var allFeatureResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var addToCartResult: MutableLiveData<Boolean> = MutableLiveData()
    var customDomainsResult: MutableLiveData<CustomDomains> = MutableLiveData()
    var updatesResult: MutableLiveData<FeaturesModel> = MutableLiveData()
    val compositeDisposable = CompositeDisposable()
    private var callTrackListResponse: MutableLiveData<CallTrackListResponse> = MutableLiveData()

    fun getSpecificFeature(): LiveData<List<FeaturesModel>> {
        return featureResult
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
    fun bundleResult(): LiveData<List<BundlesModel>> {
        return allBundleResult
    }

    fun addonsResult(): LiveData<FeaturesModel> {
        return updatesResult
    }


    fun getFeatureValues(list: List<String>) {
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .featuresDao()
                .getSpecificFeature(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        featureResult.postValue(it)
                        updatesLoader.postValue(false)
                    },
                    {
                        it.printStackTrace()
                        updatesError.postValue(it.message)
                        updatesLoader.postValue(false)
                    }
                )
        )
    }

    fun loadAddonsFromDB(boostKey: String) {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
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

    fun getCartItems() {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
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
    fun setCurrentExperienceCode(code: String, fpTag: String) {
        experienceCode = code
    }

    fun getAllBundles(): LiveData<List<BundlesModel>> {
        return allBundleResult
    }

    fun updateCustomDomainsResultResult(): LiveData<CustomDomains> {
        return customDomainsResult
    }

    fun loadPackageUpdates() {
        updatesLoader.postValue(true)
        if (Utils.isConnectedToInternet(getApplicationContext())) {
            CompositeDisposable().add(
                NewApiService.GetAllFeatures()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.e("GetAllFeatures", it.toString())
                            val bundles = arrayListOf<BundlesModel>()
                            val tempBundles = Utils.getBundlesFromJsonFile(getApplicationContext())
                             for (item in it.Data[0].bundles) {
                           // for (item in tempBundles) {
                                if (item.exclusive_for_customers != null && item.exclusive_for_customers!!.size > 0) {
                                    var applicableToCurrentFPTag = false
                                    for (code in item.exclusive_for_customers!!) {
                                        if (code.equals(_fpTag, true)) {
                                            applicableToCurrentFPTag = true
                                            break
                                        }
                                    }
                                    if (!applicableToCurrentFPTag)
                                        continue
                                }
                                if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) {
                                    var applicableToCurrentExpCode = false
                                    for (code in item.exclusive_to_categories!!) {
                                        if (code.equals(experienceCode, true)) {
                                            applicableToCurrentExpCode = true
                                            break
                                        }
                                    }
                                    if (!applicableToCurrentExpCode)
                                        continue
                                }
                                bundles.add(
                                    BundlesModel(
                                        item._kid,
                                        item.name,
                                        if (item.min_purchase_months != null && item.min_purchase_months!! > 1) item.min_purchase_months!! else 1,
                                        item.overall_discount_percent,
                                        if (item.primary_image != null) item.primary_image!!.url else null,
                                        Gson().toJson(item.included_features),
                                        item.target_business_usecase,
                                        Gson().toJson(item.exclusive_to_categories),
                                        if (item.frequently_asked_questions != null && item.frequently_asked_questions!!.isNotEmpty()) Gson().toJson(
                                            item.frequently_asked_questions
                                        ) else null,
                                        if (item.how_to_activate != null && item.how_to_activate!!.isNotEmpty()) Gson().toJson(
                                            item.how_to_activate
                                        ) else null,
                                        if (item.testimonials != null && item.testimonials!!.isNotEmpty()) Gson().toJson(
                                            item.testimonials
                                        ) else null,
                                        if (item.benefits != null && item.benefits!!.isNotEmpty()) Gson().toJson(
                                            item.benefits
                                        ) else null,
                                        item.desc,
                                    )
                                )
                            }
                                    Log.i("insertAllBundles", "Successfully")
                                    allBundleResult.postValue(bundles)
                                    updatesLoader.postValue(false)
                        },
                        {
                            Log.e("GetAllFeatures", "error" + it.message)
                            updatesLoader.postValue(false)
                        }
                    )
            )
        }
    }


    fun getAllFeatures(): LiveData<List<FeaturesModel>> {
        return allFeatureResult
    }

    fun getAllFeaturesFromDB(){
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .featuresDao()
                .getAllFeatures()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        allFeatureResult.postValue(it)
                        updatesLoader.postValue(false)
                    },{
                        Log.e("GetAllFeatures", "error" + it.message)
                        updatesLoader.postValue(false)
                    }
                )
        )
    }

    fun addItemToCartPackage1(cartItem: CartModel) {
        updatesLoader.postValue(true)
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao()
                .insertToCart(cartItem)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                updatesLoader.postValue(false)
                //add cartitem to firebase
                addToCartResult.postValue(true)
                DataLoader.updateCartItemsToFirestore(Application())
            }
            .doOnError {
                updatesError.postValue(it.message)
                addToCartResult.postValue(false)
                updatesLoader.postValue(false)
            }
            .subscribe()
    }

    fun getSuggestedDomains(domainRequest: DomainRequest) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            NewApiService.getDomains(domainRequest)
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

    fun getCallTrackingDetails(): LiveData<CallTrackListResponse> {
        return callTrackListResponse
    }

    fun loadNumberList(fpid: String, clientId: String) {
//        findingNumberLoader.postValue(true)
        if (com.boost.cart.utils.Utils.isConnectedToInternet(getApplicationContext())) {
            CompositeDisposable().add(
                NewApiService.getCallTrackDetails(fpid, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
//                            findingNumberLoader.postValue(false)
                            var NumberList = it
                            callTrackListResponse.postValue(NumberList)
                        },
                        {
//                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
//                            val errorBody: Error =
//                                Gson().fromJson(temp, object : TypeToken<Error>() {}.type)
                            Toasty.error(
                                getApplicationContext(),
                                "Error in Loading Numbers!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )

            )

        }
    }
}