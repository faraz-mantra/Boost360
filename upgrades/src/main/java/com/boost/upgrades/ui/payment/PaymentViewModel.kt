package com.boost.upgrades.ui.payment

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentPriorityEmailRequestBody
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentThroughEmailRequestBody
import com.boost.upgrades.data.api_model.customerId.create.CreateCustomerIDResponse
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.get.GetCustomerIDResponse
import com.boost.upgrades.data.api_model.gst.Error
import com.boost.upgrades.data.api_model.gst.GSTApiResponse
import com.boost.upgrades.data.api_model.paymentprofile.GetLastPaymentDetails
import com.boost.upgrades.data.api_model.stateCode.GetStates
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_KEY
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_SECREAT
import com.boost.upgrades.utils.Utils
import com.framework.analytics.SentryController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luminaire.apolloar.base_class.BaseViewModel
import com.razorpay.BaseRazorpay
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileWriter
import java.io.IOException

class PaymentViewModel(application: Application) : BaseViewModel(application) {
  private var _paymentMethods: MutableLiveData<JSONObject> = MutableLiveData()
  private var _paymentMoreBanksMethods: MutableLiveData<JSONObject> = MutableLiveData()
  private var _upiPayment: MutableLiveData<JSONObject> = MutableLiveData()
  private var _externalEmailPayment: MutableLiveData<JSONObject> = MutableLiveData()
  private var _cardData: MutableLiveData<JSONObject> = MutableLiveData()
  private var _netBankingData: MutableLiveData<JSONObject> = MutableLiveData()
  private var _paymentUsingExterLinkResponse: MutableLiveData<String?> = MutableLiveData()
  var updateCustomerInfo: MutableLiveData<GetCustomerIDResponse> = MutableLiveData()
  var customerInfoState: MutableLiveData<Boolean> = MutableLiveData()
  private var customerInfo: MutableLiveData<CreateCustomerIDResponse> = MutableLiveData()
  private var updateInfo: MutableLiveData<CreateCustomerIDResponse> = MutableLiveData()
  var cityResult: MutableLiveData<List<String>> = MutableLiveData()
  var stateResult: MutableLiveData<List<String>> = MutableLiveData()
  var stateValueResult: MutableLiveData<String> = MutableLiveData()
  var cityValueResult: MutableLiveData<String> = MutableLiveData()
  var cityNames = ArrayList<String>()
  var stateNames = ArrayList<String>()
  var stateValue: String? = null
  var cityValue: String? = null
  var selectedState: String? = null
  var selectedStateResult: MutableLiveData<String> = MutableLiveData()
  var selectedStateTinResult: MutableLiveData<String> = MutableLiveData()
  private var APIRequestStatus: String? = null
  private var gstApiInfo : MutableLiveData<GSTApiResponse> = MutableLiveData()
  private var statesInfo :MutableLiveData<GetStates> = MutableLiveData()
  private var lastPaymentDetailsInfo :MutableLiveData<GetLastPaymentDetails> = MutableLiveData()

  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

