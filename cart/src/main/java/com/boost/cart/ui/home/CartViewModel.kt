package com.boost.cart.ui.home

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.boost.cart.base_class.BaseViewModel
import com.boost.cart.utils.SingleLiveEvent
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.CustomDomain.CustomDomains
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.Domain.AlreadyPurchasedDomainResponse.PurchasedDomainResponse
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.ExpertConnect
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2.CreatePurchaseOrderV2
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.SomethingWentWrong
import com.boost.dbcenterapi.data.api_model.blockingAPI.BlockApi
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.data.api_model.cart.RecommendedAddonsRequest
import com.boost.dbcenterapi.data.api_model.cart.RecommendedAddonsResponse
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.dbcenterapi.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponse
import com.boost.dbcenterapi.data.api_model.gst.Error
import com.boost.dbcenterapi.data.api_model.gst.GSTApiResponse
import com.boost.dbcenterapi.data.api_model.paymentprofile.GetLastPaymentDetails
import com.boost.dbcenterapi.data.api_model.stateCode.GetStates
import com.boost.dbcenterapi.data.api_model.vmn.PurchasedVmnResponse
import com.boost.dbcenterapi.data.model.coupon.CouponServiceModel
import com.boost.dbcenterapi.data.remote.ApiInterface
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.data.renewalcart.*
import com.boost.dbcenterapi.data.rest.repository.MarketplaceNewRepository
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.framework.BaseApplication
import com.framework.R
import com.framework.analytics.SentryController
import com.framework.errorHandling.ErrorFlowInvokeObject
import com.framework.errorHandling.ErrorOccurredBottomSheet
import com.framework.extensions.observeOnce
import com.framework.models.toLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CartViewModel(application: Application) : BaseViewModel(application) {

    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var allFeatures: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var customDomainsResult: MutableLiveData<CustomDomains> = MutableLiveData()
    private var couponApiInfo: MutableLiveData<GetCouponResponse> = MutableLiveData()
    private var recommendedAddonsInfo: MutableLiveData<RecommendedAddonsResponse> =
        MutableLiveData()
    var updatesResult: MutableLiveData<CustomDomains> = MutableLiveData()
    var renewalPurchaseList: MutableLiveData<List<RenewalResult>> = MutableLiveData()
    var allBundles: MutableLiveData<List<BundlesModel>> = MutableLiveData()
    var updatesError: MutableLiveData<SomethingWentWrong> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var _initiatePurchaseOrder: MutableLiveData<CreatePurchaseOrderResponse> = MutableLiveData()
    var _initiatePurchaseAutoRenewOrder: MutableLiveData<CreatePurchaseOrderResponse> =
        MutableLiveData()
    val _initiateAutoRenewOrder: SingleLiveEvent<CreatePurchaseOrderResponse> = SingleLiveEvent()
    private var customerId: MutableLiveData<String> = MutableLiveData()
    private var APIRequestStatus: String? = null

    var _updateGSTIN: MutableLiveData<String> = MutableLiveData()
    var _updateTAN: MutableLiveData<String> = MutableLiveData()

    var allCoupons: MutableLiveData<List<CouponsModel>> = MutableLiveData()
    var validCouponCode: MutableLiveData<CouponsModel> = MutableLiveData()
    var createCartResult: MutableLiveData<CreateCartResult> = MutableLiveData()
    var cartResultItems: MutableLiveData<List<CartModel>> = MutableLiveData()

    var _updateCardRenew: MutableLiveData<String> = MutableLiveData()
    var _updateRenewPopup: MutableLiveData<String> = MutableLiveData()

    var _updateProceedClick: MutableLiveData<Boolean> = MutableLiveData()

    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)
    var NewApiService = Utils.getRetrofit(true).create(NewApiInterface::class.java)
    var NewApiService1 = Utils.getRetrofit(true).create(NewApiInterface::class.java)
    var NewApiService2 = Utils.getRetrofit(false).create(NewApiInterface::class.java)
    private var callTrackListResponse: MutableLiveData<CallTrackListResponse> = MutableLiveData()
    var updateDomainStatus: MutableLiveData<BlockApi> = MutableLiveData()
    var updateVmnStatus: MutableLiveData<BlockApi> = MutableLiveData()
    var purchasedDomainResult: MutableLiveData<PurchasedDomainResponse> = MutableLiveData()
    var purchasedVmnResult: MutableLiveData<PurchasedVmnResponse> = MutableLiveData()
    val compositeDisposable = CompositeDisposable()

    var customerInfoState: MutableLiveData<Boolean> = MutableLiveData()
    var cityResult: MutableLiveData<List<String>> = MutableLiveData()
    var stateResult: MutableLiveData<List<String>> = MutableLiveData()
    var cityNames = ArrayList<String>()
    var stateNames = ArrayList<String>()

    //    var redeemCouponResult: MutableLiveData<RedeemCouponResponse> = MutableLiveData()
    var redeemCouponResult: MutableLiveData<CouponServiceModel> = MutableLiveData()

    var updateCustomerInfo: MutableLiveData<GetCustomerIDResponse> = MutableLiveData()
    private var customerInfo: MutableLiveData<CreateCustomerIDResponse> = MutableLiveData()

    var _updateCheckoutClose: MutableLiveData<Boolean> = MutableLiveData()
    private var lastPaymentDetailsInfo: MutableLiveData<GetLastPaymentDetails> = MutableLiveData()

    private var statesInfo: MutableLiveData<GetStates> = MutableLiveData()

    var selectedStateResult: MutableLiveData<String> = MutableLiveData()
    var selectedStateTinResult: MutableLiveData<String> = MutableLiveData()

    var expertConnectDetails: MutableLiveData<ExpertConnect> = MutableLiveData()

    var stateValue: String? = null
    var cityValueResult: MutableLiveData<String> = MutableLiveData()

    private var updateInfo: MutableLiveData<CreateCustomerIDResponse> = MutableLiveData()

    private var gstApiInfo: MutableLiveData<GSTApiResponse> = MutableLiveData()
    var addToCartResult: MutableLiveData<Boolean> = MutableLiveData()
    var validityMonths: MutableLiveData<Int> = MutableLiveData()


    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(

        lifecycleOwner: LifecycleOwner
    ) {

        this.lifecycleOwner = lifecycleOwner
    }

    fun updatesError(): LiveData<SomethingWentWrong> {
        return updatesError
    }

    fun getExpertConnectDetails(): LiveData<ExpertConnect> {
        return expertConnectDetails
    }

    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun cartResultItems(): LiveData<List<CartModel>> {
        return cartResultItems
    }

    fun renewalResult(): LiveData<List<RenewalResult>> {
        return renewalPurchaseList
    }

    fun updateAllFeaturesResult(): LiveData<List<FeaturesModel>> {
        return allFeatures
    }

    fun updateAllBundlesResult(): LiveData<List<BundlesModel>> {
        return allBundles
    }

    fun updateAllCouponsResult(): LiveData<List<CouponsModel>> {
        return allCoupons
    }

    fun updateValidCouponResult(): LiveData<CouponsModel> {
        return validCouponCode
    }

    fun createCartRenewalResult(): LiveData<CreateCartResult> {
        return createCartResult
    }


    fun clearValidCouponResult() {
        validCouponCode.postValue(null)
    }

    fun getCustomerId(): LiveData<String> {
        return customerId
    }

    fun getAPIRequestStatus(): String? {
        return APIRequestStatus
    }

    fun updateGSTIN(gstinValue: String) {
        _updateGSTIN.postValue(gstinValue)
    }

    fun getGSTIN(): LiveData<String> {
        return _updateGSTIN
    }

    fun updateProceedClick(renewValue: Boolean) {
        _updateProceedClick.postValue(renewValue)
    }

    fun getProceedClick(): LiveData<Boolean> {
        return _updateProceedClick
    }

    fun updateRenewValue(renewValue: String) {
        _updateCardRenew.postValue(renewValue)
    }

    fun updateRenewPopupClick(renewPopValue: String) {
        _updateRenewPopup.postValue(renewPopValue)
    }

    fun getRenewPopupClick(): LiveData<String> {
        return _updateRenewPopup
    }

    fun getRenewValue(): LiveData<String> {
        return _updateCardRenew
    }

    fun updateTAN(gstinValue: String) {
        _updateTAN.postValue(gstinValue)
    }

    fun getTAN(): LiveData<String> {
        return _updateTAN
    }

    fun getLoaderStatus(): LiveData<Boolean> {
        return updatesLoader
    }

    fun getPurchaseOrderResponse(): LiveData<CreatePurchaseOrderResponse> {
        return _initiatePurchaseOrder
    }

    fun getPurchaseOrderAutoRenewResponse(): LiveData<CreatePurchaseOrderResponse> {
        return _initiateAutoRenewOrder
    }

    fun getCheckoutKycClose(): LiveData<Boolean> {
        return _updateCheckoutClose
    }

    fun updateCheckoutKycClose(checkoutCloseValue: Boolean) {
        _updateCheckoutClose.postValue(checkoutCloseValue)
    }

    fun cityResult(): LiveData<List<String>> {
        return cityResult
    }

    fun redeemCouponResult(): LiveData<CouponServiceModel> {
        return redeemCouponResult
    }

    fun getLastPayDetails(): LiveData<GetLastPaymentDetails> {
        return lastPaymentDetailsInfo
    }

    fun selectedStateResult(state: String) {
        selectedStateResult.postValue(state)
    }

    fun getSelectedStateResult(): LiveData<String> {
        return selectedStateResult
    }

    fun selectedStateTinResult(stateTin: String) {
        selectedStateTinResult.postValue(stateTin)
    }

    fun getSelectedStateTinResult(): LiveData<String> {
        return selectedStateTinResult
    }

    fun getStatesResult(): LiveData<GetStates> {
        return statesInfo
    }

    fun getUpdatedCustomerBusinessResult(): LiveData<CreateCustomerIDResponse> {
        return customerInfo
    }

    fun getUpdatedResult(): LiveData<CreateCustomerIDResponse> {
        return updateInfo
    }

    fun stateResult(): LiveData<List<String>> {
        return stateResult
    }

    fun getGstApiResult(): LiveData<GSTApiResponse> {
        return gstApiInfo
    }

    fun updateCustomDomainsResultResult(): LiveData<CustomDomains> {
        return customDomainsResult
    }

    fun getCallTrackingDetails(): LiveData<CallTrackListResponse> {
        return callTrackListResponse
    }

    fun updateStatus(): LiveData<BlockApi> {
        return updateDomainStatus
    }

    fun updateVmnStatus(): LiveData<BlockApi> {
        return updateVmnStatus
    }

    fun updateStatus1(): LiveData<BlockApi> {
        return updateDomainStatus
    }

    fun updateStatus2(): LiveData<BlockApi> {
        return updateDomainStatus
    }

    fun PurchasedDomainResponse(): LiveData<PurchasedDomainResponse> {
        return purchasedDomainResult
    }

    fun purchasedVmnResult(): LiveData<PurchasedVmnResponse> {
        return purchasedVmnResult
    }


    fun writeStringAsFile(fileContents: String?, fileName: String?) {
        val context: Context = getApplication()
        try {
            val out = FileWriter(File(context.filesDir, fileName))
            out.write(fileContents)
            out.close()
        } catch (e: IOException) {
            println("exception  $e")
            SentryController.captureException(e)
        }
    }

    fun InitiatePurchaseOrder(auth: String, createPurchaseOrderV2: CreatePurchaseOrderV2) {
        Log.d("InitiatePurchaseOld", " " + createPurchaseOrderV2)
        if (Utils.isConnectedToInternet(getApplication())) {
//            var sample = Gson().toJson(createPurchaseOrderV2)
//            writeStringAsFile(sample, "initiatePurchase.txt")
            updatesLoader.postValue(true)
            APIRequestStatus = "Order registration in progress..."
            compositeDisposable.add(
                ApiService.CreatePurchaseOrder(auth, createPurchaseOrderV2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("InitiatePurchaseOrder>>", it.toString())
                            _initiatePurchaseOrder.postValue(it)
                            updatesLoader.postValue(false)
                        },
                        {
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = it.message() ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                            updatesLoader.postValue(false)
                        }
                    )
            )
        }
    }

    fun InitiatePurchaseAutoRenewOrder(auth: String, createPurchaseOrderV2: CreatePurchaseOrderV2) {
        Log.d("InitiatePurchaseAuto", " " + createPurchaseOrderV2)
        if (Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            APIRequestStatus = "Order registration in progress..."
            compositeDisposable.add(
                ApiService.CreatePurchaseAutoRenewOrder(auth, createPurchaseOrderV2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("InitiatePurchaseOrder>>", it.toString())
                            _initiateAutoRenewOrder.postValue(it)
                            updatesLoader.postValue(false)
                        },
                        {
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = it.message() ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                            updatesLoader.postValue(false)
                        }
                    )
            )
        }
    }

    fun allPurchasedWidgets(auth: String, req: RenewalPurchasedRequest) {
        if (Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            APIRequestStatus = "Please wait..."
            compositeDisposable.add(
                ApiService.allPurchasedWidgets(
                    auth,
                    req.floatingPointId,
                    req.clientId,
                    req.widgetStatus,
                    req.widgetKey,
                    req.nextWidgetStatus,
                    req.dateFilter,
                    req.startDate,
                    req.endDate
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.i("renewal purchased >>", it.toString())
                        val data = it as RenewalPurchasedResponse
                        renewalPurchaseList.postValue(data.result ?: ArrayList())
//                                updatesLoader.postValue(false)
                    }, {
                        updatesError.postValue(
                            SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = it.message() ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                        renewalPurchaseList.postValue(ArrayList())
                        updatesLoader.postValue(false)
                    })
            )
        } else {
            updatesLoader.postValue(false)
            updatesError.postValue(
                SomethingWentWrong(
                    errorCode = 0,
                    errorMessage = "No Internet Connection.",
                    correlationId = ""
                ))
        }
    }

    fun createCartStateRenewal(auth: String, request: CreateCartStateRequest) {
        if (Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            APIRequestStatus = "Order registration in progress..."
            compositeDisposable.add(
                ApiService.createCartStateRenewal(auth, request).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe({
                        if (it?.result != null && it.result?.cartStateId.isNullOrEmpty().not()) {
                            createCartResult.postValue(it.result)
                        } else {
                            updatesError.postValue(
                                SomethingWentWrong(
                                    errorCode = 0,
                                    errorMessage = it.error?.errorList?.iNVALIDPARAMETERS
                                        ?: "Error creating cart state",
                                    correlationId = ""
                                ))
                        }
                        updatesLoader.postValue(false)
                    }, {
                        updatesError.postValue(
                            SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = it?.localizedMessage
                                    ?: "Error creating cart state",
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                        updatesLoader.postValue(false)
                    })
            )
        } else {
            updatesLoader.postValue(false)
            updatesError.postValue(
                SomethingWentWrong(
                    errorCode = 0,
                    errorMessage = "No Internet Connection.",
                    correlationId = ""
                ))
        }
    }

//  fun requestCustomerId(customerInfoRequest: CreateCustomerInfoRequest) {
//    if (Utils.isConnectedToInternet(getApplication())) {
//      updatesLoader.postValue(true)
//      APIRequestStatus = "Retrieving your payment profile..."
//      compositeDisposable.add(
//        ApiService.getCustomerId(
//          customerInfoRequest.InternalSourceId!!,
//          customerInfoRequest.ClientId!!
//        )
//          .subscribeOn(Schedulers.io())
//          .observeOn(AndroidSchedulers.mainThread())
//          .subscribe(
//            {
//              Log.i("getCustomerId>>", it.toString())
//              customerId.postValue(it.Result.ExternalSourceId)
//              updatesLoader.postValue(false)
//            },
//            {
//              val temp = (it as HttpException).response()!!.errorBody()!!.string()
//              val errorBody: CreateCustomerIDResponse = Gson().fromJson(
//                temp, object : TypeToken<CreateCustomerIDResponse>() {}.type
//              )
//              if (errorBody != null && errorBody.Error.ErrorCode.equals("INVALID CUSTOMER") && errorBody.StatusCode == 400) {
//                APIRequestStatus = "Creating a new payment profile..."
//                compositeDisposable.add(
//                  ApiService.createCustomerId(customerInfoRequest)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                      {
//                        Log.i("CreateCustomerId>>", it.toString())
//                        customerId.postValue(it.Result.CustomerId)
//                        updatesLoader.postValue(false)
//                      },
//                      {
//                        Toasty.error(
//                          getApplication(),
//                          "Failed to create new payment profile for your account - " + it.message,
//                          Toast.LENGTH_LONG
//                        ).show()
//                        updatesError.postValue(it.message)
//                        updatesLoader.postValue(false)
//                      }
//                    )
//                )
//              }
//            }
//          )
//      )
//    }
//  }

    fun addItemToCart(cartItem: CartModel) {
        Completable.fromAction {
            AppDatabase.getInstance(getApplication())?.cartDao()?.insertToCart(cartItem)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Log.d("TAG", "Success")
            }
            .doOnError {
                Log.e("TAG", "Error", it)
            }.subscribe()
    }

    fun getCartsByIds(itemIds: List<String>) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            AppDatabase.getInstance(getApplication())!!.cartDao().getCartsByIds(itemIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    cartResultItems.postValue(it)
                    updatesLoader.postValue(false)
                }.doOnError {
                    updatesLoader.postValue(false)
                }.subscribe()
        )
    }

    fun updateItemToCart(cartItem: CartModel) {
        Completable.fromAction {
            AppDatabase.getInstance(getApplication())?.cartDao()?.updateCart(cartItem)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Log.d("TAG", "Success")
            }
            .doOnError {
                Log.e("TAG", "Error", it)
            }.subscribe()
    }

    fun getCartItems() {
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
//                            updatesLoader.postValue(false)
                }
                .doOnError {
                    updatesLoader.postValue(false)
                }
                .subscribe()
        )
    }

    fun getAllFeatures() {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(getApplication())!!
                .featuresDao()
                .getAllFeatures()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    allFeatures.postValue(it)
//                            updatesLoader.postValue(false)
                }, {
                    updatesLoader.postValue(false)
                })
        )
    }

    fun getAllBundles() {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(getApplication())!!
                .bundlesDao()
                .getBundleItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    allBundles.postValue(it)
//                            updatesLoader.postValue(false)
                }, {
                    updatesLoader.postValue(false)
                })
        )
    }

    fun deleteCartItems(itemID: String) {
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
                            updatesError.postValue(
                                SomethingWentWrong(
                                    errorCode = 0,
                                    errorMessage = it.message ?: BaseApplication.instance.getString(
                                        R.string.something_went_wrong_please_tell_what_happened),
                                    correlationId = ""
                                ))
                            updatesLoader.postValue(false)
                        }
                        .subscribe()
                )
                updatesLoader.postValue(false)
            }
            .doOnError {
                updatesError.postValue(
                    SomethingWentWrong(
                        errorCode = 0,
                        errorMessage = it.message ?: BaseApplication.instance.getString(
                            R.string.something_went_wrong_please_tell_what_happened),
                        correlationId = ""
                    ))
                updatesLoader.postValue(false)
            }
            .subscribe()
    }

    fun getAllCoupon() {
        CompositeDisposable().add(
            AppDatabase.getInstance(getApplication())!!
                .couponsDao()
                .getCouponItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    allCoupons.postValue(it)
                    updatesLoader.postValue(false)
                }, {
                    updatesError.postValue(
                        SomethingWentWrong(
                            errorCode = 0,
                            errorMessage = it.message ?: BaseApplication.instance.getString(
                                R.string.something_went_wrong_please_tell_what_happened),
                            correlationId = ""
                        ))
                    updatesLoader.postValue(false)
                })
        )
    }

    fun addCouponCodeToCart(coupon: CouponsModel) {
        validCouponCode.postValue(coupon)
    }

    fun loaderShow() {
        updatesLoader.postValue(true)
    }

    fun getUpdatedCustomerResult(): LiveData<CreateCustomerIDResponse> {
        return customerInfo
    }

    fun getCustomerInfoStateResult(): LiveData<Boolean> {
        return customerInfoState
    }

    fun getCustomerInfoResult(): LiveData<GetCustomerIDResponse> {
        return updateCustomerInfo
    }

    fun getCustomerInfo(auth: String, InternalSourceId: String, clientId: String) {
        if (Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            APIRequestStatus = "Retrieving your payment profile..."
            CompositeDisposable().add(
                ApiService.getCustomerId(auth, InternalSourceId, clientId)
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
                            Log.e("getCustomerInfo", " >> " + it.message)
                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
                            val errorBody: CreateCustomerIDResponse = Gson().fromJson(
                                temp, object : TypeToken<CreateCustomerIDResponse>() {}.type
                            )
                            if (errorBody != null && errorBody.Error.ErrorCode.equals("INVALID CUSTOMER") && errorBody.StatusCode == 400) {
                                customerInfoState.postValue(false)
                            }
                            updatesLoader.postValue(false)
                        }
                    )
            )
        }
    }

    fun createCustomerInfo(auth: String, createCustomerInfoRequest: CreateCustomerInfoRequest) {
        APIRequestStatus = "Creating a new payment profile..."
        CompositeDisposable().add(
            ApiService.createCustomerId(auth, createCustomerInfoRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.i("CreateCustomerId>>", it.toString())
                        customerInfo.postValue(it)
                        updatesLoader.postValue(false)
                    },
                    {
                        Log.e("createCustomerInfo", " >> " + it.message)
//                                    Toasty.error(
//                                            getApplication(),
//                                            "Failed to create new payment profile for your account - " + it.message,
//                                            Toast.LENGTH_LONG
//                                    ).show()
//                                    if(!it.message.isNullOrEmpty())
//                                        updatesError.postValue(it.message)
                        updatesLoader.postValue(false)
                    }
                )
        )
    }

    fun updateCustomerInfo(auth: String, createCustomerInfoRequest: CreateCustomerInfoRequest) {
        APIRequestStatus = "Creating a new payment profile..."
        CompositeDisposable().add(
            ApiService.updateCustomerId(auth, createCustomerInfoRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.i("CreateCustomerId>>", it.toString())
                        customerInfo.postValue(it)
                        updatesLoader.postValue(false)
                    },
                    {
                        Log.e("updateCustomerInfo", " >> " + it.message)
//                                    Toasty.error(
//                                            getApplication(),
//                                            "Failed to create new payment profile for your account - " + it.message,
//                                            Toast.LENGTH_LONG
//                                    ).show()
//                                    if(!it.message.isNullOrEmpty())
//                                        updatesError.postValue(it.message)
                        updatesLoader.postValue(false)
                    }
                )
        )
    }

    fun getCitiesFromAssetJson(context: Context) {
        val data: String? = Utils.getCityFromAssetJsonData(context)
        try {
            val json_contact: JSONObject = JSONObject(data)
            var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
            var i: Int = 0
            var size: Int = jsonarray_info.length()
            for (i in 0..size - 1) {
                var json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
                cityNames.add(json_objectdetail.getString("name"))


            }
            cityResult.postValue(cityNames)
        } catch (ioException: JSONException) {
            ioException.printStackTrace()
            SentryController.captureException(ioException)
        }
    }

    fun getStatesFromAssetJson(context: Context) {
        val data: String? = Utils.getStatesFromAssetJsonData(context)
        try {
            val json_contact: JSONObject = JSONObject(data)
            var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
            var i: Int = 0
            var size: Int = jsonarray_info.length()
            for (i in 0..size - 1) {
                var json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
                stateNames.add(json_objectdetail.getString("state"))


            }
            stateResult.postValue(stateNames)
        } catch (ioException: JSONException) {
            ioException.printStackTrace()
            SentryController.captureException(ioException)
        }
    }

    fun getCouponRedeem(redeemCouponRequest: RedeemCouponRequest, coupon_key: String) {
        Log.v("InitiatePurchaseAuto", "----> " + redeemCouponRequest + "-----> " + coupon_key)
        if (Utils.isConnectedToInternet(getApplication())) {
//            updatesLoader.postValue(true)
//            APIRequestStatus = "Order registration in progress..."
            compositeDisposable.add(
                NewApiService.redeemCoupon(redeemCouponRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.v("getCouponRedeem>>", it.toString())

                            if (it.success) {
                                val couponServiceModel = CouponServiceModel(
                                    coupon_key,
                                    it.discountAmount,
                                    it.success,
                                    it.message
                                )
                                redeemCouponResult.postValue(couponServiceModel)
                            } else {
                                updatesError.postValue(
                                SomethingWentWrong(
                                    errorCode = 0,
                                    errorMessage = "Error occurred while applying coupon - " + it.message,
                                    correlationId = ""
                                ))
                            }

//                                        updatesLoader.postValue(false)
                        },
                        {
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = it.message() ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                            updatesLoader.postValue(false)
                        }
                    )
            )
        }
    }

    fun getLastUsedPaymentDetails(auth: String, floatingPointId: String, clientId: String) {
        if (Utils.isConnectedToInternet(getApplication())) {
            CompositeDisposable().add(
                ApiService.getLastPaymentDetails(auth, floatingPointId, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            lastPaymentDetailsInfo.postValue(it)
                        },
                        {
                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
                            val errorBody: com.boost.dbcenterapi.data.api_model.paymentprofile.Error =
                                Gson().fromJson(
                                    temp,
                                    object :
                                        TypeToken<com.boost.dbcenterapi.data.api_model.paymentprofile.Error>() {}.type
                                )
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = errorBody.toString() ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                        }
                    )
            )
        }
    }

    fun getStatesWithCodes(auth: String, clientId: String, progressBar: ProgressBar) {
        if (com.boost.dbcenterapi.utils.Utils.isConnectedToInternet(getApplication())) {
            CompositeDisposable().add(
                ApiService.getStates(auth, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("getStates", it.toString())
                            statesInfo.postValue(it)
                            progressBar.visibility = View.GONE
                        },
                        {
                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
                            val errorBody: Error = Gson().fromJson(
                                temp,
                                object :
                                    TypeToken<com.boost.dbcenterapi.data.api_model.stateCode.Error>() {}.type
                            )
                            progressBar.visibility = View.GONE
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = (it as HttpException).code() ?: 0,
                                errorMessage = errorBody.toString() ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                            ))
                        }
                    )
            )
        }
    }

    fun getStateFromCityAssetJson(context: Context, city: String) {
        val data: String? = com.boost.dbcenterapi.utils.Utils.getCityFromAssetJsonData(context)
        try {
            val json_contact: JSONObject = JSONObject(data)
            var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
            var i: Int = 0
            var size: Int = jsonarray_info.length()
            for (i in 0..size - 1) {
                var json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
                Log.v("getStateFromCity", " " + json_objectdetail.getString("name") + " " + city)
                if (json_objectdetail.getString("name").equals(city)) {
//                    stateValue = json_objectdetail.getString("state") + "("+json_objectdetail.getString("state_tin") +")"
                    stateValue = json_objectdetail.getString("state")
                }
                cityValueResult.postValue(stateValue)

            }
            cityResult.postValue(cityNames)
        } catch (ioException: JSONException) {
            ioException.printStackTrace()
            SentryController.captureException(ioException)
        }
    }

    fun getGstApiInfo(auth: String, gstIn: String, clientId: String, progressBar: ProgressBar) {
        if (com.boost.dbcenterapi.utils.Utils.isConnectedToInternet(getApplication())) {
            CompositeDisposable().add(
                ApiService.getGSTDetails(auth, gstIn, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("getGstDetails", it.toString())
                            gstApiInfo.postValue(it)
                        },
                        {
                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
                            progressBar.visibility = View.GONE
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = 0,
                                errorMessage = "Invalid GST Number!!",
                                correlationId = ""
                            ))
                        }
                    )
            )
        }
    }


    fun getCouponApiResult(): LiveData<GetCouponResponse> {
        return couponApiInfo
    }


    fun getRecommendedAddonResult(): LiveData<RecommendedAddonsResponse> {
        return recommendedAddonsInfo
    }

    fun getValidityMonths(): LiveData<Int> {
        return validityMonths
    }

    fun getCouponRedeem(couponRequest: CouponRequest) {
        if (Utils.isConnectedToInternet(getApplication())) {
            Log.e("request--->", couponRequest.toString())
            CompositeDisposable().add(
                NewApiService.getOfferCoupons(couponRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("getOfferCouponDetails", it.toString())
                            var couponResponse = it
                            couponApiInfo.postValue(couponResponse)
                        },
                        {
                            updatesError.postValue(
                                SomethingWentWrong(
                                errorCode = 0,
                                errorMessage = "Error ->" + it.message,
                                correlationId = ""
                            ))
                        }
                    )
            )

        }
    }

    fun getRecommendedAddons(recommendedAddonsRequest: RecommendedAddonsRequest) {
        if (Utils.isConnectedToInternet(getApplication())) {
            Log.e("request--->", recommendedAddonsRequest.toString())
            CompositeDisposable().add(
                NewApiService.getRecommendedAddons(recommendedAddonsRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.i("getRecommendedAddons", it.toString())
                            recommendedAddonsInfo.postValue(it)
                        },
                        {
                            updatesError.postValue(
                                SomethingWentWrong(
                                    errorCode = 0,
                                    errorMessage = "Error ->" + it.message,
                                    correlationId = ""
                                ))
                        }
                    )
            )

        }
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
                //DataLoader.updateCartItemsToFirestore(Application())
            }
            .doOnError {
                updatesError.postValue(
                    SomethingWentWrong(
                        errorCode = 0,
                        errorMessage = it.message ?: BaseApplication.instance.getString(
                            R.string.something_went_wrong_please_tell_what_happened),
                        correlationId = ""
                    ))
                addToCartResult.postValue(false)
                updatesLoader.postValue(false)
            }
            .subscribe()
    }

    fun GetSuggestedDomains(domainRequest: DomainRequest) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            NewApiService.getDomains(domainRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        updatesResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesError.postValue(
                                SomethingWentWrong(
                            errorCode = (it as HttpException).code() ?: 0,
                            errorMessage = it.message() ?: BaseApplication.instance.getString(
                                R.string.something_went_wrong_please_tell_what_happened),
                            correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                        ))
                        updatesLoader.postValue(false)
                    })
        )
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
                    })
        )
    }

    fun loadUpdates() {

        updatesLoader.postValue(true)
        if (com.boost.dbcenterapi.utils.Utils.isConnectedToInternet(getApplication())) {
            MarketplaceNewRepository.getAllFeatures().toLiveData()
                .observeOnce(lifecycleOwner, Observer {
                    if (it.isSuccess()) {
                        val response = it as? GetAllFeaturesResponse
                        if (response != null && response.Data.isNotEmpty()) {
                            expertConnectDetails.postValue(response.Data[0].expert_connect)
                        }
                    }
                })
        }
    }

    fun loadNumberList(fpid: String, clientId: String) {
//        findingNumberLoader.postValue(true)
        if (com.boost.cart.utils.Utils.isConnectedToInternet(getApplication())) {
            updatesLoader.postValue(true)
            CompositeDisposable().add(
                NewApiService2.getCallTrackDetails(fpid, clientId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            // findingNumberLoader.postValue(false)
                            var NumberList = it
                            callTrackListResponse.postValue(NumberList)
                        },
                        {
//                            val temp = (it as HttpException).response()!!.errorBody()!!.string()
//                            val errorBody: Error =
//                                Gson().fromJson(temp, object : TypeToken<Error>() {}.type)
                            updatesError.postValue(
                                SomethingWentWrong(
                                    errorCode = 0,
                                    errorMessage = "Error in Loading Numbers!!",
                                    correlationId = ""
                                ))
                        }
                    )

            )

        }
    }

    fun domainVmnBlocked(
        auth: String,
        fpid: String,
        clientId: String,
        blockedItem: String,
        OrderID: String,
        blockedItemType: Int
    ) {
        compositeDisposable.add(
            NewApiService1.getItemAvailability(
                auth,
                fpid,
                clientId,
                blockedItem,
                OrderID,
                blockedItemType
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (blockedItemType == 2) {
                            updateDomainStatus.postValue(it)
                        } else {
                            updateVmnStatus.postValue(it)
                        }
                    }, {
                        updatesError.postValue(
                                SomethingWentWrong(
                            errorCode = (it as HttpException).code() ?: 0,
                            errorMessage = it.message() ?: BaseApplication.instance.getString(
                                R.string.something_went_wrong_please_tell_what_happened),
                            correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                        ))
                    })
        )
    }

    fun getAlreadyPurchasedDomain(auth: String, fpTag: String, clientId: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            NewApiService.getAlreadyPurchasedDomain(auth, fpTag, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        purchasedDomainResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(
                                SomethingWentWrong(
                            errorCode = (it as HttpException).code() ?: 0,
                            errorMessage = it.message() ?: BaseApplication.instance.getString(
                                R.string.something_went_wrong_please_tell_what_happened),
                            correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                        ))
                    })
        )
    }

    fun getAlreadyPurchasedVmn(auth: String, fpTag: String, clientId: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            NewApiService.getAlreadyPurchasedVmn(auth, fpTag, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        purchasedVmnResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(
                                SomethingWentWrong(
                            errorCode = (it as HttpException).code() ?: 0,
                            errorMessage = it.message() ?: BaseApplication.instance.getString(
                                R.string.something_went_wrong_please_tell_what_happened),
                            correlationId = (it as HttpException).response()!!.headers().toMultimap()["x-correlation-id"]?.get(0) ?: ""
                        ))
                    })
        )
    }

    fun loadPurchasedItems(fpid: String, clientId: String) {
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            NewApiService.GetFeatureDetails(fpid, clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { it1 ->
                        val list = ArrayList<String>()
                        var numberOfMonths = 1
                        for (singleItem in it1) {
                            val activatedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(singleItem.activatedDate)
                            val expiredDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(singleItem.expiryDate)
                            val startCalendar: Calendar = GregorianCalendar()
                            startCalendar.time = activatedDate
                            val endCalendar: Calendar = GregorianCalendar()
                            endCalendar.time = expiredDate

                            val diffYear = endCalendar[Calendar.YEAR] - startCalendar[Calendar.YEAR]
                            val diffMonth = diffYear * 12 + endCalendar[Calendar.MONTH] - startCalendar[Calendar.MONTH]
                            if(diffMonth > numberOfMonths && diffMonth < 60)
                                numberOfMonths = diffMonth
                        }

                        validityMonths.postValue(numberOfMonths)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(
                            SomethingWentWrong(
                                errorCode = 0,
                                errorMessage = it.message ?: BaseApplication.instance.getString(
                                    R.string.something_went_wrong_please_tell_what_happened),
                                correlationId = ""
                            ))
                    })
        )
    }
}
