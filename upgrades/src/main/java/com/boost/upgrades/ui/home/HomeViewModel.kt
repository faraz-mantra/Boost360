package com.boost.upgrades.ui.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.GetAllFeatures.response.*
import com.boost.upgrades.data.model.*
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.utils.Utils
import com.google.gson.Gson
import com.luminaire.apolloar.base_class.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList

class HomeViewModel(application: Application) : BaseViewModel(application) {
  var updatesResult: MutableLiveData<List<WidgetModel>> = MutableLiveData()
  var allAvailableFeaturesDownloadResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()
  var allBackBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()
  var allFeatureDealsResult: MutableLiveData<List<FeatureDeals>> = MutableLiveData()
  var _totalActiveAddonsCount: MutableLiveData<Int> = MutableLiveData()
  var allVideoDetails: MutableLiveData<List<YoutubeVideoModel>> = MutableLiveData()
  var expertConnectDetails: MutableLiveData<ExpertConnect> = MutableLiveData()
  var promoBanners: MutableLiveData<List<PromoBanners>> = MutableLiveData()
  var marketplaceOffers: MutableLiveData<List<PromoBanners>> = MutableLiveData()
  var promoBannersList: MutableLiveData<List<PromoBanners>> = MutableLiveData()
  val promoList = ArrayList<PromoBanners>()
  var partnerZone: MutableLiveData<List<PartnerZone>> = MutableLiveData()
  var feedbackLink: MutableLiveData<String> = MutableLiveData()

  var updatesError: MutableLiveData<String> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
  var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
  var cartResultBack: MutableLiveData<List<CartModel>> = MutableLiveData()

  val compositeDisposable = CompositeDisposable()
  var ApiService = Utils.getRetrofit().create(ApiInterface::class.java)

  var experienceCode: String = "SVC"
  var _fpTag: String = "ABC"

  var categoryResult: MutableLiveData<String> = MutableLiveData()

  fun upgradeResult(): LiveData<List<WidgetModel>> {
    return updatesResult
  }

  fun getAllAvailableFeatures(): LiveData<List<FeaturesModel>> {
    return allAvailableFeaturesDownloadResult
  }

  fun getAllFeatureDeals(): LiveData<List<FeatureDeals>> {
    return allFeatureDealsResult
  }

  fun getAllBundles(): LiveData<List<BundlesModel>> {
    return allBundleResult
  }

  fun getBackAllBundles(): LiveData<List<BundlesModel>> {
    return allBackBundleResult
  }

  fun getYoutubeVideoDetails(): LiveData<List<YoutubeVideoModel>> {
    return allVideoDetails
  }

  fun getExpertConnectDetails(): LiveData<ExpertConnect> {
    return expertConnectDetails
  }

  fun getPromoBanners(): LiveData<List<PromoBanners>> {
    return promoBanners
  }

  fun promoBannerAndMarketOfferResult(): LiveData<List<PromoBanners>> {
    return promoBannersList
  }

  fun getPartnerZone(): LiveData<List<PartnerZone>> {
    return partnerZone
  }

  fun getFeedBackLink(): LiveData<String> {
    return feedbackLink
  }

  fun getTotalActiveWidgetCount(): LiveData<Int> {
    return _totalActiveAddonsCount
  }

  fun cartResult(): LiveData<List<CartModel>> {
    return cartResult
  }

  fun cartResultBack(): LiveData<List<CartModel>> {
    return cartResultBack
  }

  fun updatesError(): LiveData<String> {
    return updatesError
  }

  fun updatesLoader(): LiveData<Boolean> {
    return updatesLoader
  }

  fun setCurrentExperienceCode(code: String, fpTag: String) {
    experienceCode = code
    _fpTag = fpTag

  }

