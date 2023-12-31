package com.boost.marketplace.ui.pack_details

import android.animation.Animator
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.adapter.BenifitsPageTransformer
import com.boost.cart.adapter.ZoomOutPageTransformer
import com.boost.cart.utils.Constants
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.data.api_model.mycurrentPlanV3.MyPlanV3
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.CircleAnimationUtil
import com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.Utils.isExpertAvailable
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.R
import com.boost.marketplace.adapter.*
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityPackDetailsBinding
import com.boost.marketplace.interfaces.*
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.boost.marketplace.ui.comparePacksV3.ComparePacksV3Activity
import com.boost.marketplace.ui.feature_details_popup.FeatureDetailsPopup
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.boost.marketplace.ui.popup.call_track.RequestCallbackBottomSheet
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.utils.RootUtil
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pack_details.*
import kotlinx.android.synthetic.main.layout_black_for_savings.*
import java.text.NumberFormat
import java.util.*

class PackDetailsActivity : AppBaseActivity<ActivityPackDetailsBinding, ComparePacksViewModel>(),
    PackDetailsListener,
    DetailsFragmentListener,
    CompareListener, AddonsListener, MarketPlacePopupListener {

    private var purchasedDomainType: String? = null
    private var purchasedDomainName: String? = null
    private var purchasedVmnName: String? = null
    private var purchasedVmnActive: Boolean? = null
    private var featuresModel: List<FeaturesModel>? = null
    lateinit var singleAddon: FeaturesModel
    private lateinit var needMoreFeatureItem: BundlesModel
    private var includedFeaturesInPack: List<FeaturesModel>? = null
    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var itemInCart = false
    var needMoreFeatureItemInCart = false
    var fpid: String? = null
    var fpTag: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var userPurchsedWidgets = ArrayList<String>()
    private var widgetFeatureCode: String? = null
    var isOpenHomeFragment: Boolean = false
    var isOpenAddOnsFragment: Boolean = false
    var refreshViewPager: Boolean = false

    val callTrackingHelpBottomSheet = CallTrackingHelpBottomSheet()
    val requestCallbackBottomSheet = RequestCallbackBottomSheet()

    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var badgeNumber = 0
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    var featureCount = 0
    var cartCount = 0
    var allowPackageToCart = true
    var myPlanV3: MyPlanV3? = null

    val sameAddonsInCart = ArrayList<String>()
    val addonsListInCart = ArrayList<String>()

    var packageInCartStatus = false
    lateinit var prefs: SharedPrefs
    var featuresHashMap: MutableMap<String?, FeaturesModel> = HashMap<String?, FeaturesModel>()
    var upgradeList = arrayListOf<Bundles>()
    lateinit var progressDialog: ProgressDialog
    lateinit var howToUseAdapter: HowToActivateAdapter
    lateinit var faqAdapter: PackDetailsFaqAdapter
    lateinit var benefitAdaptor: PackDetailsBenefitViewPagerAdapter
    lateinit var reviewAdaptor: TestimonialItemsAdapter
    lateinit var includedFeatureAdapter: IncludedFeatureAdapter
    lateinit var needMoreFeatureAdapter: NeedMoreFeatureAdapter
    lateinit var packDetailsAdapter: PackDetailsFeatureAdapter

    companion object {
        fun newInstance() = PackDetailsActivity()
    }

    override fun getLayout(): Int {
        return R.layout.activity_pack_details
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        progressDialog = ProgressDialog(this)

        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7

        experienceCode = intent.getStringExtra("expCode")
        screenType = intent.getStringExtra("screenType")
        fpName = intent.getStringExtra("fpName")
        fpid = intent.getStringExtra("fpid")
        fpTag = intent.getStringExtra("fpTag")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        isOpenHomeFragment = intent.getBooleanExtra("isComingFromOrderConfirm", false)
        isOpenAddOnsFragment = intent.getBooleanExtra("isComingFromOrderConfirmActivation", false)
        widgetFeatureCode = intent.getStringExtra("buyItemKey")
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        bundleData = Gson().fromJson<Bundles>(
            intent.getStringExtra("bundleData"),
            object : TypeToken<Bundles>() {}.type
        )

        reviewAdaptor = TestimonialItemsAdapter(ArrayList())
        howToUseAdapter = HowToActivateAdapter(this, ArrayList())
        faqAdapter = PackDetailsFaqAdapter(this, ArrayList())
        benefitAdaptor = PackDetailsBenefitViewPagerAdapter(ArrayList())
        includedFeatureAdapter = IncludedFeatureAdapter(this, ArrayList())
        needMoreFeatureAdapter = NeedMoreFeatureAdapter(this, ArrayList())
        packDetailsAdapter = PackDetailsFeatureAdapter(this, ArrayList(), this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_COMPARE_PACKAGE_LOADED,
            PAGE_VIEW,
            NO_EVENT_VALUE
        )
        prefs = SharedPrefs(this)

        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(com.boost.cart.R.color.common_text_color)
        }

        initializeViewPager()
        initializeCustomerViewPager()
        initializeHowToUseRecycler()
        initializeFAQRecycler()
        initializeIncludedFeature()
        initializeFeatureAdapter()
        initializeNeedMoreAdapter()
        initializePackItemRecycler()
        initView()
        initMvvm()
//        loadData() //removed this due to multiple API call


        val callExpertString = SpannableString("Have a query? Call an expert")

        callExpertString.setSpan(
            UnderlineSpan(),
            callExpertString.length - 14,
            callExpertString.length,
            0
        )
        callExpertString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.started_button_start)),
            callExpertString.length - 14,
            callExpertString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding?.queryText?.text = callExpertString
        query_text.setOnClickListener {
            if (isExpertAvailable()) {
                callTrackingHelpBottomSheet.show(
                    supportFragmentManager,
                    CallTrackingHelpBottomSheet::class.java.name
                )
            } else {
                requestCallbackBottomSheet.show(
                    supportFragmentManager,
                    RequestCallbackBottomSheet::class.java.name
                )
            }
        }

        //Add to cart..
        binding?.bottomBoxOnlyBtn?.setOnClickListener {
            //disabling marketplace gaps
//            getAllowPackageToCart(bundleData!!)
//            if(!allowPackageToCart){
//                    val arg = Bundle()
//                    arg.putBoolean("allowPackageToCart", allowPackageToCart)
//                    callTrackingHelpBottomSheet.arguments = arg
//                    callTrackingHelpBottomSheet.show(
//                        supportFragmentManager,
//                        CallTrackingHelpBottomSheet::class.java.name
//                    )
//                return@setOnClickListener
//            }

            val temp1 = arrayListOf<String>()

            for (singleItem in bundleData!!.included_features){
                temp1.add(singleItem.feature_code)
            }

            if(temp1.contains("CALLTRACKER") && temp1.contains( "DOMAINPURCHASE")){
                if ((purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null") == true) &&
                    (purchasedVmnName.isNullOrEmpty()) ) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundleData))
                    args.putString("fpid", fpid)
                    args.putString("vmn", "null")
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
               else if((purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null") == true) &&
                    (!purchasedVmnName.isNullOrEmpty()) ) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putString("vmn","false")
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundleData))
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
               else if((!purchasedDomainType.isNullOrEmpty() || !purchasedDomainName?.contains("null")!! == true) &&
                    (purchasedVmnName.isNullOrEmpty()) ) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putString("vmn","true")
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundleData))
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
                else {
                    if (bundleData != null) {
                        prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

                        val itemIds = arrayListOf<String>()
                        for (i in bundleData!!.included_features) {
                            itemIds.add(i.feature_code)
                        }

                        CompositeDisposable().add(
                            AppDatabase.getInstance(application)!!
                                .featuresDao()
                                .getallFeaturesInList(itemIds)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    {
                                        if(cartList != null) {
                                            //same features available in cart
                                            for (singleItem in cartList!!) {
                                                for (singleFeature in it) {
                                                    if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                                        sameAddonsInCart.add(singleFeature.name!!)
                                                        addonsListInCart.add(singleItem.item_id)
                                                    }
                                                }
                                                //if there is any other bundle available remove it
                                                if (singleItem.item_type.equals("bundles")) {
                                                    addonsListInCart.add(singleItem.item_id)
                                                }
                                            }
                                        }

                                        if(sameAddonsInCart.size > 0){
                                            val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                            val args = Bundle()
                                            args.putStringArrayList("addonNames", sameAddonsInCart)
                                            args.putStringArrayList("addonsListInCart", addonsListInCart)
                                            args.putString("packageDetails", Gson().toJson(bundleData!!))
                                            removeFeatureBottomSheet.arguments = args
                                            removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                        }else {

                                            var bundleMonthlyMRP = 0.0
                                            val minMonth: Int =
                                                if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                            for (singleItem in it) {
                                                for (item in bundleData!!.included_features) {
                                                    if (singleItem.feature_code == item.feature_code) {
                                                        bundleMonthlyMRP += RootUtil.round(
                                                            singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                            2
                                                        )
                                                    }
                                                }
                                            }
                                            offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                                            originalBundlePrice = (bundleMonthlyMRP * minMonth)

                                            if (bundleData!!.overall_discount_percent > 0){
                                                offeredBundlePrice =originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100.0)
                                            }else
                                                offeredBundlePrice = originalBundlePrice

                                            //clear cartOrderInfo from SharedPref to requestAPI againf
                                            prefs.storeCartOrderInfo(null)

                                            //remove other bundle and add existing bundle to cart
                                            removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundleData!!, offeredBundlePrice, originalBundlePrice)

                                            val event_attributes: HashMap<String, Any> =
                                                HashMap()
                                            bundleData!!.name?.let { it1 ->
                                                event_attributes.put(
                                                    "Package Name",
                                                    it1
                                                )
                                            }
                                            bundleData!!.target_business_usecase?.let { it1 ->
                                                event_attributes.put(
                                                    "Package Tag",
                                                    it1
                                                )
                                            }
                                            event_attributes.put("Package Price", originalBundlePrice)
                                            event_attributes.put("Discounted Price", offeredBundlePrice)
                                            event_attributes.put(
                                                "Discount %",
                                                bundleData!!.overall_discount_percent
                                            )
                                            bundleData!!.min_purchase_months?.let { it1 ->
                                                event_attributes.put(
                                                    "Validity",
                                                    if (!prefs.getYearPricing()) it1 else 1
                                                )
                                            }
                                            WebEngageController.trackEvent(
                                                ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                                ADDONS_MARKETPLACE,
                                                event_attributes
                                            )
                                        }
                                    },
                                    {
                                        it.printStackTrace()

                                    }
                                )
                        )
                    }
                }
            }
            else if(temp1.contains("CALLTRACKER") && !temp1.contains("DOMAINPURCHASE")){
                if((purchasedVmnName.isNullOrEmpty()) ) {
                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putString("vmn","true")
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundleData))
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
                 else {
                     if (bundleData != null) {
                         prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

                         val itemIds = arrayListOf<String>()
                         for (i in bundleData!!.included_features) {
                             itemIds.add(i.feature_code)
                         }

                         CompositeDisposable().add(
                             AppDatabase.getInstance(application)!!
                                 .featuresDao()
                                 .getallFeaturesInList(itemIds)
                                 .subscribeOn(Schedulers.io())
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribe(
                                     {
                                         if(cartList != null) {
                                             //same features available in cart
                                             for (singleItem in cartList!!) {
                                                 for (singleFeature in it) {
                                                     if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                                         sameAddonsInCart.add(singleFeature.name!!)
                                                         addonsListInCart.add(singleItem.item_id)
                                                     }
                                                 }
                                                 //if there is any other bundle available remove it
                                                 if (singleItem.item_type.equals("bundles")) {
                                                     addonsListInCart.add(singleItem.item_id)
                                                 }
                                             }
                                         }

                                         if(sameAddonsInCart.size > 0){
                                             val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                             val args = Bundle()
                                             args.putStringArrayList("addonNames", sameAddonsInCart)
                                             args.putStringArrayList("addonsListInCart", addonsListInCart)
                                             args.putString("packageDetails", Gson().toJson(bundleData!!))
                                             removeFeatureBottomSheet.arguments = args
                                             removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                         }else {

                                             var bundleMonthlyMRP = 0.0
                                             val minMonth: Int =
                                                 if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                             for (singleItem in it) {
                                                 for (item in bundleData!!.included_features) {
                                                     if (singleItem.feature_code == item.feature_code) {
                                                         bundleMonthlyMRP += RootUtil.round(
                                                             singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                             2
                                                         )
                                                     }
                                                 }
                                             }
                                             offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                                             originalBundlePrice = (bundleMonthlyMRP * minMonth)

                                             if (bundleData!!.overall_discount_percent > 0){
                                                 offeredBundlePrice =originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100.0)
                                             }else
                                                 offeredBundlePrice = originalBundlePrice

                                             //clear cartOrderInfo from SharedPref to requestAPI againf
                                             prefs.storeCartOrderInfo(null)

                                             //remove other bundle and add existing bundle to cart
                                             removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundleData!!, offeredBundlePrice, originalBundlePrice)

                                             val event_attributes: HashMap<String, Any> =
                                                 HashMap()
                                             bundleData!!.name?.let { it1 ->
                                                 event_attributes.put(
                                                     "Package Name",
                                                     it1
                                                 )
                                             }
                                             bundleData!!.target_business_usecase?.let { it1 ->
                                                 event_attributes.put(
                                                     "Package Tag",
                                                     it1
                                                 )
                                             }
                                             event_attributes.put("Package Price", originalBundlePrice)
                                             event_attributes.put("Discounted Price", offeredBundlePrice)
                                             event_attributes.put(
                                                 "Discount %",
                                                 bundleData!!.overall_discount_percent
                                             )
                                             bundleData!!.min_purchase_months?.let { it1 ->
                                                 event_attributes.put(
                                                     "Validity",
                                                     if (!prefs.getYearPricing()) it1 else 1
                                                 )
                                             }
                                             WebEngageController.trackEvent(
                                                 ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                                 ADDONS_MARKETPLACE,
                                                 event_attributes
                                             )
                                         }
                                     },
                                     {
                                         it.printStackTrace()

                                     }
                                 )
                         )
                     }
                 }
            }
            else if(temp1.contains( "DOMAINPURCHASE")&& !temp1.contains("CALLTRACKER")){
                if(purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null")!! == true) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putString("vmn","false")
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundleData))
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
                else {
                    if (bundleData != null) {
                        prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

                        val itemIds = arrayListOf<String>()
                        for (i in bundleData!!.included_features) {
                            itemIds.add(i.feature_code)
                        }

                        CompositeDisposable().add(
                            AppDatabase.getInstance(application)!!
                                .featuresDao()
                                .getallFeaturesInList(itemIds)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    {
                                        if(cartList != null) {
                                            //same features available in cart
                                            for (singleItem in cartList!!) {
                                                for (singleFeature in it) {
                                                    if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                                        sameAddonsInCart.add(singleFeature.name!!)
                                                        addonsListInCart.add(singleItem.item_id)
                                                    }
                                                }
                                                //if there is any other bundle available remove it
                                                if (singleItem.item_type.equals("bundles")) {
                                                    addonsListInCart.add(singleItem.item_id)
                                                }
                                            }
                                        }

                                        if(sameAddonsInCart.size > 0){
                                            val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                            val args = Bundle()
                                            args.putStringArrayList("addonNames", sameAddonsInCart)
                                            args.putStringArrayList("addonsListInCart", addonsListInCart)
                                            args.putString("packageDetails", Gson().toJson(bundleData!!))
                                            removeFeatureBottomSheet.arguments = args
                                            removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                        }else {

                                            var bundleMonthlyMRP = 0.0
                                            val minMonth: Int =
                                                if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                            for (singleItem in it) {
                                                for (item in bundleData!!.included_features) {
                                                    if (singleItem.feature_code == item.feature_code) {
                                                        bundleMonthlyMRP += RootUtil.round(
                                                            singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                            2
                                                        )
                                                    }
                                                }
                                            }
                                            offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                                            originalBundlePrice = (bundleMonthlyMRP * minMonth)

                                            if (bundleData!!.overall_discount_percent > 0){
                                                offeredBundlePrice =originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100.0)
                                            }else
                                                offeredBundlePrice = originalBundlePrice

                                            //clear cartOrderInfo from SharedPref to requestAPI againf
                                            prefs.storeCartOrderInfo(null)

                                            //remove other bundle and add existing bundle to cart
                                            removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundleData!!, offeredBundlePrice, originalBundlePrice)

                                            val event_attributes: HashMap<String, Any> =
                                                HashMap()
                                            bundleData!!.name?.let { it1 ->
                                                event_attributes.put(
                                                    "Package Name",
                                                    it1
                                                )
                                            }
                                            bundleData!!.target_business_usecase?.let { it1 ->
                                                event_attributes.put(
                                                    "Package Tag",
                                                    it1
                                                )
                                            }
                                            event_attributes.put("Package Price", originalBundlePrice)
                                            event_attributes.put("Discounted Price", offeredBundlePrice)
                                            event_attributes.put(
                                                "Discount %",
                                                bundleData!!.overall_discount_percent
                                            )
                                            bundleData!!.min_purchase_months?.let { it1 ->
                                                event_attributes.put(
                                                    "Validity",
                                                    if (!prefs.getYearPricing()) it1 else 1
                                                )
                                            }
                                            WebEngageController.trackEvent(
                                                ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                                ADDONS_MARKETPLACE,
                                                event_attributes
                                            )
                                        }
                                    },
                                    {
                                        it.printStackTrace()

                                    }
                                )
                        )
                    }
                }
            }

        }

        binding?.back?.setOnClickListener {
            finish()
        }

        binding?.imageViewCart121?.setOnClickListener {
            val intent = Intent(
                applicationContext,
                CartActivity::class.java
            )
            intent.putExtra("fpid", fpid)
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("isDeepLink", isDeepLink)
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkDay", deepLinkDay)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra(
                "accountType",
                accountType
            )
            intent.putStringArrayListExtra(
                "userPurchsedWidgets",
                userPurchsedWidgets
            )
            if (email != null) {
                intent.putExtra("email", email)
            } else {
                intent.putExtra("email", "ria@nowfloats.com")
            }
            if (mobileNo != null) {
                intent.putExtra("mobileNo", mobileNo)
            } else {
                intent.putExtra("mobileNo", "9160004303")
            }
            intent.putExtra("profileUrl", profileUrl)
            startActivity(intent)
        }
    }

    fun removeOtherBundlesAndAddExistingBundle(addonsListInCart: List<String>, bundle: Bundles, offerBundlePrice: Double, originalBundlePrice: Double ){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                binding?.packageImg?.let { it1 -> makeFlyAnimation(it1) }
                viewModel.addItemToCartPackage1(
                    CartModel(
                        bundle._kid,
                        null,
                        null,
                        bundle.name,
                        "",
                        bundle.primary_image!!.url,
                        offerBundlePrice,
                        originalBundlePrice,
                        bundle.overall_discount_percent,
                        1,
                        if (!prefs.getYearPricing() && bundle.min_purchase_months != null) bundle.min_purchase_months!! else 1,
                        "bundles",
                        null,
                        ""
                    )
                )
                viewModel.getCartItems()
            }
            .doOnError {
                Toast.makeText(this, "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
                viewModel.getCartItems()
            }
            .subscribe()
    }

    private fun onClickNeedMoreFeatureCard(item: BundlesModel) {
        val event_attributes: HashMap<String, Any> = HashMap()
        item.name?.let { it1 -> event_attributes.put("Package Name", it1) }
        item.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
        event_attributes.put("Discount %", item.overall_discount_percent)
        event_attributes.put("Package Identifier", item.bundle_id)
        item.min_purchase_months.let { it1 -> event_attributes.put("Validity", it1) }
        WebEngageController.trackEvent(FEATURE_PACKS_CLICKED, ADDONS_MARKETPLACE, event_attributes)
        val intent = Intent(this, PackDetailsActivity::class.java)
        intent.putExtra("bundleData", Gson().toJson(getBundlesFromBundleModel(item)))
        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
        intent.putExtra("fpid", fpid)
        intent.putExtra("expCode", experienceCode)
        intent.putExtra("isDeepLink", isDeepLink)
        intent.putExtra("deepLinkViewType", deepLinkViewType)
        intent.putExtra("deepLinkDay", deepLinkDay)
        intent.putExtra("isOpenCardFragment", isOpenCardFragment)
        intent.putExtra(
            "accountType",
            accountType
        )
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            userPurchsedWidgets
        )
        if (email != null) {
            intent.putExtra("email", email)
        } else {
            intent.putExtra("email", "ria@nowfloats.com")
        }
        if (mobileNo != null) {
            intent.putExtra("mobileNo", mobileNo)
        } else {
            intent.putExtra("mobileNo", "9160004303")
        }
        intent.putExtra("profileUrl", profileUrl)
        startActivity(intent)
    }

    private fun getBundlesFromBundleModel(item: BundlesModel): Bundles? {
        val temp = Gson().fromJson<List<IncludedFeature>>(
            item.included_features!!,
            object : TypeToken<List<IncludedFeature>>() {}.type
        )

       return Bundles(
            item.bundle_id,
            temp,
            item.min_purchase_months,
            item.name,
            item.overall_discount_percent,
            PrimaryImage(item.primary_image),
            item.target_business_usecase,
            Gson().fromJson<List<String>>(
                item.exclusive_to_categories,
                object : TypeToken<List<String>>() {}.type
            ),
            null, Gson().fromJson<List<HowToActivate>>(
                item.how_to_activate,
                object : TypeToken<List<HowToActivate>>() {}.type
            ), Gson().fromJson<List<Testimonial>>(
                item.testimonials,
                object : TypeToken<List<Testimonial>>() {}.type
            ), Gson().fromJson<List<FrequentlyAskedQuestion>>(
                item.frequently_asked_questions,
                object : TypeToken<List<FrequentlyAskedQuestion>>() {}.type
            ),Gson().fromJson<List<String>>(
                item.benefits,
                object : TypeToken<List<String>>() {}.type
            ),item.desc
        )
    }

    override fun onResume() {
        super.onResume()
        //clear previous existing data
        sameAddonsInCart.clear()
        addonsListInCart.clear()
        loadData()
    }

    private fun loadData() {
        val pref = this?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
        val code: String =
            if (experienceCode.isNullOrEmpty()) experienceCode!! else UserSessionManager(
                this
            ).fP_AppExperienceCode!!
        if (!code.equals("null", true)) {
            viewModel?.setCurrentExperienceCode(code, fpTag!!)
        }
//        try {
//            viewModel.myPlanV3Status(
//                intent.getStringExtra("fpid") ?: "",
//                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
//            )
//        } catch (e: Exception) {
//            SentryController.captureException(e)
//        }
        try {
            viewModel?.getCartItems()
            viewModel?.getAllPackages()
            getAlreadyPurchasedDomain()
            getAlreadyPurchasedVmn()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    fun addUpdatePacks(list: ArrayList<BundlesModel>) {
        if (list.size > 0) {
            layout_need_more.visibility = VISIBLE
//            packItemAdapter.addupdates(list)
//            rv_packs.adapter = packItemAdapter
//            packItemAdapter.notifyDataSetChanged()
        } else {
            layout_need_more.visibility = GONE
        }
    }

    private fun initializePackItemRecycler() {
        val gridLayoutManager = LinearLayoutManager(applicationContext)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        binding?.rvPacks?.apply {
//            layoutManager = gridLayoutManager
//            binding?.rvPacks?.adapter = packItemAdapter
//        }
    }

    fun updatePackItemRecycler(list: ArrayList<BundlesModel>) {
        if (list.size > 0) {
            layout_need_more.visibility = VISIBLE
//            packItemAdapter.addupdates(list)
//            packItemAdapter.notifyDataSetChanged()
        }
        else {
            layout_need_more.visibility = GONE
        }
    }

    private fun initializeIncludedFeature() {
        val gridLayoutManager = LinearLayoutManager(applicationContext)
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding?.rvIncludedFeatures?.apply {
            layoutManager = gridLayoutManager
            binding?.rvIncludedFeatures?.adapter = includedFeatureAdapter
        }
    }

    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.howToUseRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.howToUseRecycler?.adapter = howToUseAdapter
        }
    }

    private fun initializeFAQRecycler() {
        var layoutManager1 = LinearLayoutManager(applicationContext)
        layoutManager1.orientation = LinearLayoutManager.VERTICAL
        binding?.faqRecycler?.apply {
            layoutManager = layoutManager1
            binding!!.faqRecycler.adapter = faqAdapter
        }
    }

    private fun initializeFeatureAdapter() {
        var layoutManager1 = LinearLayoutManager(this)
        layoutManager1.orientation = LinearLayoutManager.VERTICAL
        binding?.packRecycler?.apply {
            layoutManager = layoutManager1
            binding!!.packRecycler.adapter = packDetailsAdapter
        }
    }

    private fun initializeNeedMoreAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding?.otherFeatureRecyclerView?.apply {
            layoutManager = mLayoutManager
            binding?.otherFeatureRecyclerView?.adapter = needMoreFeatureAdapter
        }
    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsIndicator?.setViewPager2(binding?.benefitsViewpager!!)
        binding?.benefitsViewpager?.offscreenPageLimit = 1

//        binding?.benefitsViewpager?.setPageTransformer(SimplePageTransformerSmall())
        binding?.benefitsViewpager?.setPageTransformer(BenifitsPageTransformer(this))

//        val itemDecoration = HorizontalMarginItemDecoration(
//            applicationContext,
//            R.dimen.viewpager_current_item_horizontal_margin,
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)
    }


    private fun initializeCustomerViewPager() {
        binding?.whatOurCustomerViewpager?.adapter = reviewAdaptor
        binding?.whatOurCustomerIndicator?.setViewPager2(binding?.whatOurCustomerViewpager!!)
        binding?.whatOurCustomerViewpager?.offscreenPageLimit = 1

        binding?.whatOurCustomerViewpager?.setPageTransformer(ZoomOutPageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding?.whatOurCustomerViewpager?.addItemDecoration(itemDecoration)
    }

    private fun initView(){
        if (bundleData?.name!= null) {
            Glide.with(binding?.packageImg!!).load(bundleData?.primary_image!!.url)
                .into(binding?.packageImg!!)
            Glide.with(binding?.packageImg1!!).load(bundleData?.primary_image!!.url)
                .into(binding?.packageImg1!!)
            binding?.title?.text = bundleData?.name
            binding?.bottomBoxOnlyBtn?.text = "Buy " + bundleData?.name
            binding?.addItemToCart?.text = "Buy " + bundleData?.name

            if (bundleData?.benefits != null && bundleData?.benefits?.isNotEmpty()!!) {
                binding?.containerKeyBenefits?.visibility = View.VISIBLE
                benefitAdaptor.addupdates(bundleData?.benefits!!)
                benefitAdaptor.notifyDataSetChanged()
            } else {
                binding?.containerKeyBenefits?.visibility = GONE
            }
            if (bundleData?.included_features != null && bundleData?.included_features?.isNotEmpty()!!) {
                binding?.rvIncludedFeatures?.visibility = VISIBLE
                binding?.tvPremiumFeaturesNo?.visibility = VISIBLE
                val sourceString = "<b>" + bundleData?.included_features?.size!!.toString() + " PREMIUM FEATURES"  + "</b> " + " ideal for small businesses that want to get started with online sales."
                binding?.tvPremiumFeaturesNo?.text = Html.fromHtml(sourceString)
                featuresList?.let { includedFeatureAdapter.addupdates(it) }
                includedFeatureAdapter.notifyDataSetChanged()
            } else {
                binding?.rvIncludedFeatures?.visibility = GONE
                binding?.tvPremiumFeaturesNo?.visibility = GONE
            }

            if (bundleData != null && bundleData?.how_to_activate != null && bundleData?.how_to_activate?.isNotEmpty()!!) {
                binding?.howToUseContainer?.visibility = View.VISIBLE
                binding?.howToUseContainer?.setOnClickListener {
                    if (binding?.howToUseRecycler?.visibility == View.VISIBLE) {
                        how_to_use_arrow.setImageResource(R.drawable.ic_down_arrow_pack_details)
                        how_to_use_recycler.visibility = GONE
                    } else {
                        how_to_use_arrow.setImageResource(R.drawable.ic_up_arrow_with_bg)
                        how_to_use_recycler.visibility = View.VISIBLE
                    }
                }

                val steps =
                    bundleData?.how_to_activate
                howToUseAdapter.addupdates(steps!!)
                howToUseAdapter.notifyDataSetChanged()
            } else {
                binding?.howToUseContainer?.visibility = GONE

            }
            if (bundleData != null && bundleData?.testimonials != null && bundleData?.testimonials?.isNotEmpty()!!) {
                binding?.whatOurCustomerContainer?.visibility = VISIBLE
                reviewAdaptor.addupdates(bundleData?.testimonials!!)
                reviewAdaptor.notifyDataSetChanged()
            } else {
                binding?.whatOurCustomerContainer?.visibility = GONE
            }
            if (bundleData != null && bundleData?.frequently_asked_questions != null && bundleData?.frequently_asked_questions?.isNotEmpty()!!) {
                faq_container.visibility = View.VISIBLE
                val faq = bundleData?.frequently_asked_questions!!
                faqAdapter.addupdates(faq)
                faqAdapter.notifyDataSetChanged()
            } else {
                faq_container.visibility = GONE
            }


            val itemsIds = arrayListOf<String>()
            for (item in bundleData?.included_features!!) {
                itemsIds.add(item.feature_code)
            }
            val minMonth: Int =
                if (!prefs.getYearPricing() && bundleData?.min_purchase_months != null && bundleData?.min_purchase_months!! > 1) bundleData?.min_purchase_months!! else 1
            CompositeDisposable().add(
                AppDatabase.getInstance(this.application)!!
                    .featuresDao()
                    .getallFeaturesInList(itemsIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            if (it.isNotEmpty()) {
                                binding?.packContainer?.visibility = VISIBLE
                                binding?.packTitleLayout?.setOnClickListener {
                                    if (binding?.packRecycler?.visibility == VISIBLE) {
                                        pack_title_arrow.setImageResource(R.drawable.ic_down_arrow_pack_details)
                                        pack_recycler.visibility = GONE
                                    } else {
                                        pack_title_arrow.setImageResource(R.drawable.ic_up_arrow_with_bg)
                                        pack_recycler.visibility = VISIBLE
                                    }
                                }
                                packDetailsAdapter.addupdates(it)
                                includedFeatureAdapter.addupdates(it)
                                includedFeaturesInPack = it
                                binding?.packRecycler?.adapter = packDetailsAdapter
                            } else {
                                binding?.packContainer?.visibility = GONE
                            }

                            originalBundlePrice = 0.0
                            for (singleItem in it) {
                                for (item in bundleData?.included_features!!) {
                                    if (singleItem.feature_code == item.feature_code) {
                                        originalBundlePrice += RootUtil.round(
                                                (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),
                                                2
                                            ) * minMonth
                                    }
                                }
                            }

                            if (bundleData?.overall_discount_percent!! > 0) {
                                offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundleData?.overall_discount_percent!! / 100.0)
                                binding?.containerBlack?.visibility = View.VISIBLE


                                tv_saving.text =
                                    bundleData?.overall_discount_percent!!.toString() + "% SAVING"
                                tv2.text =
                                    "SAVING " + bundleData?.overall_discount_percent!!.toString() + "%"
                            } else {
                                offeredBundlePrice = originalBundlePrice
                                binding?.containerBlack?.visibility = GONE

                            }

                            val temp = offeredBundlePrice
                            val temp1 =  originalBundlePrice

                            binding?.includedBlack?.tvDesc?.text= "If you buy ${bundleData!!.included_features.size} \nfeatures seperately"
                            binding?.includedBlack?.tvDesc1?.text= "If you buy same ${bundleData!!.included_features.size} features \nin ${bundleData!!.name}"

                            if (!prefs.getYearPricing() && bundleData?.min_purchase_months != null && bundleData?.min_purchase_months!! > 1) {

                                binding?.includedBlack?.tvPrice1?.text = "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(RootUtil.round(offeredBundlePrice,2)) +
                                        Utils.yearlyOrMonthlyOrEmptyValidity(
                                            "",
                                            this,
                                            bundleData?.min_purchase_months!!
                                        )
                                binding?.price?.text = "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(RootUtil.round(offeredBundlePrice,2)) +
                                        Utils.yearlyOrMonthlyOrEmptyValidity(
                                            "",
                                            this,
                                            bundleData?.min_purchase_months!!
                                        )
                                if (offeredBundlePrice != originalBundlePrice) {
                                    spannableString(
                                        originalBundlePrice,
                                        bundleData?.min_purchase_months!!
                                    )
                                    binding?.includedBlack?.tvPrice?.visibility = VISIBLE
                                    binding?.mrpPrice?.visibility = View.VISIBLE
                                } else {
                                    binding?.includedBlack?.tvPrice?.visibility = GONE

                                    binding?.mrpPrice?.visibility = GONE
                                }
                            } else {
                                binding?.price?.text = ("₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(RootUtil.round(Utils.priceCalculatorForYear(offeredBundlePrice, "", this),2))
                                        + Utils.yearlyOrMonthlyOrEmptyValidity("", this))
                                binding?.includedBlack?.tvPrice1?.text = ("₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(RootUtil.round(Utils.priceCalculatorForYear(offeredBundlePrice, "", this),2))
                                        + Utils.yearlyOrMonthlyOrEmptyValidity("", this))
                                if (offeredBundlePrice != originalBundlePrice) {
                                    spannableString(originalBundlePrice)
                                    binding?.includedBlack?.tvPrice?.visibility = VISIBLE
                                    binding?.mrpPrice?.visibility = View.VISIBLE
                                } else {
                                    binding?.includedBlack?.tvPrice?.visibility = GONE
                                    binding?.price?.visibility = GONE
                                }
                            }

                        },
                        {
                            it.printStackTrace()
                        }
                    )
            )
        }
    }


    private fun initMvvm() {
        viewModel.addedToCartResult().observe(this, androidx.lifecycle.Observer {
            if (it) {
                binding?.addToCart?.background = ContextCompat.getDrawable(
                    this.applicationContext,
                    R.drawable.ic_packsv3_added_to_cart_bg
                )
                binding?.addToCart?.setTextColor(
                    this.getResources().getColor(R.color.tv_color_BB)
                )
                binding?.addToCart?.setText(this.getString(R.string.added_to_cart))
                binding?.addToCart?.isClickable = false

            val intent = Intent(
                applicationContext,
                CartActivity::class.java
            )
            intent.putExtra("fpid", fpid)
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("isDeepLink", isDeepLink)
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkDay", deepLinkDay)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra(
                "accountType",
                accountType
            )
            intent.putStringArrayListExtra(
                "userPurchsedWidgets",
                userPurchsedWidgets
            )
            if (email != null) {
                intent.putExtra("email", email)
            } else {
                intent.putExtra("email", "ria@nowfloats.com")
            }
            if (mobileNo != null) {
                intent.putExtra("mobileNo", mobileNo)
            } else {
                intent.putExtra("mobileNo", "9160004303")
            }
            intent.putExtra("profileUrl", profileUrl)
            startActivity(intent)
        }
        })

