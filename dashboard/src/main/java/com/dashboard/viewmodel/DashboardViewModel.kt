package com.dashboard.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.appservice.rest.repository.AzureWebsiteNewRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.dashboard.model.DisableBadgeNotificationRequest
import com.dashboard.rest.repository.*
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.google.gson.Gson
import com.inventoryorder.model.summary.request.SellerSummaryRequest
import com.inventoryorder.rest.repositories.ApiTwoWithFloatRepository
import com.inventoryorder.rest.repositories.ApiWithFloatRepository
import com.inventoryorder.rest.repositories.InventoryOrderRepository
import com.onboarding.nowfloats.model.nfxProcess.NFXProcessRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.rest.repositories.CategoryRepository
import com.onboarding.nowfloats.rest.repositories.ChannelRepository
import com.onboarding.nowfloats.rest.repositories.UploadImageRepository
import com.onboarding.nowfloats.rest.repositories.WhatsAppRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject

class DashboardViewModel : BaseViewModel() {

  fun getCategories(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategories(context).toLiveData()
  }

  fun getMoreSettings(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getMoreSettings(context).toLiveData()
  }

  fun getWebsiteNavData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getWebsiteNavData(context).toLiveData()
  }

  fun getFirebaseToken(): LiveData<BaseResponse> {
    return WithFloatTwoRepositoryD.getFirebaseAuthToken().toLiveData()
  }

  @Deprecated("NFX token API")
  fun getChannelsAccessToken(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsAccessToken(nowfloatsId).toLiveData()
  }

  fun getChannelsAccessTokenStatus(nowfloatsId: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsStatus(nowfloatsId).toLiveData()
  }

  fun getCapLimitFeatureDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return AzureWebsiteNewRepository.getCapLimitFeatureDetails(fpId,clientId).toLiveData()
  }

  fun getChannelsInsight(nowfloatsId: String?, identifier: String?): LiveData<BaseResponse> {
    return ChannelRepository.getChannelsInsights(nowfloatsId, identifier).toLiveData()
  }

  fun getWhatsappBusiness(request: String?, auth: String): LiveData<BaseResponse> {
    return WhatsAppRepository.getWhatsappBusiness(request = getJsonRequest(request), auth = auth)
      .toLiveData()
  }

  fun nfxProcess(request: NFXProcessRequest?): LiveData<BaseResponse> {
    return ChannelRepository.nfxProcess(request).toLiveData()
  }

  fun getBizFloatMessage(request: Map<String, String>): LiveData<BaseResponse> {
    return ApiTwoWithFloatRepository.getBizFloatMessage(request).toLiveData()
  }

