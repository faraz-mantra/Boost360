package com.boost.upgrades.ui.cart

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.PurchaseOrder.requestV2.CreatePurchaseOrderV2
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.model.BundlesModel
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.CouponsModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.data.renewalcart.*
import com.boost.upgrades.utils.SingleLiveEvent
import com.boost.upgrades.utils.Utils
import com.dashboard.model.live.coupon.CouponServiceModel
import com.framework.analytics.SentryController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luminaire.apolloar.base_class.BaseViewModel
import es.dmoral.toasty.Toasty
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

class CartViewModel(application: Application) : BaseViewModel(application) {

  var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
  var allFeatures: MutableLiveData<List<FeaturesModel>> = MutableLiveData()

  var renewalPurchaseList: MutableLiveData<List<RenewalResult>> = MutableLiveData()
  var allBundles: MutableLiveData<List<BundlesModel>> = MutableLiveData()
  var updatesError: MutableLiveData<String> = MutableLiveData()
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

  fun updatesError(): LiveData<String> {
    return updatesError
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

  fun InitiatePurchaseOrder(auth: String,createPurchaseOrderV2: CreatePurchaseOrderV2) {
    Log.d("InitiatePurchaseOld", " " + createPurchaseOrderV2)
    if (Utils.isConnectedToInternet(getApplication())) {
//            var sample = Gson().toJson(createPurchaseOrderV2)
//            writeStringAsFile(sample, "initiatePurchase.txt")
      updatesLoader.postValue(true)
      APIRequestStatus = "Order registration in progress..."
      compositeDisposable.add(
        ApiService.CreatePurchaseOrder(auth,createPurchaseOrderV2)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("InitiatePurchaseOrder>>", it.toString())
              _initiatePurchaseOrder.postValue(it)
              updatesLoader.postValue(false)
            },
            {
              Toasty.error(
                getApplication(),
                "Error occurred while registering your order - " + it.message,
                Toast.LENGTH_LONG
              ).show()
              updatesError.postValue(it.message)
              updatesLoader.postValue(false)
            }
          )
      )
    }
  }

  fun InitiatePurchaseAutoRenewOrder(auth: String,createPurchaseOrderV2: CreatePurchaseOrderV2) {
    Log.d("InitiatePurchaseAuto", " " + createPurchaseOrderV2)
    if (Utils.isConnectedToInternet(getApplication())) {
      updatesLoader.postValue(true)
      APIRequestStatus = "Order registration in progress..."
      compositeDisposable.add(
        ApiService.CreatePurchaseAutoRenewOrder(auth,createPurchaseOrderV2)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("InitiatePurchaseOrder>>", it.toString())
              _initiateAutoRenewOrder.postValue(it)
              updatesLoader.postValue(false)
            },
            {
              Toasty.error(
                getApplication(),
                "Error occurred while registering your order - " + it.message,
                Toast.LENGTH_LONG
              ).show()
              updatesError.postValue(it.message)
              updatesLoader.postValue(false)
            }
          )
      )
    }
  }

  fun allPurchasedWidgets(auth: String,req: RenewalPurchasedRequest) {
    if (Utils.isConnectedToInternet(getApplication())) {
      updatesLoader.postValue(true)
      APIRequestStatus = "Please wait..."
      compositeDisposable.add(
        ApiService.allPurchasedWidgets(auth,
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
            updatesLoader.postValue(false)
          }, {
            renewalPurchaseList.postValue(ArrayList())
            updatesLoader.postValue(false)
          })
      )
    } else {
      updatesLoader.postValue(false)
      Toasty.error(getApplication(), "No Internet Connection.", Toast.LENGTH_LONG).show()
    }
  }

  fun createCartStateRenewal(auth: String,request: CreateCartStateRequest) {
    if (Utils.isConnectedToInternet(getApplication())) {
      updatesLoader.postValue(true)
      APIRequestStatus = "Order registration in progress..."
      compositeDisposable.add(
        ApiService.createCartStateRenewal(auth,request).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread()).subscribe({
            if (it?.result != null && it.result?.cartStateId.isNullOrEmpty().not()) {
              createCartResult.postValue(it.result)
            } else {
              updatesError.postValue(
                it.error?.errorList?.iNVALIDPARAMETERS
                  ?: "Error creating cart state"
              )
            }
            updatesLoader.postValue(false)
          }, {
            updatesError.postValue(
              it?.localizedMessage
                ?: "Error creating cart state"
            )
            updatesLoader.postValue(false)
          })
      )
    } else {
      updatesLoader.postValue(false)
      Toasty.error(getApplication(), "No Internet Connection.", Toast.LENGTH_LONG).show()
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
          updatesLoader.postValue(false)
        }
        .doOnError {
//                            updatesError.postValue(it.message)
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
          updatesLoader.postValue(false)
        }, {
//                            updatesError.postValue(it.message)
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
          updatesLoader.postValue(false)
        }, {
//                            updatesError.postValue(it.message)
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
          updatesError.postValue(it.message)
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

  fun getCustomerInfo(auth:String,InternalSourceId: String, clientId: String) {
    if (Utils.isConnectedToInternet(getApplication())) {
      updatesLoader.postValue(true)
      APIRequestStatus = "Retrieving your payment profile..."
      CompositeDisposable().add(
        ApiService.getCustomerId(auth,InternalSourceId, clientId)
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

  fun createCustomerInfo(auth:String,createCustomerInfoRequest: CreateCustomerInfoRequest) {
    APIRequestStatus = "Creating a new payment profile..."
    CompositeDisposable().add(
      ApiService.createCustomerId(auth,createCustomerInfoRequest)
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

  fun updateCustomerInfo(auth: String,createCustomerInfoRequest: CreateCustomerInfoRequest) {
    APIRequestStatus = "Creating a new payment profile..."
    CompositeDisposable().add(
      ApiService.updateCustomerId(auth,createCustomerInfoRequest)
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
    Log.v("InitiatePurchaseAuto", " " + redeemCouponRequest + " " + coupon_key)
    if (Utils.isConnectedToInternet(getApplication())) {
//            updatesLoader.postValue(true)
//            APIRequestStatus = "Order registration in progress..."
      compositeDisposable.add(
        ApiService.redeemCoupon(redeemCouponRequest)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.v("getCouponRedeem>>", it.toString())
              val couponServiceModel =
                CouponServiceModel(coupon_key, it.discountAmount, it.success, it.message)
              redeemCouponResult.postValue(couponServiceModel)

//                                        updatesLoader.postValue(false)
            },
            {
              Toasty.error(
                getApplication(),
                "Error occurred while applying coupon - " + it.message,
                Toast.LENGTH_LONG
              ).show()
              updatesError.postValue(it.message)
              updatesLoader.postValue(false)
            }
          )
      )
    }
  }
}
