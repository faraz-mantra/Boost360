package com.boost.marketplace.ui.details

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.LearnMoreLink
import com.boost.dbcenterapi.data.remote.ApiInterface
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.*
import com.boost.marketplace.R
import com.boost.marketplace.adapter.ReviewViewPagerAdapter
import com.boost.marketplace.adapter.SecondaryImagesAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityFeatureDetailsBinding
import com.boost.marketplace.infra.utils.Constants.Companion.IMAGE_PREVIEW_POPUP_FRAGMENT
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.popup.ImagePreviewPopUpFragement
import com.boost.marketplace.ui.webview.WebViewActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.framework.analytics.SentryController
import com.framework.webengageconstant.*
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import androidx.lifecycle.Observer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.marketplace.adapter.FeaturePacksAdapter
import com.boost.marketplace.ui.Compare_Plans.ComparePacksActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feature_details.*
import retrofit2.Retrofit
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class FeatureDetailsActivity :
    AppBaseActivity<ActivityFeatureDetailsBinding, FeatureDetailsViewModel>(),
    DetailsFragmentListener {

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface

    //  lateinit var localStorage: LocalStorage
    var singleWidgetKey: String? = null
    var badgeNumber = 0
    var addonDetails: FeaturesModel? = null
    var cart_list: List<CartModel>? = null
    var itemInCartStatus = false
    var widgetLearnMoreLink: String? = null

    var experienceCode: String? = null
    var fpid: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false

    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7

    var userPurchsedWidgets = ArrayList<String>()

    lateinit var progressDialog: ProgressDialog

    lateinit var reviewAdaptor: ReviewViewPagerAdapter
    lateinit var secondaryImagesAdapter: SecondaryImagesAdapter
    lateinit var featurePacksAdapter: FeaturePacksAdapter

    val imagePreviewPopUpFragement = ImagePreviewPopUpFragement()

    lateinit var prefs: SharedPrefs

    override fun getLayout(): Int {
        return R.layout.activity_feature_details
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7

        experienceCode = intent.getStringExtra("expCode")
        fpid = intent.getStringExtra("fpid")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)

        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()

        progressDialog = ProgressDialog(this)
        secondaryImagesAdapter = SecondaryImagesAdapter(ArrayList(), this)
        featurePacksAdapter = FeaturePacksAdapter(ArrayList(), this, this)
        reviewAdaptor = ReviewViewPagerAdapter(ArrayList())
//    localStorage = LocalStorage.getInstance(applicationContext)!!
        singleWidgetKey = intent.extras?.getString("itemId")
        prefs = SharedPrefs(this)
        viewModel.setApplicationLifecycle(application, this)

        initView()
    }

    private fun initView() {

        loadData()
        initializeSecondaryImage()
        initializePackageRecycler()
//        initializeViewPager()
        initMvvm()

        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                title_appbar.visibility = View.VISIBLE
                title_image.visibility = View.VISIBLE
                // Collapsed
                toolbar_details.background =
                    ColorDrawable(resources.getColor(R.color.fullscreen_color))
            } else if (verticalOffset == 0) {
                title_appbar.visibility = View.INVISIBLE
                title_image.visibility = View.INVISIBLE
                toolbar_details.background = ColorDrawable(resources.getColor(R.color.transparent))
                // Expanded
            } else {
                title_appbar.visibility = View.INVISIBLE
                title_image.visibility = View.INVISIBLE
                toolbar_details.background = ColorDrawable(resources.getColor(R.color.transparent))
            }
        }
        )

        abcText.text = getString(R.string.addons_description)


        add_item_to_cart.setOnClickListener {
            if (!itemInCartStatus) {
                if (addonDetails != null) {
                    //clear cartOrderInfo from SharedPref to requestAPI again
                    prefs.storeCartOrderInfo(null)

                    makeFlyAnimation(image1222Copy)

                    viewModel.addItemToCart1(addonDetails!!)
                    val event_attributes: HashMap<String, Any> = HashMap()
                    addonDetails!!.name?.let { it1 -> event_attributes.put("Addon Name", it1) }
                    event_attributes.put("Addon Price", addonDetails!!.price)
                    event_attributes.put(
                        "Addon Discounted Price",
                        getDiscountedPrice(addonDetails!!.price, addonDetails!!.discount_percent)
                    )
                    event_attributes.put("Addon Discount %", addonDetails!!.discount_percent)
                    event_attributes.put("Addon Validity", 1)
                    event_attributes.put("Addon Feature Key", addonDetails!!.boost_widget_key)
                    addonDetails!!.target_business_usecase?.let { it1 ->
                        event_attributes.put(
                            "Addon Tag",
                            it1
                        )
                    }
                    WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
                        ADDONS_MARKETPLACE,
                        event_attributes
                    )
                    if (addonDetails!!.feature_code == "CUSTOM_PAYMENTGATEWAY")
                        WebEngageController.trackEvent(
                            SELF_BRANDED_PAYMENT_GATEWAY_REQUESTED,
                            SELF_BRANDED_PAYMENT_GATEWAY,
                            NO_EVENT_VALUE
                        )
                    badgeNumber = badgeNumber + 1
//                    badge121.setText(badgeNumber.toString())
//                    badge121.visibility = View.VISIBLE
                    Constants.CART_VALUE = badgeNumber

//                    localStorage.addCartItem(addons_list!!.get(itemId))

                    add_item_to_cart.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                    add_item_to_cart.text = getString(R.string.added_to_cart)
                    itemInCartStatus = true

//                    WebEngageController.trackEvent(ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART, FEATURE_KEY , event_attributes)
                }
            }
        }

        imageView121.setOnClickListener {
            finish()
        }

        imageViewCart121.setOnClickListener {
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

        widgetLearnMore.setOnClickListener {
            if (widgetLearnMoreLink != null && widgetLearnMoreLink!!.length > 0) {
//                val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
//                val args = Bundle()
//                args.putString("title", widgetLearnMore.text.toString())
//                args.putString("link", widgetLearnMoreLink)
//                webViewFragment.arguments = args
//                (activity as UpgradeActivity).addFragment(
//                    webViewFragment,
//                    WEB_VIEW_FRAGMENT
//                )
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("title", widgetLearnMore.text.toString())
                intent.putExtra("link", widgetLearnMoreLink)
                startActivity(intent)
            } else {
                Toasty.warning(
                    applicationContext,
                    "Failed to load the article.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cost_per_month.setOnClickListener {
            cost_per_month.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.rounded_stroke_orange_4)
            cost_per_month_check.setImageResource(R.drawable.ic_checked)
            cost_per_year.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.rounded_border_gray)
            cost_per_year_check.setImageResource(R.drawable.rounded_border_gray)
            cost_per_year_discount.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_feature_discount_grey)
        }

        cost_per_year.setOnClickListener {
            cost_per_month.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.rounded_border_gray)
            cost_per_month_check.setImageResource(R.drawable.rounded_border_gray)
            cost_per_year.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.rounded_stroke_orange_4)
            cost_per_year_check.setImageResource(R.drawable.ic_checked)
            cost_per_year_discount.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_feature_discount)
        }

    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm() {
        viewModel.addonsResult().observe(this, Observer {

            //if the View is from PackageView then No need to call getCartItems method
            if (!intent.extras!!.containsKey("packageView")) {
                viewModel.getCartItems()
            }
            addonDetails = it
            if (addonDetails != null) {
                val learnMoreLinkType = object : TypeToken<LearnMoreLink>() {}.type
                val learnMoreLink: LearnMoreLink? =
                    if (addonDetails!!.learn_more_link == null) null else Gson().fromJson(
                        addonDetails!!.learn_more_link,
                        learnMoreLinkType
                    )
                Glide.with(this).load(addonDetails!!.primary_image)
                    .into(image1222)
                Glide.with(this).load(addonDetails!!.primary_image)
                    .into(image1222Copy)

                Glide.with(this).load(addonDetails!!.primary_image)
                    .fitCenter()
                    .into(title_image)


                Glide.with(this).load(addonDetails!!.feature_banner)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fitCenter()
                    .into(details_image_bg)

                if (addonDetails!!.secondary_images.isNullOrEmpty())
                    secondary_images_panel.visibility = View.GONE
                else {
                    val objectType = object : TypeToken<ArrayList<String>>() {}.type
                    var secondaryImages = Gson().fromJson<ArrayList<String>>(
                        addonDetails!!.secondary_images,
                        objectType
                    )
                    if (secondaryImages != null && secondaryImages.count() > 0) {
                        addUpdateSecondaryImage(secondaryImages)
//                        val imgSize: Int = 70 * requireContext().getResources().getDisplayMetrics().density.toInt()
//                        val imgPadding: Int = 5 * requireContext().getResources().getDisplayMetrics().density.toInt()
//                        for(img in secondaryImages){
//                            val imageView = ImageView(requireContext())
//                            imageView.layoutParams = LinearLayout.LayoutParams(imgSize, imgSize, 1f)
//                            imageView.setPadding(imgPadding, imgPadding, imgPadding, imgPadding)
//                            imageView.setBackgroundResource(R.drawable.background_image_fade)
//
//                            secondary_images_panel?.addView(imageView)
//                            Glide.with(this).load(img)
//                                    .fitCenter()
//                                    .into(imageView)
//                        }
                    }
                }

//                if (addonDetails!!.is_premium)
//                    havent_bought_the_feature.visibility = View.VISIBLE
//                else
//                    havent_bought_the_feature.visibility = View.GONE

                if (addonDetails!!.target_business_usecase != null) {
                    title_top_1.visibility = View.VISIBLE
                    title_top_1.text = addonDetails!!.target_business_usecase
                } else {
                    title_top_1.visibility = View.INVISIBLE
                }
                title_top.text = addonDetails!!.name
                title_appbar.text = addonDetails!!.name
//                if (addonDetails!!.discount_percent > 0) {
//                    details_discount.visibility = View.VISIBLE
//                    details_discount.text = addonDetails!!.discount_percent.toString() + "% OFF"
//                } else {
//                    details_discount.visibility = View.GONE
//                }
                if (addonDetails!!.total_installs.isNullOrEmpty() || addonDetails!!.total_installs.equals(
                        "--"
                    )
                ) {
                    title_bottom2.text = "Less than 100 businesses have added this"
                } else {
                    val totalInstall = addonDetails!!.total_installs + " people brought"
                    val businessUses = SpannableString(totalInstall)
                    businessUses.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_blue)),
                        0,
                        addonDetails!!.total_installs!!.length,
                        0
                    )
                    title_bottom2.text = businessUses
                }

                loadCostToButtons()

                if (learnMoreLink != null) {
                    widgetLearnMore.text = learnMoreLink.link_description
                    widgetLearnMoreLink = learnMoreLink.link
                    widgetLearnMore.visibility = View.VISIBLE
                } else {
                    widgetLearnMore.visibility = View.GONE
                }
                xheader.text = addonDetails!!.description_title
                abcText.text = addonDetails!!.description
//                review_layout.visibility = View.GONE
                var event_attributes: HashMap<String, Any> = HashMap()
                event_attributes.put("Feature details", addonDetails!!.description!!)
                event_attributes.put("Feature Name", addonDetails!!.name!!)
                event_attributes.put("Feature Key", addonDetails!!.boost_widget_key)
                WebEngageController.trackEvent(
                    ADDONS_MARKETPLACE_FEATURE_DETAILS_LOADED,
                    Feature_Details,
                    event_attributes,
                    ""
                )
//                WebEngageController.trackEvent(ADDONS_MARKETPLACE_FEATURE_DETAILS + addonDetails!!.boost_widget_key + " Loaded", Feature_Details, event_attributes)

            }
        })

        viewModel.bundleResult().observe(this, Observer {
            if (it != null) {
                val bundlesList = arrayListOf<BundlesModel>()
                for (singleBundle in it) {
                    if (singleBundle.included_features != null) {
                        val temp = Gson().fromJson<List<IncludedFeature>>(
                            singleBundle.included_features!!,
                            object : TypeToken<List<IncludedFeature>>() {}.type
                        )
                        for (item in temp) {
                            if (item.feature_code.equals(singleWidgetKey)) {
                                bundlesList.add(singleBundle)
                            }
                        }
                    }
                }
                //update recyclerview
                addUpdatePacks(bundlesList)
            }
        })

        viewModel.cartResult().observe(this, Observer
        {
            cart_list = it
            itemInCartStatus = false
            if (cart_list != null && cart_list!!.size > 0) {
                badge121.visibility = View.VISIBLE
                for (item in cart_list!!) {
                    if (item.feature_code == singleWidgetKey) {
                        add_item_to_cart.background = ContextCompat.getDrawable(
                            this,
                            R.drawable.added_to_cart_grey
                        )
                        add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                        add_item_to_cart.text = getString(R.string.added_to_cart)
//                        havent_bought_the_feature.visibility = View.INVISIBLE
                        itemInCartStatus = true
                        break
                    }
                }
                badgeNumber = cart_list!!.size
                badge121.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
                if (!itemInCartStatus) {
                    loadCostToButtons()
                }
            } else {
                badgeNumber = 0
                badge121.visibility = View.GONE
                itemInCartStatus = false
                loadCostToButtons()

                //clear coupon Applyed in cart if the cart is empty
                prefs.storeApplyedCouponDetails(null)
            }
        })

        viewModel.addonsError().observe(this, Observer
        {
//            longToast(requireContext(), "onFailure: " + it)
            println("addonsError ${it}")
            if (it.contains("Query returned empty"))
                finish()
        })

        viewModel.addonsLoader().observe(this, Observer
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
    }


    fun initializeSecondaryImage() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        secondary_image_recycler.apply {
            layoutManager = gridLayoutManager
        }
    }


    fun initializePackageRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        pack_recycler.apply {
            layoutManager = gridLayoutManager
        }
    }


