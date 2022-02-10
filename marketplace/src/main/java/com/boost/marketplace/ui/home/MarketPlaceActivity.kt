package com.boost.marketplace.ui.home

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.rest.repository.AzureWebsiteNewRepository
import com.boost.cart.CartActivity
import com.boost.cart.adapter.SimplePageTransformer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.recycleritem.RecyclerStringItemClickListener
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.dbcenterapi.utils.*
import com.boost.dbcenterapi.utils.Utils.longToast
import com.boost.marketplace.R
import com.boost.marketplace.adapter.*
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ActivityMarketplaceBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareBackListener
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksActivity
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanActivity
import com.boost.marketplace.ui.browse.BrowseFeaturesActivity
import com.boost.marketplace.ui.coupons.OfferCouponsActivity
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.boost.marketplace.ui.marketplace_Offers.MarketPlaceOffersActivity
import com.boost.marketplace.ui.videos.HelpVideosBottomSheet
import com.boost.marketplace.ui.videos.VideosPopUpBottomSheet
import com.boost.marketplace.ui.webview.WebViewActivity
import com.framework.analytics.SentryController
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.models.toLiveData
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getAccessTokenAuth
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inventoryorder.utils.DynamicLinkParams
import com.inventoryorder.utils.DynamicLinksManager
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_marketplace.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MarketPlaceActivity : AppBaseActivity<ActivityMarketplaceBinding, MarketPlaceHomeViewModel>(),
    RecyclerStringItemClickListener, CompareBackListener, HomeListener,AddonsListener {

    lateinit var packageViewPagerAdapter: PackageViewPagerAdapter
    lateinit var featureDealsAdapter: FeatureDealsAdapter
    lateinit var partnerViewPagerAdapter: PartnerViewPagerAdapter
    lateinit var bannerViewPagerAdapter: BannerViewPagerAdapter
    lateinit var upgradeAdapter: UpgradeAdapter
    lateinit var addonsCategoryAdapter: AddonsCategoryAdapter
    lateinit var videosListAdapter: VideosListAdapter

    //    private var deepLinkUtil: DeepLinkUtil? = null
    var badgeNumber = 0
    var fpRefferalCode: String = ""
    var feedBackLink: String? = null
    lateinit var prefs: SharedPrefs
    var packageInCartStatus = false
    var offeredBundlePrice = 0
    var originalBundlePrice = 0
    var featuresList: List<FeaturesModel>? = null
    private var itemsArrayList: ArrayList<String>? = ArrayList()
    private var packsArrayList: ArrayList<String>? = ArrayList()
    private var itemTypeArrayList: ArrayList<String>? = ArrayList()

    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var fpid: String? = null
    var fpTag: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var isBackCart: Boolean = false
    var isOpenHomeFragment: Boolean = false
    var isOpenAddOnsFragment: Boolean = false

    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    private var widgetFeatureCode: String? = null

    private var initialLoadUpgradeActivity: Int = 0
    lateinit var progressDialog: ProgressDialog
    private var loadingStatus: Boolean = true
    var userPurchsedWidgets = ArrayList<String>()
    var timerCallbackConst: Long = 1000


    override fun onCreateView() {
        super.onCreateView()
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
        isOpenHomeFragment = intent.getBooleanExtra("isComingFromOrderConfirm",false)
        isOpenAddOnsFragment = intent.getBooleanExtra("isComingFromOrderConfirmActivation",false)
        //user buying item directly
        widgetFeatureCode = intent.getStringExtra("buyItemKey")
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()

        progressDialog = ProgressDialog(this)

        prefs = SharedPrefs(this)
//        session?.let { deepLinkUtil = DeepLinkUtil(this as AppCompatActivity, it) }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.elevation = 0F
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.circular_menu_back)
        viewModel.setApplicationLifecycle(application, this)
        initView()
    }

    private fun initView() {
        packageViewPagerAdapter = PackageViewPagerAdapter(ArrayList(), this, this)
        featureDealsAdapter = FeatureDealsAdapter(ArrayList(), ArrayList(), this, this)
        partnerViewPagerAdapter = PartnerViewPagerAdapter(ArrayList(), this, this)
        bannerViewPagerAdapter = BannerViewPagerAdapter(ArrayList(), this, this)
        upgradeAdapter = UpgradeAdapter(ArrayList(), this,this)
        addonsCategoryAdapter = AddonsCategoryAdapter(this, ArrayList(), this)
        videosListAdapter = VideosListAdapter(ArrayList(), this)

        //emptyCouponTable everytime for new coupon code
        viewModel.emptyCouponTable()
        viewModel.GetHelp()
//        setSpannableStrings()
        loadData()
        initMvvm()
//        setBackListener (this)
        shimmer_view_package.startShimmer()
        shimmer_view_banner.startShimmer()
//        shimmer_view_recommended.startShimmer()
        shimmer_view_recomm_addons.startShimmer()
        shimmer_view_addon_category.startShimmer()
        WebEngageController.trackEvent(ADDONS_MARKETPLACE_HOME, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
//        Glide.with(this).load(R.drawable.back_beau).apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))).into(back_image)

        imageView21.setOnClickListener {
            finish()
        }

        imageViewCart1.setOnClickListener {
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

        initializeVideosRecycler()
        initializePartnerViewPager()
        initializeBannerViewPager()
        initializePackageViewPager()
        initializeFeatureDeals()
        initializeRecycler()
        initializeAddonCategoryRecycler()

//        if (accountType != null) {
////            recommended_features_account_type.setText(accountType!!.toLowerCase())
//            recommended_features_section.visibility = View.VISIBLE
//            if (shimmer_view_recommended.isShimmerStarted) {
//                shimmer_view_recommended.stopShimmer()
//                shimmer_view_recommended.visibility = View.GONE
//            }
//        } else {
//            recommended_features_section.visibility = View.GONE
//            if (shimmer_view_recommended.isShimmerStarted) {
//                shimmer_view_recommended.stopShimmer()
//                shimmer_view_recommended.visibility = View.GONE
//            }
//        }

        viewModel.getCategoriesFromAssetJson(
            this,
            experienceCode
        )

//        share_refferal_code_btn.setOnClickListener {
//            WebEngageController.trackEvent(
//                ADDONS_MARKETPLACE_REFFER_BOOST_CLICKED,
//                GENERIC,
//                NO_EVENT_VALUE
//            )
//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(
//                Intent.EXTRA_TEXT,
//                getString(R.string.referral_text_1) + fpRefferalCode
//            )
//            sendIntent.type = "text/plain"
//            try {
//                startActivity(sendIntent)
//            } catch (ex: ActivityNotFoundException) {
//                SentryController.captureException(ex)
//                Toasty.error(
//                    applicationContext,
//                    "Failed to share the referral code. Please try again.",
//                    Toast.LENGTH_SHORT,
//                    true
//                ).show()
//            }
//        }

        refer_and_earn.setOnClickListener {
            startReferralView()
        }
        /*share_fb_1.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_REFFER_BOOST_CLICKED, FACEBOOK_MESSENGER, NO_EVENT_VALUE)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent
                    .putExtra(
                            Intent.EXTRA_TEXT,
                            getString(R.string.referral_text_2) + fpRefferalCode
                    )
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.facebook.orca")
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {

                Toasty.error(
                        applicationContext,
                        "Please Install Facebook Messenger",
                        Toast.LENGTH_SHORT,
                        true
                ).show()

            }
        }
        share_whatsapp_1.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_REFFER_BOOST_CLICKED, WHATSAPP, NO_EVENT_VALUE)
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            whatsappIntent.setPackage(getString(R.string.whatsapp_package))
            whatsappIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.referral_text_1) + fpRefferalCode
            )
            try {
                Objects.requireNonNull(this).startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")
                        )
                )
            }

        }*/
        /*share_referal.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_REFFER_BOOST_CLICKED, CARD, NO_EVENT_VALUE)
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    R.string.referral_text_2
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }*/

//        if(::localStorage.isInitialized) {
//            cart_list = localStorage.getCartItems()
//            if (cart_list != null) {
//                badgeNumber = cart_list!!.size
//                badge.setText(badgeNumber.toString())
//                Constants.CART_VALUE = badgeNumber
//            }
//        }


        view_my_current_plan.setOnClickListener {
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            addFragmentHome(
//                MyAddonsFragment.newInstance(),
//                MYADDONS_FRAGMENT, args
//            )
            val intent = Intent(this, MyCurrentPlanActivity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
            intent.putExtra("fpid", fpid)
            startActivity(intent)
        }


        all_recommended_addons.setOnClickListener {
            WebEngageController.trackEvent(
                CLICKED_VIEW_ALL_RECOMMENDED_ADD_ONS,
                ADDONS_MARKETPLACE,
                NULL
            )
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            addFragmentHome(
//                ViewAllFeaturesFragment.newInstance(),
//                VIEW_ALL_FEATURE, args
//            )
            val intent = Intent(this, BrowseFeaturesActivity::class.java)
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
            // intent.putExtra("itemId", it.feature_code)

            startActivity(intent)

        }

        if (screenType == "myAddOns") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            addFragmentHome(
//                MyAddonsFragment.newInstance(),
//                MYADDONS_FRAGMENT, args
//            )
            val intent = Intent(this, MyCurrentPlanActivity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
            startActivity(intent)
        } else if (screenType == "recommendedAddOns") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            addFragmentHome(
//                ViewAllFeaturesFragment.newInstance(),
//                VIEW_ALL_FEATURE, args
//            )

            val intent = Intent(this, BrowseFeaturesActivity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
            startActivity(intent)
        } else if (screenType == "comparePackageSelection") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            addFragmentHome(
//                ComparePackageFragment.newInstance(),
//                COMPARE_FRAGMENT, args
//            )
            val intent = Intent(this, ComparePacksActivity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
            startActivity(intent)
        } else if (screenType == "packageBundle") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            Log.v(
                "getPackageItem",
                " " + widgetFeatureCode + " " + screenType
            )
//            args.putString("packageIdentifier", widgetFeatureCode)
            try {
                getPackageItem(widgetFeatureCode)
            } catch (e: Exception) {
                SentryController.captureException(e)
            }

//            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            /*addFragmentHome(
                    PackageFragment.newInstance(),
                    PACKAGE_FRAGMENT, args
            )*/
        } else if (screenType == "promoBanner") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }

            try {
                getItemPromoBanner(widgetFeatureCode)
            } catch (e: Exception) {
                SentryController.captureException(e)
            }
        } else if (screenType == "expertContact") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            callExpertContact(prefs.getExpertContact())


        }

        //chat bot view button clicked
//        view_chat.setOnClickListener {
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putString("itemId", "CHATBOT")
//            details.arguments = args
//            addFragment(details, Constants.DETAILS_FRAGMENT)
//        }

        //share feed back button
        share_feedback_button.setOnClickListener {
            if (feedBackLink != null) {
//                val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
//                val args = Bundle()
//                args.putString("link", feedBackLink)
//                webViewFragment.arguments = args
//                addFragment(
//                        webViewFragment,
//                        Constants.WEB_VIEW_FRAGMENT
//                )
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(feedBackLink)
                startActivity(i)
            } else {
                Toasty.error(
                    applicationContext,
                    "Feedback Service Not Available. Try Later..",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        package_compare_layout.setOnClickListener {
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            addFragmentHome(
//                ComparePackageFragment.newInstance(),
//                COMPARE_FRAGMENT, args
//            )
            val intent = Intent(this, ComparePacksActivity::class.java)
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
        mp_review_cart_close_iv.setOnClickListener{
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_WAITING_CART_CROSS_CLICKED,EVENT_LABEL_ADDONS_MARKETPLACE_WAITING_CART_CROSS_CLICKED,NO_EVENT_VALUE)
            mp_view_cart_rl.visibility = View.GONE
        }
        mp_review_cart_tv.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_WAITING_CART_EXPERT_REVIEW_CLICKED,EVENT_LABEL_ADDONS_MARKETPLACE_WAITING_CART_EXPERT_REVIEW_CLICKED,NO_EVENT_VALUE)
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

    override fun getLayout(): Int {
        return R.layout.activity_marketplace
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add -> {
                WebEngageController.trackEvent(ADDONS_MARKETPLACE_WAITING_CART_EXPERT_REVIEW_CLICKED,EVENT_LABEL_ADDONS_MARKETPLACE_WAITING_CART_EXPERT_REVIEW_CLICKED,NO_EVENT_VALUE)
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
                true
            }
            R.id.overflowMenu -> {
                Toast.makeText(applicationContext, "Clicked on More Button", Toast.LENGTH_LONG)
                    .show()
                true
            }
            R.id.plan_history -> {
                Toast.makeText(applicationContext, "Clicked on Plan History", Toast.LENGTH_LONG)
                    .show()
                return true
            }
            R.id.offer_coupons -> {
                startSpecificActivity(OfferCouponsActivity::class.java)
                Toast.makeText(applicationContext, "Clicked on Offer Coupons", Toast.LENGTH_LONG)
                    .show()
                return true
            }
            R.id.help_section -> {
                val videoshelp = HelpVideosBottomSheet()
                val args = Bundle()
                videoshelp.arguments= args
                videoshelp.show(this.supportFragmentManager, HelpVideosBottomSheet::class.java.name)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler.apply {
            layoutManager = gridLayoutManager
        }
    }


  fun startSpecificActivity(otherActivityClass: Class<*>?) {
    val intent = Intent(applicationContext, otherActivityClass)
    startActivity(intent)
  }
    fun startReferralView() {
        try {
            WebEngageController.trackEvent(REFER_A_FRIEND_CLICK, CLICK, TO_BE_ADDED)
            val webIntent =
                Intent(this, Class.forName("com.nowfloats.helper.ReferralTransActivity"))
            startActivity(webIntent)
//            overridePendingTransition(0, 0)
        } catch (e: ClassNotFoundException) {
            SentryController.captureException(e)
            e.printStackTrace()
            SentryController.captureException(e)
        }
    }

    private fun initializeAddonCategoryRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        addons_category_recycler.apply {
            layoutManager = gridLayoutManager
            addons_category_recycler.adapter = addonsCategoryAdapter
        }
    }


//    fun setSpannableStrings() {
//        var fpId = fpid
//        if (fpId != null) {
//            val minLength = 5
//
//            fpRefferalCode = ""
//            for (c in fpId) {
//                fpRefferalCode += (c + 1).toString().trim().toUpperCase()
//
//                if (fpRefferalCode.length >= minLength)
//                    break
//            }
//
//            var lengthDiff = minLength - (fpRefferalCode.length % 5)
//
//            while (lengthDiff-- > 0) {
//                fpRefferalCode += lengthDiff.toString()
//            }
//        }
//        fpRefferalCode = experienceCode + fpRefferalCode
//        referral_code.text = fpRefferalCode
//
//
//        val referralText = SpannableString(getString(R.string.upgrade_boost_tnc_link))
//        referralText.setSpan(UnderlineSpan(), 0, referralText.length, 0)
//        boost360_tnc.text = referralText
//        /*boost360_tnc.setOnClickListener {
//            WebEngageController.trackEvent(ADDONS_MARKETPLACE_TN_C_CLICKED, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
//            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
//            val args = Bundle()
//            args.putString("link", "https://www.getboost360.com/tnc")
//            webViewFragment.arguments = args
//            addFragment(
//                    webViewFragment,
//                    Constants.WEB_VIEW_FRAGMENT
//            )
//        }*/
//
//        /* val refText = SpannableString(getString(R.string.referral_card_explainer_text))
//         refText.setSpan(StyleSpan(Typeface.BOLD), 18, 26, 0)
//         refText.setSpan(StyleSpan(Typeface.BOLD), refText.length - 4, refText.length, 0)
//         refText.setSpan(
//                 ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.common_text_color)),
//                 0,
//                 refText.length,
//                 0
//         )
//         ref_txt.text = refText
//         ref_txt.text = "Get special discounts for you and your friends after successful signup."*/
//    }

    @SuppressLint("LongLogTag")
    fun loadData() {
        val pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        var code: String = experienceCode!!
        if (!code.equals("null", true)) {
            viewModel.setCurrentExperienceCode(code, fpTag!!)
        }

        try {
            viewModel.loadUpdates(
                getAccessToken() ?: "",
                this.fpid!!,
                this.clientid,
                this.experienceCode,
                this.fpTag
            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

        //check if there is a active addons
        AzureWebsiteNewRepository.getFeatureDetails(this.fpid!!, clientId).toLiveData()
            .observeForever {
                var paidUser = false
                if (it.isSuccess() || it != null) {
                    val data = it.arrayResponse as? Array<CapLimitFeatureResponseItem>
                    Log.e("checkExpiryAddonsPackages >>", Gson().toJson(data))
                    for (singleitem in data!!) {
                        val date1: Date =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(singleitem.expiryDate!!)

                        val diff: Long = date1.getTime() - Date().getTime()
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        val days = hours / 24

                        if(days>0){
                            paidUser = true
                            break
                        }
                    }
                }
                if(paidUser){
                    bottom_box.visibility = View.VISIBLE
                    footer.visibility = View.VISIBLE
                }else{
                    bottom_box.visibility = View.GONE
                    footer.visibility = View.GONE
                }
            }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {
        viewModel.updatesError().observe(this, androidx.lifecycle.Observer {
            longToast(applicationContext, "onFailure: " + it)
        })

        viewModel.getAllAvailableFeatures().observe(this, androidx.lifecycle.Observer {
            all_recommended_addons.visibility = View.VISIBLE
            updateRecycler(it)
            updateAddonCategoryRecycler(it)
        })

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            val list = arrayListOf<Bundles>()
            for (item in it) {
                val temp = Gson().fromJson<List<IncludedFeature>>(
                    item.included_features,
                    object : TypeToken<List<IncludedFeature>>() {}.type
                )
                list.add(
                    Bundles(
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
                        null, item.desc
                    )
                )
            }
            if (list.size > 0) {
                if (shimmer_view_package.isShimmerStarted) {
                    shimmer_view_package.stopShimmer()
                    shimmer_view_package.visibility = View.GONE
                }
                package_layout.visibility = View.VISIBLE
                package_compare_layout.visibility = View.VISIBLE
                updatePackageViewPager(list)
            } else {
                if (shimmer_view_package.isShimmerStarted) {
                    shimmer_view_package.stopShimmer()
                    shimmer_view_package.visibility = View.GONE
                }
                package_layout.visibility = View.GONE
                package_compare_layout.visibility = View.GONE
            }
        })

        viewModel.getBackAllBundles().observe(this, androidx.lifecycle.Observer {
            val list = arrayListOf<Bundles>()
            for (item in it) {
                val temp = Gson().fromJson<List<IncludedFeature>>(
                    item.included_features,
                    object : TypeToken<List<IncludedFeature>>() {}.type
                )
                list.add(
                    Bundles(
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
                        null, item.desc
                    )
                )
            }
            if (list.size > 0) {
                package_layout.visibility = View.VISIBLE
                package_compare_layout.visibility = View.VISIBLE
//                updatePackageViewPager(list)
                updatePackageBackPressViewPager(list)
            } else {
                package_layout.visibility = View.GONE
                package_compare_layout.visibility = View.GONE
            }
        })

        viewModel.getAllFeatureDeals().observe(this, androidx.lifecycle.Observer {
            if (it.size > 0) {
                var cartItems: List<CartModel> = arrayListOf()
                if (viewModel.cartResult.value != null) {
                    cartItems = viewModel.cartResult.value!!
                }
                updateFeatureDealsViewPager(it, cartItems)
            }
        })

//        viewModel.getTotalActiveWidgetCount().observe(this, androidx.lifecycle.Observer {
//            total_active_widget_count.text = it.toString()
//        })

        viewModel.categoryResult().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
//                if (recommended_features_account_type.paint.measureText(
//                        Html.fromHtml(it!!.toLowerCase()).toString()
//                    ) > recommended_features_account_type.measuredWidth
//                ) {
//                    recommended_features_account_type.visibility = View.GONE
//                    recommended_features_additional_tv.text = Html.fromHtml(it!!.toLowerCase())
//
//                } else {
//                    recommended_features_account_type.setText(Html.fromHtml(it!!.toLowerCase()))
//                    recommended_features_additional_tv.visibility = View.GONE
//                }
                recommended_features_additional_tv.text = Html.fromHtml("Top "+it!!.toLowerCase()+" features")
            }

        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })

        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            UserSessionManager(this).storeIntDetails(
                Key_Preferences.KEY_FP_CART_COUNT,
                it.size ?: 0
            )
            if (it != null && it.size > 0) {
//                packageInCartStatus = false
                mp_view_cart_rl.visibility = View.VISIBLE
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())

                itemTypeArrayList?.clear()
                it.forEach {
                    itemTypeArrayList?.add(it.item_type.toString())
                }
                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
                    if (badgeNumber == 1) {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " item waiting in cart"
                    } else {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " items waiting in cart"
                    }
                } else if (itemTypeArrayList!!.contains("features")) {
                    if (badgeNumber == 1) {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " item waiting in cart"
                    } else {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " items waiting in cart"
                    }
                } else if (itemTypeArrayList!!.contains("bundles")) {
                    if (badgeNumber == 1) {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " pack waiting in cart"
                    } else {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " packs waiting in cart"
                    }

                }
                itemsArrayList?.clear()
                it.forEach {
                    if (it.item_type.equals("features")) {
                        itemsArrayList?.add(it.item_name.toString())
                    }
                }

                var cartItems = " "
                if (itemsArrayList != null && itemsArrayList!!.size > 0) {
                    for (items in itemsArrayList!!) {
                        cartItems += items + ", "
                    }
                    cartItems = cartItems.substring(0, cartItems.length - 2)
                    var cartUpdatedItems = ""
                    if (mp_items_name_tv.paint.measureText(cartItems) > 2 * (mp_items_name_tv.measuredWidth)) {
                        val index = itemsArrayList!!.size - 1
                        itemsArrayList!!.removeAt(index)
                        for (updatedItems in itemsArrayList!!) {
                            cartUpdatedItems += updatedItems + ", "
                        }
                        cartUpdatedItems =
                            cartUpdatedItems.substring(0, cartUpdatedItems.length - 2)
                        val displayString =
                            cartUpdatedItems + " + " + (it.size - itemsArrayList!!.size) + " more"
                        var cartUpdatedItems1 = ""
                        if (mp_items_name_tv.paint.measureText(displayString) > 2 * (mp_items_name_tv.measuredWidth)) {
                            val index1 = itemsArrayList!!.size - 1
                            itemsArrayList!!.removeAt(index1)
                            for (updatedItems1 in itemsArrayList!!) {
                                cartUpdatedItems1 += updatedItems1 + ", "
                            }
                            cartUpdatedItems1 =
                                cartUpdatedItems1.substring(0, cartUpdatedItems1.length - 2)
                            val displayString1 =
                                cartUpdatedItems1 + " + " + (it.size - itemsArrayList!!.size) + " more"
                            var cartLatestItems = ""
                            if (mp_items_name_tv.paint.measureText(displayString1) > 2 * (mp_items_name_tv.measuredWidth)) {
                                val latestIndex = itemsArrayList!!.size - 1
                                itemsArrayList!!.removeAt(latestIndex)
                                for (latestItems in itemsArrayList!!) {
                                    cartLatestItems += latestItems + ", "
                                }
                                cartLatestItems =
                                    cartLatestItems.substring(0, cartLatestItems.length - 2)
                                val displayString2 =
                                    cartLatestItems + " + " + (it.size - itemsArrayList!!.size) + " more"
                                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                        "bundles"
                                    ).not()
                                ) {
                                    if (itemsArrayList!!.size == 1) {
                                        val singleFeature = displayString2.replace(",", "")
                                        mp_items_name_tv.text = singleFeature.replace(" ", "\u00A0")
                                    } else {
                                        mp_items_name_tv.text =
                                            displayString2.replace(" ", "\u00A0")
                                    }

                                }
                            } else {
                                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                        "bundles"
                                    ).not()
                                ) {
                                    if (itemsArrayList!!.size == 1) {
                                        val singleFeature1 = displayString1.replace(",", "")
                                        mp_items_name_tv.text =
                                            singleFeature1.replace(" ", "\u00A0")

                                    } else {
                                        mp_items_name_tv.text =
                                            displayString1.replace(" ", "\u00A0")
                                    }
                                }
                            }
                        } else {
                            if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                    "bundles"
                                ).not()
                            ) {
                                if (itemsArrayList!!.size == 1) {
                                    val singleFeature2 = displayString.replace(",", "")
                                    mp_items_name_tv.text = singleFeature2.replace(" ", "\u00A0")

                                } else {
                                    mp_items_name_tv.text = displayString.replace(" ", "\u00A0")
                                }
                            }
                        }
                    } else {
                        if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                "bundles"
                            ).not()
                        ) {
                            if (itemsArrayList!!.size == 1) {
                                val singleFeature3 = cartItems.replace(",", "")
                                mp_items_name_tv.text = singleFeature3.replace(" ", "\u00A0")

                            } else {
                                mp_items_name_tv.text = cartItems.replace(" ", "\u00A0")
                            }
                        }
                    }

                }
                val itemDesc = prefs.getStoreAddedPackageDesc()
                packsArrayList?.clear()
                it.forEach { it ->
                    if (it.item_type.equals("bundles"))
                        packsArrayList?.add(it.item_name.toString())
                }
                var packCartItems = ""
                if (packsArrayList != null && packsArrayList!!.size > 0) {
                    for (packs in packsArrayList!!) {
                        packCartItems += packs
                    }
                    if (packsArrayList!!.size == 1) {
                        if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains(
                                "features"
                            ).not()
                        ) {
                            mp_items_name_tv.text = itemDesc
                        }
                    } else if (packsArrayList!!.size >= 2) {
                        if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains(
                                "features"
                            ).not()
                        ) {
                            mp_items_name_tv.text =
                                "Get a pack that serves the need of your growing business."
                        }
                    }
                }

                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
                    if (packsArrayList != null && packsArrayList!!.size > 0 && itemsArrayList != null && itemsArrayList!!.size > 0) {
                        var totalPackItems = ""
                        for (totalPacks in packsArrayList!!) {
                            totalPackItems += totalPacks + " Pack" + ", "
                        }
                        var totalFeatureItems = ""
                        for (totalFeatures in itemsArrayList!!) {
                            totalFeatureItems += totalFeatures + ", "
                        }
                        totalFeatureItems =
                            totalFeatureItems.substring(0, totalFeatureItems.length - 2)
                        mp_items_name_tv.text = totalPackItems + totalFeatureItems
                    }
                }

                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge.visibility = View.GONE
                mp_view_cart_rl.visibility = View.GONE
            }
            //refresh FeatureDeals adaptor when cart is updated
            if (viewModel.allFeatureDealsResult.value != null) {
                val list = viewModel.allFeatureDealsResult.value!!
                if (list.size > 0) {
                    updateFeatureDealsViewPager(list, it)
                }
            }

            /*if (viewModel.allBundleResult.value != null){
                var list = viewModel.allBundleResult.value!!
                if (list.size > 0){
                    for (item in list){
                        it.forEach {
                            if(it.item_id.equals(item.bundle_id)){
                                packageInCartStatus = true
                            }
                        }
                    }
                }
            }*/

            /*if (viewModel.allBundleResult.value != null) {


                var list = viewModel.allBundleResult.value!!
                if (list.size > 0) {
                    val listItem = arrayListOf<Bundles>()
                    for (item in list) {
                        val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                        listItem.add(Bundles(
                                item.bundle_id,
                                temp,
                                item.min_purchase_months,
                                item.name,
                                item.overall_discount_percent,
                                PrimaryImage(item.primary_image),
                                item.target_business_usecase,
                                Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                                null,item.desc
                        ))
                    }
                    if (list.size > 0) {
      //                        updatePackageViewPager(listItem)
                        packageViewPagerAdapter.addupdates(listItem)
                        packageViewPagerAdapter.notifyDataSetChanged()
                    }
                }
            }*/
        })

        viewModel.cartResultBack().observe(this, androidx.lifecycle.Observer {
            UserSessionManager(this).storeIntDetails(
                Key_Preferences.KEY_FP_CART_COUNT,
                it.size ?: 0
            )
            if (it != null && it.size > 0) {
//                packageInCartStatus = false
                mp_view_cart_rl.visibility = View.VISIBLE
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                itemTypeArrayList?.clear()
                it.forEach {
                    itemTypeArrayList?.add(it.item_type.toString())
                }
                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
                    if (badgeNumber == 1) {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " item waiting in cart"
                    } else {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " items waiting in cart"
                    }
                } else if (itemTypeArrayList!!.contains("features")) {
                    if (badgeNumber == 1) {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " item waiting in cart"
                    } else {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " items waiting in cart"
                    }
                } else if (itemTypeArrayList!!.contains("bundles")) {
                    if (badgeNumber == 1) {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " pack waiting in cart"
                    } else {
                        mp_no_of_cart_items_tv.text =
                            badgeNumber.toString() + " packs waiting in cart"
                    }

                }
                itemsArrayList?.clear()
                it.forEach {
                    if (it.item_type.equals("features")) {
                        itemsArrayList?.add(it.item_name.toString())
                    }
                }

                var cartItems = ""
                if (itemsArrayList != null && itemsArrayList!!.size > 0) {
                    for (items in itemsArrayList!!) {
                        cartItems += items + ", "
                    }
                    cartItems = cartItems.substring(0, cartItems.length - 2)
                    var cartUpdatedItems = ""
                    if (mp_items_name_tv.paint.measureText(cartItems) > 2 * (mp_items_name_tv.measuredWidth)) {
                        val index = itemsArrayList!!.size - 1
                        itemsArrayList!!.removeAt(index)
                        for (updatedItems in itemsArrayList!!) {
                            cartUpdatedItems += updatedItems + ", "
                        }
                        cartUpdatedItems =
                            cartUpdatedItems.substring(0, cartUpdatedItems.length - 2)
                        val displayString =
                            cartUpdatedItems + " +" + (it.size - itemsArrayList!!.size) + " more"
                        var cartUpdatedItems1 = ""
                        if (mp_items_name_tv.paint.measureText(displayString) > 2 * (mp_items_name_tv.measuredWidth)) {
                            val index1 = itemsArrayList!!.size - 1
                            itemsArrayList!!.removeAt(index1)
                            for (updatedItems1 in itemsArrayList!!) {
                                cartUpdatedItems1 += updatedItems1 + ", "
                            }
                            cartUpdatedItems1 =
                                cartUpdatedItems1.substring(0, cartUpdatedItems1.length - 2)
                            val displayString1 =
                                cartUpdatedItems1 + " +" + (it.size - itemsArrayList!!.size) + " more"
                            var cartLatestItems = ""
                            if (mp_items_name_tv.paint.measureText(displayString1) > 2 * (mp_items_name_tv.measuredWidth)) {
                                val latestIndex = itemsArrayList!!.size - 1
                                itemsArrayList!!.removeAt(latestIndex)
                                for (latestItems in itemsArrayList!!) {
                                    cartLatestItems += latestItems + ", "
                                }
                                cartLatestItems =
                                    cartLatestItems.substring(0, cartLatestItems.length - 2)
                                val displayString2 =
                                    cartLatestItems + " +" + (it.size - itemsArrayList!!.size) + " more"
                                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                        "bundles"
                                    ).not()
                                ) {
                                    if (itemsArrayList!!.size == 1) {
                                        val singleFeature = displayString2.replace(",", "")
                                        mp_items_name_tv.text = singleFeature.replace(" ", "\u00A0")
                                    } else {
                                        mp_items_name_tv.text =
                                            displayString2.replace(" ", "\u00A0")
                                    }
                                }
                            } else {
                                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                        "bundles"
                                    ).not()
                                ) {
                                    if (itemsArrayList!!.size == 1) {
                                        val singleFeature1 = displayString1.replace(",", "")
                                        mp_items_name_tv.text =
                                            singleFeature1.replace(" ", "\u00A0")

                                    } else {
                                        mp_items_name_tv.text =
                                            displayString1.replace(" ", "\u00A0")
                                    }
                                }
                            }
                        } else {
                            if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                    "bundles"
                                ).not()
                            ) {
                                if (itemsArrayList!!.size == 1) {
                                    val singleFeature2 = displayString.replace(",", "")
                                    mp_items_name_tv.text = singleFeature2.replace(" ", "\u00A0")

                                } else {
                                    mp_items_name_tv.text = displayString.replace(" ", "\u00A0")
                                }
                            }
                        }
                    } else {
                        if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains(
                                "bundles"
                            ).not()
                        ) {
                            if (itemsArrayList!!.size == 1) {
                                val singleFeature3 = cartItems.replace(",", "")
                                mp_items_name_tv.text = singleFeature3.replace(" ", "\u00A0")

                            } else {
                                mp_items_name_tv.text = cartItems.replace(" ", "\u00A0")
                            }
                        }
                    }


                }
                val itemDesc = prefs.getStoreAddedPackageDesc()
                packsArrayList?.clear()
                it.forEach { it ->
                    if (it.item_type.equals("bundles"))
                        packsArrayList?.add(it.item_name.toString())
                }
                var packCartItems = ""
                if (packsArrayList != null && packsArrayList!!.size > 0) {
                    for (packs in packsArrayList!!) {
                        packCartItems += packs
                    }
                    if (packsArrayList!!.size == 1) {
                        if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains(
                                "features"
                            ).not()
                        ) {
                            mp_items_name_tv.text = itemDesc
                        }
                    } else if (packsArrayList!!.size >= 2) {
                        if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains(
                                "features"
                            ).not()
                        ) {
                            mp_items_name_tv.text =
                                "Get a pack that serves the need of your growing business."
                        }
                    }
                }

                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
                    if (packsArrayList != null && packsArrayList!!.size > 0 && itemsArrayList != null && itemsArrayList!!.size > 0) {
                        var totalPackItems = ""
                        for (totalPacks in packsArrayList!!) {
                            totalPackItems += totalPacks + " Pack" + ", "
                        }
                        var totalFeatureItems = ""
                        for (totalFeatures in itemsArrayList!!) {
                            totalFeatureItems += totalFeatures + ", "
                        }
                        totalFeatureItems =
                            totalFeatureItems.substring(0, totalFeatureItems.length - 2)
                        mp_items_name_tv.text = totalPackItems + totalFeatureItems
                    }
                }
                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge.visibility = View.GONE
                mp_view_cart_rl.visibility = View.GONE
            }
            //refresh FeatureDeals adaptor when cart is updated
            if (viewModel.allFeatureDealsResult.value != null) {
                val list = viewModel.allFeatureDealsResult.value!!
                if (list.size > 0) {
                    updateFeatureDealsViewPager(list, it)
                }
            }
            if (Constants.COMPARE_BACK_VALUE == 1) {
                Constants.COMPARE_BACK_VALUE = 0
                if (viewModel.allBundleResult.value != null) {


                    var list = viewModel.allBundleResult.value!!
                    if (list.size > 0) {
                        val listItem = arrayListOf<Bundles>()
                        for (item in list) {
                            val temp = Gson().fromJson<List<IncludedFeature>>(
                                item.included_features,
                                object : TypeToken<List<IncludedFeature>>() {}.type
                            )
                            listItem.add(
                                Bundles(
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
                                    null, item.desc
                                )
                            )
                        }
                        if (list.size > 0) {
//                        updatePackageViewPager(listItem)
                            packageViewPagerAdapter.addupdates(listItem)
                            packageViewPagerAdapter.notifyDataSetChanged()
                        }
                    }
                }
                viewModel.getCartItemsBack()
            }
        })

        viewModel.getYoutubeVideoDetails().observe(this, androidx.lifecycle.Observer {
            Log.e("getYoutubeVideoDetails", it.toString())
            updateVideosViewPager(it)
        })

        viewModel.getExpertConnectDetails().observe(this, androidx.lifecycle.Observer {
            Log.e("getYoutubeVideoDetails", it.toString())
            val expertConnectDetails = it
            if (it.is_online) {
                prefs.storeExpertContact(it.contact_number)
                callnow_layout.visibility = View.VISIBLE
                callnow_image.visibility = View.VISIBLE
                callnow_title.setText(it.line1)
                callnow_desc.setText(it.line2)
                callnow_button.setOnClickListener {
                    WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_EXPERT_SPEAK,
                        CLICK,
                        NO_EVENT_VALUE
                    )
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:" + expertConnectDetails.contact_number)
                    startActivity(Intent.createChooser(callIntent, "Call by:"))
                }
                mp_talk_expert_tv.setOnClickListener {
                    WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_WAITING_CART_EXPERT_CALL_CLICKED,
                        EVENT_LABEL_ADDONS_MARKETPLACE_WAITING_CART_EXPERT_CALL_CLICKED,
                        NO_EVENT_VALUE
                    )
                    val callExpertIntent = Intent(Intent.ACTION_DIAL)
                    callExpertIntent.data = Uri.parse("tel:" + expertConnectDetails.contact_number)
                    startActivity(Intent.createChooser(callExpertIntent, "Call by:"))
                }
            } else {
                callnow_layout.visibility = View.GONE
                callnow_image.visibility = View.GONE
//                call_shedule_layout.visibility = View.VISIBLE
//                call_shedule_title.setText(it.line1)
//                call_shedule_desc.setText(it.line2)
//                if (it.offline_message != null) {
//                    val spannableString = SpannableString(it.offline_message)
//                    spannableString.setSpan(
//                        ForegroundColorSpan(resources.getColor(R.color.navy_blue)),
//                        0,
//                        18,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    call_schedule_timinig.setText(spannableString)
//                }
            }
        })

        viewModel.promoBannerAndMarketOfferResult().observe(this, androidx.lifecycle.Observer {
            if (it.size > 0) {
                if (shimmer_view_banner.isShimmerStarted) {
                    shimmer_view_banner.stopShimmer()
                    shimmer_view_banner.visibility = View.GONE
                }
//                updateBannerViewPager(it)
                banner_layout.visibility = View.VISIBLE
            } else {
                if (shimmer_view_banner.isShimmerStarted) {
                    shimmer_view_banner.stopShimmer()
                    shimmer_view_banner.visibility = View.GONE
                }
                banner_layout.visibility = View.GONE
            }

        })


        viewModel.getPromoBanners().observe(this, androidx.lifecycle.Observer {
            Log.e("getPromoBanners", it.toString())
            if (it.size > 0) {
                if (shimmer_view_banner.isShimmerStarted) {
                    shimmer_view_banner.stopShimmer()
                    shimmer_view_banner.visibility = View.GONE
                }
//                checkBannerDetails(it as ArrayList<PromoBanners>)
//                checkBannerDetailsNew(it as ArrayList<PromoBanners>)
                updateBannerViewPager(it)
                banner_layout.visibility = View.VISIBLE
            } else {
                if (shimmer_view_banner.isShimmerStarted) {
                    shimmer_view_banner.stopShimmer()
                    shimmer_view_banner.visibility = View.GONE
                }
                banner_layout.visibility = View.GONE
            }
        })

        viewModel.getPartnerZone().observe(this, androidx.lifecycle.Observer {
            Log.e("getPartnerZone", it.toString())
            if (it.size > 0) {
                updatePartnerViewPager(it)
                partner_layout.visibility = View.VISIBLE
            } else {
                partner_layout.visibility = View.GONE
            }
        })

        viewModel.getFeedBackLink().observe(this, androidx.lifecycle.Observer {
            Log.e("getFeedBackLink", it.toString())
            feedBackLink = it
        })
