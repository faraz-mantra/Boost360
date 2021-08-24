package com.boost.upgrades.ui.home

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.*
import com.boost.upgrades.data.api_model.GetAllFeatures.response.*
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.data.model.YoutubeVideoModel
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.CompareBackListener
import com.boost.upgrades.interfaces.HomeListener
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.compare.ComparePackageFragment
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.marketplace_offers.MarketPlaceOfferFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.packages.PackageFragmentNew
import com.boost.upgrades.ui.webview.WebViewFragment
import com.boost.upgrades.utils.*
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.COMPARE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MARKET_OFFER_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.NEW_PACKAGE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.PACKAGE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.Utils.getRetrofit
import com.boost.upgrades.utils.Utils.longToast
import com.dashboard.utils.DeepLinkUtil
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inventoryorder.utils.DynamicLinkParams
import com.inventoryorder.utils.DynamicLinksManager
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import android.text.style.StyleSpan

import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener

import android.widget.TextView




class HomeFragment : BaseFragment(), HomeListener, CompareBackListener {

    lateinit var root: View
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface
    lateinit var localStorage: LocalStorage

    lateinit var addonsCategoryAdapter: AddonsCategoryAdapter
    lateinit var upgradeAdapter: UpgradeAdapter

    lateinit var progressDialog: ProgressDialog

    lateinit var videosListAdapter: VideosListAdapter
    lateinit var partnerViewPagerAdapter: PartnerViewPagerAdapter
    lateinit var bannerViewPagerAdapter: BannerViewPagerAdapter
    lateinit var packageViewPagerAdapter: PackageViewPagerAdapter
    lateinit var featureDealsAdapter: FeatureDealsAdapter

    var cart_list: List<WidgetModel>? = null
    var badgeNumber = 0
    var fpRefferalCode: String = ""
    var feedBackLink: String? = null
    lateinit var prefs: SharedPrefs
    var packageInCartStatus = false
    var offeredBundlePrice = 0
    var originalBundlePrice = 0
    var featuresList: List<FeaturesModel>? = null
    private var session: UserSessionManager? = null
    private var deepLinkUtil: DeepLinkUtil? = null
    private var itemsArrayList : ArrayList<String>? = ArrayList()


    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.home_fragment, container, false)
        homeViewModelFactory = HomeViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), homeViewModelFactory)
            .get(HomeViewModel::class.java)
        upgradeAdapter = UpgradeAdapter((activity as UpgradeActivity), ArrayList())
        addonsCategoryAdapter =
            AddonsCategoryAdapter((activity as UpgradeActivity), ArrayList(), this)
        videosListAdapter = VideosListAdapter(ArrayList(), this)
        partnerViewPagerAdapter =
            PartnerViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        bannerViewPagerAdapter =
            BannerViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        packageViewPagerAdapter =
            PackageViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        featureDealsAdapter =
            FeatureDealsAdapter(ArrayList(), ArrayList(), (activity as UpgradeActivity), this)
        //request retrofit instance
        ApiService = getRetrofit().create(ApiInterface::class.java)
        progressDialog = ProgressDialog(requireContext())
        localStorage = LocalStorage.getInstance(requireContext())!!
        cart_list = localStorage.getCartItems()
        prefs = SharedPrefs(activity as UpgradeActivity)
        session = UserSessionManager(requireActivity())
        session?.let { deepLinkUtil = DeepLinkUtil(requireActivity() as AppCompatActivity, it) }

        return root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //emptyCouponTable everytime for new coupon code
        viewModel.emptyCouponTable()
        setSpannableStrings()
        loadData()
        initMvvm()
        (activity as UpgradeActivity) setBackListener (this)