//    private fun initializeViewPager() {
//        reviewViewpager.adapter = reviewAdaptor
//        dots_indicator.setViewPager2(reviewViewpager)
//        reviewViewpager.offscreenPageLimit = 1
//
//        reviewViewpager.setPageTransformer(ZoomOutPageTransformer())
//
//        val itemDecoration = HorizontalMarginItemDecoration(
//            applicationContext,
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        reviewViewpager.addItemDecoration(itemDecoration)
////        viewpager.setPageTransformer(ZoomOutPageTransformer())
//    }


    fun addUpdatePacks(list: ArrayList<BundlesModel>) {
        if(list.size>0) {
            featurePacksAdapter.addupdates(list, addonDetails!!.name ?: "")
            pack_recycler.adapter = featurePacksAdapter
            featurePacksAdapter.notifyDataSetChanged()
            pack_container.visibility = View.VISIBLE
        }else{
            pack_container.visibility = View.GONE
        }
    }

    fun addUpdateSecondaryImage(list: ArrayList<String>) {
        secondaryImagesAdapter.addUpdates(list)
        secondary_image_recycler.adapter = secondaryImagesAdapter
        secondaryImagesAdapter.notifyDataSetChanged()
    }

    fun loadCostToButtons() {
        try {

            //if the View is opened from package then hide button, price, discount and Cart icon
            if (intent.extras != null && intent.extras!!.containsKey("packageView")) {
                imageViewCart121.visibility = View.INVISIBLE
                unit_container.visibility = View.GONE
//                orig_cost.visibility = View.GONE
//                details_discount.visibility = View.GONE
                add_item_to_cart.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.grey_button_click_effect
                )
                add_item_to_cart.setTextColor(Color.parseColor("#bbbbbb"))
                add_item_to_cart.text = "ITEM BELONG TO PACKAGE"
                add_item_to_cart.isEnabled = false
//                havent_bought_the_feature.visibility = View.INVISIBLE
                return
            }

            if (addonDetails!!.is_premium) {
                add_item_to_cart.visibility = View.VISIBLE
                add_item_to_cart.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.orange_button_click_effect
                )
                add_item_to_cart.setTextColor(Color.WHITE)
                val discount = 100 - addonDetails!!.discount_percent
                val paymentPrice = (discount * addonDetails!!.price) / 100
                cost_per_month.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                    .format(paymentPrice) + "/unit/month"
                cost_per_year.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                    .format(paymentPrice * 12) + "/unit/year"
                if(addonDetails!!.discount_percent>0) {
                    cost_per_year_discount.setText(discount.toString() + "% SAVING")
                    cost_per_year_discount.visibility = View.VISIBLE
                }else{
                    cost_per_year_discount.visibility = View.GONE
                }

//                //hide or show MRP price
//                if (paymentPrice != addonDetails!!.price) {
//                    orig_cost.visibility = View.VISIBLE
//                    spannableString(addonDetails!!.price)
//                } else {
//                    orig_cost.visibility = View.INVISIBLE
//                }

//                add_item_to_cart.text = "Add for ₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(paymentPrice) + "/Month"
//                havent_bought_the_feature.visibility = View.VISIBLE
            } else {
                add_item_to_cart.visibility = View.GONE
//                orig_cost.visibility = View.GONE
//                money.text = "Free Forever"
            }
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
            SentryController.captureException(e)
        }
    }