//
//        viewModel.getBundleExxists().observe(this,androidx.lifecycle.Observer{
//            Log.d("getBundleExxists", it.toString())
//        })
    }

    fun updateFeatureDealsViewPager(list: List<FeatureDeals>, cartList: List<CartModel>) {
        feature_deals_layout.visibility = View.VISIBLE
        feature_deals_viewpager.offscreenPageLimit = if (list.size > 0) list.size else 1
        featureDealsAdapter.addupdates(list, cartList)
        featureDealsAdapter.notifyDataSetChanged()
        if (list.size > 1) {
            feature_deals_indicator.visibility = View.VISIBLE
        } else {
            feature_deals_indicator.visibility = View.INVISIBLE
        }
    }

    fun updatePartnerViewPager(list: List<PartnerZone>) {
        partner_layout.visibility = View.VISIBLE
        partner_viewpager.offscreenPageLimit = list.size
        partnerViewPagerAdapter.addupdates(list)
        partnerViewPagerAdapter.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
        if (list.size > 1) {
            partner_indicator.visibility = View.VISIBLE
        } else {
            partner_indicator.visibility = View.INVISIBLE
        }
    }

    fun updateBannerViewPager(list: List<PromoBanners>) {
        banner_layout.visibility = View.VISIBLE
        banner_viewpager.offscreenPageLimit = list.size
        bannerViewPagerAdapter.addupdates(list)
        bannerViewPagerAdapter.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
        if (list.size > 1) {
            if (list.size > 2) {
                banner_viewpager.setPageTransformer(SimplePageTransformer())

                val itemDecoration = HorizontalMarginItemDecoration(
                    applicationContext,
//                        R.dimen.viewpager_current_item_horizontal_margin
                    R.dimen.viewpager_current_item_horizontal_margin1
                )
                banner_viewpager.addItemDecoration(itemDecoration)
            }
            banner_indicator.visibility = View.VISIBLE
        } else {
            banner_indicator.visibility = View.INVISIBLE
        }
    }

    fun updatePackageBackPressViewPager(list: List<Bundles>) {
        initializePackageViewPager()
        package_viewpager.offscreenPageLimit = list.size
        packageViewPagerAdapter.addupdates(list)
        packageViewPagerAdapter.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
        if (list.size > 1) {
            package_indicator.visibility = View.VISIBLE
        } else {
            package_indicator.visibility = View.INVISIBLE
            package_compare_layout.visibility = View.INVISIBLE
        }
    }

    fun updateVideosViewPager(list: List<YoutubeVideoModel>) {
        val link: List<String> = list.get(0).youtube_link!!.split('/')
//        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
////    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
//        videoPlayerWebView.setWebViewClient(WebViewClient())
//        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
        videosListAdapter.addUpdates(list)
        videosListAdapter.notifyDataSetChanged()
//        video_sub_title.text = list.get(0).title
//        video_sub_desc.text = list.get(0).desc
    }

    fun updateRecycler(list: List<FeaturesModel>) {
        if (shimmer_view_recomm_addons.isShimmerStarted) {
            shimmer_view_recomm_addons.stopShimmer()
            shimmer_view_recomm_addons.visibility = View.GONE
        }
        val temp: List<FeaturesModel> = arrayListOf()

        upgradeAdapter.addupdates(list)
        recycler.adapter = upgradeAdapter
        upgradeAdapter.notifyDataSetChanged()
        recycler.isFocusable = false
//        back_image.isFocusable = true
    }

    fun updateAddonCategoryRecycler(list: List<FeaturesModel>) {
        val addonsCategoryTypes = arrayListOf<String>()
        for (singleFeaturesModel in list) {
            if (singleFeaturesModel.target_business_usecase != null && !addonsCategoryTypes.contains(
                    singleFeaturesModel.target_business_usecase
                )
            ) {
                addonsCategoryTypes.add(singleFeaturesModel.target_business_usecase!!)
            }
        }

//        if (shimmer_view_recomm_addons.isShimmerStarted) {
//            shimmer_view_recomm_addons.stopShimmer()
//            shimmer_view_recomm_addons.visibility = View.GONE
//        }
//        if (shimmer_view_addon_category.isShimmerStarted) {
//            shimmer_view_addon_category.stopShimmer()
//            shimmer_view_addon_category.visibility = View.GONE
//        }
        addonsCategoryAdapter.addupdates(addonsCategoryTypes)
        addons_category_recycler.adapter = addonsCategoryAdapter
        addonsCategoryAdapter.notifyDataSetChanged()
        addons_category_recycler.isFocusable = false
//        back_image.isFocusable = true
    }


    fun updatePackageViewPager(list: List<Bundles>) {
        package_viewpager.offscreenPageLimit = list.size
        packageViewPagerAdapter.addupdates(list)
        packageViewPagerAdapter.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
        if (list.size > 1) {
            package_indicator.visibility = View.VISIBLE
        } else {
            package_indicator.visibility = View.INVISIBLE
            package_compare_layout.visibility = View.INVISIBLE
        }
    }

    override fun backComparePress() {
        if (prefs.getCompareState() == 1) {
            prefs.storeCompareState(0)
            val pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
            var code: String = experienceCode!!
            if (!code.equals("null", true)) {
                viewModel.setCurrentExperienceCode(code, fpTag!!)
            }

            try {
                viewModel.loadPackageUpdates(
                    fpid!!,
                    clientid
                )
            } catch (e: Exception) {
                SentryController.captureException(e)
            }
        }
    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }

    override fun onItemClick(position: Int, item: String?, actionType: Int) {
        var intent: Intent? = null
        if (actionType == RecyclerViewActionType.MARKETPLACE_PROMO_BANNER_CLICK.ordinal) {
            intent = Intent(this, MarketPlaceOffersActivity::class.java)
        } else if (actionType == RecyclerViewActionType.PACKS_CLICK.ordinal) {
            intent = Intent(this, ComparePacksActivity::class.java)
        } else if (actionType == RecyclerViewActionType.TOP_FEATURES_CLICK.ordinal) {
            intent = Intent(this, FeatureDetailsActivity::class.java)

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
        } else if (actionType == RecyclerViewActionType.SECONDARY_IMAGE_CLICK.ordinal) {
            intent = Intent(this, FeatureDetailsActivity::class.java)

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
        }
        startActivity(intent)
    }

    override fun onAddonsClicked(item: FeaturesModel) {
        val intent = Intent(this, FeatureDetailsActivity::class.java)

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
        intent.putExtra("itemId", item.feature_code)

//                                                            startActivity(intent)


//                intent.putExtra("itemId", it.feature_code)
//                startActivity(intent)
        // intent.putExtra("itemId", item!!.cta_feature_key)
        startActivity(intent)
    }

    override fun onPackageClicked(item: Bundles?) {

//        WebEngageController.trackEvent(FEATURE_PACKS_CLICKED, CLICK, item?.name
//                ?: NO_EVENT_VALUE)



        val event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
        item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
        item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }

        event_attributes.put("Discount %", item!!.overall_discount_percent)
        event_attributes.put("Package Identifier", item!!._kid)
        item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
        WebEngageController.trackEvent(FEATURE_PACKS_CLICKED, ADDONS_MARKETPLACE, event_attributes)

        /*  val packageFragment = PackageFragment.newInstance()
          val args = Bundle()
          args.putString("bundleData", Gson().toJson(item))
          packageFragment.arguments = args
          addFragment(packageFragment, PACKAGE_FRAGMENT)*/