//    initYouTube()
        shimmer_view_package.startShimmer()
        shimmer_view_banner.startShimmer()
        shimmer_view_recommended.startShimmer()
        shimmer_view_recomm_addons.startShimmer()
        shimmer_view_addon_category.startShimmer()
        WebEngageController.trackEvent(ADDONS_MARKETPLACE_HOME, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
//        Glide.with(this).load(R.drawable.back_beau).apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))).into(back_image)

        imageView21.setOnClickListener {
            (activity as UpgradeActivity).finish()
        }

        imageViewCart1.setOnClickListener {
            (activity as UpgradeActivity).addFragment(CartFragment.newInstance(), CART_FRAGMENT)
        }

        initializeVideosRecycler()
        initializePartnerViewPager()
        initializeBannerViewPager()
        initializePackageViewPager()
        initializeFeatureDeals()
        initializeRecycler()
        initializeAddonCategoryRecycler()

        if ((activity as UpgradeActivity).accountType != null) {
//            recommended_features_account_type.setText((activity as UpgradeActivity).accountType!!.toLowerCase())
            recommended_features_section.visibility = View.VISIBLE
            if (shimmer_view_recommended.isShimmerStarted) {
                shimmer_view_recommended.stopShimmer()
                shimmer_view_recommended.visibility = View.GONE
            }
        } else {
            recommended_features_section.visibility = View.GONE
            if (shimmer_view_recommended.isShimmerStarted) {
                shimmer_view_recommended.stopShimmer()
                shimmer_view_recommended.visibility = View.GONE
            }
        }

        viewModel.getCategoriesFromAssetJson(
            requireActivity(),
            (activity as UpgradeActivity).experienceCode
        )

        share_refferal_code_btn.setOnClickListener {
            WebEngageController.trackEvent(
                ADDONS_MARKETPLACE_REFFER_BOOST_CLICKED,
                GENERIC,
                NO_EVENT_VALUE
            )
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.referral_text_1) + fpRefferalCode
            )
            sendIntent.type = "text/plain"
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
                Toasty.error(
                    requireContext(),
                    "Failed to share the referral code. Please try again.",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        }

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
                        requireContext(),
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


        view_my_addons.setOnClickListener {
            val args = Bundle()
            args.putStringArrayList(
                "userPurchsedWidgets",
                arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as UpgradeActivity).addFragmentHome(
                MyAddonsFragment.newInstance(),
                MYADDONS_FRAGMENT, args
            )
        }


        all_recommended_addons.setOnClickListener {
            WebEngageController.trackEvent(
                CLICKED_VIEW_ALL_RECOMMENDED_ADD_ONS,
                ADDONS_MARKETPLACE,
                NULL
            )
            val args = Bundle()
            args.putStringArrayList(
                "userPurchsedWidgets",
                arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as UpgradeActivity).addFragmentHome(
                ViewAllFeaturesFragment.newInstance(),
                VIEW_ALL_FEATURE, args
            )
        }

        if (arguments?.getString("screenType") == "myAddOns") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            args.putStringArrayList(
                "userPurchsedWidgets",
                arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as UpgradeActivity).addFragmentHome(
                MyAddonsFragment.newInstance(),
                MYADDONS_FRAGMENT, args
            )
        } else if (arguments?.getString("screenType") == "recommendedAddOns") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            args.putStringArrayList(
                "userPurchsedWidgets",
                arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as UpgradeActivity).addFragmentHome(
                ViewAllFeaturesFragment.newInstance(),
                VIEW_ALL_FEATURE, args
            )
        } else if (arguments?.getString("screenType") == "comparePackageSelection") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            args.putStringArrayList(
                "userPurchsedWidgets",
                arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as UpgradeActivity).addFragmentHome(
                ComparePackageFragment.newInstance(),
                COMPARE_FRAGMENT, args
            )
        } else if (arguments?.getString("screenType") == "packageBundle") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            Log.v(
                "getPackageItem",
                " " + arguments?.getString("buyItemKey") + " " + arguments?.getString("screenType")
            )
//            args.putString("packageIdentifier", arguments?.getString("buyItemKey"))
            getPackageItem(arguments?.getString("buyItemKey"))

