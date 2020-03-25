package com.boost.upgrades.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.razorpay.BaseRazorpay
import com.razorpay.Razorpay
import org.json.JSONObject

class PaymentViewModel : ViewModel() {
    private var _paymentMethods: MutableLiveData<JSONObject> = MutableLiveData()
    private var _upiPayment: MutableLiveData<JSONObject> = MutableLiveData()
    private var _cardData: MutableLiveData<JSONObject> = MutableLiveData()
    private var _netBankingData: MutableLiveData<JSONObject> = MutableLiveData()

    fun getPaymentMethods(): JSONObject {
        return _paymentMethods.value!!
    }

    fun walletPaymentData(): LiveData<JSONObject> {
        return _paymentMethods
    }

    fun upiPaymentData(): LiveData<JSONObject> {
        return _upiPayment
    }

    fun UpdateUPIPaymentData(data: JSONObject){
        _upiPayment.postValue(data)
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

    fun loadpaymentMethods(razorpay: Razorpay) {
        razorpay.getPaymentMethods(object : BaseRazorpay.PaymentMethodsCallback {
            override fun onPaymentMethodsReceived(result: String?) {
                val paymentMethods = JSONObject(result!!);
                Log.i("onPaymentMethods :",paymentMethods.toString())
                _paymentMethods.postValue(paymentMethods)
            }

            override fun onError(e: String?) {
                Log.e("onError :",e!!)
            }

        })
    }

}
