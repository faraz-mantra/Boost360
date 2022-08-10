package com.boost.marketplace.ui.pack_details

import android.animation.Animator
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.adapter.BenifitsPageTransformer
import com.boost.cart.adapter.ZoomOutPageTransformer
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.CircleAnimationUtil
import com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.R
import com.boost.marketplace.adapter.*
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityPackDetailsBinding
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.interfaces.PackDetailsListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.boost.marketplace.ui.comparePacksV3.ComparePacksV3Activity
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.boost.marketplace.ui.popup.call_track.RequestCallbackBottomSheet
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.utils.RootUtil
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pack_details.*
import kotlinx.android.synthetic.main.layout_black_for_savings.*
import java.text.NumberFormat
import java.util.*

class PackDetailsActivity : AppBaseActivity<ActivityPackDetailsBinding, ComparePacksViewModel>(),
    PackDetailsListener,
    DetailsFragmentListener {

    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var itemInCart = false

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
    lateinit var packDetailsAdapter: PackDetailsFeatureAdapter
    lateinit var packItemAdapter: PackDetailsPacksAdapter


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
        packDetailsAdapter = PackDetailsFeatureAdapter(this, ArrayList(), this)
        packItemAdapter = PackDetailsPacksAdapter(ArrayList(), this, this)

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
        initializePackItemRecycler()
        initMvvm()


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
            val from = 900
            val to = 1800
            val date = Date()
            val c = Calendar.getInstance()
            c.time = date
            val t = c[Calendar.HOUR_OF_DAY] * 100 + c[Calendar.MINUTE]
            val isBetween = to > from && t >= from && t <= to || to < from && (t >= from || t <= to)
            if (isBetween) {
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

            if (bundleData != null) {
                prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

                val itemIds = arrayListOf<String>()
                for (i in bundleData!!.included_features) {
                    itemIds.add(i.feature_code)
                }

                binding?.packageImg?.let { it1 -> makeFlyAnimation(it1) }

                CompositeDisposable().add(
                    AppDatabase.getInstance(application)!!
                        .featuresDao()
                        .getallFeaturesInList(itemIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
//                                            featuresList = it
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

                                if (bundleData!!.overall_discount_percent > 0)
                                    offeredBundlePrice = RootUtil.round(
                                        originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100),
                                        2
                                    )
                                else
                                    offeredBundlePrice = originalBundlePrice

                                //clear cartOrderInfo from SharedPref to requestAPI again
                                prefs.storeCartOrderInfo(null)
                                viewModel.addItemToCartPackage1(
                                    CartModel(
                                        bundleData!!._kid,
                                        null,
                                        null,
                                        bundleData!!.name,
                                        "",
                                        bundleData!!.primary_image!!.url,
                                        offeredBundlePrice.toDouble(),
                                        originalBundlePrice.toDouble(),
                                        bundleData!!.overall_discount_percent,
                                        1,
                                        if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null) bundleData!!.min_purchase_months!! else 1,
                                        "bundles",
                                        null,
                                        ""
                                    )
                                )
                                val event_attributes: java.util.HashMap<String, Any> =
                                    java.util.HashMap()
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
                            },
                            {
                                it.printStackTrace()

                            }
                        )
                )
            }

