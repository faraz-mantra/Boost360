package com.boost.marketplace.ui.History_Orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2.GetPurchaseOrderResponseV2
import com.boost.dbcenterapi.data.remote.ApiInterface
import com.boost.dbcenterapi.utils.Utils
import com.framework.models.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class HistoryOrdersViewModel: BaseViewModel() {
    private var purchaseResult: MutableLiveData<GetPurchaseOrderResponseV2> = MutableLiveData()
    private var _upiPayment: MutableLiveData<JSONObject> = MutableLiveData()
    private var _cardData: MutableLiveData<JSONObject> = MutableLiveData()
    private var _netBankingData: MutableLiveData<JSONObject> = MutableLiveData()


    var updatesError: MutableLiveData<String> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()

    val compositeDisposable = CompositeDisposable()
    var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)


    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun updateResult(): LiveData<GetPurchaseOrderResponseV2> {
        return purchaseResult
    }

    fun loadPurchasedItems(auth:String,fpid: String, clientId: String) {
        updatesLoader.postValue(true)
        compositeDisposable.add(
            ApiService.getPurchasedOrdersV2(auth,fpid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        purchaseResult.postValue(it)
                        updatesLoader.postValue(false)
                    }, {
                        updatesLoader.postValue(false)
                        updatesError.postValue(it.message)
                    })
        )
    }
}