//  fun loadMarketPlaceData(application: Application, expCode: String?, fpTag: String?) {
//    val NewApiService = Utils.getRetrofit(true).create(NewApiInterface::class.java)
//
//    if (Utils.isConnectedToInternet(application)) {
//      CompositeDisposable().add(
//        NewApiService.GetAllFeatures()
//          .subscribeOn(Schedulers.io())
//          .observeOn(AndroidSchedulers.mainThread())
//          .subscribe(
//            {
//              Log.e("GetAllFeatures", it.toString())
//              var data = arrayListOf<FeaturesModel>()
//              for (item in it.Data[0].features) {
//                if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) {
//                  var applicableToCurrentExpCode = false
//                  for (code in item.exclusive_to_categories!!) {
//                    if (code.equals(expCode, true))
//                      applicableToCurrentExpCode = true
//                  }
//                  if (!applicableToCurrentExpCode)
//                    continue
//                }
//
//                val primaryImage = if (item.primary_image == null) null else item.primary_image!!.url
//                val secondaryImages = ArrayList<String>()
//                if (item.secondary_images != null) {
//                  for (sec_images in item.secondary_images!!) {
//                    if (sec_images.url != null) {
//                      secondaryImages.add(sec_images.url!!)
//                    }
//                  }
//                }
//                data.add(
//                  FeaturesModel(
//                  item._kid,
//                  item.boost_widget_key,
//                  item.name,
//                  item.feature_code,
//                  item.description,
//                  item.description_title,
//                  item.createdon,
//                  item.updatedon,
//                  item.websiteid,
//                  item.isarchived,
//                  item.is_premium,
//                  item.target_business_usecase,
//                  item.feature_importance,
//                  item.discount_percent,
//                  item.price,
//                  item.time_to_activation,
//                  primaryImage,
//                  if (item.feature_banner == null) null else item.feature_banner.url,
//                  if (secondaryImages.size == 0) null else Gson().toJson(secondaryImages),
//                  Gson().toJson(item.learn_more_link),
//                  if (item.total_installs == null) "--" else item.total_installs,
//                  if (item.extended_properties != null && item.extended_properties!!.size > 0) Gson().toJson(item.extended_properties) else null,
//                  if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) Gson().toJson(item.exclusive_to_categories) else null
//                )
//                )
//              }
//              Completable.fromAction {
//                AppDatabase.getInstance(application)!!
//                  .featuresDao()
//                  .insertAllFeatures(data)
//              }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete {
//                  Log.e("insertAllFeatures", "Successfully")
//                }
//                .doOnError {
//                  Log.e("insertAllFeatures", "Failed")
//                }
//                .subscribe()
//
//              //saving bundle info in bundle table
//              val bundles = arrayListOf<BundlesModel>()
//              for (item in it.Data[0].bundles) {
//                if (item.exclusive_for_customers != null && item.exclusive_for_customers!!.size > 0) {
//                  var applicableToCurrentFPTag = false
//                  for (code in item.exclusive_for_customers!!) {
//                    if (code.equals(fpTag, true)) {
//                      applicableToCurrentFPTag = true
//                      break
//                    }
//                  }
//                  if (!applicableToCurrentFPTag)
//                    continue
//                }
//                if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) {
//                  var applicableToCurrentExpCode = false
//                  for (code in item.exclusive_to_categories!!) {
//                    if (code.equals(expCode, true)) {
//                      applicableToCurrentExpCode = true
//                      break
//                    }
//                  }
//                  if (!applicableToCurrentExpCode)
//                    continue
//                }
//                bundles.add(BundlesModel(
//                  item._kid,
//                  item.name,
//                  if (item.min_purchase_months != null && item.min_purchase_months!! > 1) item.min_purchase_months!! else 1,
//                  item.overall_discount_percent,
//                  if (item.primary_image != null) item.primary_image!!.url else null,
//                  Gson().toJson(item.included_features),
//                  item.target_business_usecase,
//                  Gson().toJson(item.exclusive_to_categories),item.desc
//                ))
//              }
//              Completable.fromAction {
//                AppDatabase.getInstance(application)!!
//                  .bundlesDao()
//                  .insertAllBundles(bundles)
//              }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete {
//                  Log.i("insertAllBundles", "Successfully")
//                }
//                .doOnError {
//                  Log.i("insertAllBundles", "Failed")
//                }
//                .subscribe()
//
//              //saving coupons in DB
//              if (it.Data[0].discount_coupons != null && it.Data[0].discount_coupons.size > 0) {
//                val coupons = arrayListOf<CouponsModel>()
//                for (singleCoupon in it.Data[0].discount_coupons) {
//                  coupons.add(CouponsModel(
//                    singleCoupon.code,
//                    singleCoupon.discount_percent
//                  ))
//                }
//                Completable.fromAction {
//                  AppDatabase.getInstance(application)!!
//                    .couponsDao()
//                    .insertAllCoupons(coupons)
//                }
//                  .subscribeOn(Schedulers.io())
//                  .observeOn(AndroidSchedulers.mainThread())
//                  .doOnComplete {
//                    Log.i("insertAllCoupons", "Successfully")
//                  }
//                  .doOnError {
//                    Log.i("insertAllCoupons", "Failed")
//                  }
//                  .subscribe()
//              }
//
//              //saving videoGallery
//              if (it.Data[0].video_gallery != null && it.Data[0].video_gallery.size > 0) {
//                val videoGallery = arrayListOf<YoutubeVideoModel>()
//                for (singleVideoDetails in it.Data[0].video_gallery) {
//                  videoGallery.add(YoutubeVideoModel(
//                    singleVideoDetails._kid,
//                    singleVideoDetails.desc,
//                    singleVideoDetails.duration,
//                    singleVideoDetails.title,
//                    singleVideoDetails.youtube_link
//                  ))
//                }
//
//                Completable.fromAction {
//                  AppDatabase.getInstance(application)!!
//                    .youtubeVideoDao()
//                    .insertAllYoutubeVideos(videoGallery)
//                }
//                  .subscribeOn(Schedulers.io())
//                  .observeOn(AndroidSchedulers.mainThread())
//                  .doOnComplete {
//                    Log.i("insertAllYoutubeVideos", "Successfully")
//                  }
//                  .doOnError {
//                    Log.i("insertAllYoutubeVideos", "Failed")
//                  }
//                  .subscribe()
//              }
//
//
//              //promobanner
//              if (it.Data[0].promo_banners != null && it.Data[0].promo_banners.size > 0) {
//
//
//                //marketplace offers
//                if (it.Data[0].marketplace_offers != null && it.Data[0].marketplace_offers.size > 0) {
//                  val marketplaceOffersFilter = (it.Data[0].marketplace_offers
//                    ?: ArrayList()).promoMarketOfferFilter(expCode, fpTag)
//                  var marketOfferData = arrayListOf<MarketOfferModel>()
//                  for (item in marketplaceOffersFilter) {
//                    marketOfferData.add(MarketOfferModel(
//                      item.coupon_code,
//                      item.extra_information,
//                      item.createdon,
//                      item.updatedon,
//                      item._kid,
//                      item.websiteid,
//                      item.isarchived,
//                      item.expiry_date,
//                      item.title,
//                      if (item.exclusive_to_categories != null && item.exclusive_to_categories.size > 0) Gson().toJson(item.exclusive_to_categories) else null,
//                      if (item.image != null) item.image!!.url else null,
//                      if (item.cta_offer_identifier != null) item.cta_offer_identifier else null,
//                    ))
//                  }
//                  if(marketOfferData.size == 0){
//                    Completable.fromAction {
//                      AppDatabase.getInstance(application)!!
//                        .marketOffersDao()
//                        .emptyMarketOffers()
//                    }
//                      .subscribeOn(Schedulers.io())
//                      .observeOn(AndroidSchedulers.mainThread())
//                      .doOnComplete {
//                        Log.i("deleteMarketOffers", "Successfully")
//
//                      }
//                      .doOnError {
//                        Log.i("deleteMarketOffers", "Failed")
//                      }
//                      .subscribe()
//                  }
//
//                  /*start check*/
//                  marketOfferData.forEach {
//                    val itemFeature = it
//                    val marketModel = MarketOfferModel(
//                      it.coupon_code,
//                      it.extra_information,
//                      it.createdon,
//                      it.updatedon,
//                      it._kid,
//                      it.websiteid,
//                      it.isarchived,
//                      it.expiry_date,
//                      it.title,
//                      if (it.exclusive_to_categories != null ) Gson().toJson(it.exclusive_to_categories) else null,
//                      if (it.image != null) it.image else null,
//                      if (it.cta_offer_identifier != null) it.cta_offer_identifier else null,
//                    )
//                    Log.d("itemFeature"," "+ itemFeature + " "+itemFeature.coupon_code)
//                    CompositeDisposable().add(
//                      AppDatabase.getInstance(application)!!
//                        .marketOffersDao()
//                        .checkOffersIDExist(it._kid!!)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                          if (it == 0) {
//                            Completable.fromAction {
//                              AppDatabase.getInstance(application)!!
//                                .marketOffersDao()
//                                .insertToMarketOffers(itemFeature)
//                            }
//                              .subscribeOn(Schedulers.io())
//                              .observeOn(AndroidSchedulers.mainThread())
//                              .doOnComplete {
//                                Log.d("insertMarketOffers", "Successfully")
//
//                              }
//                              .doOnError {
//                                Log.d("insertMarketOffers", "Failed")
//                              }
//                              .subscribe()
//                          }else{
//                            Completable.fromAction {
//                              AppDatabase.getInstance(application)!!
//                                .marketOffersDao()
//                                .updateAllMarketOffers(marketModel)
//                            }
//                              .subscribeOn(Schedulers.io())
//                              .observeOn(AndroidSchedulers.mainThread())
//                              .doOnComplete {
//                                Log.d("updateMarketOffers", "Successfully")
//
//                              }
//                              .doOnError {
//                                Log.d("updateMarketOffers", "Failed")
//                              }
//                              .subscribe()
//                          }
//                        }, {
//                          it.printStackTrace()
//                        })
//                    )
//                  }
//
//                }
//              }
//            },
//            {
//              Log.e("GetAllFeatures", "error" + it.message)
//            }
//          )
//      )
//    }
//  }

