package com.boost.upgrades.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.webview.WebViewFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.COMPARE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.PACKAGE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.HorizontalMarginItemDecoration
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils.getRetrofit
import com.boost.upgrades.utils.Utils.longToast
import com.boost.upgrades.utils.WebEngageController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

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

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.home_fragment, container, false)
        homeViewModelFactory = HomeViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), homeViewModelFactory).get(HomeViewModel::class.java)

        upgradeAdapter = UpgradeAdapter((activity as UpgradeActivity), ArrayList())
        addonsCategoryAdapter = AddonsCategoryAdapter((activity as UpgradeActivity), ArrayList(), this)
        videosListAdapter = VideosListAdapter(ArrayList(), this)
        partnerViewPagerAdapter = PartnerViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        bannerViewPagerAdapter = BannerViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        packageViewPagerAdapter = PackageViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        featureDealsAdapter = FeatureDealsAdapter(ArrayList(), ArrayList(), (activity as UpgradeActivity), this)
        //request retrofit instance
        ApiService = getRetrofit().create(ApiInterface::class.java)
        progressDialog = ProgressDialog(requireContext())
        localStorage = LocalStorage.getInstance(context!!)!!
        cart_list = localStorage.getCartItems()
        prefs = SharedPrefs(activity as UpgradeActivity)

        return root
    }

    override fun onResume(){
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //emptyCouponTable everytime for new coupon code
        viewModel.emptyCouponTable()
        setSpannableStrings()
        loadData()
        initMvvm()
        (activity as UpgradeActivity)setBackListener(this)
//    initYouTube()

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Loaded", "ADDONS_MARKETPLACE", "")
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
            recommended_features_account_type.setText((activity as UpgradeActivity).accountType!!.toLowerCase())
            recommended_features_section.visibility = View.VISIBLE
        } else {
            recommended_features_section.visibility = View.GONE
        }

        share_refferal_code_btn.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "Generic", "")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.referral_text_1) + fpRefferalCode)
            sendIntent.type = "text/plain"
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
                Toasty.error(requireContext(), "Failed to share the referral code. Please try again.", Toast.LENGTH_SHORT, true).show()
            }
        }

        share_fb_1.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "Facebook Messenger", "")
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
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "WHATSAPP", "")
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            whatsappIntent.setPackage("com.whatsapp")
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

        }
        share_referal.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "CARD", "")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    R.string.referral_text_2
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

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
            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            (activity as UpgradeActivity).addFragmentHome(
                    MyAddonsFragment.newInstance(),
                    MYADDONS_FRAGMENT,args
            )
        }


        all_recommended_addons.setOnClickListener {
            WebEngageController.trackEvent("Clicked view all recommended add-ons", "ADDONS_MARKETPLACE", "null")
            val args = Bundle()
            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            (activity as UpgradeActivity).addFragmentHome(
                    ViewAllFeaturesFragment.newInstance(),
                    VIEW_ALL_FEATURE,args
            )
        }

        if (arguments?.getString("screenType") == "myAddOns") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            (activity as UpgradeActivity).addFragmentHome(
                    MyAddonsFragment.newInstance(),
                    MYADDONS_FRAGMENT,args
            )
        } else if (arguments?.getString("screenType") == "recommendedAddOns") {
            if (progressDialog.isShowing) {
                progressDialog.hide()
            }
            val args = Bundle()
            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            (activity as UpgradeActivity).addFragmentHome(
                    ViewAllFeaturesFragment.newInstance(),
                    VIEW_ALL_FEATURE,args
            )
        }

        //chat bot view button clicked
        view_chat.setOnClickListener {
            val details = DetailsFragment.newInstance()
            val args = Bundle()
            args.putString("itemId", "CHATBOT")
            details.arguments = args
            (activity as UpgradeActivity).addFragment(details, Constants.DETAILS_FRAGMENT)
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
                Toasty.error(requireContext(), "Feedback Service Not Available. Try Later..", Toast.LENGTH_LONG).show()
            }
        }

        package_compare_layout.setOnClickListener {
            val args = Bundle()
            args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
            (activity as UpgradeActivity).addFragmentHome(
                    ComparePackageFragment.newInstance(),
                    COMPARE_FRAGMENT,args
            )
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
        boost360_tnc.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE TnC Clicked", "ADDONS_MARKETPLACE", "")
            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("link", "https://www.getboost360.com/tnc")
            webViewFragment.arguments = args
            (activity as UpgradeActivity).addFragment(
                    webViewFragment,
                    Constants.WEB_VIEW_FRAGMENT
            )
        }

        val refText = SpannableString(getString(R.string.referral_card_explainer_text))
        refText.setSpan(StyleSpan(Typeface.BOLD), 18, 26, 0)
        refText.setSpan(StyleSpan(Typeface.BOLD), refText.length - 4, refText.length, 0)
        refText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
                0,
                refText.length,
                0
        )
        ref_txt.text = refText
    }

    fun loadData() {
        val pref = activity!!.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        var code: String = (activity as UpgradeActivity).experienceCode!!
        if (!code.equals("null", true)) {
            viewModel.setCurrentExperienceCode(code, fpTag!!)
        }

        viewModel.loadUpdates((activity as UpgradeActivity).fpid!!, (activity as UpgradeActivity).clientid)
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
            updateRecycler(it)
            updateAddonCategoryRecycler(it)
        })

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            val list = arrayListOf<Bundles>()
            for (item in it) {
                val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                list.add(Bundles(
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
                package_layout.visibility = View.VISIBLE
                updatePackageViewPager(list)
            } else {
                package_layout.visibility = View.GONE
            }
        })

        viewModel.getBackAllBundles().observe(this, androidx.lifecycle.Observer {
            val list = arrayListOf<Bundles>()
            for (item in it) {
                val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                list.add(Bundles(
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
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge.visibility = View.GONE
            }
            //refresh FeatureDeals adaptor when cart is updated
            if (viewModel.allFeatureDealsResult.value != null) {
                val list = viewModel.allFeatureDealsResult.value!!
                if (list.size > 0) {
                    updateFeatureDealsViewPager(list, it)
                }
            }

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
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge.visibility = View.GONE
            }
            //refresh FeatureDeals adaptor when cart is updated
            if (viewModel.allFeatureDealsResult.value != null) {
                val list = viewModel.allFeatureDealsResult.value!!
                if (list.size > 0) {
                    updateFeatureDealsViewPager(list, it)
                }
            }
            if(Constants.COMPARE_BACK_VALUE == 1) {
                Constants.COMPARE_BACK_VALUE = 0
                if (viewModel.allBundleResult.value != null) {


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
                                    null, item.desc
                            ))
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
                callnow_layout.visibility = View.VISIBLE
                callnow_image.visibility = View.VISIBLE
                callnow_title.setText(it.line1)
                callnow_desc.setText(it.line2)
                call_shedule_layout.visibility = View.GONE
                callnow_button.setOnClickListener {
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:" + expertConnectDetails.contact_number)
                    startActivity(Intent.createChooser(callIntent, "Call by:"))
                }
            } else {
                callnow_layout.visibility = View.GONE
                callnow_image.visibility = View.GONE
                call_shedule_layout.visibility = View.VISIBLE
                call_shedule_title.setText(it.line1)
                call_shedule_desc.setText(it.line2)
                if (it.offline_message != null) {
                    val spannableString = SpannableString(it.offline_message)
                    spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.navy_blue)), 0, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    call_schedule_timinig.setText(spannableString)
                }
            }
        })

        viewModel.getPromoBanners().observe(this, androidx.lifecycle.Observer {
            Log.e("getPromoBanners", it.toString())
            if (it.size > 0) {
//                checkBannerDetails(it as ArrayList<PromoBanners>)
                checkBannerDetailsNew(it as ArrayList<PromoBanners>)
//                updateBannerViewPager(it)
                banner_layout.visibility = View.VISIBLE
            } else {
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
    }

    fun updateRecycler(list: List<FeaturesModel>) {
        upgradeAdapter.addupdates(list)
        recycler.adapter = upgradeAdapter
        upgradeAdapter.notifyDataSetChanged()
        recycler.isFocusable = false
        back_image.isFocusable = true
    }

    fun updateAddonCategoryRecycler(list: List<FeaturesModel>) {
        val addonsCategoryTypes = arrayListOf<String>()
        for (singleFeaturesModel in list) {
            if (!addonsCategoryTypes.contains(singleFeaturesModel.target_business_usecase)) {
                addonsCategoryTypes.add(singleFeaturesModel.target_business_usecase!!)
            }
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
        WebEngageController.trackEvent("Feature packs Clicked", "ADDONS_MARKETPLACE", item?.name?:"")
        val packageFragment = PackageFragment.newInstance()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        packageFragment.arguments = args
        (activity as UpgradeActivity).addFragment(packageFragment, PACKAGE_FRAGMENT)
/*        val packageFragment = NewPackageFragment.newInstance()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        packageFragment.arguments = args
        (activity as UpgradeActivity).addFragment(packageFragment, NEW_PACKAGE_FRAGMENT)*/
    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
//        Log.v("PromoBannerClicked >>", item!!.cta_web_link.isNullOrBlank().toString()  + " "+item!!.cta_feature_key.isNullOrBlank().toString() )
        if(!item!!.cta_feature_key.isNullOrBlank()){
            if (item!!.cta_feature_key != null) {
                val details = DetailsFragment.newInstance()
                val args = Bundle()
                args.putString("itemId", item!!.cta_feature_key)
                details.arguments = args
                (activity as UpgradeActivity).addFragment(details, Constants.DETAILS_FRAGMENT)

            }
        }else{
            if(!item!!.cta_bundle_identifier.isNullOrBlank()){
                if (item!!.cta_bundle_identifier != null) {
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

                                                                        val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                                                                        selectedBundle = Bundles(
                                                                                item.bundle_id,
                                                                                temp,
                                                                                item.min_purchase_months,
                                                                                item.name,
                                                                                item.overall_discount_percent,
                                                                                PrimaryImage(item.primary_image),
                                                                                item.target_business_usecase,
                                                                                Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                                                                                null,
                                                                                item.desc)
                                                                val packageFragment = PackageFragment.newInstance()
                                                                val args = Bundle()
                                                                args.putString("bundleData", Gson().toJson(selectedBundle))
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
                }
            }else{
                if(!item!!.cta_web_link.isNullOrBlank()){
                    if (item!!.cta_web_link != null) {
                        val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
                        val args = Bundle()
                        args.putString("title", "Browser")
                        args.putString("link", item!!.cta_web_link)
                        webViewFragment.arguments = args
                        (activity as UpgradeActivity).addFragment(
                                webViewFragment,
                                Constants.WEB_VIEW_FRAGMENT
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
        WebEngageController.trackEvent("Partner's Promo banners Clicked", "ADDONS_MARKETPLACE", item?.title?:"")
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
        } else if (item.cta_web_link.isNullOrEmpty().not()) {

            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("title", "Browser")
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
            WebEngageController.trackEvent("Feature deals add cart Clicked", "ADDONS_MARKETPLACE", item.name?:"")
            viewModel.addItemToCart(item, minMonth)
        }
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
        val viewallFeatures = ViewAllFeaturesFragment.newInstance()
        val args = Bundle()
        args.putString("categoryType", categoryType)
        args.putStringArrayList("userPurchsedWidgets", arguments?.getStringArrayList("userPurchsedWidgets"))
        viewallFeatures.arguments = args
        (activity as UpgradeActivity).addFragment(
                viewallFeatures,
                VIEW_ALL_FEATURE
        )
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        WebEngageController.trackEvent("Video gallery Clicked", "ADDONS_MARKETPLACE", videoItem.title?:"")
        Log.i("onPlayYouTubeVideo", videoItem.youtube_link)
        val link: List<String> = videoItem.youtube_link!!.split('/')
        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
//    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
        videoPlayerWebView.setWebViewClient(WebViewClient())
        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
//    videoPlayerWebView.setWebChromeClient(WebChromeClient())
    }

    override fun onPackageAddToCart(item: Bundles?) {
        Log.v("onPackageAddToCart", " "+ item.toString())
//        var bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)
//        var bundleData =  Gson().fromJson<PackageBundles>(item.toString(), object : TypeToken<PackageBundles>() {}.type)
        if (!packageInCartStatus) {
            if (item != null) {

                val itemIds = arrayListOf<String>()
                for(i in item.included_features){
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
                                            val minMonth:Int = if (item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1

                                            for (singleItem in it) {
                                                for (item in item!!.included_features) {
                                                    if (singleItem.feature_code == item.feature_code) {
                                                        bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
                                                    }
                                                }
                                            }

                                            offeredBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
                                            originalBundlePrice = (bundleMonthlyMRP * minMonth).toInt()

                                            if(item!!.overall_discount_percent > 0)
                                                offeredBundlePrice = originalBundlePrice - (originalBundlePrice * item!!.overall_discount_percent/100)
                                            else
                                                offeredBundlePrice = originalBundlePrice

                                            //clear cartOrderInfo from SharedPref to requestAPI again
                                            prefs.storeCartOrderInfo(null)
                                            viewModel.addItemToCartPackage(CartModel(
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
                                            ))
                                            val event_attributes: HashMap<String, Any> = HashMap()
                                            item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
                                            item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
                                            event_attributes.put("Package Price", originalBundlePrice)
                                            event_attributes.put("Discounted Price", offeredBundlePrice)
                                            event_attributes.put("Discount %", item!!.overall_discount_percent)
                                            item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
                                            WebEngageController.trackEvent("ADDONS_MARKETPLACE Package added to cart", "ADDONS_MARKETPLACE", event_attributes)
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
                                            viewModel.getCartItems()
                                        },
                                        {
                                            it.printStackTrace()

                                        }
                                )
                )


               /* //clear cartOrderInfo from SharedPref to requestAPI again
                prefs.storeCartOrderInfo(null)
                viewModel.addItemToCartPackage(CartModel(
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
                ))
                val event_attributes: HashMap<String, Any> = HashMap()
                item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
                item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
                event_attributes.put("Package Price", originalBundlePrice)
                event_attributes.put("Discounted Price", offeredBundlePrice)
                event_attributes.put("Discount %", item!!.overall_discount_percent)
                item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
                WebEngageController.trackEvent("ADDONS_MARKETPLACE Package added to cart", "ADDONS_MARKETPLACE", event_attributes)
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
                Constants.CART_VALUE = badgeNumber*/
            }
        }

    }

    fun checkBannerDetails(list: ArrayList<PromoBanners>){
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
                                                /*  if (singleBanner.cta_feature_key == list.get(position)!!.cta_feature_key) {
                                                      list.remove(singleBanner)
                                                      notifyDataSetChanged()
                                                      homeListener.onShowHidePromoBannerIndicator(list.size > 1)
                                                  }*/
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

                                    } catch (e: Exception){
                                        e.printStackTrace()
                                    }
                                },{
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
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                    }
                                },{
                                    it.printStackTrace()
                                })
                )

            }

        }



    }

    fun checkBannerDetailsNew(list: ArrayList<PromoBanners>) {
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
    }

    override fun backComparePress() {
        if(prefs.getCompareState() == 1) {
            prefs.storeCompareState(0)
            val pref = activity!!.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
            var code: String = (activity as UpgradeActivity).experienceCode!!
            if (!code.equals("null", true)) {
                viewModel.setCurrentExperienceCode(code, fpTag!!)
            }

            viewModel.loadPackageUpdates((activity as UpgradeActivity).fpid!!, (activity as UpgradeActivity).clientid)
        }
    }

}