//        viewModel.myplanResultV3().observe(this, androidx.lifecycle.Observer {
//            allowPackageToCart = true
//            if(it!=null) {
//                binding?.shimmerViewPacksv3?.visibility=View.GONE
//                binding?.scrollView?.visibility=View.VISIBLE
//                myPlanV3 = it
//                //disabling marketplace gaps
//                //getAllowPackageToCart(bundleData!!)
//            } else{
//                binding?.scrollView?.visibility=View.GONE
//                binding?.shimmerViewPacksv3?.visibility=View.VISIBLE
//            }
//        })

        viewModel.purchasedVmnResult().observe(this) {
            if(!it.Vmn.isNullOrEmpty()) {
                prefs.storeSelectedVMNName(it.Vmn)
                prefs.storeVmnOrderType(1)
                purchasedVmnName = it.Vmn
                purchasedVmnActive = true
            }
            viewModel.getCartItems()
        }

        viewModel.PurchasedDomainResponse().observe(this) {
            if(it!=null) {
                binding?.shimmerViewPacksv3?.visibility=View.GONE
                binding?.scrollView?.visibility=View.VISIBLE

                purchasedDomainName = it.domainName
                purchasedDomainType = it.domainType

                if(it.domainName != null && it.domainType != null) {
                    if(!(it.domainName.contains("null") || it.domainType.contains("null"))) {
                        prefs.storeDomainOrderType(1)
                        prefs.storeSelectedDomainName(it.domainName + it.domainType)
                    }
                }
                viewModel.getCartItems()
            }else{
                binding?.scrollView?.visibility=View.GONE
                binding?.shimmerViewPacksv3?.visibility=View.VISIBLE
            }

        }

        // viewmodel
        viewModel.bundleResult().observe(this) { list ->
            if (list != null) {
                if(bundleData?.name == "Online Basic" ) {
                    // show Online Classic
                    val onlineClassic = list.find { it.name == "Online Classic" }
                    onlineClassic?.let { it1 -> setupPackItemRecycler(it1) }
                } else if(bundleData?.name == "Online Classic") {
                    //show Online Advance
                    val onlineAdvance = list.find { it.name == "Online Advanced" }
                    onlineAdvance?.let { it1 -> setupPackItemRecycler(it1) }
                } else {
                    // hide the section - Need More Features
                    layout_need_more.visibility = View.GONE
                }

                // this is for domain & Vmn selection addon details.
                val itemsIds = arrayListOf<String>()
                for (item in bundleData?.included_features!!) {
                    itemsIds.add(item.feature_code)
                }
                CompositeDisposable().add(
                    AppDatabase.getInstance(this.application)!!
                        .featuresDao()
                        .getallFeaturesInList(itemsIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                if (it.isNotEmpty()) {
                                    for (singleItem in it){
                                        if (singleItem.feature_code == "DOMAINPURCHASE"){
                                            singleAddon= singleItem
                                        }
                                    }
                                }
                            },
                            {
                                it.printStackTrace()
                            }
                        )
                )
            }
        }
        viewModel.addonsError().observe(this, androidx.lifecycle.Observer {
            println("addonsError ${it}")
            if (it.contains("Query returned empty"))
                finish()
        })
