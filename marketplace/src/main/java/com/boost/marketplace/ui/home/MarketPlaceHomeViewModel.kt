package com.boost.marketplace.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.dbcenterapi.data.rest.repository.DeveloperBoostKitRepository
import com.boost.dbcenterapi.data.rest.repository.WithFloatsRepository
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MarketPlaceHomeViewModel(): BaseViewModel() {

  var allAvailableFeaturesDownloadResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
  var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
  var updatesError: MutableLiveData<String> = MutableLiveData()
  var _totalActiveAddonsCount: MutableLiveData<Int> = MutableLiveData()
  var allBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()
  var allFeatureDealsResult: MutableLiveData<List<FeatureDeals>> = MutableLiveData()
  var allVideoDetails: MutableLiveData<List<YoutubeVideoModel>> = MutableLiveData()
  var expertConnectDetails: MutableLiveData<ExpertConnect> = MutableLiveData()
  var promoBanners: MutableLiveData<List<PromoBanners>> = MutableLiveData()
  var promoBannersList: MutableLiveData<List<PromoBanners>> = MutableLiveData()
  var marketplaceOffers: MutableLiveData<List<PromoBanners>> = MutableLiveData()
  val promoList = ArrayList<PromoBanners>()
  var partnerZone: MutableLiveData<List<PartnerZone>> = MutableLiveData()
  var feedbackLink: MutableLiveData<String> = MutableLiveData()
  var cartResult: MutableLiveData<List<CartModel>> = MutableLiveData()
  var cartResultBack: MutableLiveData<List<CartModel>> = MutableLiveData()


  var experienceCode: String = "SVC"
  var _fpTag: String = "ABC"

  fun setCurrentExperienceCode(code: String, fpTag: String) {
    experienceCode = code
    _fpTag = fpTag
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

  fun updatesLoader(): LiveData<Boolean> {
    return updatesLoader
  }

  fun getAllFeaturesForMarketplace(application: Application, auth:String,fpid: String, clientId: String, expCode: String?, fpTag: String? ,website_id: String? = "5e7a3cf46e0572000109a5b2",activity: MarketPlaceActivity) {
     DeveloperBoostKitRepository.getAllMarketPlaceData(website_id = website_id).toLiveData().observe(
       activity, Observer {
         if (it.isSuccess() == true) {
           val response: GetAllFeaturesResponse = it.responseBody as GetAllFeaturesResponse

           Log.e("GetAllFeatures", response.toString())
           var data = arrayListOf<FeaturesModel>()
           for (item in response.Data[0].features) {
             if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) {
               var applicableToCurrentExpCode = false
               for (code in item.exclusive_to_categories!!) {
                 if (code.equals(experienceCode, true))
                   applicableToCurrentExpCode = true
               }
               if (!applicableToCurrentExpCode)
                 continue
             }

             val primaryImage = if (item.primary_image == null) null else item.primary_image!!.url
             val secondaryImages = ArrayList<String>()
             if (item.secondary_images != null) {
               for (sec_images in item.secondary_images!!) {
                 if (sec_images.url != null) {
                   secondaryImages.add(sec_images.url!!)
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
                 if (item.extended_properties != null && item.extended_properties!!.size > 0) Gson().toJson(item.extended_properties) else null,
                 if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) Gson().toJson(item.exclusive_to_categories) else null
               )
             )
           }
           Completable.fromAction {
             AppDatabase.getInstance(application)!!
               .featuresDao()
               .insertAllFeatures(data)
           }
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .doOnComplete {
               Log.i("insertAllFeatures", "Successfully")
               CompositeDisposable().add(
                 AppDatabase.getInstance(application)!!
                   .featuresDao()
                   .getFeaturesItems(true)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .doOnSuccess {
                     allAvailableFeaturesDownloadResult.postValue(it)
                     updatesLoader.postValue(false)

                     WithFloatsRepository.getFpWidgets(auth, fpid, clientId).toLiveData().observe(
                       activity, Observer {
                         val response = it as GetFloatingPointWebWidgetsResponse
                         if (it.isSuccess() == true) {
                           CompositeDisposable().add(
                             AppDatabase.getInstance(application)!!
                               .featuresDao()
                               .getallActivefeatureCount(response.Result)
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
                         } else {
                           updatesError.postValue(it.message)
                           updatesLoader.postValue(false)
                         }
                       }
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
           for (item in response.Data[0].bundles) {
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
                 Gson().toJson(item.exclusive_to_categories), item.desc
               )
             )
           }
           Completable.fromAction {
             AppDatabase.getInstance(application)!!
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
           if (response.Data[0].feature_deals.size > 0)
             allFeatureDealsResult.postValue(response.Data[0].feature_deals)

           //saving coupons in DB
           if (response.Data[0].discount_coupons != null && response.Data[0].discount_coupons.size > 0) {
             val coupons = arrayListOf<CouponsModel>()
             for (singleCoupon in response.Data[0].discount_coupons) {
               coupons.add(
                 CouponsModel(
                   singleCoupon.code,
                   singleCoupon.discount_percent
                 )
               )
             }
             Completable.fromAction {
               AppDatabase.getInstance(application)!!
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
           if (response.Data[0].video_gallery != null && response.Data[0].video_gallery.size > 0) {
             val videoGallery = arrayListOf<YoutubeVideoModel>()
             for (singleVideoDetails in response.Data[0].video_gallery) {
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
               AppDatabase.getInstance(application)!!
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
           expertConnectDetails.postValue(response.Data[0].expert_connect)

           //promobanner
           if (response.Data[0].promo_banners != null && response.Data[0].promo_banners.size > 0) {
//                                            promoBanners.value = response.Data[0].promo_banners.filter {  it1 -> it1.exclusive_to_categories.toString() == expCode }
             val promoBannerFilter = (response.Data[0].promo_banners
               ?: ArrayList()).promoBannerFilter(expCode, fpTag)
             promoBanners.postValue(promoBannerFilter)

             promoList.addAll(promoBannerFilter)
             //marketplace offers
             if (response.Data[0].marketplace_offers != null && response.Data[0].marketplace_offers.size > 0) {
//                                            marketplaceOffers.value = response.Data[0].marketplace_offers
               marketplaceOffers.postValue(response.Data[0].marketplace_offers)
               val marketplaceOffersFilter = (response.Data[0].marketplace_offers
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
                     if (item.exclusive_to_categories != null && item.exclusive_to_categories.size > 0) Gson().toJson(item.exclusive_to_categories) else null,
                     if (item.image != null) item.image!!.url else null,
                     if (item.cta_offer_identifier != null) item.cta_offer_identifier else null,
                   )
                 )
               }
               if (marketOfferData.size == 0) {
                 Completable.fromAction {
                   AppDatabase.getInstance(application)!!
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

               /*start check*/
               marketOfferData.forEach {
                 val itemFeature = it
                 val marketModel = MarketOfferModel(
                   it.coupon_code,
                   it.extra_information,
                   it.createdon,
                   it.updatedon,
                   it._kid,
                   it.websiteid,
                   it.isarchived,
                   it.expiry_date,
                   it.title,
                   if (it.exclusive_to_categories != null) Gson().toJson(it.exclusive_to_categories) else null,
                   if (it.image != null) it.image else null,
                   if (it.cta_offer_identifier != null) it.cta_offer_identifier else null,
                 )
                 Log.d("itemFeature", " " + itemFeature + " " + itemFeature.coupon_code)
                 CompositeDisposable().add(
                   AppDatabase.getInstance(application)!!
                     .marketOffersDao()
                     .checkOffersIDExist(it._kid!!)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe({
                       if (it == 0) {
                         Completable.fromAction {
                           AppDatabase.getInstance(application)!!
                             .marketOffersDao()
                             .insertToMarketOffers(itemFeature)
                         }
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .doOnComplete {
                             Log.d("insertMarketOffers", "Successfully")

                           }
                           .doOnError {
                             updatesError.postValue(it.message)
                             updatesLoader.postValue(false)
                           }
                           .subscribe()
                       } else {
                         Completable.fromAction {
                           AppDatabase.getInstance(application)!!
                             .marketOffersDao()
                             .updateAllMarketOffers(marketModel)
                         }
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .doOnComplete {
                             Log.d("updateMarketOffers", "Successfully")

                           }
                           .doOnError {
                             updatesError.postValue(it.message)
                             updatesLoader.postValue(false)
                           }
                           .subscribe()
                       }
                     }, {
                       it.printStackTrace()
                     })
                 )
               }


             } else {
               promoBannersList.postValue(promoList)
             }

           }

           //partnerZone
           if (response.Data[0].partner_zone != null && response.Data[0].partner_zone.size > 0) {
             partnerZone.postValue(response.Data[0].partner_zone)
           }

           //feedbackURL
           if (response.Data[0].feedback_link != null && response.Data[0].feedback_link.isNotEmpty()) {
             feedbackLink.postValue(response.Data[0].feedback_link)
           }

         } else {
           Log.e("GetAllFeatures", "error" + it.message)
           updatesLoader.postValue(false)
         }
       }
     )
       }

}