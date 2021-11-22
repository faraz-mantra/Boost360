package com.boost.dbcenterapi.utils

import android.app.Application
import android.util.Log
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.promoMarketOfferFilter
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

object DataLoader {

    fun loadMarketPlaceData(application: Application, expCode: String?, fpTag: String?) {
        val NewApiService = Utils.getRetrofit(true).create(NewApiInterface::class.java)

        if (Utils.isConnectedToInternet(application)) {
            CompositeDisposable().add(
                NewApiService.GetAllFeatures()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Log.e("GetAllFeatures", it.toString())
                            var data = arrayListOf<FeaturesModel>()
                            for (item in it.Data[0].features) {
                                if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) {
                                    var applicableToCurrentExpCode = false
                                    for (code in item.exclusive_to_categories!!) {
                                        if (code.equals(expCode, true))
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
                                    Log.e("insertAllFeatures", "Successfully")
                                }
                                .doOnError {
                                    Log.e("insertAllFeatures", "Failed")
                                }
                                .subscribe()

                            //saving bundle info in bundle table
                            val bundles = arrayListOf<BundlesModel>()
                            for (item in it.Data[0].bundles) {
                                if (item.exclusive_for_customers != null && item.exclusive_for_customers!!.size > 0) {
                                    var applicableToCurrentFPTag = false
                                    for (code in item.exclusive_for_customers!!) {
                                        if (code.equals(fpTag, true)) {
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
                                        if (code.equals(expCode, true)) {
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
                                    Gson().toJson(item.exclusive_to_categories),item.desc
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
                                    Log.e("insertAllBundles", "Successfully")
                                }
                                .doOnError {
                                    Log.e("insertAllBundles", "Failed")
                                }
                                .subscribe()

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
                                    AppDatabase.getInstance(application)!!
                                        .couponsDao()
                                        .insertAllCoupons(coupons)
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnComplete {
                                        Log.e("insertAllCoupons", "Successfully")
                                    }
                                    .doOnError {
                                        Log.e("insertAllCoupons", "Failed")
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
                                    AppDatabase.getInstance(application)!!
                                        .youtubeVideoDao()
                                        .insertAllYoutubeVideos(videoGallery)
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnComplete {
                                        Log.e("insertAllYoutubeVideos", "Successfully")
                                    }
                                    .doOnError {
                                        Log.e("insertAllYoutubeVideos", "Failed")
                                    }
                                    .subscribe()
                            }


                            //promobanner
                            if (it.Data[0].promo_banners != null && it.Data[0].promo_banners.size > 0) {


                                //marketplace offers
                                if (it.Data[0].marketplace_offers != null && it.Data[0].marketplace_offers.size > 0) {
                                    val marketplaceOffersFilter = (it.Data[0].marketplace_offers
                                        ?: ArrayList()).promoMarketOfferFilter(expCode, fpTag)
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
                                    if(marketOfferData.size == 0){
                                        Completable.fromAction {
                                            AppDatabase.getInstance(application)!!
                                                .marketOffersDao()
                                                .emptyMarketOffers()
                                        }
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnComplete {
                                                Log.e("deleteMarketOffers", "Successfully")

                                            }
                                            .doOnError {
                                                Log.e("deleteMarketOffers", "Failed")
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
                                            if (it.exclusive_to_categories != null ) Gson().toJson(it.exclusive_to_categories) else null,
                                            if (it.image != null) it.image else null,
                                            if (it.cta_offer_identifier != null) it.cta_offer_identifier else null,
                                        )
                                        Log.d("itemFeature"," "+ itemFeature + " "+itemFeature.coupon_code)
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
                                                                Log.d("insertMarketOffers", "Failed")
                                                            }
                                                            .subscribe()
                                                    }else{
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
                                                                Log.d("updateMarketOffers", "Failed")
                                                            }
                                                            .subscribe()
                                                    }
                                                }, {
                                                    it.printStackTrace()
                                                })
                                        )
                                    }

                                }
                            }
                        },
                        {
                            Log.e("GetAllFeatures", "error" + it.message)
                        }
                    )
            )
        }
    }
}