//        viewModel.addonsLoader().observe(this, androidx.lifecycle.Observer {
//            if (it) {
//                val status = "Loading. Please wait..."
//                progressDialog.setMessage(status)
//                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
//                progressDialog.show()
//            } else {
//                progressDialog.dismiss()
//            }
//        })
        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            cartList = it
            itemInCart = false
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {
                // packsv3footerAdapter.updateCartItem(cartList!!)
                if (refreshViewPager) {
                    refreshViewPager = false
                }

                if (cartList?.size!! > 0) {
                    if (cartList != null) {
                        for (singleCartItem in cartList!!) {
                            if (singleCartItem.item_id.equals(bundleData!!._kid)) {
                                itemInCart = true
                                break
                            }
                        }
                    }
                }
                if (!itemInCart) {
                    binding?.bottomBoxOnlyBtn?.setTextColor(this.resources.getColor(R.color.white))
                    binding?.bottomBoxOnlyBtn?.background = ContextCompat.getDrawable(
                        this.applicationContext,
                        R.drawable.ic_cart_continue_bg
                    )
                    binding?.bottomBoxOnlyBtn?.setText("Buy ${bundleData!!.name}")
                    binding?.bottomBoxOnlyBtn?.isClickable = true
                } else {
                    binding?.bottomBoxOnlyBtn?.background = ContextCompat.getDrawable(
                        this.applicationContext,
                        R.drawable.ic_packsv3_added_to_cart_bg
                    )
                    binding?.bottomBoxOnlyBtn?.setTextColor(
                        this.getResources().getColor(R.color.tv_color_BB)
                    )
                    binding?.bottomBoxOnlyBtn?.setText(this.getString(R.string.added_to_cart))
                    binding?.bottomBoxOnlyBtn?.isClickable = false
                }

                cartCount = cartList!!.size
                badgeNumber = cartList!!.size
                binding?.badge121?.setText(badgeNumber.toString())
                binding?.badge121?.visibility = View.VISIBLE
                Log.v("badgeNumber", " " + badgeNumber)

            } else {
                cartCount = 0
                badgeNumber = 0
                binding?.badge121?.visibility = GONE
                packageInCartStatus = false

                binding?.bottomBoxOnlyBtn?.setTextColor(this.resources.getColor(R.color.white))
                binding?.bottomBoxOnlyBtn?.background = ContextCompat.getDrawable(
                    this.applicationContext,
                    R.drawable.ic_cart_continue_bg
                )
                binding?.bottomBoxOnlyBtn?.setText("Buy ${bundleData!!.name}")
                binding?.bottomBoxOnlyBtn?.isClickable = true

            }
        })
    }

    private fun setupPackItemRecycler(bundlesModel: BundlesModel) {
        needMoreFeatureItem = bundlesModel
        everythingText.text = "Everything in \"" + bundleData?.name + "\" plus"
        needMoreTitle.setText(bundlesModel.name)
        Glide.with(getApplicationContext()).load(bundlesModel.primary_image).into(needMorePackageImg)
        Glide.with(getApplicationContext()).load(bundlesModel.primary_image).into(needMorePackageImg1)
        if (bundlesModel.overall_discount_percent > 0) {
            discount.visibility = View.VISIBLE
            discount.setText(bundlesModel.overall_discount_percent.toString() + "% saving")
        } else {
            discount.visibility = GONE
        }
        try {
            getPackageInfoFromDB( bundlesModel)
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

        setupFeatureIcons(bundlesModel)

        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            needMoreFeatureItemInCart = false
            if (cartList != null && cartList!!.size > 0) {

                if (cartList?.size!! > 0) {
                    if (cartList != null) {
                        for (singleCartItem in cartList!!) {
                            if (singleCartItem.item_id.equals(needMoreFeatureItem.bundle_id)) {
                                needMoreFeatureItemInCart = true
                                binding?.addToCart?.background = ContextCompat.getDrawable(
                                    this.applicationContext,
                                    R.drawable.ic_packsv3_added_to_cart_bg
                                )
                                binding?.addToCart?.setTextColor(
                                    this.getResources().getColor(R.color.tv_color_BB)
                                )
                                binding?.addToCart?.setText(this.getString(R.string.added_to_cart))
                                binding?.addToCart?.isClickable = false
                                break
                            } else {
                                binding?.addToCart?.setTextColor(this.resources.getColor(R.color.white))
                                binding?.addToCart?.background = ContextCompat.getDrawable(
                                    this.applicationContext,
                                    R.drawable.ic_cart_continue_bg
                                )
                                binding?.addToCart?.setText(this.getString(R.string.add_to_cart))
                                binding?.addToCart?.isClickable = true
                            }
                        }
                    }
                }
            } else {
                binding?.addToCart?.setTextColor(this.resources.getColor(R.color.white))
                binding?.addToCart?.background = ContextCompat.getDrawable(
                    this.applicationContext,
                    R.drawable.ic_cart_continue_bg
                )
                binding?.addToCart?.setText(this.getString(R.string.add_to_cart))
                binding?.addToCart?.isClickable = true
            }
        })

        comparePacks.setOnClickListener {
            val intent = Intent(this, ComparePacksV3Activity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)

            intent.putExtra("fpid", fpid)
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("isDeepLink", isDeepLink)
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkDay", deepLinkDay)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra(
                "accountType",
                accountType
            )
            intent.putStringArrayListExtra(
                "userPurchsedWidgets",
                userPurchsedWidgets
            )
            if (email != null) {
                intent.putExtra("email", email)
            } else {
                intent.putExtra("email", "ria@nowfloats.com")
            }
            if (mobileNo != null) {
                intent.putExtra("mobileNo", mobileNo)
            } else {
                intent.putExtra("mobileNo", "9160004303")
            }
            intent.putExtra("profileUrl", profileUrl)
            startActivity(intent)
        }

        addToCart.setOnClickListener{

            val temp = Gson().fromJson<List<IncludedFeature>>(
                bundlesModel.included_features,
                object : TypeToken<List<IncludedFeature>>() {}.type
            )
            val faq = Gson().fromJson<List<FrequentlyAskedQuestion>>(
                bundlesModel.frequently_asked_questions,
                object : TypeToken<List<FrequentlyAskedQuestion>>() {}.type
            )
            val steps = Gson().fromJson<List<HowToActivate>>(
                bundlesModel.how_to_activate,
                object : TypeToken<List<HowToActivate>>() {}.type
            )
            val benefits = if(bundlesModel.benefits != null) Gson().fromJson<List<String>>(
                bundlesModel.benefits!!,
                object : TypeToken<List<String>>() {}.type
            ) else arrayListOf()
            val bundle = Bundles(
                bundlesModel.bundle_id,
                temp,
                bundlesModel.min_purchase_months,
                bundlesModel.name,
                bundlesModel.overall_discount_percent,
                PrimaryImage(bundlesModel.primary_image),
                bundlesModel.target_business_usecase,
                Gson().fromJson<List<String>>(
                    bundlesModel.exclusive_to_categories,
                    object : TypeToken<List<String>>() {}.type
                ),
                null, steps, null, faq, benefits, bundlesModel.desc ?: ""
            )
            //disabling marketplace gaps
//            getAllowPackageToCart(bundle)
//            if(!allowPackageToCart){
//                    val arg = Bundle()
//                    arg.putBoolean("allowPackageToCart", allowPackageToCart)
//                    callTrackingHelpBottomSheet.arguments = arg
//                    callTrackingHelpBottomSheet.show(
//                        supportFragmentManager,
//                        CallTrackingHelpBottomSheet::class.java.name
//                    )
//                return@setOnClickListener
//            }
            var temp1 = arrayListOf<String>()

            for (singleItem in bundle.included_features){
             temp1.add(singleItem.feature_code)
            }

            if(temp1.contains("CALLTRACKER") && temp1.contains( "DOMAINPURCHASE")){
                if ((purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null") == true) &&
                    (purchasedVmnName.isNullOrEmpty()) ) {
                    prefs.storeCartOrderInfo(null)

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundle))
                    args.putString("vmn", "null")
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }

                }
                else if((purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null") == true) &&
                    (!purchasedVmnName.isNullOrEmpty()) ) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundle))
                    args.putString("vmn", "false")
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }

                }
                else if((!purchasedDomainType.isNullOrEmpty() || !purchasedDomainName?.contains("null")!! == true) &&
                    (purchasedVmnName.isNullOrEmpty()) ) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundle))
                    args.putString("vmn", "true")
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
                else {
                    prefs.storeCartOrderInfo(null)

                    binding?.needMorePackageImg?.let { it1 -> makeFlyAnimation(it1) }

                    val temp = Gson().fromJson<List<IncludedFeature>>(
                        bundlesModel.included_features,
                        object : TypeToken<List<IncludedFeature>>() {}.type
                    )
                    val faq = Gson().fromJson<List<FrequentlyAskedQuestion>>(
                        bundlesModel.frequently_asked_questions,
                        object : TypeToken<List<FrequentlyAskedQuestion>>() {}.type
                    )
                    val steps = Gson().fromJson<List<HowToActivate>>(
                        bundlesModel.how_to_activate,
                        object : TypeToken<List<HowToActivate>>() {}.type
                    )
                    val benefits = if(bundlesModel.benefits != null) Gson().fromJson<List<String>>(
                        bundlesModel.benefits!!,
                        object : TypeToken<List<String>>() {}.type
                    ) else arrayListOf()
                    val bundle = Bundles(
                        bundlesModel.bundle_id,
                        temp,
                        bundlesModel.min_purchase_months,
                        bundlesModel.name,
                        bundlesModel.overall_discount_percent,
                        PrimaryImage(bundlesModel.primary_image),
                        bundlesModel.target_business_usecase,
                        Gson().fromJson<List<String>>(
                            bundlesModel.exclusive_to_categories,
                            object : TypeToken<List<String>>() {}.type
                        ),
                        null, steps, null, faq, benefits, bundlesModel.desc ?: ""
                    )


                    val itemIds = arrayListOf<String>()
                    for (i in bundle.included_features) {
                        itemIds.add(i.feature_code)
                    }
                    CompositeDisposable().add(
                        AppDatabase.getInstance(Application())!!
                            .featuresDao()
                            .getallFeaturesInList(itemIds)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {
                                    if(cartList != null) {
                                        //same features available in cart
                                        for (singleItem in cartList!!) {
                                            for (singleFeature in it) {
                                                if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                                    sameAddonsInCart.add(singleFeature.name!!)
                                                    addonsListInCart.add(singleItem.item_id)
                                                }
                                            }
                                            //if there is any other bundle available remove it
                                            if (singleItem.item_type.equals("bundles")) {
                                                addonsListInCart.add(singleItem.item_id)
                                            }
                                        }
                                    }

                                    if(sameAddonsInCart.size > 0){
                                        val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                        val args = Bundle()
                                        args.putStringArrayList("addonNames", sameAddonsInCart)
                                        args.putStringArrayList("addonsListInCart", addonsListInCart)
                                        args.putString("packageDetails", Gson().toJson(bundle))
                                        removeFeatureBottomSheet.arguments = args
                                        removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                    }else {
                                        var offeredBundlePrice = 0.0
                                        var originalBundlePrice = 0.0
                                        val minMonth: Int =
                                            if (!prefs.getYearPricing() && bundle.min_purchase_months != null && bundle.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                        for (singleItem in it) {
                                            for (item in bundle.included_features) {
                                                if (singleItem.feature_code == item.feature_code) {
                                                    originalBundlePrice += RootUtil.round(
                                                        singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                        2
                                                    ) * minMonth
                                                }
                                            }
                                        }


                                        if (bundle.overall_discount_percent > 0)
                                            offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundle.overall_discount_percent / 100.0)
                                        else
                                            offeredBundlePrice = originalBundlePrice

                                        //clear cartOrderInfo from SharedPref to requestAPI again
                                        prefs.storeCartOrderInfo(null)
                                        //remove other bundle and add existing bundle to cart
                                        removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundle, offeredBundlePrice, originalBundlePrice)

                                        val event_attributes: java.util.HashMap<String, Any> =
                                            java.util.HashMap()
                                        bundle.name?.let { it1 ->
                                            event_attributes.put(
                                                "Package Name",
                                                it1
                                            )
                                        }
                                        bundle.target_business_usecase?.let { it1 ->
                                            event_attributes.put(
                                                "Package Tag",
                                                it1
                                            )
                                        }
                                        event_attributes.put("Package Price", originalBundlePrice)
                                        event_attributes.put("Discounted Price", offeredBundlePrice)
                                        event_attributes.put(
                                            "Discount %",
                                            bundle.overall_discount_percent
                                        )
                                        bundle.min_purchase_months?.let { it1 ->
                                            event_attributes.put(
                                                "Validity",
                                                if (!prefs.getYearPricing()) it1 else 1
                                            )
                                        }
                                        WebEngageController.trackEvent(
                                            ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                            ADDONS_MARKETPLACE,
                                            event_attributes
                                        )
                                    }
                                },
                                {
                                    it.printStackTrace()

                                }

                            )
                    )

                    badgeNumber = badgeNumber + 1
                    Constants.CART_VALUE = badgeNumber
                    add_item_to_cart.background = ContextCompat.getDrawable(
                        applicationContext,
                        com.boost.cart.R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                    add_item_to_cart.text = getString(com.boost.cart.R.string.added_to_cart)
                }
            }
            else if(temp1.contains( "DOMAINPURCHASE")&& !temp1.contains("CALLTRACKER")){
                if ((purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null") == true) ) {
                    prefs.storeCartOrderInfo(null)

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundle))
                    args.putString("vmn", "false")
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }

                }
                else {
                    prefs.storeCartOrderInfo(null)

                    binding?.needMorePackageImg?.let { it1 -> makeFlyAnimation(it1) }

                    val temp = Gson().fromJson<List<IncludedFeature>>(
                        bundlesModel.included_features,
                        object : TypeToken<List<IncludedFeature>>() {}.type
                    )
                    val faq = Gson().fromJson<List<FrequentlyAskedQuestion>>(
                        bundlesModel.frequently_asked_questions,
                        object : TypeToken<List<FrequentlyAskedQuestion>>() {}.type
                    )
                    val steps = Gson().fromJson<List<HowToActivate>>(
                        bundlesModel.how_to_activate,
                        object : TypeToken<List<HowToActivate>>() {}.type
                    )
                    val benefits = if(bundlesModel.benefits != null) Gson().fromJson<List<String>>(
                        bundlesModel.benefits!!,
                        object : TypeToken<List<String>>() {}.type
                    ) else arrayListOf()
                    val bundle = Bundles(
                        bundlesModel.bundle_id,
                        temp,
                        bundlesModel.min_purchase_months,
                        bundlesModel.name,
                        bundlesModel.overall_discount_percent,
                        PrimaryImage(bundlesModel.primary_image),
                        bundlesModel.target_business_usecase,
                        Gson().fromJson<List<String>>(
                            bundlesModel.exclusive_to_categories,
                            object : TypeToken<List<String>>() {}.type
                        ),
                        null, steps, null, faq, benefits, bundlesModel.desc ?: ""
                    )


                    val itemIds = arrayListOf<String>()
                    for (i in bundle.included_features) {
                        itemIds.add(i.feature_code)
                    }
                    CompositeDisposable().add(
                        AppDatabase.getInstance(Application())!!
                            .featuresDao()
                            .getallFeaturesInList(itemIds)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {
                                    if(cartList != null) {
                                        //same features available in cart
                                        for (singleItem in cartList!!) {
                                            for (singleFeature in it) {
                                                if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                                    sameAddonsInCart.add(singleFeature.name!!)
                                                    addonsListInCart.add(singleItem.item_id)
                                                }
                                            }
                                            //if there is any other bundle available remove it
                                            if (singleItem.item_type.equals("bundles")) {
                                                addonsListInCart.add(singleItem.item_id)
                                            }
                                        }
                                    }

                                    if(sameAddonsInCart.size > 0){
                                        val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                        val args = Bundle()
                                        args.putStringArrayList("addonNames", sameAddonsInCart)
                                        args.putStringArrayList("addonsListInCart", addonsListInCart)
                                        args.putString("packageDetails", Gson().toJson(bundle))
                                        removeFeatureBottomSheet.arguments = args
                                        removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                    }else {
                                        var offeredBundlePrice = 0.0
                                        var originalBundlePrice = 0.0
                                        val minMonth: Int =
                                            if (!prefs.getYearPricing() && bundle.min_purchase_months != null && bundle.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                        for (singleItem in it) {
                                            for (item in bundle.included_features) {
                                                if (singleItem.feature_code == item.feature_code) {
                                                    originalBundlePrice += RootUtil.round(
                                                        singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                        2
                                                    ) * minMonth
                                                }
                                            }
                                        }


                                        if (bundle.overall_discount_percent > 0)
                                            offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundle.overall_discount_percent / 100.0)
                                        else
                                            offeredBundlePrice = originalBundlePrice

                                        //clear cartOrderInfo from SharedPref to requestAPI again
                                        prefs.storeCartOrderInfo(null)
                                        //remove other bundle and add existing bundle to cart
                                        removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundle, offeredBundlePrice, originalBundlePrice)

                                        val event_attributes: java.util.HashMap<String, Any> =
                                            java.util.HashMap()
                                        bundle.name?.let { it1 ->
                                            event_attributes.put(
                                                "Package Name",
                                                it1
                                            )
                                        }
                                        bundle.target_business_usecase?.let { it1 ->
                                            event_attributes.put(
                                                "Package Tag",
                                                it1
                                            )
                                        }
                                        event_attributes.put("Package Price", originalBundlePrice)
                                        event_attributes.put("Discounted Price", offeredBundlePrice)
                                        event_attributes.put(
                                            "Discount %",
                                            bundle.overall_discount_percent
                                        )
                                        bundle.min_purchase_months?.let { it1 ->
                                            event_attributes.put(
                                                "Validity",
                                                if (!prefs.getYearPricing()) it1 else 1
                                            )
                                        }
                                        WebEngageController.trackEvent(
                                            ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                            ADDONS_MARKETPLACE,
                                            event_attributes
                                        )
                                    }
                                },
                                {
                                    it.printStackTrace()

                                }

                            )
                    )

                    badgeNumber = badgeNumber + 1
                    Constants.CART_VALUE = badgeNumber
                    add_item_to_cart.background = ContextCompat.getDrawable(
                        applicationContext,
                        com.boost.cart.R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                    add_item_to_cart.text = getString(com.boost.cart.R.string.added_to_cart)
                }
            }
            else if(temp1.contains("CALLTRACKER") && !temp1.contains("DOMAINPURCHASE")){
                if((purchasedVmnName.isNullOrEmpty()) ) {

                    val dialogCard = FeatureDetailsPopup(this, this, this)
                    val args = Bundle()
                    args.putString("expCode", experienceCode)
                    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                    args.putString("bundleData", Gson().toJson(bundle))
                    args.putString("vmn", "true")
                    args.putString("fpid", fpid)
                    args.putString("expCode", experienceCode)
                    args.putBoolean("isDeepLink", isDeepLink)
                    args.putString("deepLinkViewType", deepLinkViewType)
                    args.putInt("deepLinkDay", deepLinkDay)
                    args.putBoolean("isOpenCardFragment", isOpenCardFragment)
                    args.putString(
                        "accountType",
                        accountType
                    )
                    args.putStringArrayList(
                        "userPurchsedWidgets",
                        userPurchsedWidgets
                    )
                    if (email != null) {
                        args.putString("email", email)
                    } else {
                        args.putString("email", "ria@nowfloats.com")
                    }
                    if (mobileNo != null) {
                        args.putString("mobileNo", mobileNo)
                    } else {
                        args.putString("mobileNo", "9160004303")
                    }
                    args.putString("profileUrl", profileUrl)
                    dialogCard.arguments = args
                    this.supportFragmentManager.let { dialogCard.show(it, FeatureDetailsPopup::class.java.name) }
                }
                else {
                    prefs.storeCartOrderInfo(null)

                    binding?.needMorePackageImg?.let { it1 -> makeFlyAnimation(it1) }

                    val temp = Gson().fromJson<List<IncludedFeature>>(
                        bundlesModel.included_features,
                        object : TypeToken<List<IncludedFeature>>() {}.type
                    )
                    val faq = Gson().fromJson<List<FrequentlyAskedQuestion>>(
                        bundlesModel.frequently_asked_questions,
                        object : TypeToken<List<FrequentlyAskedQuestion>>() {}.type
                    )
                    val steps = Gson().fromJson<List<HowToActivate>>(
                        bundlesModel.how_to_activate,
                        object : TypeToken<List<HowToActivate>>() {}.type
                    )
                    val benefits = if(bundlesModel.benefits != null) Gson().fromJson<List<String>>(
                        bundlesModel.benefits!!,
                        object : TypeToken<List<String>>() {}.type
                    ) else arrayListOf()
                    val bundle = Bundles(
                        bundlesModel.bundle_id,
                        temp,
                        bundlesModel.min_purchase_months,
                        bundlesModel.name,
                        bundlesModel.overall_discount_percent,
                        PrimaryImage(bundlesModel.primary_image),
                        bundlesModel.target_business_usecase,
                        Gson().fromJson<List<String>>(
                            bundlesModel.exclusive_to_categories,
                            object : TypeToken<List<String>>() {}.type
                        ),
                        null, steps, null, faq, benefits, bundlesModel.desc ?: ""
                    )


                    val itemIds = arrayListOf<String>()
                    for (i in bundle.included_features) {
                        itemIds.add(i.feature_code)
                    }
                    CompositeDisposable().add(
                        AppDatabase.getInstance(Application())!!
                            .featuresDao()
                            .getallFeaturesInList(itemIds)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {
                                    if(cartList != null) {
                                        //same features available in cart
                                        for (singleItem in cartList!!) {
                                            for (singleFeature in it) {
                                                if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                                    sameAddonsInCart.add(singleFeature.name!!)
                                                    addonsListInCart.add(singleItem.item_id)
                                                }
                                            }
                                            //if there is any other bundle available remove it
                                            if (singleItem.item_type.equals("bundles")) {
                                                addonsListInCart.add(singleItem.item_id)
                                            }
                                        }
                                    }

                                    if(sameAddonsInCart.size > 0){
                                        val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                        val args = Bundle()
                                        args.putStringArrayList("addonNames", sameAddonsInCart)
                                        args.putStringArrayList("addonsListInCart", addonsListInCart)
                                        args.putString("packageDetails", Gson().toJson(bundle))
                                        removeFeatureBottomSheet.arguments = args
                                        removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                    }else {
                                        var offeredBundlePrice = 0.0
                                        var originalBundlePrice = 0.0
                                        val minMonth: Int =
                                            if (!prefs.getYearPricing() && bundle.min_purchase_months != null && bundle.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                        for (singleItem in it) {
                                            for (item in bundle.included_features) {
                                                if (singleItem.feature_code == item.feature_code) {
                                                    originalBundlePrice += RootUtil.round(
                                                        singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                                        2
                                                    ) * minMonth
                                                }
                                            }
                                        }


                                        if (bundle.overall_discount_percent > 0)
                                            offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundle.overall_discount_percent / 100.0)
                                        else
                                            offeredBundlePrice = originalBundlePrice

                                        //clear cartOrderInfo from SharedPref to requestAPI again
                                        prefs.storeCartOrderInfo(null)
                                        //remove other bundle and add existing bundle to cart
                                        removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundle, offeredBundlePrice, originalBundlePrice)

                                        val event_attributes: java.util.HashMap<String, Any> =
                                            java.util.HashMap()
                                        bundle.name?.let { it1 ->
                                            event_attributes.put(
                                                "Package Name",
                                                it1
                                            )
                                        }
                                        bundle.target_business_usecase?.let { it1 ->
                                            event_attributes.put(
                                                "Package Tag",
                                                it1
                                            )
                                        }
                                        event_attributes.put("Package Price", originalBundlePrice)
                                        event_attributes.put("Discounted Price", offeredBundlePrice)
                                        event_attributes.put(
                                            "Discount %",
                                            bundle.overall_discount_percent
                                        )
                                        bundle.min_purchase_months?.let { it1 ->
                                            event_attributes.put(
                                                "Validity",
                                                if (!prefs.getYearPricing()) it1 else 1
                                            )
                                        }
                                        WebEngageController.trackEvent(
                                            ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                            ADDONS_MARKETPLACE,
                                            event_attributes
                                        )
                                    }
                                },
                                {
                                    it.printStackTrace()

                                }

                            )
                    )

                    badgeNumber = badgeNumber + 1
                    Constants.CART_VALUE = badgeNumber
                    add_item_to_cart.background = ContextCompat.getDrawable(
                        applicationContext,
                        com.boost.cart.R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                    add_item_to_cart.text = getString(com.boost.cart.R.string.added_to_cart)
                }
            }

        }

        binding?.needMoreFeatureLayout?.setOnClickListener {
            onClickNeedMoreFeatureCard(needMoreFeatureItem)
        }
    }


    private fun setupFeatureIcons(bundlesModel: BundlesModel) {
        val temp = Gson().fromJson<List<IncludedFeature>>(
            bundlesModel.included_features!!,
            object : TypeToken<List<IncludedFeature>>() {}.type
        )


        val itemsIds = arrayListOf<String>()
        for (item in temp) {
            itemsIds.add(item.feature_code)
        }

        CompositeDisposable().add(
            AppDatabase.getInstance(this.application)!!
                .featuresDao()
                .getallFeaturesInList(itemsIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.isNotEmpty()) {
                            featuresModel = it
                            needMoreFeatureAdapter.addupdates(compareFeatureIconsAndSet(it))
                        } else {
                            binding?.otherFeatureRecyclerView?.visibility = GONE
                        }
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    private fun compareFeatureIconsAndSet(it: List<FeaturesModel>?): MutableList<FeaturesModel> {
        val list = mutableListOf<FeaturesModel>()
        it?.forEachIndexed { _, featuresModel ->
            val isThere = includedFeaturesInPack?.find {it.feature_id == featuresModel.feature_id }
            if(isThere == null) {
                list.add(featuresModel)
            }
        }

        return list
    }

    fun getPackageInfoFromDB(bundles: BundlesModel) {
        val itemsIds = arrayListOf<String>()
        val includedFeatures =
            Gson().fromJson<List<IncludedFeature>>(
                bundles.included_features,
                object :
                    TypeToken<List<IncludedFeature>>() {}.type
            )
        for (item in includedFeatures) {
            itemsIds.add(item.feature_code)
        }

        var offeredBundlePrice1 = 0.0
        var originalBundlePrice1 = 0.0
        val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
//    val minMonth: Int = 12
        CompositeDisposable().add(
            AppDatabase.getInstance(this.application)!!
                .featuresDao()
                .getallFeaturesInList(itemsIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        for (singleItem in it) {
                            for (item in includedFeatures) {
                                if (singleItem.feature_code == item.feature_code) {
                                    originalBundlePrice1 += Utils.priceCalculatorForYear(
                                        RootUtil.round(
                                            (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),
                                            2
                                        ) * minMonth, singleItem.widget_type, this)
                                }
                            }
                        }
                        if (bundles.overall_discount_percent > 0) {
                            offeredBundlePrice1 = RootUtil.round(
                                (originalBundlePrice1 - (originalBundlePrice1 * bundles.overall_discount_percent / 100.0)),
                                2
                            )
                            discount.visibility = View.VISIBLE
                            discount.setText(bundles.overall_discount_percent.toString() + "%\nSAVING")
                        } else {
                            offeredBundlePrice1 = originalBundlePrice1
                            discount.visibility = GONE
                        }
                        needMorePrice.setText("₹" + offeredBundlePrice1 + Utils.yearlyOrMonthlyOrEmptyValidity("", this))
                        val mrpPriceString = SpannableString("₹" + originalBundlePrice1 + Utils.yearlyOrMonthlyOrEmptyValidity("", this))
                        mrpPriceString.setSpan(StrikethroughSpan(), 0, mrpPriceString.length, 0)
                        needMoreMrpPrice.setText(mrpPriceString)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun spannableString(
        value: Double,
        minMonth: Int = 1
    ) {

        val origCost = SpannableString(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(Utils.priceCalculatorForYear(value, "", this))
                    + Utils.yearlyOrMonthlyOrEmptyValidity("", this, minMonth)
        )
        binding?.includedBlack?.tvPrice?.text = origCost

        origCost.setSpan(
            StrikethroughSpan(),
            0,
            origCost.length,
            0
        )
        binding?.mrpPrice?.text = origCost
    }

    override fun onclick(item: FeaturesModel) {
        val packagePopup = PackDetailsPopUpFragment( )
        val args = Bundle()
        args.putString("fpid", fpid)
        args.putString("expCode", experienceCode)
        args.putString("isDeepLink", isDeepLink.toString())
        args.putString("deepLinkViewType", deepLinkViewType)
        args.putInt("deepLinkDay", deepLinkDay)
        args.putString("isOpenCardFragment", isOpenCardFragment.toString())
        args.putString(
            "accountType",
            accountType
        )
        args.putStringArrayList(
            "userPurchsedWidgets",
            userPurchsedWidgets
        )
        if (email != null) {
            args.putString("email", email)
        } else {
            args.putString("email", "ria@nowfloats.com")
        }
        if (mobileNo != null) {
            args.putString("mobileNo", mobileNo)
        } else {
            args.putString("mobileNo", "9160004303")
        }
        args.putString("profileUrl", profileUrl)
        args.putString("fpid", fpid)
        args.putString("expCode", experienceCode)
        args.putString("features", Gson().toJson(item))
        args.putDouble("price", offeredBundlePrice)
        args.putDouble("price1", originalBundlePrice)
        if (item != null) {
            args.putInt("addons", bundleData?.included_features?.size!!)
        }
        packagePopup.arguments = args
        packagePopup.show(supportFragmentManager, "PACKAGE_POPUP")

    }

    override fun imagePreviewPosition(list: ArrayList<String>, pos: Int) {
    }

    override fun onPackageClicked(item: Bundles?) {
        val intent = Intent(this, ComparePacksV3Activity::class.java)
        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)

        intent.putExtra("fpid", fpid)
        intent.putExtra("expCode", experienceCode)
        intent.putExtra("isDeepLink", isDeepLink)
        intent.putExtra("deepLinkViewType", deepLinkViewType)
        intent.putExtra("deepLinkDay", deepLinkDay)
        intent.putExtra("isOpenCardFragment", isOpenCardFragment)
        intent.putExtra(
            "accountType",
            accountType
        )
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            userPurchsedWidgets
        )
        if (email != null) {
            intent.putExtra("email", email)
        } else {
            intent.putExtra("email", "ria@nowfloats.com")
        }
        if (mobileNo != null) {
            intent.putExtra("mobileNo", mobileNo)
        } else {
            intent.putExtra("mobileNo", "9160004303")
        }
        intent.putExtra("profileUrl", profileUrl)
        startActivity(intent)

    }

    override fun itemAddedToCart(status: Boolean) {
        viewModel.getCartItems()

    }

    override fun goToCart() {
        navigateToCart()
    }

    fun navigateToCart() {
        val intent = Intent(
            applicationContext,
            CartActivity::class.java
        )
        intent.putExtra("fpid", fpid)
        intent.putExtra("expCode", experienceCode)
        intent.putExtra("isDeepLink", isDeepLink)
        intent.putExtra("deepLinkViewType", deepLinkViewType)
        intent.putExtra("deepLinkDay", deepLinkDay)
        intent.putExtra("isOpenCardFragment", isOpenCardFragment)
        intent.putExtra(
            "accountType",
            accountType
        )
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            userPurchsedWidgets
        )
        if (email != null) {
            intent.putExtra("email", email)
        } else {
            intent.putExtra("email", "ria@nowfloats.com")
        }
        if (mobileNo != null) {
            intent.putExtra("mobileNo", mobileNo)
        } else {
            intent.putExtra("mobileNo", "9160004303")
        }
        intent.putExtra("profileUrl", profileUrl)
        startActivity(intent)
    }

     private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(600)
            .setDestView(featureDetailsCartIcon)
            .setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    viewModel.getCartItems()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()
    }

    override fun onAddonsClicked(item: FeaturesModel) {
    }

    override fun onRefreshCart() {
        viewModel.getCartItems()
    }

    override fun onPackageClicked(item: Bundles?, image: ImageView?) {
        val itemIds = arrayListOf<String>()
        for (i in item?.included_features!!) {
            itemIds.add(i.feature_code)
        }
        CompositeDisposable().add(
            AppDatabase.getInstance(application)!!
                .featuresDao()
                .getallFeaturesInList(itemIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        var bundleMonthlyMRP = 0.0
                        val minMonth: Int =
                            if (item.min_purchase_months != null && item.min_purchase_months!! > 1) item.min_purchase_months!! else 1

                        for (singleItem in it) {
                            for (items in item.included_features) {
                                if (singleItem.feature_code == items.feature_code) {
                                    bundleMonthlyMRP += RootUtil.round(
                                        singleItem.price - ((singleItem.price * items.feature_price_discount_percent) / 100.0),
                                        2
                                    )
                                }
                            }
                        }

                        offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                        originalBundlePrice = (bundleMonthlyMRP * minMonth)

                        if (item.overall_discount_percent > 0)
                            offeredBundlePrice = RootUtil.round(
                                originalBundlePrice - (originalBundlePrice * item.overall_discount_percent / 100),
                                2
                            )
                        else
                            offeredBundlePrice = originalBundlePrice

                        //clear cartOrderInfo from SharedPref to requestAPI again
                        prefs.storeCartOrderInfo(null)
                        viewModel.addItemToCartPackage1(
                            CartModel(
                                item._kid,
                                null,
                                null,
                                item.name,
                                "",
                                item.primary_image!!.url,
                                offeredBundlePrice.toDouble(),
                                originalBundlePrice.toDouble(),
                                item.overall_discount_percent,
                                1,
                                if (item.min_purchase_months != null) item.min_purchase_months!! else 1,
                                "bundles",
                                null,
                                ""
                            )
                        )
                    },
                    {
                        it.printStackTrace()

                    }
                )
        )
        viewModel.getCartItems()
    }

    override fun featureDetailsPopup(domain: String) {
//        cartPackageAdaptor.selectedDomain(domain)
        prefs.storeSelectedDomainName(domain)
    }

    override fun featureDetailsPopup1(vmn: String) {
        prefs.storeSelectedVMNName(vmn)
    }

    private fun getAlreadyPurchasedDomain() {
        val pref = this?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
        val auth = UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
        viewModel.getAlreadyPurchasedDomain(
            auth,
            fpTag?:"",
            "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21")
    }

    private fun getAllowPackageToCart(selectedBundle: Bundles) {
        allowPackageToCart = true
        val tempList = arrayListOf<String>()
        for (item in selectedBundle.included_features){
            tempList.add(item.feature_code)
        }
        for(singleItem in myPlanV3!!.Result){
            if(tempList.contains(singleItem.FeatureDetails.FeatureKey) && singleItem.FeatureDetails.FeatureState != 7){
                allowPackageToCart = false
                break
            }
        }
    }

    private fun getAlreadyPurchasedVmn() {
        val pref = this.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
        val auth = this.let { UserSessionManager(it).getAccessTokenAuth()?.barrierToken() } ?: ""
        viewModel.getAlreadyPurchasedVmn(
            auth,
            fpTag?:"",
            "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21")
    }
}