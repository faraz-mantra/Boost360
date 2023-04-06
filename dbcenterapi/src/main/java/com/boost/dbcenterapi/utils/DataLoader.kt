package com.boost.dbcenterapi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.promoMarketOfferFilter
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.framework.firebaseUtils.firestore.marketplaceCart.CartFirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.sendNotification
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object DataLoader {
    val NewApiService = Utils.getRetrofit(true).create(NewApiInterface::class.java)

    fun loadMarketPlaceData(
        application: Application,
        userSessionManager: UserSessionManager
    ) {
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
                                var applicableToClientId = false
                                if ((item.exclusive_to_clientids != null && item.exclusive_to_clientids!!.size > 0)) {
                                    for (singleClientId in item.exclusive_to_clientids!!) {
                                        if (singleClientId.equals(clientId, true)) {
                                            applicableToClientId = true
                                            break
                                        }
                                    }
                                    if (!applicableToClientId)
                                        continue
                                }
                                if ((item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) && !applicableToClientId) {
                                    var applicableToCurrentExpCode = false
                                    for (code in item.exclusive_to_categories!!) {
                                        if (code.equals(
                                                userSessionManager.fP_AppExperienceCode,
                                                true
                                            )
                                        )
                                            applicableToCurrentExpCode = true
                                    }
                                    if (!applicableToCurrentExpCode)
                                        continue
                                }

                                val primaryImage =
                                    if (item.primary_image == null) null else item.primary_image!!.url
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
                                        if (secondaryImages.size == 0) null else Gson().toJson(
                                            secondaryImages
                                        ),
                                        Gson().toJson(item.learn_more_link),
                                        if (item.total_installs == null) "--" else item.total_installs,
                                        if (item.extended_properties != null && item.extended_properties!!.size > 0) Gson().toJson(
                                            item.extended_properties
                                        ) else null,
                                        if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) Gson().toJson(
                                            item.exclusive_to_categories
                                        ) else null,
                                        item.widget_type,
                                        if (item.benefits != null && item.benefits!!.size > 0) Gson().toJson(
                                            item.benefits
                                        ) else null,
                                        if (item.all_testimonials != null && item.all_testimonials!!.size > 0) Gson().toJson(
                                            item.all_testimonials
                                        ) else null,
                                        if (item.all_frequently_asked_questions != null && item.all_frequently_asked_questions!!.size > 0) Gson().toJson(
                                            item.all_frequently_asked_questions
                                        ) else null,
                                        if (item.how_to_use_steps != null && item.how_to_use_steps!!.size > 0) Gson().toJson(
                                            item.how_to_use_steps
                                        ) else null,
                                        item.how_to_use_title
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
                                    checkExpiryAddonsPackages(
                                        application.applicationContext,
                                        userSessionManager,
                                        data
                                    )
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
                                        if (code.equals(userSessionManager.fpTag, true)) {
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
                                        if (code.equals(
                                                userSessionManager.fP_AppExperienceCode,
                                                true
                                            )
                                        ) {
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
                                            singleCoupon.discount_percent.toDouble()
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
                                            singleVideoDetails.youtube_link,
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
                                        ?: ArrayList()).promoMarketOfferFilter(
                                        userSessionManager.fP_AppExperienceCode,
                                        userSessionManager.fpTag
                                    )
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
                                            if (it.exclusive_to_categories != null) Gson().toJson(it.exclusive_to_categories) else null,
                                            if (it.image != null) it.image else null,
                                            if (it.cta_offer_identifier != null) it.cta_offer_identifier else null,
                                        )
                                        Log.d(
                                            "itemFeature",
                                            " " + itemFeature + " " + itemFeature.coupon_code
                                        )
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
                                                                Log.d(
                                                                    "insertMarketOffers",
                                                                    "Successfully"
                                                                )

                                                            }
                                                            .doOnError {
                                                                Log.d(
                                                                    "insertMarketOffers",
                                                                    "Failed"
                                                                )
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
                                                                Log.d(
                                                                    "updateMarketOffers",
                                                                    "Successfully"
                                                                )

                                                            }
                                                            .doOnError {
                                                                Log.d(
                                                                    "updateMarketOffers",
                                                                    "Failed"
                                                                )
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
                            if (it.Data[0].expert_connect.is_online) {
                                val prefs = SharedPrefs(Activity())
                                prefs.storeExpertContact(it.Data[0].expert_connect.contact_number)
                            }
                        },
                        {
                            Log.e("GetAllFeatures", "error" + it.message)
                        }
                    )
            )
        }
    }

    fun addItemtoCart(application: Application, itemsId: String) {
        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getFeaturesItemByFeatureCode(itemsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val discount = 100 - it.discount_percent
                        val paymentPrice = (discount * it.price) / 100.0
                        val cartItem = CartModel(
                            it.feature_id,
                            it.boost_widget_key,
                            it.feature_code,
                            it.name,
                            it.description,
                            it.primary_image,
                            paymentPrice,
                            it.price.toDouble(),
                            it.discount_percent,
                            1,
                            1,
                            "features",
                            it.extended_properties
                        )

                        CompositeDisposable().add(
                            AppDatabase.getInstance(application)!!
                                .cartDao()
                                .checkCartFeatureTableKeyExist()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it == 1) {
                                        Completable.fromAction {
                                            AppDatabase.getInstance(application)!!.cartDao()
                                                .emptyCart()
                                        }
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnError {
                                                //in case of error
                                            }
                                            .doOnComplete {
                                                Completable.fromAction {
                                                    AppDatabase.getInstance(application)!!.cartDao()
                                                        .insertToCart(cartItem)
                                                }
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .doOnComplete {
                                                        Log.d("addItemtoCart", "Success")
                                                    }
                                                    .doOnError {
                                                        Log.e("addItemtoCart", "Error", it)
                                                    }.subscribe()
                                            }
                                            .subscribe()
                                    } else {
                                        Completable.fromAction {
                                            AppDatabase.getInstance(application)!!.cartDao()
                                                .insertToCart(cartItem)
                                        }
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnComplete {
                                                Log.d("addItemtoCart", "Success")
                                            }
                                            .doOnError {
                                                Log.e("addItemtoCart", "Error", it)
                                            }.subscribe()
                                    }
                                }, {
                                    Toasty.error(
                                        application.applicationContext,
                                        "Something went wrong. Try Later..",
                                        Toast.LENGTH_LONG
                                    ).show()
                                })
                        )
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun addAllItemstoFirebaseCart(application: Application, itemsId: List<String>) {
        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getallFeaturesInList(itemsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val cartItemList = ArrayList<CartModel>()
                        for (singleItem in it) {
                            val discount = 100 - singleItem.discount_percent
                            val paymentPrice = (discount * singleItem.price) / 100.0
                            val cartItem = CartModel(
                                singleItem.feature_id,
                                singleItem.boost_widget_key,
                                singleItem.feature_code,
                                singleItem.name,
                                singleItem.description,
                                singleItem.primary_image,
                                paymentPrice,
                                singleItem.price.toDouble(),
                                singleItem.discount_percent,
                                1,
                                1,
                                "features",
                                singleItem.extended_properties
                            )
                            cartItemList.add(cartItem)
                        }
                        Completable.fromAction {
                            AppDatabase.getInstance(application)!!.cartDao()
                                .insertAllUPdates(cartItemList)
                        }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete {
                                Log.d("addAllItemstoCart", "Success")
                                //disable items
//                                updateCartItemsToFirestore(application)
                            }
                            .doOnError {
                                it.printStackTrace()
                            }
                            .subscribe()
                    }, {
                        it.printStackTrace()
                    })
        )
    }

    @SuppressLint("LongLogTag")
    private fun checkExpiryAddonsPackages(
        applicationContext: Context,
        userSessionManager: UserSessionManager,
        data: ArrayList<FeaturesModel>
    ) {
        CompositeDisposable().add(
            NewApiService.GetFeatureDetails(userSessionManager.fPID?:"", clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("checkExpiryAddonsPackages >>", Gson().toJson(it))
                    val paidList = data.filter { it.price > 0 }.map { it.feature_code }
                    for (singleitem in it!!) {
                        if (paidList.contains(singleitem.featureKey)) {
                            val date1: Date =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(singleitem.expiryDate!!)

                            val diff: Long = date1.getTime() - Date().getTime()
                            val seconds = diff / 1000
                            val minutes = seconds / 60
                            val hours = minutes / 60
                            val days = hours / 24

                            if (days.toInt() > 7 && days.toInt() < 6) {
                                sendNotification(
                                    applicationContext,
                                    "Renewal Reminder!",
                                    "Your add-ons are expiring in 7 days. Few of the features will stop working after expiry.",
                                    "http://boost.nowfloats.com?viewType=CART_FRAGMENT&buyItemKey=${singleitem.featureKey}"
                                )
                            } else if (days.toInt() > 3 && days.toInt() < 2) {
                                sendNotification(
                                    applicationContext,
                                    "Alert! Your Add-ons are expiring in 3 days!",
                                    "Renew them to ensure you don’t miss out on key features.",
                                    "http://boost.nowfloats.com?viewType=CART_FRAGMENT&buyItemKey=${singleitem.featureKey}"
                                )
                            } else if (days.toInt() > 1 && days.toInt() < 0) {
                                sendNotification(
                                    applicationContext,
                                    "Last Day For Renewal!",
                                    "Your add-ons are expiring in the next 24 hours! Renew them now.",
                                    "http://boost.nowfloats.com?viewType=CART_FRAGMENT&buyItemKey=${singleitem.featureKey}"
                                )
                            } else if (days.toInt() <= 0) {
                                sendNotification(
                                    applicationContext,
                                    "Your Add-ons have expired! :(",
                                    "But you can still renew them to keep enjoying the features.",
                                    "http://boost.nowfloats.com?viewType=CART_FRAGMENT&buyItemKey=${singleitem.featureKey}"
                                )
                            }
                        }
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }


//    @SuppressLint("LongLogTag")
//    fun updateCartItemsToFirestore(application: Application) {
//        CompositeDisposable().add(
//            AppDatabase.getInstance(application)!!
//                .cartDao()
//                .getCartItems()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSuccess {
//                    Log.d("updateCartItemsToFirestore", "Success")
//                    val map: MutableMap<String, Any> = HashMap<String, Any>()
//                    for (i in it) {
//                        map[i.item_id] = i
//                    }
//                    if (map.size > 0) {
//                        CartFirestoreManager.updateDocument(map as HashMap<String, Any>)
//                    }
//                }
//                .doOnError {
//                    Log.e("updateCartItemsToFirestore", "Error", it)
//                }
//                .subscribe()
//        )
//    }
}