//            val intent = Intent(
//                applicationContext,
//                CartActivity::class.java
//            )
//            intent.putExtra("fpid", fpid)
//            intent.putExtra("expCode", experienceCode)
//            intent.putExtra("isDeepLink", isDeepLink)
//            intent.putExtra("deepLinkViewType", deepLinkViewType)
//            intent.putExtra("deepLinkDay", deepLinkDay)
//            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
//            intent.putExtra(
//                "accountType",
//                accountType
//            )
//            intent.putStringArrayListExtra(
//                "userPurchsedWidgets",
//                userPurchsedWidgets
//            )
//            if (email != null) {
//                intent.putExtra("email", email)
//            } else {
//                intent.putExtra("email", "ria@nowfloats.com")
//            }
//            if (mobileNo != null) {
//                intent.putExtra("mobileNo", mobileNo)
//            } else {
//                intent.putExtra("mobileNo", "9160004303")
//            }
//            intent.putExtra("profileUrl", profileUrl)
//            startActivity(intent)
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

    override fun onResume() {
        super.onResume()
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
        try {
            viewModel?.getCartItems()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    fun addUpdatePacks(list: ArrayList<BundlesModel>) {
        if (list.size > 0) {
            layout_need_more.visibility = VISIBLE
            packItemAdapter.addupdates(list)
//            rv_packs.adapter = packItemAdapter
            packItemAdapter.notifyDataSetChanged()
        } else {
            layout_need_more.visibility = GONE
        }
    }

    private fun initializePackItemRecycler() {
        val gridLayoutManager = LinearLayoutManager(applicationContext)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvPacks?.apply {
            layoutManager = gridLayoutManager
            binding?.rvPacks?.adapter = packItemAdapter
        }
    }

    fun updatePackItemRecycler(list: ArrayList<BundlesModel>) {

        packItemAdapter.addupdates(list)
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


    private fun initMvvm() {

        val bundlesList = ArrayList<BundlesModel>()

        viewModel.bundleResult().observe(this, androidx.lifecycle.Observer {
            if (it != null) {

                for (singleBundle in it) {

                    if (bundleData?.name != singleBundle.name) {
                        bundlesList.add(singleBundle)

                    }
                }
                //update recyclerview
//                addUpdatePacks(bundlesList)
                updatePackItemRecycler(bundlesList)
                packItemAdapter.notifyDataSetChanged()
            }
        })
        viewModel.addonsError().observe(this, androidx.lifecycle.Observer
        {
            println("addonsError ${it}")
            if (it.contains("Query returned empty"))
                finish()
        })

        viewModel.addonsLoader().observe(this, androidx.lifecycle.Observer
        {
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
                binding?.badge121?.visibility = View.GONE
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
                binding?.containerKeyBenefits?.visibility = View.GONE
            }
            if (bundleData?.included_features != null && bundleData?.included_features?.isNotEmpty()!!) {
                binding?.rvIncludedFeatures?.visibility = VISIBLE
                binding?.tvPremiumFeaturesNo?.visibility = VISIBLE
                val sourceString = "<b>" + bundleData?.included_features?.size!!.toString() + " PREMIUM FEATURES"  + "</b> " + " ideal for small businesses that want to get started with online sales."
                binding?.tvPremiumFeaturesNo?.text = Html.fromHtml(sourceString)
                featuresList?.let { includedFeatureAdapter.addupdates(it) }
                includedFeatureAdapter.notifyDataSetChanged()
            } else {
                binding?.rvIncludedFeatures?.visibility = View.GONE
                binding?.tvPremiumFeaturesNo?.visibility = GONE
            }

            if (bundleData != null && bundleData?.how_to_activate != null && bundleData?.how_to_activate?.isNotEmpty()!!) {
                binding?.howToUseContainer?.visibility = View.VISIBLE
                binding?.howToUseContainer?.setOnClickListener {
                    if (binding?.howToUseRecycler?.visibility == View.VISIBLE) {
                        how_to_use_arrow.setImageResource(R.drawable.ic_down_arrow_pack_details)
                        how_to_use_recycler.visibility = View.GONE
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
                faq_container.visibility = View.GONE
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
                                binding?.packRecycler?.adapter = packDetailsAdapter
                            } else {
                                binding?.packContainer?.visibility = GONE

                            }

                            for (singleItem in it) {
                                for (item in bundleData?.included_features!!) {
                                    if (singleItem.feature_code == item.feature_code) {
                                        originalBundlePrice += Utils.priceCalculatorForYear(
                                            RootUtil.round(
                                                (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),
                                                2
                                            ) * minMonth, singleItem.widget_type, this
                                        )
                                    }
                                }
                            }
                            if (bundleData?.overall_discount_percent!! > 0) {
                                offeredBundlePrice = RootUtil.round(
                                    originalBundlePrice - (originalBundlePrice * bundleData?.overall_discount_percent!! / 100.0),
                                    2
                                )
                                binding?.containerBlack?.visibility = View.VISIBLE


                                tv_saving.text =
                                    bundleData?.overall_discount_percent!!.toString() + "% SAVING"
                                tv2.text =
                                    "SAVING " + bundleData?.overall_discount_percent!!.toString() + "%"
                            } else {
                                offeredBundlePrice = originalBundlePrice
                                binding?.containerBlack?.visibility = View.GONE

                            }

                            if (!prefs.getYearPricing() && bundleData?.min_purchase_months != null && bundleData?.min_purchase_months!! > 1) {

                                binding?.includedBlack?.tvPrice1?.text = "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice) +
                                        Utils.yearlyOrMonthlyOrEmptyValidity(
                                            "",
                                            this,
                                            bundleData?.min_purchase_months!!
                                        )
                                binding?.price?.text = "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice) +
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

                                    binding?.mrpPrice?.visibility = View.GONE
                                }
                            } else {
                                binding
                                    ?.price?.text = ("₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice)
                                        + Utils.yearlyOrMonthlyOrEmptyValidity("", this))
                                binding?.includedBlack?.tvPrice1?.text = ("₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice)
                                        + Utils.yearlyOrMonthlyOrEmptyValidity("", this))
                                if (offeredBundlePrice != originalBundlePrice) {
                                    spannableString(originalBundlePrice)
                                    binding?.includedBlack?.tvPrice?.visibility = VISIBLE
                                    binding?.mrpPrice?.visibility = View.VISIBLE
                                } else {
                                    binding?.includedBlack?.tvPrice?.visibility = GONE
                                    binding?.price?.visibility = View.GONE
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

    fun spannableString(
        value: Double,
        minMonth: Int = 1
    ) {

        val origCost = SpannableString(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value)
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
//        args.putString("bundleData", Gson().toJson(item))
//        args.putString("cartList", Gson().toJson(cart_list))


//        val dialogCard = PackDetailsBottomSheet()
//        val args = Bundle()
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
//        dialogCard.arguments = args
//        dialogCard.show(this.supportFragmentManager, PackDetailsBottomSheet::class.java.name)
    }

    override fun imagePreviewPosition(list: ArrayList<String>, pos: Int) {
    }

    override fun onPackageClicked(item: Bundles?) {
        startActivity(Intent(this, ComparePacksV3Activity::class.java))
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

}