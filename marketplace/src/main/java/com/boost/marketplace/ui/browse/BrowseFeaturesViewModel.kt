package com.boost.marketplace.ui.browse

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.dbcenterapi.data.rest.repository.MarketplaceRepository
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Utils
import com.framework.analytics.SentryController
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BrowseFeaturesViewModel: BaseViewModel()  {

    var allAvailableFeaturesDownloadResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()
    var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
    var categoryResult: MutableLiveData<String> = MutableLiveData()

    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(application: Application,
                                lifecycleOwner: LifecycleOwner
    ){
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }

    fun getAllAvailableFeatures(): LiveData<List<FeaturesModel>> {
        return allAvailableFeaturesDownloadResult
    }

    fun categoryResult(): LiveData<String> {
        return categoryResult
    }

    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun loadAllFeaturesfromDB(){
        updatesLoader.postValue(true)
        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getFeaturesItems(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    allAvailableFeaturesDownloadResult.postValue(it)
                    updatesLoader.postValue(false)
                }
                .doOnError {
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                }
                .subscribe()
        )
    }

    fun getCartItems() {
        updatesLoader.postValue(false)
        CompositeDisposable().add(
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

    fun getCategoriesFromAssetJson(context: Context, expCode: String?) {
        val data: String? = Utils.getAssetJsonData(context)
        try {
            val json_contact: JSONObject = JSONObject(data)
            var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
            var i: Int = 0
            var size: Int = jsonarray_info.length()
            for (i in 0..size - 1) {
                var json_objectdetail: JSONObject = jsonarray_info.getJSONObject(i)
                if (json_objectdetail.getString("experience_code") == expCode) {
                    categoryResult.postValue(json_objectdetail.getString("category_Name"))
                }
            }
        } catch (ioException: JSONException) {
            ioException.printStackTrace()
            SentryController.captureException(ioException)
        }
    }

}