//            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            /*(activity as UpgradeActivity).addFragmentHome(
                    PackageFragment.newInstance(),
                    PACKAGE_FRAGMENT, args
            )*/
        } else if (arguments?.getString("screenType") == "promoBanner") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }

            getItemPromoBanner(arguments?.getString("buyItemKey"))
        } else if (arguments?.getString("screenType") == "expertContact") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            callExpertContact(prefs.getExpertContact())


        }

        //chat bot view button clicked
        view_chat.setOnClickListener {
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putString("itemId", "CHATBOT")
//            details.arguments = args
//            (activity as UpgradeActivity).addFragment(details, Constants.DETAILS_FRAGMENT)
        }

        //share feed back button
        share_feedback_button.setOnClickListener {
            if (feedBackLink != null) {
//                val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
//                val args = Bundle()
//                args.putString("link", feedBackLink)
//                webViewFragment.arguments = args
//                (activity as UpgradeActivity).addFragment(
//                        webViewFragment,
//                        Constants.WEB_VIEW_FRAGMENT
//                )
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(feedBackLink)
                startActivity(i)
            } else {
                Toasty.error(
                    requireContext(),
                    "Feedback Service Not Available. Try Later..",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        package_compare_layout.setOnClickListener {
            val args = Bundle()
            args.putStringArrayList(
                "userPurchsedWidgets",
                arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as UpgradeActivity).addFragmentHome(
                ComparePackageFragment.newInstance(),
                COMPARE_FRAGMENT, args
            )
        }
        mp_review_cart_close_iv.setOnClickListener{
            mp_view_cart_rl.visibility = View.GONE
        }
        mp_review_cart_tv.setOnClickListener {
            (activity as UpgradeActivity).addFragment(CartFragment.newInstance(), CART_FRAGMENT)
        }

    }

//  private fun initYouTube() {
//      val youTubePlayerFragment = childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerFragment
////      (activity as UpgradeActivity).initYoutube()
////      youTubePlayerFragment = (activity as UpgradeActivity).youTubePlayerFragment
//
//      youTubePlayerFragment.initialize(youtube_API_KEY, object : YouTubePlayer.OnInitializedListener {
//        override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
//          Log.i("Detail", "YouTube Player onInitializationSuccess")
//
//          // Don't do full screen
//          player.setFullscreen(false)
//          if (!wasRestored) {
//            youTubePlayer = player
//          }
//        }
//
//        override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {
//          Log.i("Detail", "Failed: $youTubeInitializationResult")
//        }
//      })
//
//    val transaction = childFragmentManager.beginTransaction().add
//    transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit()
//
//  }

    fun setSpannableStrings() {
        var fpId = (activity as UpgradeActivity).fpid
        if (fpId != null) {
            val minLength = 5

            fpRefferalCode = ""
            for (c in fpId) {
                fpRefferalCode += (c + 1).toString().trim().toUpperCase()

                if (fpRefferalCode.length >= minLength)
                    break
            }

            var lengthDiff = minLength - (fpRefferalCode.length % 5)

            while (lengthDiff-- > 0) {
                fpRefferalCode += lengthDiff.toString()
            }
        }
        fpRefferalCode = (activity as UpgradeActivity).experienceCode + fpRefferalCode
        referral_code.text = fpRefferalCode


        val referralText = SpannableString(getString(R.string.upgrade_boost_tnc_link))
        referralText.setSpan(UnderlineSpan(), 0, referralText.length, 0)
        boost360_tnc.text = referralText
        /*boost360_tnc.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_TN_C_CLICKED, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("link", "https://www.getboost360.com/tnc")
            webViewFragment.arguments = args
            (activity as UpgradeActivity).addFragment(
                    webViewFragment,
                    Constants.WEB_VIEW_FRAGMENT
            )
        }*/

        /* val refText = SpannableString(getString(R.string.referral_card_explainer_text))
         refText.setSpan(StyleSpan(Typeface.BOLD), 18, 26, 0)
         refText.setSpan(StyleSpan(Typeface.BOLD), refText.length - 4, refText.length, 0)
         refText.setSpan(
                 ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.common_text_color)),
                 0,
                 refText.length,
                 0
         )
         ref_txt.text = refText
         ref_txt.text = "Get special discounts for you and your friends after successful signup."*/
    }

    fun loadData() {
        val pref = requireActivity().getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        var code: String = (activity as UpgradeActivity).experienceCode!!
        if (!code.equals("null", true)) {
            viewModel.setCurrentExperienceCode(code, fpTag!!)
        }

        viewModel.loadUpdates(
            (activity as UpgradeActivity).fpid!!,
            (activity as UpgradeActivity).clientid,
            (activity as UpgradeActivity).experienceCode,
            (activity as UpgradeActivity).fpTag
        )
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm() {

        viewModel.updatesError().observe(this, androidx.lifecycle.Observer {
            //            Snackbar.make(root, viewModel.errorMessage, Snackbar.LENGTH_LONG).show()
//            if (shimmer_view_container.isAnimationStarted) {
//                shimmer_view_container.stopShimmerAnimation()
//                shimmer_view_container.visibility = View.GONE
//            }
            longToast(requireContext(), "onFailure: " + it)
        })

//        viewModel.upgradeResult().observe(this, androidx.lifecycle.Observer {
//            updateRecycler(it)
//        })

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
                updatePackageViewPager(list)
            } else {
                if (shimmer_view_package.isShimmerStarted) {
                    shimmer_view_package.stopShimmer()
                    shimmer_view_package.visibility = View.GONE
                }
                package_layout.visibility = View.GONE
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
//                updatePackageViewPager(list)
                updatePackageBackPressViewPager(list)
            } else {
                package_layout.visibility = View.GONE
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

        viewModel.getTotalActiveWidgetCount().observe(this, androidx.lifecycle.Observer {
            total_active_widget_count.text = it.toString()
        })

        viewModel.categoryResult().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                recommended_features_account_type.setText(Html.fromHtml(it!!.toLowerCase()))
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
            if (it != null && it.size > 0) {
//                packageInCartStatus = false
                mp_view_cart_rl.visibility = View.VISIBLE
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                if(badgeNumber == 1){
                    mp_no_of_cart_items_tv.text = badgeNumber.toString() + " item waiting in cart"
                }else{
                    mp_no_of_cart_items_tv.text = badgeNumber.toString() + " items waiting in cart"
                }
                itemsArrayList?.clear()
                it.forEach {
                    itemsArrayList?.add(it.item_name.toString())
                }
                var cartItems = " "
                if(itemsArrayList!= null && itemsArrayList!!.size > 0){
                     for (items in itemsArrayList!!){
                         cartItems += items + ","
                     }
                    if(isTooLarge(mp_items_name_tv,cartItems)){

                    }
                    mp_items_name_tv.text = cartItems

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
            if (it != null && it.size > 0) {
//                packageInCartStatus = false
                mp_view_cart_rl.visibility = View.VISIBLE
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                if(badgeNumber == 1){
                    mp_no_of_cart_items_tv.text = badgeNumber.toString() + " item waiting in cart"
                }else{
                    mp_no_of_cart_items_tv.text = badgeNumber.toString() + " items waiting in cart"
                }
                itemsArrayList?.clear()
                it.forEach {
                    itemsArrayList?.add(it.item_name.toString())
                }
                var cartItems = ""
              if(itemsArrayList!= null && itemsArrayList!!.size > 0){
                for (items in itemsArrayList!!){
                  cartItems += items + ","
                }
                  mp_items_name_tv.text = cartItems

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
                call_shedule_layout.visibility = View.GONE
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
                    WebEngageController.trackEvent(ADDONS_MARKETPLACE_EXPERT_SPEAK,
                        CLICK,
                        NO_EVENT_VALUE)
                    val callExpertIntent = Intent(Intent.ACTION_DIAL)
                    callExpertIntent.data = Uri.parse("tel:" + expertConnectDetails.contact_number)
                    startActivity(Intent.createChooser(callExpertIntent,"Call by:"))
                }
            } else {
                callnow_layout.visibility = View.GONE
                callnow_image.visibility = View.GONE
                call_shedule_layout.visibility = View.VISIBLE
                call_shedule_title.setText(it.line1)
                call_shedule_desc.setText(it.line2)
                if (it.offline_message != null) {
                    val spannableString = SpannableString(it.offline_message)
                    spannableString.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.navy_blue)),
                        0,
                        18,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    call_schedule_timinig.setText(spannableString)
                }
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
//                checkBannerDetails(it as ArrayList<PromoBanners>)F
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

        viewModel.getBundleExxists().observe(this,androidx.lifecycle.Observer{
            Log.d("getBundleExxists", it.toString())
        })
    }

    fun updateRecycler(list: List<FeaturesModel>) {
        if (shimmer_view_recomm_addons.isShimmerStarted) {
            shimmer_view_recomm_addons.stopShimmer()
            shimmer_view_recomm_addons.visibility = View.GONE
        }
        upgradeAdapter.addupdates(list)
        recycler.adapter = upgradeAdapter
        upgradeAdapter.notifyDataSetChanged()
        recycler.isFocusable = false
        back_image.isFocusable = true
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

        if (shimmer_view_recomm_addons.isShimmerStarted) {
            shimmer_view_recomm_addons.stopShimmer()
            shimmer_view_recomm_addons.visibility = View.GONE
        }
        if (shimmer_view_addon_category.isShimmerStarted) {
            shimmer_view_addon_category.stopShimmer()
            shimmer_view_addon_category.visibility = View.GONE
        }
        addonsCategoryAdapter.addupdates(addonsCategoryTypes)
        addons_category_recycler.adapter = addonsCategoryAdapter
        addonsCategoryAdapter.notifyDataSetChanged()
        addons_category_recycler.isFocusable = false
        back_image.isFocusable = true
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler.apply {
            layoutManager = gridLayoutManager
        }
    }

    private fun initializeAddonCategoryRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        addons_category_recycler.apply {
            layoutManager = gridLayoutManager
            addons_category_recycler.adapter = addonsCategoryAdapter
        }
    }

    fun updateVideosViewPager(list: List<YoutubeVideoModel>) {
        val link: List<String> = list.get(0).youtube_link!!.split('/')
        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
//    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
        videoPlayerWebView.setWebViewClient(WebViewClient())
        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
        videosListAdapter.addUpdates(list)
        videosListAdapter.notifyDataSetChanged()
        video_sub_title.text = list.get(0).title
        video_sub_desc.text = list.get(0).desc
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
//                banner_viewpager.setPageTransformer(SimplePageTransformer())
//
//                val itemDecoration = HorizontalMarginItemDecoration(
//                        requireContext(),
////                        R.dimen.viewpager_current_item_horizontal_margin
//                        R.dimen.viewpager_current_item_horizontal_margin1
//                )
//                banner_viewpager.addItemDecoration(itemDecoration)
            }
            banner_indicator.visibility = View.VISIBLE
        } else {
            banner_indicator.visibility = View.INVISIBLE
        }
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

    private fun initializeBannerViewPager() {
        banner_layout.visibility = View.VISIBLE
        banner_viewpager.adapter = bannerViewPagerAdapter
        banner_viewpager.offscreenPageLimit = 4
        banner_indicator.setViewPager2(banner_viewpager)
        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
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
//                requireContext(),
//                R.dimen.viewpager_current_item_horizontal_margin
//        )
//        partner_viewpager.addItemDecoration(itemDecoration)

    }

    private fun initializeVideosRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        videos_recycler.apply {
            layoutManager = gridLayoutManager
            videos_recycler.adapter = videosListAdapter
        }

    }

    private fun initializePackageViewPager() {
        package_viewpager.adapter = packageViewPagerAdapter
        package_indicator.setViewPager2(package_viewpager)

//        package_viewpager.setPageTransformer(SimplePageTransformer())

//    val itemDecoration = HorizontalMarginItemDecoration(
//        requireContext(),
//        R.dimen.viewpager_current_item_horizontal_margin
//    )
//    package_viewpager.addItemDecoration(itemDecoration)

    }

    private fun initializeFeatureDeals() {
        feature_deals_viewpager.adapter = featureDealsAdapter
        feature_deals_indicator.setViewPager2(feature_deals_viewpager)

        feature_deals_viewpager.setPageTransformer(SimplePageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_current_item_horizontal_margin
        )
        feature_deals_viewpager.addItemDecoration(itemDecoration)
    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
//            viewModel.getCartItems()
            viewModel.getCartItemsBack()
        }
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
          (activity as UpgradeActivity).addFragment(packageFragment, PACKAGE_FRAGMENT)*/
        val packageFragment = PackageFragmentNew.newInstance()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        packageFragment.arguments = args
        (activity as UpgradeActivity).addFragment(packageFragment, NEW_PACKAGE_FRAGMENT)
    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
//        Log.v("PromoBannerClicked >>", item!!.cta_web_link.isNullOrBlank().toString()  + " "+item!!.cta_feature_key.isNullOrBlank().toString() )
        val event_attributes: HashMap<String, Any> = HashMap()
        item?.let { event_attributes.put("title", it.title) }
//        (activity as UpgradeActivity).experienceCode?.let { event_attributes.put("category", it) }
//        (activity as UpgradeActivity).fpTag?.let { event_attributes.put("customer", it) }
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
                val details = DetailsFragment.newInstance()
                val args = Bundle()
                args.putString("itemId", item!!.cta_feature_key)
                details.arguments = args
                (activity as UpgradeActivity).addFragment(details, Constants.DETAILS_FRAGMENT)

            }
        } else {
            if (!item!!.cta_bundle_identifier.isNullOrBlank()) {
                if (item!!.cta_bundle_identifier != null) {
                    if (item.cta_bundle_identifier.contains("#")) {

                        var cataCategoryIdentifier =
                            item.cta_bundle_identifier.trim().split("#").toTypedArray()
                        var bundleCategory = cataCategoryIdentifier.get(0)
                        var bundleID = cataCategoryIdentifier.get(1)
                        if ((activity as UpgradeActivity).experienceCode.equals(bundleCategory)) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(requireActivity().application)!!
                                    .bundlesDao()
                                    .checkBundleKeyExist(bundleID)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it == 1) {
                                            CompositeDisposable().add(
                                                AppDatabase.getInstance(requireActivity().application)!!
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
                                                        val packageFragment =
                                                            PackageFragment.newInstance()
                                                        val args = Bundle()
                                                        args.putString(
                                                            "bundleData",
                                                            Gson().toJson(selectedBundle)
                                                        )
                                                        packageFragment.arguments = args
                                                        (activity as UpgradeActivity).addFragment(
                                                            packageFragment,
                                                            PACKAGE_FRAGMENT
                                                        )

                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                            )
                                        } else {
                                            Toasty.error(
                                                requireContext(),
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
                            " bundleCategory: " + bundleCategory + " bundleID: " + bundleID + " " + (activity as UpgradeActivity).experienceCode
                        )
                    } else {
                        CompositeDisposable().add(
                            AppDatabase.getInstance(requireActivity().application)!!
                                .bundlesDao()
                                .checkBundleKeyExist(item!!.cta_bundle_identifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it == 1) {
                                        CompositeDisposable().add(
                                            AppDatabase.getInstance(requireActivity().application)!!
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
                                                    val packageFragment =
                                                        PackageFragment.newInstance()
                                                    val args = Bundle()
                                                    args.putString(
                                                        "bundleData",
                                                        Gson().toJson(selectedBundle)
                                                    )
                                                    packageFragment.arguments = args
                                                    (activity as UpgradeActivity).addFragment(
                                                        packageFragment,
                                                        PACKAGE_FRAGMENT
                                                    )

                                                }, {
                                                    it.printStackTrace()
                                                })
                                        )
                                    } else {
                                        Toasty.error(
                                            requireContext(),
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
                            if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
                                val viewType = deepHashMap[DynamicLinkParams.viewType]
                                val buyItemKey = deepHashMap[DynamicLinkParams.buyItemKey]
                                deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
                            } else deepLinkUtil?.deepLinkPage(item.cta_web_link!!, "", false)

                        } else {

                            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
                            val args = Bundle()
                            args.putString("title", "")
                            args.putString("link", item!!.cta_web_link)
                            webViewFragment.arguments = args
                            (activity as UpgradeActivity).addFragment(
                                webViewFragment,
                                Constants.WEB_VIEW_FRAGMENT
                            )

                        }


                    }
                } else if (!item.coupon_code.isNullOrBlank() || !item.cta_offer_identifier.isNullOrBlank()) {
                    if (item.coupon_code != null || item.cta_offer_identifier != null) {
                        CompositeDisposable().add(
                            AppDatabase.getInstance(requireActivity().application)!!
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
                                    val marketPlaceOfferFragment =
                                        MarketPlaceOfferFragment.newInstance()
                                    val args = Bundle()
                                    args.putString(
                                        "marketOffersData",
                                        Gson().toJson(selectedMarketOfferModel)
                                    )
                                    marketPlaceOfferFragment.arguments = args
                                    (activity as UpgradeActivity).addFragment(
                                        marketPlaceOfferFragment,
                                        MARKET_OFFER_FRAGMENT
                                    )

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
            (activity as UpgradeActivity).addFragment(details, Constants.DETAILS_FRAGMENT)

        } else if (item.cta_bundle_identifier.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                    AppDatabase.getInstance(requireActivity().application)!!
                            .bundlesDao()
                            .checkBundleKeyExist(item!!.cta_bundle_identifier)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (it == 1) {
                                    CompositeDisposable().add(
                                            AppDatabase.getInstance(requireActivity().application)!!
                                                    .bundlesDao()
                                                    .getBundleItemById(item!!.cta_bundle_identifier)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({

                                                        val packageFragment = PackageFragment.newInstance()
                                                        val args = Bundle()
                                                        args.putString("bundleData", Gson().toJson(it))
                                                        packageFragment.arguments = args
                                                        (activity as UpgradeActivity).addFragment(packageFragment, PACKAGE_FRAGMENT)

                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                    )
                                } else {
                                    Toasty.error(requireContext(), "Bundle Not Available To This Account", Toast.LENGTH_LONG).show()
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
            (activity as UpgradeActivity).addFragment(
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

            val details = DetailsFragment.newInstance()
            val args = Bundle()
            args.putString("itemId", item!!.cta_feature_key)
            details.arguments = args
            (activity as UpgradeActivity).addFragment(details, Constants.DETAILS_FRAGMENT)

        } else if (item.cta_bundle_identifier.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                AppDatabase.getInstance(requireActivity().application)!!
                    .bundlesDao()
                    .checkBundleKeyExist(item!!.cta_bundle_identifier)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(requireActivity().application)!!
                                    .bundlesDao()
                                    .getBundleItemById(item!!.cta_bundle_identifier)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({

                                        val packageFragment = PackageFragment.newInstance()
                                        val args = Bundle()
                                        args.putString("bundleData", Gson().toJson(it))
                                        packageFragment.arguments = args
                                        (activity as UpgradeActivity).addFragment(
                                            packageFragment,
                                            PACKAGE_FRAGMENT
                                        )

                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        } else {
                            Toasty.error(
                                requireContext(),
                                "Bundle Not Available To This Account",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, {
                        it.printStackTrace()
                    })
            )
        } else if (item.cta_web_link.isNullOrEmpty().not()) {

            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("title", "")
            args.putString("link", item!!.cta_web_link)
            webViewFragment.arguments = args
            (activity as UpgradeActivity).addFragment(
                webViewFragment,
                Constants.WEB_VIEW_FRAGMENT
            )
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
        val viewallFeatures = ViewAllFeaturesFragment.newInstance()
        val args = Bundle()
        args.putString("categoryType", categoryType)
        args.putStringArrayList(
            "userPurchsedWidgets",
            arguments?.getStringArrayList("userPurchsedWidgets")
        )
        viewallFeatures.arguments = args
        (activity as UpgradeActivity).addFragment(
            viewallFeatures,
            VIEW_ALL_FEATURE
        )
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        WebEngageController.trackEvent(
            VIDEO_GALLERY_CLICKED,
            CLICK,
            videoItem.title ?: NO_EVENT_VALUE
        )
        Log.i("onPlayYouTubeVideo", videoItem.youtube_link ?: "")
        val link: List<String> = videoItem.youtube_link!!.split('/')
        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
//    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
        videoPlayerWebView.setWebViewClient(WebViewClient())
        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
//    videoPlayerWebView.setWebChromeClient(WebChromeClient())
        video_sub_title.text = videoItem.title
        video_sub_desc.text = videoItem.desc
    }

    override fun onPackageAddToCart(item: Bundles?,imageView: ImageView) {
        Log.d("onPackageAddToCart", " " + item.toString())
//        var bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)
//        var bundleData =  Gson().fromJson<PackageBundles>(item.toString(), object : TypeToken<PackageBundles>() {}.type)

        if (!packageInCartStatus) {
            if (item != null) {

                CompositeDisposable().add(
                    AppDatabase.getInstance(requireActivity().application)!!
                        .cartDao()
                        .checkCartBundleExist(item._kid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it == 0) {
                                makeFlyAnimation(imageView)
                                callBundleCart(item,imageView)
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
                    AppDatabase.getInstance(requireActivity().application)!!
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
//                        requireContext(),
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

    /*fun checkBannerDetails(list: ArrayList<PromoBanners>){
        for(singleItem in list){

            if (singleItem!!.cta_feature_key != null) {
                CompositeDisposable().add(
                        AppDatabase.getInstance(activity!!.application)!!
                                .featuresDao()
                                .checkFeatureTableKeyExist(singleItem!!.cta_feature_key)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    try {
                                        if (it == 0) {
                                            for (singleBanner in list) {
  //                                                  if (singleBanner.cta_feature_key == list.get(position)!!.cta_feature_key) {
  //                                                      list.remove(singleBanner)
  //                                                      notifyDataSetChanged()
  //                                                      homeListener.onShowHidePromoBannerIndicator(list.size > 1)
  //                                                  }
                                            }
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_feature_key == singleItem!!.cta_feature_key && singleItem!!.cta_feature_key.isNotEmpty() && singleBanner.cta_feature_key.isNotEmpty()) {
                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode) && !singleBanner.exclusive_to_categories.isEmpty()) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        } else {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_feature_key == singleItem!!.cta_feature_key && singleItem!!.cta_feature_key.isNotEmpty() && singleBanner.cta_feature_key.isNotEmpty()) {
                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode) && !singleBanner.exclusive_to_categories.isEmpty()) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                )
            }
            else if (singleItem!!.cta_bundle_identifier != null) {
                CompositeDisposable().add(
                        AppDatabase.getInstance(activity!!.application)!!
                                .bundlesDao()
                                .checkBundleKeyExist(singleItem!!.cta_bundle_identifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    try {
                                        if (it == 0) {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_bundle_identifier == singleItem!!.cta_bundle_identifier) {
                                                    list.remove(singleBanner)
                                                    updateBannerViewPager(list)
                                                }
                                            }
                                        } else {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_bundle_identifier == singleItem!!.cta_bundle_identifier) {
                                                    if (singleBanner.exclusive_to_customers != null && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                )

            }

        }



    }*/

    /*fun checkBannerDetailsNew(list: ArrayList<PromoBanners>) {
        for(singleItem in list){
            if (singleItem!!.cta_feature_key != null && singleItem!!.cta_feature_key.isNotEmpty()) {
                CompositeDisposable().add(
                        AppDatabase.getInstance(activity!!.application)!!
                                .featuresDao()
                                .checkFeatureTableKeyExist(singleItem!!.cta_feature_key)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    try {
                                        if (it == 0) {

                                            for (singleBanner in list) {
                                                if (singleBanner.cta_feature_key == singleBanner!!.cta_feature_key) {

                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        } else {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_feature_key == singleBanner!!.cta_feature_key) {

                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty()
                                                            && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)

                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                )
            } else if (singleItem!!.cta_bundle_identifier != null && singleItem!!.cta_bundle_identifier.isNotEmpty()) {
                CompositeDisposable().add(
                        AppDatabase.getInstance(activity!!.application)!!
                                .bundlesDao()
                                .checkBundleKeyExist(singleItem!!.cta_bundle_identifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    try {
                                        if (it == 0) {

                                            for (singleBanner in list) {
                                                if (singleBanner.cta_bundle_identifier == singleItem!!.cta_bundle_identifier) {

                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        } else {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_bundle_identifier == singleItem!!.cta_bundle_identifier) {
                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                )
            }
        }
    }*/
    /*fun checkBannerDetailsMarketOffer(list: ArrayList<PromoBanners>) {
        for(singleItem in list){
            if (singleItem!!.cta_feature_key != null && singleItem!!.cta_feature_key.isNotEmpty()) {
                CompositeDisposable().add(
                        AppDatabase.getInstance(activity!!.application)!!
                                .featuresDao()
                                .checkFeatureTableKeyExist(singleItem!!.cta_feature_key)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    try {
                                        if (it == 0) {

                                            for (singleBanner in list) {
                                                if (singleBanner.cta_feature_key == singleBanner!!.cta_feature_key) {

                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        } else {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_feature_key == singleBanner!!.cta_feature_key) {

                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty()
                                                            && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)

                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                )
            } else if (singleItem!!.cta_bundle_identifier != null && singleItem!!.cta_bundle_identifier.isNotEmpty()) {
                CompositeDisposable().add(
                        AppDatabase.getInstance(activity!!.application)!!
                                .bundlesDao()
                                .checkBundleKeyExist(singleItem!!.cta_bundle_identifier)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    try {
                                        if (it == 0) {

                                            for (singleBanner in list) {
                                                if (singleBanner.cta_bundle_identifier == singleItem!!.cta_bundle_identifier) {

                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        } else {
                                            for (singleBanner in list) {
                                                if (singleBanner.cta_bundle_identifier == singleItem!!.cta_bundle_identifier) {
                                                    if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                                                        list.remove(singleBanner)
                                                        updateBannerViewPager(list)
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, {
                                    it.printStackTrace()
                                })
                )
            }
            else {
                for (singleBanner in list) {

                    if (!singleBanner.coupon_code.isNullOrBlank() *//*|| singleBanner.coupon_code.isNotBlank()*//*) {
                        Log.v("oldBannerOffer", " "+ singleBanner.title)
                        if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
                            Log.v("oldBannerOffer1", " "+ singleBanner.title)
                            list.remove(singleBanner)
//                            updateBannerViewPager(list)
                        } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
                            Log.v("oldBannerOffer2", " "+ singleBanner.title)
                            list.remove(singleBanner)
//                            updateBannerViewPager(list)
                        }
//                        updateBannerViewPager(list)
                    }

                    updateBannerViewPager(list)
                }
                for(singleBanner in list){
                    Log.v("newMarketOffers", " "+ singleBanner.title + " "+singleBanner.cta_feature_key + " "+ singleBanner.coupon_code)

                }
            }
//        else{
//                for (singleBanner in list) {
//                    Log.v("newMarketOffers", " "+ singleBanner.title)
//                    if (singleBanner.coupon_code.isNotEmpty() || singleBanner.coupon_code.isNotBlank()) {
//
//                        if (singleBanner.exclusive_to_customers != null && singleBanner.exclusive_to_customers.isNotEmpty() && !singleBanner.exclusive_to_customers.contains((activity as UpgradeActivity).fpTag)) {
//                            list.remove(singleBanner)
//                            updateBannerViewPager(list)
//                        } else if (singleBanner.exclusive_to_categories != null && singleBanner.exclusive_to_categories.isNotEmpty() && !singleBanner.exclusive_to_categories.contains((activity as UpgradeActivity).experienceCode)) {
//                            list.remove(singleBanner)
//                            updateBannerViewPager(list)
//                        }
//                    }
//                }
//            }
        }
    }*/

    override fun backComparePress() {
        if (prefs.getCompareState() == 1) {
            prefs.storeCompareState(0)
            val pref =
                requireActivity().getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
            var code: String = (activity as UpgradeActivity).experienceCode!!
            if (!code.equals("null", true)) {
                viewModel.setCurrentExperienceCode(code, fpTag!!)
            }

            viewModel.loadPackageUpdates(
                (activity as UpgradeActivity).fpid!!,
                (activity as UpgradeActivity).clientid
            )
        }
    }

    fun startReferralView() {
        try {
            WebEngageController.trackEvent(REFER_A_FRIEND_CLICK, CLICK, TO_BE_ADDED)
            val webIntent =
                Intent(activity, Class.forName("com.nowfloats.helper.ReferralTransActivity"))
            startActivity(webIntent)
//            overridePendingTransition(0, 0)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getPackageItem(packageIdentifier: String?) {
        if (packageIdentifier.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                AppDatabase.getInstance(requireActivity().application)!!
                    .bundlesDao()
                    .checkBundleKeyExist(packageIdentifier!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(requireActivity().application)!!
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

                                        val packageFragment = PackageFragment.newInstance()
                                        val args = Bundle()
                                        args.putString("bundleData", Gson().toJson(selectedBundle))
                                        args.putStringArrayList(
                                            "userPurchsedWidgets",
                                            arguments?.getStringArrayList("userPurchsedWidgets")
                                        )
                                        packageFragment.arguments = args
//                                                        (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)

                                        (activity as UpgradeActivity).addFragmentHome(
                                            PackageFragment.newInstance(),
                                            PACKAGE_FRAGMENT, args
                                        )
//                                                        bundleData = Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type)
//                                                        packageAdaptor = PackageAdaptor((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type))

                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        } else {
                            Toasty.error(
                                requireContext(),
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

    fun getItemPromoBanner(item: String?) {
        if (item.isNullOrEmpty().not()) {
            CompositeDisposable().add(
                AppDatabase.getInstance(requireActivity().application)!!
                    .bundlesDao()
                    .checkBundleKeyExist(item!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it == 1) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(requireActivity().application)!!
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

                                        val packageFragment = PackageFragment.newInstance()
                                        val args = Bundle()
                                        args.putString("bundleData", Gson().toJson(selectedBundle))
                                        args.putStringArrayList(
                                            "userPurchsedWidgets",
                                            arguments?.getStringArrayList("userPurchsedWidgets")
                                        )
                                        packageFragment.arguments = args
//                                                        (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)

                                        (activity as UpgradeActivity).addFragmentHome(
                                            PackageFragment.newInstance(),
                                            PACKAGE_FRAGMENT, args
                                        )
//                                                        bundleData = Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type)
//                                                        packageAdaptor = PackageAdaptor((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(Gson().toJson(it), object : TypeToken<Bundles>() {}.type))

                                    }, {
                                        it.printStackTrace()
                                    })
                            )
                        } else {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(requireActivity().application)!!
                                    .featuresDao()
                                    .checkFeatureTableKeyExist(item!!)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it == 1) {
                                            Log.v("getItemPromoBanner2", " " + item)
                                            CompositeDisposable().add(
                                                AppDatabase.getInstance(requireActivity().application)!!
                                                    .featuresDao()
                                                    .getFeaturesItemByFeatureCode(item)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({

                                                        val details = DetailsFragment.newInstance()
                                                        val args = Bundle()
                                                        args.putString("itemId", it.feature_code)
                                                        details.arguments = args
                                                        (activity as UpgradeActivity).addFragment(
                                                            details,
                                                            Constants.DETAILS_FRAGMENT
                                                        )
//

                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                            )
                                        } else {
                                            Log.v("getItemPromoBanner3", " " + item)
                                            CompositeDisposable().add(
                                                AppDatabase.getInstance(requireActivity().application)!!
                                                    .marketOffersDao()
                                                    .checkOffersTableKeyExist(item!!)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        if (it == 1) {
                                                            CompositeDisposable().add(
                                                                AppDatabase.getInstance(
                                                                    requireActivity().application
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
                                                                        val marketPlaceOfferFragment =
                                                                            MarketPlaceOfferFragment.newInstance()
                                                                        val args = Bundle()
                                                                        args.putString(
                                                                            "marketOffersData",
                                                                            Gson().toJson(
                                                                                selectedMarketOfferModel
                                                                            )
                                                                        )
                                                                        marketPlaceOfferFragment.arguments =
                                                                            args
                                                                        (activity as UpgradeActivity).addFragment(
                                                                            marketPlaceOfferFragment,
                                                                            MARKET_OFFER_FRAGMENT
                                                                        )


                                                                    }, {
                                                                        it.printStackTrace()
                                                                    })
                                                            )
                                                        } else {
                                                            Log.v("getItemPromoBanner3", " " + item)
                                                            if (item.contains("http")) {
                                                                val webViewFragment: WebViewFragment =
                                                                    WebViewFragment.newInstance()
                                                                val args = Bundle()
                                                                args.putString("title", "")
                                                                args.putString("link", item)
                                                                webViewFragment.arguments = args
                                                                (activity as UpgradeActivity).addFragment(
                                                                    webViewFragment,
                                                                    Constants.WEB_VIEW_FRAGMENT
                                                                )
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

    fun callExpertContact(phone: String?) {
        Log.d("callExpertContact", " " + phone)
        if (phone != null) {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_EXPERT_SPEAK, CLICK, NO_EVENT_VALUE)
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + phone)
            startActivity(Intent.createChooser(callIntent, "Call by:"))
        } else {
            Toasty.error(
                requireContext(),
                "Expert will be available tomorrow from 10AM",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun makeFlyAnimation(targetView: ImageView) {

        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(600)
            .setDestView(imageViewCart1).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    viewModel.getCartItems()

                }


                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()

    }

    private fun isTooLarge(text: TextView, newText: String): Boolean {
        val textWidth = text.paint.measureText(newText)
        return textWidth >= text.measuredWidth
    }

    fun callBundleCart(item: Bundles, imageView: ImageView){
        val itemIds = arrayListOf<String>()
        for (i in item.included_features) {
            itemIds.add(i.feature_code)
        }
        CompositeDisposable().add(
            AppDatabase.getInstance(requireActivity().application)!!
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
                        badgeNumber = badgeNumber + 1
                        Constants.CART_VALUE = badgeNumber
                    },
                    {
                        it.printStackTrace()

                    }
                )
        )
    }

}