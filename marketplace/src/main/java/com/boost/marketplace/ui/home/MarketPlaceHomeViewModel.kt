package com.boost.marketplace.ui.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.dbcenterapi.data.remote.NewApiInterface
import com.boost.dbcenterapi.data.rest.repository.MarketplaceNewRepository
import com.boost.dbcenterapi.data.rest.repository.MarketplaceRepository
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.dbcenterapi.utils.Utils
import com.boost.dbcenterapi.utils.Utils.getBundlesFromJsonFile
import com.framework.analytics.SentryController
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MarketPlaceHomeViewModel() : BaseViewModel() {

    var allAvailableFeaturesDownloadResult: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var updatesLoader: MutableLiveData<Boolean> = MutableLiveData()
    var itemAddedToCartAndGoToCart: MutableLiveData<Boolean> = MutableLiveData()
    var updatesError: MutableLiveData<String> = MutableLiveData()

    //    var _totalActiveAddonsCount: MutableLiveData<Int> = MutableLiveData()
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
    var categoryResult: MutableLiveData<String> = MutableLiveData()
    var updatesResult: MutableLiveData<List<WidgetModel>> = MutableLiveData()
    var allBackBundleResult: MutableLiveData<List<BundlesModel>> = MutableLiveData()
    var bundleExistsBool: MutableLiveData<Boolean> = MutableLiveData()
    var activePremiumWidgetList: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var activePremiumWidgetList1: MutableLiveData<List<FeaturesModel>> = MutableLiveData()
    var NewApiService =
        com.boost.cart.utils.Utils.getRetrofit(true).create(NewApiInterface::class.java)
    var ApiService = Utils.getRetrofit().create(NewApiInterface::class.java)


    var experienceCode: String = "SVC"
    var _fpTag: String = "ABC"

    lateinit var application: Application
    lateinit var lifecycleOwner: LifecycleOwner

    fun setApplicationLifecycle(
        application: Application,
        lifecycleOwner: LifecycleOwner
    ) {
        this.application = application
        this.lifecycleOwner = lifecycleOwner
    }

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

//    fun getTotalActiveWidgetCount(): LiveData<Int> {
//        return _totalActiveAddonsCount
//    }

    fun cartResult(): LiveData<List<CartModel>> {
        return cartResult
    }

    fun cartResultBack(): LiveData<List<CartModel>> {
        return cartResultBack
    }

    fun updatesLoader(): LiveData<Boolean> {
        return updatesLoader
    }

    fun itemAddedToCartAndGoToCart(): LiveData<Boolean> {
        return itemAddedToCartAndGoToCart
    }

    fun categoryResult(): LiveData<String> {
        return categoryResult
    }

    fun upgradeResult(): LiveData<List<WidgetModel>> {
        return updatesResult
    }

    fun getBackAllBundles(): LiveData<List<BundlesModel>> {
        return allBackBundleResult
    }

    fun updatesError(): LiveData<String> {
        return updatesError
    }

    fun getBundleExxists(): LiveData<Boolean> {
        return bundleExistsBool
    }

    fun getActivePremiumWidgets(): LiveData<List<FeaturesModel>> {
        return activePremiumWidgetList
    }

    fun getActivePremiumWidgets1(): LiveData<List<FeaturesModel>> {
        return activePremiumWidgetList1
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

    fun loadUpdates(
        auth: String,
        fpid: String,
        clientId: String,
        expCode: String?,
        fpTag: String?
    ) {
        Log.v("loadUpdates ", " " + expCode + " " + fpTag)
        updatesLoader.postValue(true)

        if (Utils.isConnectedToInternet(application)) {
            MarketplaceNewRepository.getAllFeatures().toLiveData()
                .observeOnce(lifecycleOwner, Observer {
                    if (it.isSuccess()) {
                        var response = it as? GetAllFeaturesResponse
                        Log.e("GetAllFeatures", response.toString())
                        var data = arrayListOf<FeaturesModel>()
                        for (item in response!!.Data[0].features) {
                            if (item.exclusive_to_categories != null && item.exclusive_to_categories!!.size > 0) {
                                var applicableToCurrentExpCode = false
                                for (code in item.exclusive_to_categories!!) {
                                    if (code.equals(experienceCode, true))
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
                                            MarketplaceRepository.GetFloatingPointWebWidgets(
                                                fpid,
                                                clientId
                                            ).toLiveData()
                                                .observe(lifecycleOwner, Observer {
                                                    val response1 =
                                                        it as? GetFloatingPointWebWidgetsResponse
//                                                    CompositeDisposable().add(
//                                                        AppDatabase.getInstance(application)!!
//                                                            .featuresDao()
//                                                            .getallActivefeatureCount(response1!!.Result)
//                                                            .subscribeOn(Schedulers.io())
//                                                            .observeOn(AndroidSchedulers.mainThread())
//                                                            .doOnSuccess {
//                                                                _totalActiveAddonsCount.postValue(
//                                                                    it
//                                                                )
//                                                            }
//                                                            .doOnError {
//                                                                updatesError.postValue(it.message)
//                                                            }
//                                                            .subscribe()
//                                                    )
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
                        val tempBundles = getBundlesFromJsonFile(application.applicationContext)
                        for (item in response.Data[0].bundles) {
                            //  for (item in tempBundles) {
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
                                    Gson().toJson(item.exclusive_to_categories),
                                    if (item.frequently_asked_questions != null && item.frequently_asked_questions!!.isNotEmpty()) Gson().toJson(
                                        item.frequently_asked_questions
                                    ) else null,
                                    if (item.how_to_activate != null && item.how_to_activate!!.isNotEmpty()) Gson().toJson(
                                        item.how_to_activate
                                    ) else null,
                                    if (item.testimonials != null && item.testimonials!!.isNotEmpty()) Gson().toJson(
                                        item.testimonials
                                    ) else null,
                                    if (item.benefits != null && item.benefits!!.isNotEmpty()) Gson().toJson(
                                        item.benefits
                                    ) else null,
                                    item.desc,
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

                        //   saving videoGallery
//                        if (response.Data[0].video_gallery != null && response.Data[0].video_gallery.size > 0) {
//                            val videoGallery = arrayListOf<YoutubeVideoModel>()
//                            for (singleVideoDetails in response.Data[0].video_gallery) {
//                                videoGallery.add(
//                                    YoutubeVideoModel(
//                                        singleVideoDetails._kid,
//                                        singleVideoDetails.desc,
//                                        singleVideoDetails.duration,
//                                        singleVideoDetails.title,
//                                        singleVideoDetails.youtube_link
//                                    )
//                                )
//                            }
//
//                            Completable.fromAction {
//                                AppDatabase.getInstance(application)!!
//                                    .youtubeVideoDao()
//                                    .insertAllYoutubeVideos(videoGallery)
//                            }
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .doOnComplete {
//                                    Log.i("insertAllYoutubeVideos", "Successfully")
//                                    allVideoDetails.postValue(videoGallery)
//                                    updatesLoader.postValue(false)
//                                }
//                                .doOnError {
//                                    Log.i("insertAllYoutubeVideos", "Successfully")
//                                    updatesError.postValue(it.message)
//                                    updatesLoader.postValue(false)
//                                }
//                                .subscribe()
//                        }

                        //get ExpertConnect Details
                        expertConnectDetails.postValue(response.Data[0].expert_connect)

                        //promobanner
                        if (response.Data[0].promo_banners != null && response.Data[0].promo_banners.size > 0) {
//                                            promoBanners.value = it.Data[0].promo_banners.filter {  it1 -> it1.exclusive_to_categories.toString() == expCode }
                            val promoBannerFilter = (response.Data[0].promo_banners
                                ?: ArrayList()).promoBannerFilter(expCode, fpTag)
                            promoBanners.postValue(promoBannerFilter)

                            promoList.addAll(promoBannerFilter)
                            //marketplace offers
                            if (response.Data[0].marketplace_offers != null && response.Data[0].marketplace_offers.size > 0) {
//                                            marketplaceOffers.value = it.Data[0].marketplace_offers
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
                                                            Log.d(
                                                                "updateMarketOffers",
                                                                "Successfully"
                                                            )

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

                                /*end check*/

                                /*Completable.fromAction {
                                AppDatabase.getInstance(application)!!
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
                                    .subscribe()*/

                            } else {
                                promoBannersList.postValue(promoList)
                            }

                        } else {
                            //empty list
                            promoBanners.postValue(arrayListOf())
                        }

                        //partnerZone
                        if (response.Data[0].partner_zone != null && response.Data[0].partner_zone.size > 0) {
                            partnerZone.postValue(response.Data[0].partner_zone)
                        }

                        //feedbackURL
                        if (response.Data[0].feedback_link != null && response.Data[0].feedback_link.isNotEmpty()) {
                            feedbackLink.postValue(response.Data[0].feedback_link)
                        }
                    }
                }
                )
        } else {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
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

        if (Utils.isConnectedToInternet(application)) {
            MarketplaceNewRepository.getAllFeatures().toLiveData()
                .observe(lifecycleOwner, Observer {
                    Log.e("GetAllFeatures", it.toString())
                    var it = it as GetAllFeaturesResponse

                    //saving bundle info in bundle table
                    val bundles = arrayListOf<BundlesModel>()
                    for (item in it.Data[0].bundles) {
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
                                Gson().toJson(item.exclusive_to_categories),
                                if (item.frequently_asked_questions != null && item.frequently_asked_questions!!.isNotEmpty()) Gson().toJson(
                                    item.frequently_asked_questions
                                ) else null,
                                if (item.how_to_activate != null && item.how_to_activate!!.isNotEmpty()) Gson().toJson(
                                    item.how_to_activate
                                ) else null,
                                if (item.testimonials != null && item.testimonials!!.isNotEmpty()) Gson().toJson(
                                    item.testimonials
                                ) else null,
                                if (item.benefits != null && item.benefits!!.isNotEmpty()) Gson().toJson(
                                    item.benefits
                                ) else null,
                                item.desc,
                            )
                        )
                    }
                    Completable.fromAction {
//                                            AppDatabase.getInstance(application)!!
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


                }
                )
        } else {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
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
        try {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
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
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    fun getCartItemsBack() {
        try {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
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
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
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
            updatesModel.extended_properties,
            updatesModel.widget_type
        )


        Completable.fromAction {
            AppDatabase.getInstance(application)!!.cartDao()
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
            updatesModel.extended_properties,
            updatesModel.widget_type
        )

        try {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
                    .cartDao()
                    .checkCartFeatureTableKeyExist()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            Completable.fromAction {
                                AppDatabase.getInstance(application)!!.cartDao().emptyCart()
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
                                AppDatabase.getInstance(application)!!.cartDao()
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
        } catch (e: Exception) {
            SentryController.captureException(e)
        }


    }

    fun addItemAndGoTOCart(updatesModel: FeaturesModel, minMonth: Int) {
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
            updatesModel.extended_properties,
            updatesModel.widget_type
        )

        try {
            CompositeDisposable().add(
                Completable.fromAction {
                    AppDatabase.getInstance(application)!!.cartDao()
                        .insertToCart(cartItem)
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        itemAddedToCartAndGoToCart.postValue(true)
                        updatesLoader.postValue(false)
                    }
                    .doOnError {
                        updatesError.postValue(it.message)
                        updatesLoader.postValue(false)
                    }
                    .subscribe())
    } catch (e: Exception)
    {
        SentryController.captureException(e)
    }


}

fun addItemToCartPackage1(cartItem: CartModel) {
    updatesLoader.postValue(false)
    Completable.fromAction {
        AppDatabase.getInstance(application)!!.cartDao()
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
    Log.v(
        "addItemToCartPackage",
        " " + cartItem.boost_widget_key + " " + cartItem.boost_widget_key
    )
    updatesLoader.postValue(false)


    Completable.fromAction {
        AppDatabase.getInstance(application)!!.cartDao().emptyCart()
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

fun checkBundlePackExists(kid: String) {
    CompositeDisposable().add(
        AppDatabase.getInstance(application)!!
            .cartDao()
            .checkCartBundleExist(kid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it == 0) {
                    bundleExistsBool.postValue(false)
                } else {
                    bundleExistsBool.postValue(true)
                }
            }, {
//                            Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG).show()
            })
    )
}

fun emptyCouponTable() {
    Completable.fromAction {
        AppDatabase.getInstance(application)!!
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

fun GetHelp() {
    if (Utils.isConnectedToInternet(application)) {
        CompositeDisposable().add(
            NewApiService.GetHelp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.Data[0].marketplacecustomervideos != null && it.Data[0].marketplacecustomervideos.size > 0) {
                            val videoGallery = arrayListOf<YoutubeVideoModel>()
                            for (singleVideoDetails in it.Data[0].marketplacecustomervideos) {
                                videoGallery.add(
                                    YoutubeVideoModel(
                                        singleVideoDetails._kid,
                                        singleVideoDetails.videodescription,
                                        singleVideoDetails.videodurationseconds.toString(),
                                        singleVideoDetails.videotitle,
                                        singleVideoDetails.videourl.url,
                                        singleVideoDetails.thumbnailimage.url
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
                                    Log.i("insertAllYoutubeVideos", "failed")
                                    updatesError.postValue(it.message)
                                    updatesLoader.postValue(false)
                                }
                                .subscribe()
                        }
                    },
                    {
                        Log.e("GetAllVideos", "error" + it.message)
                        updatesLoader.postValue(false)
                    })
        )
    } else {
        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .youtubeVideoDao()
                .getYoutubeVideoItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    allVideoDetails.postValue(it)
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

//for enabling dark mode based on expired addons
fun loadPurchasedItems1(fpid: String, clientId: String) {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
        ApiService.GetFeatureDetails(fpid, clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it1 ->
                    val list = ArrayList<String>()
                    for (singleItem in it1) {
                        list.add(singleItem.featureCode)
                    }
                    CompositeDisposable().add(
                        AppDatabase.getInstance(application)!!
                            .featuresDao()
                            .getallActiveFeatures1(list)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess { it2 ->
                                val listFeaturesModel = it2.map { it3 ->
                                    it1.firstOrNull { it.featureCode.equals(it3.feature_code) }
                                        .apply {
                                            it3.expiryDate = this?.expiryDate
                                            it3.activatedDate = this?.activatedDate
                                            it3.featureState = this?.featureState
                                        };it3
                                }
                                Completable.fromAction {
                                    AppDatabase.getInstance(application)!!
                                        .featuresDao()
                                        .insertAllFeatures(listFeaturesModel)
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnComplete {
                                        Log.i("insertAllFeatures", "Successfully")
                                        activePremiumWidgetList1.postValue(listFeaturesModel)
                                        updatesLoader.postValue(false)

                                    }.doOnError {
                                        updatesError.postValue(it.message)
                                        updatesLoader.postValue(false)
                                    }
                                    .subscribe()

                            }
                            .doOnError {
                                updatesError.postValue(it.message)
                                updatesLoader.postValue(false)
                            }
                            .subscribe()
                    )
                }, {
                    updatesLoader.postValue(false)
                    updatesError.postValue(it.message)
                })
    )
}

//for displaying referall section based on paid user.
fun loadPurchasedItems(fpid: String, clientId: String) {
    updatesLoader.postValue(true)
    CompositeDisposable().add(
        NewApiService.GetFeatureDetails(fpid, clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { it1 ->
                    val list = ArrayList<String>()
                    for (singleItem in it1) {
                        list.add(singleItem.featureCode)
                    }
                    CompositeDisposable().add(
                        AppDatabase.getInstance(application)!!
                            .featuresDao()
                            .getallActiveFeatures(list, true)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess { it2 ->
                                val listFeaturesModel = it2.map { it3 ->
                                    it1.firstOrNull { it.featureCode.equals(it3.feature_code) }
                                        .apply {
                                            it3.expiryDate = this?.expiryDate
                                            it3.activatedDate = this?.activatedDate
                                            it3.featureState = this?.featureState
                                        };it3
                                }
                                activePremiumWidgetList.postValue(listFeaturesModel)
                                updatesLoader.postValue(false)
                            }
                            .doOnError {
                                updatesError.postValue(it.message)
                                updatesLoader.postValue(false)
                            }
                            .subscribe()
                    )
                }, {
                    updatesLoader.postValue(false)
                    updatesError.postValue(it.message)
                })
    )
}
}