//        val packageFragment = PackageFragmentNew.newInstance()
//        val args = Bundle()
//        args.putString("bundleData", Gson().toJson(item))
//        packageFragment.arguments = args
//        addFragment(packageFragment, NEW_PACKAGE_FRAGMENT)
        val intent = Intent(this, ComparePacksActivity::class.java)
        intent.putExtra("bundleData", Gson().toJson(item))
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

    override fun onPromoBannerClicked(item: PromoBanners?) {
//        Log.v("PromoBannerClicked >>", item!!.cta_web_link.isNullOrBlank().toString()  + " "+item!!.cta_feature_key.isNullOrBlank().toString() )
        val event_attributes: HashMap<String, Any> = HashMap()
        item?.let { event_attributes.put("title", it.title) }
//        experienceCode?.let { event_attributes.put("category", it) }
//        fpTag?.let { event_attributes.put("customer", it) }
        if (event_attributes.isEmpty()) WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_PROMO_BANNER,
            CLICK,
            NO_EVENT_VALUE
        )
        else WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_PROMO_BANNER,
            CLICK,
            event_attributes
        )
        if (!item!!.cta_feature_key.isNullOrBlank()) {
            if (item!!.cta_feature_key != null) {
//                val details = DetailsFragment.newInstance()
//                val args = Bundle()
//                args.putString("itemId", item!!.cta_feature_key)
//                details.arguments = args
//                addFragment(details, Constants.DETAILS_FRAGMENT)
                val intent = Intent(this, FeatureDetailsActivity::class.java)

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

//                                                            startActivity(intent)


//                intent.putExtra("itemId", it.feature_code)
//                startActivity(intent)
                intent.putExtra("itemId", item!!.cta_feature_key)
                startActivity(intent)

            }
        } else {
            if (!item!!.cta_bundle_identifier.isNullOrBlank()) {
                if (item!!.cta_bundle_identifier != null) {
                    if (item.cta_bundle_identifier.contains("#")) {

                        var cataCategoryIdentifier =
                            item.cta_bundle_identifier.trim().split("#").toTypedArray()
                        var bundleCategory = cataCategoryIdentifier.get(0)
                        var bundleID = cataCategoryIdentifier.get(1)
                        if (experienceCode.equals(bundleCategory)) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(application)!!
                                    .bundlesDao()
                                    .checkBundleKeyExist(bundleID)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it == 1) {
                                            CompositeDisposable().add(
                                                AppDatabase.getInstance(application)!!
                                                    .bundlesDao()
                                                    .getBundleItemById(bundleID)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        var selectedBundle: Bundles? = null
                                                        var item = it
                                                        val temp =
                                                            Gson().fromJson<List<IncludedFeature>>(
                                                                item.included_features,
                                                                object :
                                                                    TypeToken<List<IncludedFeature>>() {}.type
                                                            )
                                                        selectedBundle = Bundles(
                                                            bundleID,
                                                            temp,
                                                            item.min_purchase_months,
                                                            item.name,
                                                            item.overall_discount_percent,
                                                            PrimaryImage(item.primary_image),
                                                            item.target_business_usecase,
                                                            Gson().fromJson<List<String>>(
                                                                item.exclusive_to_categories,
                                                                object :
                                                                    TypeToken<List<String>>() {}.type
                                                            ),
                                                            null,
                                                            item.desc
                                                        )
//                                                        val packageFragment =
//                                                            PackageFragment.newInstance()
//                                                        val args = Bundle()
//                                                        args.putString(
//                                                            "bundleData",
//                                                            Gson().toJson(selectedBundle)
//                                                        )
//                                                        packageFragment.arguments = args
//                                                        addFragment(
//                                                            packageFragment,
//                                                            PACKAGE_FRAGMENT
//                                                        )
                                                        val intent = Intent(this, ComparePacksActivity::class.java)
                                                        intent.putExtra("bundleData", Gson().toJson(selectedBundle))
                                                        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
                                                        startActivity(intent)

                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                            )
                                        } else {
                                            Toasty.error(
                                                applicationContext,
                                                "Bundle Not Available To This Account",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        }
                        Log.d(
                            "bundleCategory",
                            " bundleCategory: " + bundleCategory + " bundleID: " + bundleID + " " + experienceCode
                        )
                    } else {
                        CompositeDisposable().add(
                            AppDatabase.getInstance(application)!!
                                .bundlesDao()
                                .checkBundleKeyExist(item!!.cta_bundle_identifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it == 1) {
                                        CompositeDisposable().add(
                                            AppDatabase.getInstance(application)!!
                                                .bundlesDao()
                                                .getBundleItemById(item!!.cta_bundle_identifier)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe({
                                                    var selectedBundle: Bundles? = null
                                                    var item = it

                                                    val temp =
                                                        Gson().fromJson<List<IncludedFeature>>(
                                                            item.included_features,
                                                            object :
                                                                TypeToken<List<IncludedFeature>>() {}.type
                                                        )
                                                    selectedBundle = Bundles(
                                                        item.bundle_id,
                                                        temp,
                                                        item.min_purchase_months,
                                                        item.name,
                                                        item.overall_discount_percent,
                                                        PrimaryImage(item.primary_image),
                                                        item.target_business_usecase,
                                                        Gson().fromJson<List<String>>(
                                                            item.exclusive_to_categories,
                                                            object :
                                                                TypeToken<List<String>>() {}.type
                                                        ),
                                                        null,
                                                        item.desc
                                                    )
//                                                    val packageFragment =
//                                                        PackageFragment.newInstance()
//                                                    val args = Bundle()
//                                                    args.putString(
//                                                        "bundleData",
//                                                        Gson().toJson(selectedBundle)
//                                                    )
//                                                    packageFragment.arguments = args
//                                                    addFragment(
//                                                        packageFragment,
//                                                        PACKAGE_FRAGMENT
//                                                    )
                                                    val intent = Intent(this, ComparePacksActivity::class.java)
                                                    intent.putExtra("bundleData", Gson().toJson(selectedBundle))
                                                    intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
                                                    startActivity(intent)

                                                }, {
                                                    it.printStackTrace()
                                                })
                                        )
                                    } else {
                                        Toasty.error(
                                            applicationContext,
                                            "Bundle Not Available To This Account",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                        )
                    }
                }
            } else {
                if (!item!!.cta_web_link.isNullOrBlank()) {
                    if (item!!.cta_web_link != null) {

                        if (item!!.cta_web_link.contains("com.biz2.nowfloats.keyboard.home")) {

                            val deepHashMap: HashMap<DynamicLinkParams, String> =
                                DynamicLinksManager().getURILinkParams(Uri.parse(item.cta_web_link))
//                            if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
//                                val viewType = deepHashMap[DynamicLinkParams.viewType]
//                                val buyItemKey = deepHashMap[DynamicLinkParams.buyItemKey]
//                                deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
//                            } else deepLinkUtil?.deepLinkPage(item.cta_web_link!!, "", false)

                        } else {

//                            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
//                            val args = Bundle()
//                            args.putString("title", "")
//                            args.putString("link", item!!.cta_web_link)
//                            webViewFragment.arguments = args
//                            addFragment(
//                                webViewFragment,
//                                Constants.WEB_VIEW_FRAGMENT
//                            )

                            val intent = Intent(this, WebViewActivity::class.java)
                            intent.putExtra("title","")
                            intent.putExtra("link", item!!.cta_web_link)
                            startActivity(intent)
                        }


                    }
                } else if (!item.coupon_code.isNullOrBlank() || !item.cta_offer_identifier.isNullOrBlank()) {
                    if (item.coupon_code != null || item.cta_offer_identifier != null) {
                        CompositeDisposable().add(
                            AppDatabase.getInstance(application)!!
                                .marketOffersDao()
//                                        .getMarketOffersByCouponCode(item!!.coupon_code)
//                                        .getMarketOffersByCouponCode(if(item!!.coupon_code != null ) item.coupon_code else item.cta_offer_identifier )
                                .getMarketOffersById(item.cta_offer_identifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError { }
                                .subscribe({
                                    var selectedMarketOfferModel: MarketPlaceOffers? = null
                                    var item = it

                                    selectedMarketOfferModel = MarketPlaceOffers(
                                        coupon_code = item.coupon_code,
                                        extra_information = item.extra_information!!,
                                        createdon = item.createdon!!,
                                        updatedon = item.updatedon!!,
                                        _kid = item._kid!!,
                                        websiteid = item.websiteid!!,
                                        isarchived = item.isarchived!!,
                                        expiry_date = item.expiry_date!!,
                                        title = item.title!!,
//                                      exclusive_to_categories = Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                                        exclusive_to_categories = arrayListOf(),
                                        image = PrimaryImage(item.image),
                                    )
//                                    val marketPlaceOfferFragment =
//                                        MarketPlaceOfferFragment.newInstance()
//                                    val args = Bundle()
//                                    args.putString(
//                                        "marketOffersData",
//                                        Gson().toJson(selectedMarketOfferModel)
//                                    )
//                                    marketPlaceOfferFragment.arguments = args
//                                    addFragment(
//                                        marketPlaceOfferFragment,
//                                        MARKET_OFFER_FRAGMENT
//                                    )

                                    val intent = Intent(this,MarketPlaceOffersActivity::class.java)
                                    intent.putExtra("marketOffersData", Gson().toJson(selectedMarketOfferModel))
                                    startActivity(intent)

                                }, {
                                    it.printStackTrace()
                                })
                        )
                    }
                }
            }
        }
        /*if (item!!.cta_feature_key != null) {
            val details = DetailsFragment.newInstance()
            val args = Bundle()
            args.putString("itemId", item!!.cta_feature_key)
            details.arguments = args
            addFragment(details, Constants.DETAILS_FRAGMENT)

        } else if (item.cta_bundle_identifier.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                    AppDatabase.getInstance(application)!!
                            .bundlesDao()
                            .checkBundleKeyExist(item!!.cta_bundle_identifier)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (it == 1) {
                                    CompositeDisposable().add(
                                            AppDatabase.getInstance(application)!!
                                                    .bundlesDao()
                                                    .getBundleItemById(item!!.cta_bundle_identifier)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({

                                                        val packageFragment = PackageFragment.newInstance()
                                                        val args = Bundle()
                                                        args.putString("bundleData", Gson().toJson(it))
                                                        packageFragment.arguments = args
                                                        addFragment(packageFragment, PACKAGE_FRAGMENT)

                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                    )
                                } else {
                                    Toasty.error(applicationContext, "Bundle Not Available To This Account", Toast.LENGTH_LONG).show()
                                }
                            }, {
                                it.printStackTrace()
                            })
            )
        } else if (item!!.cta_web_link != null) {
            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("title", "Browser")
            args.putString("link", item!!.cta_web_link)
            webViewFragment.arguments = args
            addFragment(
                    webViewFragment,
                    Constants.WEB_VIEW_FRAGMENT
            )
        }*/

    }

    override fun onShowHidePromoBannerIndicator(status: Boolean) {
        if (status) {
            banner_indicator.visibility = View.VISIBLE
        } else {
            banner_indicator.visibility = View.GONE
        }
    }

    override fun onPartnerZoneClicked(item: PartnerZone?) {

        WebEngageController.trackEvent(
            PARTNER_S_PROMO_BANNERS_CLICKED,
            CLICK,
            item?.title ?: NO_EVENT_VALUE
        )
        Log.i("onPartnerZoneClicked >>", item.toString())
        if (item!!.cta_feature_key.isNullOrEmpty().not()) {

//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putString("itemId", item!!.cta_feature_key)
//            details.arguments = args
//            addFragment(details, Constants.DETAILS_FRAGMENT)

            val intent = Intent(this, FeatureDetailsActivity::class.java)
            intent.putExtra("itemId", item!!.cta_feature_key)
            startActivity(intent)


        } else if (item.cta_bundle_identifier.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
                    .bundlesDao()
                    .checkBundleKeyExist(item!!.cta_bundle_identifier)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(application)!!
                                    .bundlesDao()
                                    .getBundleItemById(item!!.cta_bundle_identifier)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({

//                                        val packageFragment = PackageFragment.newInstance()
//                                        val args = Bundle()
//                                        args.putString("bundleData", Gson().toJson(it))
//                                        packageFragment.arguments = args
//                                        addFragment(
//                                            packageFragment,
//                                            PACKAGE_FRAGMENT
//                                        )
                                        val intent = Intent(this, ComparePacksActivity::class.java)
                                        intent.putExtra("bundleData", Gson().toJson(it))
                                        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
                                        startActivity(intent)

                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        } else {
                            Toasty.error(
                                applicationContext,
                                "Bundle Not Available To This Account",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, {
                        it.printStackTrace()
                    })
            )
        } else if (item.cta_web_link.isNullOrEmpty().not()) {

//            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
//            val args = Bundle()
//            args.putString("title", "")
//            args.putString("link", item!!.cta_web_link)
//            webViewFragment.arguments = args
//            addFragment(
//                webViewFragment,
//                Constants.WEB_VIEW_FRAGMENT
//            )
            val intent = Intent(this, com.boost.marketplace.ui.webview.WebViewActivity::class.java)
            intent.putExtra("title","")
            intent.putExtra("link", item!!.cta_web_link)
            startActivity(intent)
        }
    }

    override fun onShowHidePartnerZoneIndicator(status: Boolean) {
        if (status) {
            partner_indicator.visibility = View.VISIBLE
        } else {
            partner_indicator.visibility = View.GONE
        }
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
        if (item != null) {
            WebEngageController.trackEvent(
                FEATURE_DEALS_ADD_CART_CLICKED, ADDONS_MARKETPLACE, item.name
                    ?: NO_EVENT_VALUE
            )
            viewModel.addItemToCart(item, minMonth)
        }
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
//        val viewallFeatures = ViewAllFeaturesFragment.newInstance()
//        val args = Bundle()
//        args.putString("categoryType", categoryType)
//        args.putStringArrayList(
//            "userPurchsedWidgets",
//            arguments?.getStringArrayList("userPurchsedWidgets")
//        )
//        viewallFeatures.arguments = args
//        addFragment(
//            viewallFeatures,
//            VIEW_ALL_FEATURE
//        )
        val intent = Intent(this, BrowseFeaturesActivity::class.java)
        intent.putExtra("categoryType", categoryType)
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
        // intent.putExtra("itemId", it.feature_code)

        startActivity(intent)
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        WebEngageController.trackEvent(
            VIDEO_GALLERY_CLICKED,
            CLICK,
            videoItem.title ?: NO_EVENT_VALUE
        )
        Log.i("onPlayYouTubeVideo", videoItem.youtube_link ?: "")

        val link = videoItem.youtube_link!!

        val dialogCard = VideosPopUpBottomSheet()
        val args = Bundle()
        args.putString("fpid",fpid)
        args.putString("expCode",experienceCode)
        args.putString("fptag",fpTag)
        args.putString("title",videoItem.title)
        args.putString("link", videoItem.youtube_link)
        dialogCard.arguments = args
        dialogCard.show(this.supportFragmentManager, VideosPopUpBottomSheet::class.java.name)
//        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
////    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
//        videoPlayerWebView.setWebViewClient(WebViewClient())
//        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
////    videoPlayerWebView.setWebChromeClient(WebChromeClient())
//        video_sub_title.text = videoItem.title
//        video_sub_desc.text = videoItem.desc
    }

    override fun onPackageAddToCart(item: Bundles?, imageView: ImageView) {

        Log.d("onPackageAddToCart", " " + item.toString())
//        var bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)
//        var bundleData =  Gson().fromJson<PackageBundles>(item.toString(), object : TypeToken<PackageBundles>() {}.type)

        if (!packageInCartStatus) {
            if (item != null) {
                prefs.storeAddedPackageDesc(item.desc.toString())
                CompositeDisposable().add(
                    AppDatabase.getInstance(application)!!
                        .cartDao()
                        .checkCartBundleExist(item._kid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it == 0) {
                                makeFlyAnimation(imageView)
                                try {
                                    callBundleCart(item,imageView)
                                } catch (e: Exception) {
                                    SentryController.captureException(e)
                                }
                            }else{

                            }
                        }, {
//                            Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG).show()
                        })
                )

//                makeFlyAnimation(imageView)

                /*val itemIds = arrayListOf<String>()
                for (i in item.included_features) {
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
                                featuresList = it
                                var bundleMonthlyMRP = 0
                                val minMonth: Int =
                                    if (item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1

                                for (singleItem in it) {
                                    for (item in item!!.included_features) {
                                        if (singleItem.feature_code == item.feature_code) {
                                            bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
                                        }
                                    }
                                }

                                offeredBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
                                originalBundlePrice = (bundleMonthlyMRP * minMonth).toInt()

                                if (item!!.overall_discount_percent > 0)
                                    offeredBundlePrice =
                                        originalBundlePrice - (originalBundlePrice * item!!.overall_discount_percent / 100)
                                else
                                    offeredBundlePrice = originalBundlePrice

                                //clear cartOrderInfo from SharedPref to requestAPI again
                                prefs.storeCartOrderInfo(null)
                                viewModel.addItemToCartPackage1(
                                    CartModel(
                                        item!!._kid,
                                        null,
                                        null,
                                        item!!.name,
                                        "",
                                        item!!.primary_image!!.url,
                                        offeredBundlePrice.toDouble(),
                                        originalBundlePrice.toDouble(),
                                        item!!.overall_discount_percent,
                                        1,
                                        if (item!!.min_purchase_months != null) item!!.min_purchase_months!! else 1,
                                        "bundles",
                                        null
                                    )
                                )
                                val event_attributes: HashMap<String, Any> = HashMap()
                                item!!.name?.let { it1 ->
                                    event_attributes.put(
                                        "Package Name",
                                        it1
                                    )
                                }
                                item!!.target_business_usecase?.let { it1 ->
                                    event_attributes.put(
                                        "Package Tag",
                                        it1
                                    )
                                }
                                event_attributes.put("Package Price", originalBundlePrice)
                                event_attributes.put("Discounted Price", offeredBundlePrice)
                                event_attributes.put("Discount %", item!!.overall_discount_percent)
                                item!!.min_purchase_months?.let { it1 ->
                                    event_attributes.put(
                                        "Validity",
                                        it1
                                    )
                                }
                                WebEngageController.trackEvent(
                                    ADDONS_MARKETPLACE_PACKAGE_ADDED_TO_CART,
                                    ADDED,
                                    event_attributes
                                )
//                packageInCartStatus = true
//                package_submit.background = ContextCompat.getDrawable(
//                        applicationContext,
//                        R.drawable.added_to_cart_grey
//                )
//                package_submit.setTextColor(Color.parseColor("#bbbbbb"))
//                package_submit.setText(getString(R.string.added_to_cart))
                                badgeNumber = badgeNumber + 1
//                badge121.setText(badgeNumber.toString())
//                badge121.visibility = View.VISIBLE
                                Constants.CART_VALUE = badgeNumber
//                                viewModel.getCartItems()
                            },
                            {
                                it.printStackTrace()

                            }
                        )
                )*/

            }
        }
    }


    private fun initializeBannerViewPager() {
        banner_layout.visibility = View.VISIBLE
        banner_viewpager.adapter = bannerViewPagerAdapter
        banner_viewpager.offscreenPageLimit = 4
        banner_indicator.setViewPager2(banner_viewpager)
        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
//                        R.dimen.viewpager_current_item_horizontal_margin
            R.dimen.viewpager_current_item_horizontal_margin1
        )
        banner_viewpager.addItemDecoration(itemDecoration)

    }

    private fun initializePartnerViewPager() {
        partner_layout.visibility = View.VISIBLE
        partner_viewpager.adapter = partnerViewPagerAdapter
        partner_viewpager.offscreenPageLimit = 4
        partner_indicator.setViewPager2(partner_viewpager)

//        partner_viewpager.setPageTransformer(SimplePageTransformer())
//
//        val itemDecoration = HorizontalMarginItemDecoration(
//                applicationContext,
//                R.dimen.viewpager_current_item_horizontal_margin
//        )
//        partner_viewpager.addItemDecoration(itemDecoration)

    }

    private fun initializeVideosRecycler() {
        val gridLayoutManager = LinearLayoutManager(applicationContext)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        videos_recycler.apply {
            layoutManager = gridLayoutManager
            videos_recycler.adapter = videosListAdapter
        }

    }

    private fun initializePackageViewPager() {
        package_viewpager.adapter = packageViewPagerAdapter
        package_indicator.setViewPager2(package_viewpager)

        package_viewpager.setPageTransformer(SimplePageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        package_viewpager.addItemDecoration(itemDecoration)

    }

    private fun initializeFeatureDeals() {
        feature_deals_viewpager.adapter = featureDealsAdapter
        feature_deals_indicator.setViewPager2(feature_deals_viewpager)

        feature_deals_viewpager.setPageTransformer(SimplePageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        feature_deals_viewpager.addItemDecoration(itemDecoration)
    }

    private fun makeFlyAnimation(targetView: ImageView) {

        CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(600)
            .setDestView(imageViewCart1).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    viewModel.getCartItems()

                }


                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()

    }

    fun callBundleCart(item: Bundles, imageView: ImageView){
        val itemIds = arrayListOf<String>()
        for (i in item.included_features) {
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
                        featuresList = it
                        var bundleMonthlyMRP = 0
                        val minMonth: Int =
                            if (item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1

                        for (singleItem in it) {
                            for (item in item!!.included_features) {
                                if (singleItem.feature_code == item.feature_code) {
                                    bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
                                }
                            }
                        }

                        offeredBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
                        originalBundlePrice = (bundleMonthlyMRP * minMonth).toInt()

                        if (item!!.overall_discount_percent > 0)
                            offeredBundlePrice =
                                originalBundlePrice - (originalBundlePrice * item!!.overall_discount_percent / 100)
                        else
                            offeredBundlePrice = originalBundlePrice

                        //clear cartOrderInfo from SharedPref to requestAPI again
                        prefs.storeCartOrderInfo(null)
                        try {
                            viewModel.addItemToCartPackage1(
                                CartModel(
                                    item!!._kid,
                                    null,
                                    null,
                                    item!!.name,
                                    "",
                                    item!!.primary_image!!.url,
                                    offeredBundlePrice.toDouble(),
                                    originalBundlePrice.toDouble(),
                                    item!!.overall_discount_percent,
                                    1,
                                    if (item!!.min_purchase_months != null) item!!.min_purchase_months!! else 1,
                                    "bundles",
                                    null
                                )
                            )
                        } catch (e: Exception) {
                            SentryController.captureException(e)
                        }
                        val event_attributes: HashMap<String, Any> = HashMap()
                        item!!.name?.let { it1 ->
                            event_attributes.put(
                                "Package Name",
                                it1
                            )
                        }
                        item!!.target_business_usecase?.let { it1 ->
                            event_attributes.put(
                                "Package Tag",
                                it1
                            )
                        }
                        event_attributes.put("Package Price", originalBundlePrice)
                        event_attributes.put("Discounted Price", offeredBundlePrice)
                        event_attributes.put("Discount %", item!!.overall_discount_percent)
                        item!!.min_purchase_months?.let { it1 ->
                            event_attributes.put(
                                "Validity",
                                it1
                            )
                        }
                        WebEngageController.trackEvent(
                            ADDONS_MARKETPLACE_PACKAGE_ADDED_TO_CART,
                            ADDED,
                            event_attributes
                        )
                        badgeNumber = badgeNumber + 1
                        Constants.CART_VALUE = badgeNumber
                    },
                    {
                        it.printStackTrace()

                    }
                )
        )
    }


    fun getPackageItem(packageIdentifier: String?) {
        if (packageIdentifier.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
                    .bundlesDao()
                    .checkBundleKeyExist(packageIdentifier!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(application)!!
                                    .bundlesDao()
                                    .getBundleItemById(packageIdentifier)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({


                                        Log.v("getPackageItem1", " " + it.bundle_id)

                                        var selectedBundle: Bundles? = null
                                        var item = it

                                        val temp = Gson().fromJson<List<IncludedFeature>>(
                                            item.included_features,
                                            object : TypeToken<List<IncludedFeature>>() {}.type
                                        )
                                        selectedBundle = Bundles(
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
                                            null,
                                            item.desc
                                        )

//                                        val packageFragment = PackageFragment.newInstance()
//                                        val args = Bundle()
//                                        args.putString("bundleData", Gson().toJson(selectedBundle))
//                                        args.putStringArrayList(
//                                            "userPurchsedWidgets",
//                                            arguments?.getStringArrayList("userPurchsedWidgets")
//                                        )
//                                        packageFragment.arguments = args
////                                                        (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
//
//                                        (activity as UpgradeActivity).addFragmentHome(
//                                            PackageFragment.newInstance(),
//                                            PACKAGE_FRAGMENT, args
//                                        )
                                        val intent = Intent(this, ComparePacksActivity::class.java)
                                        intent.putExtra("bundleData", Gson().toJson(selectedBundle))
                                        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
                                        startActivity(intent)
//                                                        bundleData = Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type)
//                                                        packageAdaptor = PackageAdaptor((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type))

                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        } else {
                            Toasty.error(
                                applicationContext,
                                "Bundle Not Available To This Account",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, {
                        it.printStackTrace()
                    })
            )
        }
    }

    fun callExpertContact(phone: String?) {
        Log.d("callExpertContact", " " + phone)
        if (phone != null) {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_EXPERT_SPEAK, CLICK, NO_EVENT_VALUE)
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + phone)
            startActivity(Intent.createChooser(callIntent, "Call by:"))
        } else {
            Toasty.error(
                applicationContext,
                "Expert will be available tomorrow from 10AM",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun getItemPromoBanner(item: String?) {
        if (item.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
                    .bundlesDao()
                    .checkBundleKeyExist(item!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(application)!!
                                    .bundlesDao()
                                    .getBundleItemById(item)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({


                                        Log.v("getItemPromoBanner1", " " + item)

                                        var selectedBundle: Bundles? = null
                                        var item = it

                                        val temp = Gson().fromJson<List<IncludedFeature>>(
                                            item.included_features,
                                            object : TypeToken<List<IncludedFeature>>() {}.type
                                        )
                                        selectedBundle = Bundles(
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
                                            null,
                                            item.desc
                                        )

//                                        val packageFragment = PackageFragment.newInstance()
//                                        val args = Bundle()
//                                        args.putString("bundleData", Gson().toJson(selectedBundle))
//                                        args.putStringArrayList(
//                                            "userPurchsedWidgets",
//                                            arguments?.getStringArrayList("userPurchsedWidgets")
//                                        )
//                                        packageFragment.arguments = args
////                                                        (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
//
//                                        (activity as UpgradeActivity).addFragmentHome(
//                                            PackageFragment.newInstance(),
//                                            PACKAGE_FRAGMENT, args
//                                        )
                                        val intent = Intent(this, ComparePacksActivity::class.java)
                                        intent.putExtra("bundleData", Gson().toJson(selectedBundle))
                                        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
                                        startActivity(intent)


//                                                        bundleData = Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type)
//                                                        packageAdaptor = PackageAdaptor((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type))

                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        } else {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(application)!!
                                    .featuresDao()
                                    .checkFeatureTableKeyExist(item!!)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it == 1) {
                                            Log.v("getItemPromoBanner2", " " + item)
                                            CompositeDisposable().add(
                                                AppDatabase.getInstance(application)!!
                                                    .featuresDao()
                                                    .getFeaturesItemByFeatureCode(item)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({

//                                                        val details = DetailsFragment.newInstance()
//                                                        val args = Bundle()
//                                                        args.putString("itemId", it.feature_code)
//                                                        details.arguments = args
//                                                        (activity as UpgradeActivity).addFragment(
//                                                            details,
//                                                            Constants.DETAILS_FRAGMENT
//                                                        )
                                                        val intent = Intent(this, FeatureDetailsActivity::class.java)

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

//                                                            startActivity(intent)


                                                        intent.putExtra("itemId", it.feature_code)
                                                        startActivity(intent)


                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                            )
                                        } else {
                                            Log.v("getItemPromoBanner3", " " + item)
                                            CompositeDisposable().add(
                                                AppDatabase.getInstance(application)!!
                                                    .marketOffersDao()
                                                    .checkOffersTableKeyExist(item!!)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        if (it == 1) {
                                                            CompositeDisposable().add(
                                                                AppDatabase.getInstance(
                                                                    application
                                                                )!!
                                                                    .marketOffersDao()
                                                                    .getMarketOffersById(item)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe({


                                                                        var selectedMarketOfferModel: MarketPlaceOffers? =
                                                                            null
                                                                        var item = it

                                                                        selectedMarketOfferModel =
                                                                            MarketPlaceOffers(
                                                                                coupon_code = item.coupon_code,
                                                                                extra_information = item.extra_information!!,
                                                                                createdon = item.createdon!!,
                                                                                updatedon = item.updatedon!!,
                                                                                _kid = item._kid!!,
                                                                                websiteid = item.websiteid!!,
                                                                                isarchived = item.isarchived!!,
                                                                                expiry_date = item.expiry_date!!,
                                                                                title = item.title!!,
                                                                                exclusive_to_categories = Gson().fromJson<List<String>>(
                                                                                    item.exclusive_to_categories,
                                                                                    object :
                                                                                        TypeToken<List<String>>() {}.type
                                                                                ),
                                                                                image = PrimaryImage(
                                                                                    item.image
                                                                                ),
                                                                            )
//                                                                        val marketPlaceOfferFragment =
//                                                                            MarketPlaceOfferFragment.newInstance()
//                                                                        val args = Bundle()
//                                                                        args.putString(
//                                                                            "marketOffersData",
//                                                                            Gson().toJson(
//                                                                                selectedMarketOfferModel
//                                                                            )
//                                                                        )
//                                                                        marketPlaceOfferFragment.arguments =
//                                                                            args
//                                                                        (activity as UpgradeActivity).addFragment(
//                                                                            marketPlaceOfferFragment,
//                                                                            MARKET_OFFER_FRAGMENT
//                                                                        )
//
                                                                        val intent = Intent(this, MarketPlaceOffersActivity::class.java)
                                                                        intent.putExtra("marketOffersData",
                                                                            Gson().toJson(
                                                                                selectedMarketOfferModel
                                                                            ))
                                                                        startActivity(intent)


                                                                    }, {
                                                                        it.printStackTrace()
                                                                    })
                                                            )
                                                        } else {
                                                            Log.v("getItemPromoBanner3", " " + item)
                                                            if (item.contains("http")) {
//                                                                val webViewFragment: WebViewFragment =
//                                                                    WebViewFragment.newInstance()
//                                                                val args = Bundle()
//                                                                args.putString("title", "")
//                                                                args.putString("link", item)
//                                                                webViewFragment.arguments = args
//                                                                (activity as UpgradeActivity).addFragment(
//                                                                    webViewFragment,
//                                                                    Constants.WEB_VIEW_FRAGMENT
//                                                                )
                                                                val intent = Intent(this, com.boost.marketplace.ui.webview.WebViewActivity::class.java)
                                                                intent.putExtra("title","")
                                                                intent.putExtra("link", item)
                                                                startActivity(intent)
                                                            }

                                                        }
                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                            )
                                        }
                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        }
                    }, {
                        it.printStackTrace()
                    })
            )
        }
    }


}