  fun categoryResult(): LiveData<String> {
    return categoryResult
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
    }
  }

  fun loadUpdates(fpid: String, clientId: String, expCode: String?, fpTag: String?) {
    Log.v("loadUpdates ", " " + expCode + " " + fpTag)
    updatesLoader.postValue(true)

    if (Utils.isConnectedToInternet(getApplication())) {
      compositeDisposable.add(
        ApiService.GetAllFeatures()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.e("GetAllFeatures", it.toString())
              var data = arrayListOf<FeaturesModel>()
              for (item in it.Data[0].features) {
                if (item.exclusive_to_categories != null && item.exclusive_to_categories.size > 0) {
                  var applicableToCurrentExpCode = false
                  for (code in item.exclusive_to_categories) {
                    if (code.equals(experienceCode, true))
                      applicableToCurrentExpCode = true
                  }
                  if (!applicableToCurrentExpCode)
                    continue
                }

                val primaryImage = if (item.primary_image == null) null else item.primary_image.url
                val secondaryImages = ArrayList<String>()
                if (item.secondary_images != null) {
                  for (sec_images in item.secondary_images) {
                    if (sec_images.url != null) {
                      secondaryImages.add(sec_images.url)
                    }
                  }
                }
                data.add(
                  FeaturesModel(
                    item._kid,
                    item.boost_widget_key,
                    item.name,
                    item.feature_code,
                    item.description,
                    item.description_title,
                    item.createdon,
                    item.updatedon,
                    item.websiteid,
                    item.isarchived,
                    item.is_premium,
                    item.target_business_usecase,
                    item.feature_importance,
                    item.discount_percent,
                    item.price,
                    item.time_to_activation,
                    primaryImage,
                    if (item.feature_banner == null) null else item.feature_banner.url,
                    if (secondaryImages.size == 0) null else Gson().toJson(secondaryImages),
                    Gson().toJson(item.learn_more_link),
                    if (item.total_installs == null) "--" else item.total_installs,
                    if (item.extended_properties != null && item.extended_properties.size > 0) Gson().toJson(
                      item.extended_properties
                    ) else null,
                    if (item.exclusive_to_categories != null && item.exclusive_to_categories.size > 0) Gson().toJson(
                      item.exclusive_to_categories
                    ) else null
                  )
                )
              }
              Completable.fromAction {
                AppDatabase.getInstance(getApplication())!!
                  .featuresDao()
                  .insertAllFeatures(data)
              }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                  Log.i("insertAllFeatures", "Successfully")
                  compositeDisposable.add(
                    AppDatabase.getInstance(getApplication())!!
                      .featuresDao()
                      .getFeaturesItems(true)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .doOnSuccess {
                        allAvailableFeaturesDownloadResult.postValue(it)
                        updatesLoader.postValue(false)

                        compositeDisposable.add(
                          ApiService.GetFloatingPointWebWidgets(fpid, clientId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                              {
                                compositeDisposable.add(
                                  AppDatabase.getInstance(getApplication())!!
                                    .featuresDao()
                                    .getallActivefeatureCount(it.Result)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSuccess {
                                      _totalActiveAddonsCount.postValue(it)
                                    }
                                    .doOnError {
                                      updatesError.postValue(it.message)
                                    }
                                    .subscribe()
                                )
                              },
                              {
                                updatesError.postValue(it.message)
                                updatesLoader.postValue(false)
                              }
                            )

                        )

                      }
                      .doOnError {
                        updatesError.postValue(it.message)
                        updatesLoader.postValue(false)
                      }
                      .subscribe()
                  )
                }
                .doOnError {
                  updatesError.postValue(it.message)
                  updatesLoader.postValue(false)
                }
                .subscribe()

              //saving bundle info in bundle table
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
                    Gson().toJson(item.exclusive_to_categories)
                  )
                )
              }
              Completable.fromAction {
                AppDatabase.getInstance(getApplication())!!
                  .bundlesDao()
                  .insertAllBundles(bundles)
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

              //getting features deals
              if (it.Data[0].feature_deals.size > 0)
                allFeatureDealsResult.postValue(it.Data[0].feature_deals)

              //saving coupons in DB
              if (it.Data[0].discount_coupons != null && it.Data[0].discount_coupons.size > 0) {
                val coupons = arrayListOf<CouponsModel>()
                for (singleCoupon in it.Data[0].discount_coupons) {
                  coupons.add(
                    CouponsModel(
                      singleCoupon.code,
                      singleCoupon.discount_percent
                    )
                  )
                }
                Completable.fromAction {
                  AppDatabase.getInstance(getApplication())!!
                    .couponsDao()
                    .insertAllCoupons(coupons)
                }
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnComplete {
                    Log.i("insertAllCoupons", "Successfully")
                    updatesLoader.postValue(false)
                  }
                  .doOnError {
                    Log.i("insertAllCoupons", "Successfully")
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                  }
                  .subscribe()
              }

              //saving videoGallery
              if (it.Data[0].video_gallery != null && it.Data[0].video_gallery.size > 0) {
                val videoGallery = arrayListOf<YoutubeVideoModel>()
                for (singleVideoDetails in it.Data[0].video_gallery) {
                  videoGallery.add(
                    YoutubeVideoModel(
                      singleVideoDetails._kid,
                      singleVideoDetails.desc,
                      singleVideoDetails.duration,
                      singleVideoDetails.title,
                      singleVideoDetails.youtube_link
                    )
                  )
                }

                Completable.fromAction {
                  AppDatabase.getInstance(getApplication())!!
                    .youtubeVideoDao()
                    .insertAllYoutubeVideos(videoGallery)
                }
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnComplete {
                    Log.i("insertAllYoutubeVideos", "Successfully")
                    allVideoDetails.postValue(videoGallery)
                    updatesLoader.postValue(false)
                  }
                  .doOnError {
                    Log.i("insertAllYoutubeVideos", "Successfully")
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                  }
                  .subscribe()
              }

              //get ExpertConnect Details
              expertConnectDetails.postValue(it.Data[0].expert_connect)

              //promobanner
              if (it.Data[0].promo_banners != null && it.Data[0].promo_banners.size > 0) {
//                                            promoBanners.value = it.Data[0].promo_banners.filter {  it1 -> it1.exclusive_to_categories.toString() == expCode }
                val promoBannerFilter = (it.Data[0].promo_banners
                  ?: ArrayList()).promoBannerFilter(expCode, fpTag)
                promoBanners.postValue(promoBannerFilter)

                promoList.addAll(promoBannerFilter)
                //marketplace offers
                if (it.Data[0].marketplace_offers != null && it.Data[0].marketplace_offers.size > 0) {
//                                            marketplaceOffers.value = it.Data[0].marketplace_offers
                  marketplaceOffers.postValue(it.Data[0].marketplace_offers)
                  val marketplaceOffersFilter = (it.Data[0].marketplace_offers
                    ?: ArrayList()).promoMarketOfferFilter(expCode, fpTag)
                  promoList.addAll(marketplaceOffersFilter)
                  promoBannersList.postValue(promoList)
                  var marketOfferData = arrayListOf<MarketOfferModel>()
                  for (item in marketplaceOffersFilter) {
                    marketOfferData.add(
                      MarketOfferModel(
                        item.coupon_code,
                        item.extra_information,
                        item.createdon,
                        item.updatedon,
                        item._kid,
                        item.websiteid,
                        item.isarchived,
                        item.expiry_date,
                        item.title,
                        if (item.exclusive_to_categories != null && item.exclusive_to_categories.size > 0) Gson().toJson(
                          item.exclusive_to_categories
                        ) else null,
                        if (item.image != null) item.image.url else null,
                        if (item.cta_offer_identifier != null) item.cta_offer_identifier else null,
                      )
                    )
                  }
                  if (marketOfferData.size == 0) {
                    Completable.fromAction {
                      AppDatabase.getInstance(getApplication())!!
                        .marketOffersDao()
                        .emptyMarketOffers()
                    }
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .doOnComplete {
                        Log.i("deleteMarketOffers", "Successfully")

                      }
                      .doOnError {

                      }
                      .subscribe()
                  }
                  Completable.fromAction {
                    AppDatabase.getInstance(getApplication())!!
                      .marketOffersDao()
                      .insertAllMarketOffers(marketOfferData)
                  }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                      Log.i("insertMarketOffers", "Successfully")

                    }
                    .doOnError {
                      updatesError.postValue(it.message)
                      updatesLoader.postValue(false)
                    }
                    .subscribe()

                } else {
                  promoBannersList.postValue(promoList)
                }

              }

              //partnerZone
              if (it.Data[0].partner_zone != null && it.Data[0].partner_zone.size > 0) {
                partnerZone.postValue(it.Data[0].partner_zone)
              }

              //feedbackURL
              if (it.Data[0].feedback_link != null && it.Data[0].feedback_link.isNotEmpty()) {
                feedbackLink.postValue(it.Data[0].feedback_link)
              }

            },
            {
              Log.e("GetAllFeatures", "error" + it.message)
              Log.e("GetAllFeatures", "error" + it.localizedMessage)
              updatesLoader.postValue(false)
            }
          )
      )
    } else {
      compositeDisposable.add(
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
    }
  }

  fun loadPackageUpdates(fpid: String, clientId: String) {
    updatesLoader.postValue(true)

    if (Utils.isConnectedToInternet(getApplication())) {
      compositeDisposable.add(
        ApiService.GetAllFeatures()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            {
              Log.e("GetAllFeatures", it.toString())


              //saving bundle info in bundle table
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
                    Gson().toJson(item.exclusive_to_categories)
                  )
                )
              }
              Completable.fromAction {
//                                            AppDatabase.getInstance(getApplication())!!
//                                                    .bundlesDao()
//                                                    .insertAllBundles(bundles)
              }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                  Log.i("insertAllBundles", "Successfully")
                  allBackBundleResult.postValue(bundles)
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
    } else {
      compositeDisposable.add(
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
    }
  }

  fun getCartItems() {
    compositeDisposable.add(
      AppDatabase.getInstance(getApplication())!!
        .cartDao()
        .getCartItems()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          cartResult.postValue(it)
          updatesLoader.postValue(false)
        }, {
          updatesError.postValue(it.message)
          updatesLoader.postValue(false)
        }
        )
    )
  }

  fun getCartItemsBack() {
    compositeDisposable.add(
      AppDatabase.getInstance(getApplication())!!
        .cartDao()
        .getCartItems()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          cartResultBack.postValue(it)
          updatesLoader.postValue(false)
        }, {
          updatesError.postValue(it.message)
          updatesLoader.postValue(false)
        }
        )
    )
  }

  fun addItemToCart1(updatesModel: FeaturesModel, minMonth: Int) {
    updatesLoader.postValue(true)
    val discount = 100 - updatesModel.discount_percent
    val paymentPrice = (discount * updatesModel.price) / 100.0
    val cartItem = CartModel(
      updatesModel.feature_id,
      updatesModel.boost_widget_key,
      updatesModel.feature_code,
      updatesModel.name,
      updatesModel.description,
      updatesModel.primary_image,
      paymentPrice,
      updatesModel.price.toDouble(),
      updatesModel.discount_percent,
      1,
      minMonth,
      "features",
      updatesModel.extended_properties
    )


    Completable.fromAction {
      AppDatabase.getInstance(getApplication())!!.cartDao()
        .insertToCart(cartItem)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete {
        getCartItems()
        updatesLoader.postValue(false)
      }
      .doOnError {
        updatesError.postValue(it.message)
        updatesLoader.postValue(false)
      }
      .subscribe()
  }

  fun addItemToCart(updatesModel: FeaturesModel, minMonth: Int) {
    updatesLoader.postValue(true)
    val discount = 100 - updatesModel.discount_percent
    val paymentPrice = (discount * updatesModel.price) / 100.0
    val cartItem = CartModel(
      updatesModel.feature_id,
      updatesModel.boost_widget_key,
      updatesModel.feature_code,
      updatesModel.name,
      updatesModel.description,
      updatesModel.primary_image,
      paymentPrice,
      updatesModel.price.toDouble(),
      updatesModel.discount_percent,
      1,
      minMonth,
      "features",
      updatesModel.extended_properties
    )

    CompositeDisposable().add(
      AppDatabase.getInstance(getApplication())!!
        .cartDao()
        .checkCartFeatureTableKeyExist()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          if (it == 1) {
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
                    getCartItems()
                    updatesLoader.postValue(false)
                  }
                  .doOnError {
                    updatesError.postValue(it.message)
                    updatesLoader.postValue(false)
                  }
                  .subscribe()
              }
              .subscribe()
          } else {
            Completable.fromAction {
              AppDatabase.getInstance(getApplication())!!.cartDao()
                .insertToCart(cartItem)
            }
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .doOnComplete {
                getCartItems()
                updatesLoader.postValue(false)
              }
              .doOnError {
                updatesError.postValue(it.message)
                updatesLoader.postValue(false)
              }
              .subscribe()
          }
        }, {
//                            Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG).show()
        })
    )


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
    Log.v("addItemToCartPackage", " " + cartItem.boost_widget_key + " " + cartItem.boost_widget_key)
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
            getCartItems()
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


  fun emptyCouponTable() {
    Completable.fromAction {
      AppDatabase.getInstance(getApplication())!!
        .couponsDao()
        .emptyCoupons()
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete {
        Log.i("emptyCouponTable", "Successfull")
      }.doOnError {
        Log.i("emptyCouponTable", "Failure")
      }
      .subscribe()
  }

  fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
  ): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
      result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
      result.value = block(this.value, liveData.value)
    }
    return result
  }

  fun <T, A, B> LiveData<A>.combineAndCompute(
    other: LiveData<B>,
    onChange: (A, B) -> T
  ): MediatorLiveData<T> {

    var source1emitted = false
    var source2emitted = false

    val result = MediatorLiveData<T>()

    val mergeF = {
      val source1Value = this.value
      val source2Value = other.value

      if (source1emitted && source2emitted) {
        result.value = onChange.invoke(source1Value!!, source2Value!!)
      }
    }

    result.addSource(this) { source1emitted = true; mergeF.invoke() }
    result.addSource(other) { source2emitted = true; mergeF.invoke() }

    return result
  }

}