//    fun spannableString(value: Int) {
//        val origCost = SpannableString("Original cost ₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/month")
//
//        origCost.setSpan(
//            StrikethroughSpan(),
//            14,
//            origCost.length,
//            0
//        )
//        orig_cost.text = origCost
//    }

    fun loadData() {
        viewModel.loadAddonsFromDB(singleWidgetKey!!)
        viewModel.getAllPackages()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.addItemToCart -> {
                val dialog = FeaturesDetailsDialog()
                dialog.show(this.supportFragmentManager, "FeatureDialog")
            }
        }
    }

    override fun imagePreviewPosition(list: ArrayList<String>, pos: Int) {
        val args = Bundle()
        args.putInt("position", pos)
        args.putStringArrayList("list", list)
        imagePreviewPopUpFragement.arguments = args
        imagePreviewPopUpFragement.show(supportFragmentManager, IMAGE_PREVIEW_POPUP_FRAGMENT)
    }

    override fun onPackageClicked(item: Bundles?) {

        val event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
        item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
        item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }

        event_attributes.put("Discount %", item!!.overall_discount_percent)
        event_attributes.put("Package Identifier", item!!._kid)
        item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
        WebEngageController.trackEvent(FEATURE_PACKS_CLICKED, ADDONS_MARKETPLACE, event_attributes)

        val intent = Intent(this, ComparePacksActivity::class.java)
        intent.putExtra("bundleData", Gson().toJson(item))
        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
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

    private fun getDiscountedPrice(price: Int, discountPercent: Int): Int {
        return price - ((discountPercent / 100) * price)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

}