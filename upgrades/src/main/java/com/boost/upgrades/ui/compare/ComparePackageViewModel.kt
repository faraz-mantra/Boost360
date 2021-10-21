package com.boost.upgrades.ui.compare

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.model.*
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.data.remote.NewApiInterface
import com.boost.upgrades.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ComparePackageViewModel(application: Application) : BaseViewModel(application) {
  var updatesResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var featureResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
  var cartResultBack: MutableLiveData<List<CartModel>> = MutableLiveData()
  var bundleKeysResult: MutableLiveData<List<String>> = MutableLiveData()

  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
  var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)
  var NewApiService = Utils.getRetrofit(true).create(NewApiInterface::class.java)
  var experienceCode: String = "SVC"
  var _fpTag: String = "ABC"
  var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()

  fun getSpecificFeature(): LiveData<List<FeaturesModel>> {
    return featureResult
  }

  fun getUpgradeResult(): LiveData<List<FeaturesModel>> {
    return updatesResult
  }

  fun cartResult(): LiveData<List<CartModel>> {
    return cartResult
  }

  fun cartResultBack(): LiveData<List<CartModel>> {
    return cartResultBack
  }

  fun getBundleWidgetKeys(): LiveData<List<String>> {
    return bundleKeysResult
  }

  fun updatesLoader(): LiveData<Boolean> {
    return updatesLoader
  }

  fun loadUpdates(list: List<String>) {
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .featuresDao()
        .getallFeaturesInList(list)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            updatesResult.postValue(it)
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

  fun getFeatureValues(list: List<String>) {
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
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

  fun addItemToCart(cartItem: CartModel) {
    updatesLoader.postValue(true)
    Completable.fromAction {
      AppDatabase.getInstance(getApplication())!!.cartDao()
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

  fun getCartItems() {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
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
          updatesError.postValue(it.message)
          updatesLoader.postValue(false)
        }
        .subscribe()
    )
  }

  fun getCartItemsBack() {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .cartDao()
        .getCartItems()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess {
          cartResultBack.postValue(it)
          updatesLoader.postValue(false)
        }
        .doOnError {
          updatesError.postValue(it.message)
          updatesLoader.postValue(false)
        }
        .subscribe()
    )
  }

  fun getAssociatedWidgetKeys(bundleId: String) {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .bundlesDao()
        .getIncludedKeysInBundle(bundleId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess {
          var keys = Gson().fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
          bundleKeysResult.postValue(keys)
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

  fun loadPackageUpdates() {
    updatesLoader.postValue(true)

    if (Utils.isConnectedToInternet(getApplication())) {
      CompositeDisposable().add(
        NewApiService.GetAllFeatures()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.e("GetAllFeatures", it.toString())
              val bundles = arrayListOf<BundlesModel>()
              for (item in it.Data[0].bundles) {
                if (item.exclusive_for_customers != null && item.exclusive_for_customers.size > 0) {
                  var applicableToCurrentFPTag = false
                  for (code in item.exclusive_for_customers) {
                    if (code.equals(_fpTag, true)) {
                      applicableToCurrentFPTag = true
                      break
                    }
                  }
                  if (!applicableToCurrentFPTag)
                    continue
                }
                if (item.exclusive_to_categories != null && item.exclusive_to_categories.size > 0) {
                  var applicableToCurrentExpCode = false
                  for (code in item.exclusive_to_categories) {
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
                    if (item.min_purchase_months != null && item.min_purchase_months > 1) item.min_purchase_months else 1,
                    item.overall_discount_percent,
                    if (item.primary_image != null) item.primary_image.url else null,
                    Gson().toJson(item.included_features),
                    item.target_business_usecase,
                    Gson().toJson(item.exclusive_to_categories), item.desc
                  )
                )
              }
              Completable.fromAction {
                /*AppDatabase.getInstance(getApplication())!!
                        .bundlesDao()
                        .insertAllBundles(bundles)*/
              }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                  Log.i("insertAllBundles", "Successfully")
                  allBundleResult.postValue(bundles)
                  updatesLoader.postValue(false)
                }
                .doOnError {
                  updatesError.postValue(it.message)
                  updatesLoader.postValue(false)
                }
                .subscribe()

            },
            {
              Log.e("GetAllFeatures", "error" + it.message)
              updatesLoader.postValue(false)
            }
          )
      )
    } /*else {
            CompositeDisposable().add(
                    AppDatabase.getInstance(getApplication())!!
                            .widgetDao()
                            .queryUpdates()
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
                            .subscribe()
            )
        }*/
  }

  fun addItemToCartPackage1(cartItem: CartModel) {
    updatesLoader.postValue(true)
    Completable.fromAction {
      AppDatabase.getInstance(getApplication())!!.cartDao()
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

  fun addItemToCartPackage(cartItem: CartModel) {
    updatesLoader.postValue(true)

    Completable.fromAction {
      AppDatabase.getInstance(getApplication())!!.cartDao().emptyCart()
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnError {
        //in case of error
      }
      .doOnComplete {
        Completable.fromAction {
          AppDatabase.getInstance(getApplication())!!.cartDao()
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
      .subscribe()


  }
}