//  fun fpOnboardingUpdate(request: OnBoardingUpdateModel?): LiveData<BaseResponse> {
//    return KitWebActionRepository.fpOnboardingUpdate(request).toLiveData()
//  }

  fun getNotificationCount(clientId: String?, fpId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getNotificationCount(clientId, fpId).toLiveData()
  }

  fun getNavDashboardData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getNavDashboardData(context).toLiveData()
  }

  fun getSearchAnalytics(fpTag: String?, startDate: String?, endDate: String?): LiveData<BaseResponse> {
    return DevBoostKitRepository.getSearchAnalytics(fpTag, startDate, endDate).toLiveData()
  }

  fun getDrScoreUi(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getDrScoreUi(context).toLiveData()
  }

  fun getQuickActionData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getQuickActionData(context).toLiveData()
  }

  fun getBoostAddOnsTop(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostAddOnsTop(context).toLiveData()
  }

  fun getWelcomeDashboardData(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getWelcomeDashboardData(context).toLiveData()
  }

  fun getBoostVisitingMessage(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostVisitingMessage(context).toLiveData()
  }

  fun getBoostCustomerItem(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostCustomerItem(context).toLiveData()
  }

  fun getBoostWebsiteItem(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostWebsiteItem(context).toLiveData()
  }

  fun getSellerSummary(clientId: String?, sellerId: String?): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerSummary(clientId, sellerId).toLiveData()
  }

  fun getSellerSummaryV2_5(clientId: String?, sellerId: String?, request: SellerSummaryRequest?): LiveData<BaseResponse> {
    return InventoryOrderRepository.getSellerSummaryV2_5(clientId, sellerId, request).toLiveData()
  }

  fun getUserSummary(fpTag: String?, clientId: String?, fpIdParent: String?, scope: String?, startDate: String? = null, endDate: String? = null): LiveData<BaseResponse> {
    return ApiWithFloatRepository.getUserSummary(fpTag, clientId, fpIdParent, scope, startDate, endDate).toLiveData()
  }

  fun getSubscriberCount(fpTag: String?, clientId: String?, startDate: String?, endDate: String?): LiveData<BaseResponse> {
    return ApiWithFloatRepository.getSubscriberCount(fpTag, clientId, startDate, endDate).toLiveData()
  }

  fun getMapVisits(fpTag: String?, mapData: Map<String, String>?): LiveData<BaseResponse> {
    return ApiWithFloatRepository.getMapVisits(fpTag, mapData).toLiveData()
  }

  fun getUserCallSummary(
    clientId: String?, fpIdParent: String?, identifierType: String?, startDate: String? = null, endDate: String? = null
  ): LiveData<BaseResponse> {
    return ApiWithFloatRepository.getUserCallSummary(clientId, fpIdParent, identifierType, startDate, endDate).toLiveData()
  }

  fun putUploadSecondaryImage(request: UploadFileBusinessRequest): LiveData<BaseResponse> {
    return UploadImageRepository.putUploadSecondaryImage(request).toLiveData()
  }

  fun putUploadBusinessLogo(
    clientId: String?, fpId: String?, reqType: String?, reqId: String?, totalChunks: String?,
    currentChunkNumber: String?, file: RequestBody?
  ): LiveData<BaseResponse> {
    return WithFloatTwoRepositoryD.uploadBusinessLogo(clientId, fpId, reqType, reqId, totalChunks, currentChunkNumber, file).toLiveData()
  }

  fun getUpgradePremiumBanner(website_id: String? = "5e7a3cf46e0572000109a5b2"): LiveData<BaseResponse> {
    return DevBoostKitRepository.getUpgradePremiumBanner(website_id = website_id).toLiveData()
  }

  fun getUpgradeDashboardBanner(website_id: String? = "5fd88e1fb456eb000133ad31"): LiveData<BaseResponse> {
    return DevBoostKitRepository.getUpgradeDashboardBanner(website_id = website_id).toLiveData()
  }

  fun getDomainDetailsForFloatingPoint(fpTag: String?, map: Map<String, String>?): LiveData<BaseResponse> {
    return PluginFloatRepository.getDomainDetailsForFloatingPoint(fpTag, map).toLiveData()
  }

  fun getMerchantSummary(clientId: String?,fpTag: String?): LiveData<BaseResponse> {
   return WithFloatTwoRepository.getMerchantSummary(clientId,fpTag).toLiveData()
  }

  fun getUserProfileData(loginId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepositoryD.getUserProfileData(loginId).toLiveData()
  }

  fun disableBadgeNotification(request: DisableBadgeNotificationRequest): LiveData<BaseResponse> {
    return UsCentralNowFloatsCloudRepository.disableBadgeNotification(request).toLiveData()
  }
}


fun getJsonRequest(fpTag: String?): String {
  val jsonObject = JSONObject()
  jsonObject.put("WebsiteId", fpTag)
  return jsonObject.toString()
}