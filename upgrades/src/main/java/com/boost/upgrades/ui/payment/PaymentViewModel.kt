package com.boost.upgrades.ui.payment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentPriorityEmailRequestBody
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentThroughEmailRequestBody
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_KEY
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_SECREAT
import com.boost.upgrades.utils.Utils
import com.google.gson.Gson
import com.razorpay.BaseRazorpay
import com.razorpay.Razorpay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Credentials
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

class PaymentViewModel : ViewModel() {
    private var _paymentMethods: MutableLiveData<JSONObject> = MutableLiveData()
    private var _upiPayment: MutableLiveData<JSONObject> = MutableLiveData()
    private var _externalEmailPayment: MutableLiveData<JSONObject> = MutableLiveData()
    private var _cardData: MutableLiveData<JSONObject> = MutableLiveData()
    private var _netBankingData: MutableLiveData<JSONObject> = MutableLiveData()
    private var _paymentUsingExterLinkResponse: MutableLiveData<String?> = MutableLiveData()


    val compositeDisposable = CompositeDisposable()
    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

    fun getPaymentMethods(): JSONObject {
        return _paymentMethods.value!!
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

    fun loadPamentUsingExternalLink(clientId: String, data: PaymentThroughEmailRequestBody) {
        CompositeDisposable().add(
                ApiService.createPaymentThroughEmail(clientId, data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _paymentUsingExterLinkResponse.postValue(it)
                        }, {
                            it.printStackTrace()
                        })
        )
    }
    fun loadPaymentLinkPriority(clientId: String, data: PaymentPriorityEmailRequestBody) {
        CompositeDisposable().add(
                ApiService.createPaymentThroughEmailPriority( data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _paymentUsingExterLinkResponse.postValue(it)
                        }, {
                            it.printStackTrace()
                        })
        )
    }


}