  val compositeDisposable = CompositeDisposable()
  var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)
  var gstSwitchFlag: MutableLiveData<Boolean> = MutableLiveData()
  var closeBusinessPopupFlag: MutableLiveData<Boolean> = MutableLiveData()

  //    fun getPaymentMethods(): JSONObject {
  fun getPaymentMethods(): LiveData<JSONObject> {
//        return _paymentMethods.value!!
    return _paymentMoreBanksMethods
  }

  fun walletPaymentData(): LiveData<JSONObject> {
    return _paymentMethods
  }

  fun upiPaymentData(): LiveData<JSONObject> {
    return _upiPayment
  }

  fun externalEmailPaymentData(): LiveData<JSONObject> {
    return _externalEmailPayment
  }

  fun UpdateUPIPaymentData(data: JSONObject) {
    _upiPayment.postValue(data)
  }

  fun UpdateExternalEmailPaymentData(data: JSONObject) {
    _externalEmailPayment.postValue(data)
  }

  fun cardData(): LiveData<JSONObject> {
    return _cardData
  }

  fun UpdateCardData(data: JSONObject) {
    _cardData.postValue(data)
  }

  fun netBankingData(): LiveData<JSONObject> {
    return _netBankingData
  }

  fun UpdateNetBankingData(data: JSONObject) {
    _netBankingData.postValue(data)
  }

  fun getPamentUsingExternalLink(): LiveData<String?> {
    return _paymentUsingExterLinkResponse
  }

  fun getUpdatedCustomerResult(): LiveData<CreateCustomerIDResponse> {
    return customerInfo
  }

  fun getUpdatedCustomerBusinessResult(): LiveData<CreateCustomerIDResponse> {
    return customerInfo
  }

  fun getUpdatedResult(): LiveData<CreateCustomerIDResponse> {
    return updateInfo
  }


  fun getCustomerInfoStateResult(): LiveData<Boolean> {
    return customerInfoState
  }

  fun getCustomerInfoResult(): LiveData<GetCustomerIDResponse> {
    return updateCustomerInfo
  }
  fun getGstApiResult(): LiveData<GSTApiResponse>{
    return gstApiInfo
  }
  fun getStatesResult(): LiveData<GetStates>{
    return statesInfo
  }
  fun getLastPayDetails() :LiveData<GetLastPaymentDetails>{
    return lastPaymentDetailsInfo
  }

  fun cityResult(): LiveData<List<String>> {
    return cityResult
  }

  fun stateResult(): LiveData<List<String>> {
    return stateResult
  }

  fun stateValueResult(): LiveData<String> {
    return stateValueResult
  }

  fun cityValueResult(): LiveData<String> {
    return cityValueResult
  }


  fun selectedStateResult(state: String) {
    selectedStateResult.postValue(state)
  }

  fun getSelectedStateResult(): LiveData<String> {
    return selectedStateResult
  }

  fun selectedStateTinResult(stateTin :String){
      selectedStateTinResult.postValue(stateTin)
  }
  fun getSelectedStateTinResult():LiveData<String>{
    return selectedStateTinResult
  }

  fun updatesError(): LiveData<String> {
    return updatesError
  }

  fun getLoaderStatus(): LiveData<Boolean> {
    return updatesLoader
  }

  fun getGstSwitchFlag(): LiveData<Boolean> {
    return gstSwitchFlag
  }

  fun updatesBusinessPopup(popUp: Boolean) {
    return closeBusinessPopupFlag.postValue(popUp)
  }

  fun getBusinessPopup(): LiveData<Boolean> {
    return closeBusinessPopupFlag
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

  fun loadpaymentMethods(razorpay: Razorpay) {
    razorpay.getPaymentMethods(object : BaseRazorpay.PaymentMethodsCallback {
      override fun onPaymentMethodsReceived(result: String?) {
        val paymentMethods = JSONObject(result!!)
        Log.i("onPaymentMethods :", paymentMethods.toString())
        _paymentMethods.postValue(paymentMethods)
      }

      override fun onError(e: String?) {
        Log.e("onError :", e!!)
      }

    })
  }

  fun loadMoreBanks(razorpay: Razorpay) {
    razorpay.getPaymentMethods(object : BaseRazorpay.PaymentMethodsCallback {
      override fun onPaymentMethodsReceived(result: String?) {
        val paymentMethods = JSONObject(result!!)
        writeStringAsFile(paymentMethods.toString(), "loadMoreBanks.txt")
        Log.i("onPaymentMethods :", paymentMethods.toString())
        _paymentMoreBanksMethods.postValue(paymentMethods)
      }

      override fun onError(e: String?) {
        Log.e("onError :", e!!)
      }

    })
  }

  fun getRazorPayToken(customerId: String) {
    val header = Credentials.basic(RAZORPAY_KEY, RAZORPAY_SECREAT)
    compositeDisposable.add(
      ApiService.getRazorPayTokens(header, customerId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          Log.e("getRazorPayTokens", ">> " + it.toString())
        }, {
          it.printStackTrace()
        })
    )
  }

  fun loadPamentUsingExternalLink(auth: String,clientId: String, data: PaymentThroughEmailRequestBody) {
    CompositeDisposable().add(
      ApiService.createPaymentThroughEmail(auth,clientId, data)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          _paymentUsingExterLinkResponse.postValue(it)
        }, {
          it.printStackTrace()
        })
    )
  }

  fun loadPaymentLinkPriority(auth: String,clientId: String, data: PaymentPriorityEmailRequestBody) {
    CompositeDisposable().add(
      ApiService.createPaymentThroughEmailPriority(auth,data)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          _paymentUsingExterLinkResponse.postValue(it)
        }, {
          it.printStackTrace()
        })
    )
  }

  fun getCustomerInfo(auth:String,InternalSourceId: String, clientId: String) {
    if (Utils.isConnectedToInternet(getApplication())) {
      CompositeDisposable().add(
        ApiService.getCustomerId(auth,InternalSourceId, clientId)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("getCustomerId>>", it.toString())
              updateCustomerInfo.postValue(it)
              customerInfoState.postValue(true)
//                            customerInfoState.postValue(false)
            },
            {
              val temp = (it as HttpException).response()!!.errorBody()!!.string()
              val errorBody: CreateCustomerIDResponse = Gson().fromJson(
                temp, object : TypeToken<CreateCustomerIDResponse>() {}.type
              )
              if (errorBody != null && errorBody.Error.ErrorCode.equals("INVALID CUSTOMER") && errorBody.StatusCode == 400) {
                customerInfoState.postValue(false)
              }
            }
          )
      )
    }
  }
  fun getGstApiInfo(auth: String,gstIn:String,clientId: String,progressBar: ProgressBar){
    if(Utils.isConnectedToInternet(getApplication())){
      CompositeDisposable().add(
        ApiService.getGSTDetails(auth,gstIn,clientId)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("getGstDetails",it.toString())
              gstApiInfo.postValue(it)
            },
            {
              val temp = (it as HttpException).response()!!.errorBody()!!.string()
              val errorBody : Error = Gson().fromJson(temp,object : TypeToken<Error>() {}.type)
              progressBar.visibility = View.GONE
              Toasty.error(getApplication(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
            }
          )
      )
    }
  }

  fun getStatesWithCodes(auth: String,clientId: String,progressBar: ProgressBar){
    if(Utils.isConnectedToInternet(getApplication())){
      CompositeDisposable().add(
        ApiService.getStates(auth,clientId)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.i("getStates",it.toString())
              statesInfo.postValue(it)
              progressBar.visibility = View.GONE
            },
            {
              val temp = (it as HttpException).response()!!.errorBody()!!.string()
              val errorBody : Error = Gson().fromJson(temp,object : TypeToken<com.boost.upgrades.data.api_model.stateCode.Error>() {}.type)
              progressBar.visibility = View.GONE
              Toasty.error(getApplication(), errorBody.toString(), Toast.LENGTH_LONG).show()
            }
          )
      )
    }
  }

  fun getLastUsedPaymentDetails(auth: String,floatingPointId :String,clientId: String){
    if(Utils.isConnectedToInternet(getApplication())){
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
              val errorBody : Error = Gson().fromJson(temp,object : TypeToken<com.boost.upgrades.data.api_model.paymentprofile.Error>() {}.type)
              Toasty.error(getApplication(), errorBody.toString(), Toast.LENGTH_LONG).show()
            }
          )
      )
    }
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

  fun getStateFromCityAssetJson(context: Context, city: String) {
    val data: String? = Utils.getCityFromAssetJsonData(context)
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

  fun getExistingFromAssetJson(context: Context, state: String) {
    val data: String? = Utils.getStatesFromAssetJsonData(context)
    try {
      val json_contact: JSONObject = JSONObject(data)
      var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
      var i: Int = 0
      var size: Int = jsonarray_info.length()
      for (i in 0..size - 1) {
        var json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
        if (json_objectdetail.getString("state").equals(state)) {
          stateValue = json_objectdetail.getString("state")
        }

      }
      stateValueResult.postValue(stateValue)
    } catch (ioException: JSONException) {
      ioException.printStackTrace()
      SentryController.captureException(ioException)
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
            updateInfo.postValue(it)
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
//                    var sample = Gson().toJson(createCustomerInfoRequest)
//            writeStringAsFile(sample, "updateCustomer.txt")
    APIRequestStatus = "Creating a new payment profile..."
    CompositeDisposable().add(
      ApiService.updateCustomerId(auth,createCustomerInfoRequest)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            Log.i("updateCustomerInfo>>", it.toString())
            customerInfo.postValue(it)
//                        updatesLoader.postValue(false)
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
