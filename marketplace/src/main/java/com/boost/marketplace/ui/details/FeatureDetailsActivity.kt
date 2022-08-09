package com.boost.marketplace.ui.details

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.adapter.BenifitsPageTransformer
import com.boost.cart.adapter.BenifitsPageTransformerFullWidth
import com.boost.cart.adapter.SimplePageTransformerSmall
import com.boost.cart.adapter.ZoomOutPageTransformer
import com.boost.cart.utils.Utils
import com.boost.cart.utils.Utils.priceCalculatorForYear
import com.boost.cart.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.*
import com.boost.marketplace.R
import com.boost.marketplace.adapter.*
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityFeatureDetailsBinding
import com.boost.marketplace.infra.utils.Constants.Companion.IMAGE_PREVIEW_POPUP_FRAGMENT
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.details.domain.CustomDomainActivity
import com.boost.marketplace.ui.details.staff.StaffManagementBottomSheet
import com.boost.marketplace.ui.popup.ImagePreviewPopUpFragement
import com.boost.marketplace.ui.popup.PackagePopUpFragement
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.boost.marketplace.ui.popup.call_track.FindingNumberLoaderBottomSheet
import com.boost.marketplace.ui.popup.call_track.RequestCallbackBottomSheet
import com.boost.marketplace.ui.popup.call_track.SelectNumberBottomSheet
import com.boost.marketplace.ui.popup.removeItems.RemovePackageBottomSheet
import com.boost.marketplace.ui.webview.WebViewActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import com.framework.webengageconstant.*
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feature_details.*
import retrofit2.Retrofit
import java.text.NumberFormat
import java.util.*

class FeatureDetailsActivity :
    AppBaseActivity<ActivityFeatureDetailsBinding, FeatureDetailsViewModel>(),
    DetailsFragmentListener, CompareListener, AddonsListener {

    lateinit var retrofit: Retrofit
    val callTrackingHelpBottomSheet = CallTrackingHelpBottomSheet()
    val requestCallbackBottomSheet = RequestCallbackBottomSheet()
    lateinit var staffManagementBottomSheet: StaffManagementBottomSheet
    lateinit var removePackageBottomSheet: RemovePackageBottomSheet


    //  lateinit voar localStorage: LocalStorage
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
    var addedToCart: Boolean = false
    var packageItem: Boolean = false
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0

    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7

    var userPurchsedWidgets = ArrayList<String>()
    val bundlesList = arrayListOf<BundlesModel>()

    lateinit var progressDialog: ProgressDialog
    val dialogCard = FindingNumberLoaderBottomSheet()

    lateinit var reviewAdaptor: ReviewViewPagerAdapter
    lateinit var howToUseAdapter: HowToUseAdapter
    lateinit var faqAdapter: FAQAdapter
    lateinit var benefitAdaptor: BenefitsViewPagerAdapter
    lateinit var secondaryImagesAdapter: SecondaryImagesAdapter
    lateinit var featurePacksAdapter: FeaturePacksAdapter

    val imagePreviewPopUpFragement = ImagePreviewPopUpFragement()

    lateinit var prefs: SharedPrefs

    var numberprice: String? = null
    var pricing: String? = null

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
        howToUseAdapter = HowToUseAdapter(this, ArrayList())
        faqAdapter = FAQAdapter(this, ArrayList())
        benefitAdaptor = BenefitsViewPagerAdapter(ArrayList(), this)
        singleWidgetKey = intent.extras?.getString("itemId")
        prefs = SharedPrefs(this)
        viewModel.setApplicationLifecycle(application, this)
        staffManagementBottomSheet = StaffManagementBottomSheet(this)
        removePackageBottomSheet = RemovePackageBottomSheet(this)

        initView()
    }

    private fun initView() {

        loadData()
        initMvvm()

        initializeSecondaryImage()
        initializePackageRecycler()
        initializeViewPager()
        initializeCustomerViewPager()
        initializeHowToUseRecycler()
        initializeFAQRecycler()


        featureEdgeCase()

        val termsString =
            SpannableString("This feature is provided by “Reseller Club” and is subject to their terms and conditions. Read T&C")

        termsString.setSpan(
            UnderlineSpan(),
            termsString.length - 8,
            termsString.length,
            0
        )
        termsString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.started_button_start)),
            termsString.length - 8,
            termsString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        terms.text = termsString

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
        );

        query_text.setText(callExpertString)
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
                    CallTrackingHelpBottomSheet::class.java.getName()
                )
            } else {
                requestCallbackBottomSheet.show(
                    supportFragmentManager,
                    RequestCallbackBottomSheet::class.java.getName()
                )
            }
        }

        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                title_appbar.visibility = View.VISIBLE

                toolbar_details.background =
                    ColorDrawable(resources.getColor(R.color.fullscreen_color))
            } else if (verticalOffset == 0) {
                title_appbar.visibility = View.INVISIBLE
                toolbar_details.background = ColorDrawable(resources.getColor(R.color.transparent))
            } else {
                title_appbar.visibility = View.INVISIBLE
                toolbar_details.background = ColorDrawable(resources.getColor(R.color.transparent))
            }
        }
        )




        add_item_to_cart.setOnClickListener {
            if (packageItem) {
                val args = Bundle()
                args.putString("addonName", addonDetails!!.name)
                removePackageBottomSheet.arguments = args
                removePackageBottomSheet.show(
                    supportFragmentManager,
                    RemovePackageBottomSheet::class.java.name
                )
            } else addItemToCart()
        }

        add_item_to_cart_new.setOnClickListener {
            if (packageItem) {
                val args = Bundle()
                args.putString("addonName", addonDetails!!.name)
                removePackageBottomSheet.arguments = args
                removePackageBottomSheet.show(
                    supportFragmentManager,
                    RemovePackageBottomSheet::class.java.name
                )
            } else addItemToCart()
        }
        learn_more_btn.setOnClickListener {
            when {
                addonDetails?.boost_widget_key?.equals("IVR")!! || addonDetails?.boost_widget_key?.equals(
                    "CALL TRACKER"
                )!! -> {
                    add_item_to_cart.text = "Buy call tracking"
                }
                addonDetails?.boost_widget_key?.equals("DOMAINPURCHASE")!! -> {
                    add_item_to_cart.text = "Choose custom domain"

                }
                else -> {
                    add_item_to_cart.text = "Add to cart"
                }
            }

            learn_more_btn.visibility = View.GONE
            learn_less_btn.visibility = View.VISIBLE
            title_bottom3.maxLines = 20
            bottom_box.visibility = VISIBLE
            bottom_box_only_btn.visibility = GONE
        }

        learn_less_btn.setOnClickListener {
            when {
                addonDetails?.boost_widget_key?.equals("IVR")!! || addonDetails?.boost_widget_key?.equals(
                    "CALL TRACKER"
                )!! -> {
                    add_item_to_cart_new.text = "Buy call tracking"
                }
                addonDetails?.boost_widget_key?.equals("DOMAINPURCHASE")!! -> {
                    add_item_to_cart_new.text = "Choose custom domain"
                }
                else -> {
                    add_item_to_cart_new.text = "Add to cart"
                }
            }
            learn_more_btn.visibility = View.VISIBLE
            learn_less_btn.visibility = View.GONE
            title_bottom3.maxLines = 2
            title_bottom3.ellipsize = TextUtils.TruncateAt.END
            bottom_box.visibility = GONE
            bottom_box_only_btn.visibility = VISIBLE
        }

        imageView121.setOnClickListener {
            finish()
        }
        imageViewCart121.setOnClickListener {
            navigateToCart()
        }

        widgetLearnMore.setOnClickListener {
            if (widgetLearnMoreLink != null && widgetLearnMoreLink!!.length > 0) {
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

    }

    private fun loadNumberList() {
        try {
            viewModel.loadNumberList(
                intent.getStringExtra("fpid") ?: "",
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

    }


    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm() {
        viewModel.getCallTrackingDetails().observe(this) {
            if (it != null) {

                System.out.println("numberList" + it)

                val dialogCard = SelectNumberBottomSheet()
                val bundle = Bundle()

                val discount = 100 - addonDetails!!.discount_percent
                val paymentPrice = Utils.priceCalculatorForYear(
                    (discount * addonDetails!!.price) / 100.0,
                    addonDetails!!.widget_type ?: "",
                    this
                )
                pricing = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                    .format(paymentPrice) + Utils.yearlyOrMonthlyOrEmptyValidity(
                    addonDetails!!.widget_type ?: "", this
                )
                numberprice = pricing
                bundle.putString("price", numberprice)


                bundle.putString("bundleData", Gson().toJson(addonDetails))
                bundle.putDouble(
                    "AddonDiscountedPrice",
                    getDiscountedPrice(addonDetails!!.price, addonDetails!!.discount_percent)
                )
                bundle.putString("phone_number", it[0])
                bundle.putString("fpid", fpid)
                bundle.putString("expCode", experienceCode)
                bundle.putBoolean("isDeepLink", isDeepLink)
                bundle.putString("deepLinkViewType", deepLinkViewType)
                bundle.putInt("deepLinkDay", deepLinkDay)
                bundle.putBoolean("isOpenCardFragment", isOpenCardFragment)
                bundle.putString(
                    "accountType",
                    accountType
                )
                bundle.putStringArrayList(
                    "userPurchsedWidgets",
                    userPurchsedWidgets
                )
                if (email != null) {
                    bundle.putString("email", email)
                } else {
                    bundle.putString("email", "ria@nowfloats.com")
                }
                if (mobileNo != null) {
                    bundle.putString("mobileNo", mobileNo)
                } else {
                    bundle.putString("mobileNo", "9160004303")
                }
                bundle.putString("profileUrl", profileUrl)
                dialogCard.arguments = bundle
                dialogCard.show(
                    this.supportFragmentManager,
                    SelectNumberBottomSheet::class.java.name
                )

            }
        }
        viewModel.addonsResult().observe(this, Observer {

            //if the View is from PackageView then No need to call getCartItems method
            if (!intent.extras!!.containsKey("packageView")) {
                viewModel.getCartItems()
            }
            addonDetails = it
            prefs.storeAddonDetails(addonDetails)

            when {
                it.boost_widget_key.equals("DOMAINPURCHASE") || it.feature_code.equals("DOMAINPURCHASE") -> {
                    terms.visibility = VISIBLE
                }
                else -> {
                    terms.visibility = View.GONE
                }
            }

            when {
                it.boost_widget_key.equals("IVR") || it.boost_widget_key.equals("CALL TRACKER") -> {
                    add_item_to_cart.text = "Buy call tracking"
                    add_item_to_cart_new.text = "Buy call tracking"

                }
                it.boost_widget_key?.equals("DOMAINPURCHASE")!! -> {
                    add_item_to_cart.text = "Choose custom domain"
                    add_item_to_cart_new.text = "Choose custom domain"
                }
                else -> {
                    add_item_to_cart.text = "Add to cart"
                    add_item_to_cart_new.text = "Add to cart"

                }
            }

            if (addonDetails != null && addonDetails?.benefits != null) {
                benefit_container.visibility = VISIBLE
                val benefits = Gson().fromJson<List<String>>(
                    addonDetails?.benefits!!,
                    object : TypeToken<List<String>>() {}.type
                )
                benefitAdaptor.addupdates(benefits)
                benefitAdaptor.notifyDataSetChanged()
            }
            if (addonDetails != null && addonDetails?.all_testimonials != null) {
                what_our_customer_container.visibility = VISIBLE
                val testimonials = Gson().fromJson<List<AllTestimonial>>(
                    addonDetails?.all_testimonials,
                    object : TypeToken<List<AllTestimonial>>() {}.type
                )
                reviewAdaptor.addupdates(testimonials)
                reviewAdaptor.notifyDataSetChanged()
            }
            if (addonDetails != null && addonDetails?.how_to_use_steps != null) {
                how_to_use_title_layout.visibility = VISIBLE
                how_to_use_title_layout.setOnClickListener {
                    if (how_to_use_recycler.visibility == VISIBLE) {
                        how_to_use_arrow.setImageResource(R.drawable.ic_arrow_down_gray)
                        how_to_use_recycler.visibility = GONE
                    } else {
                        how_to_use_arrow.setImageResource(R.drawable.ic_arrow_up_gray)
                        how_to_use_recycler.visibility = VISIBLE
                    }
                }
//                if (addonDetails!!.target_business_usecase != null) {
//                    tv_how_to_use_title.text =
//                        "How To Use " + addonDetails!!.how_to_use_title
//                }
                val steps = Gson().fromJson<List<HowToUseStep>>(
                    addonDetails?.how_to_use_steps,
                    object : TypeToken<List<HowToUseStep>>() {}.type
                )
                howToUseAdapter.addupdates(steps)
                howToUseAdapter.notifyDataSetChanged()
            }

            if (addonDetails != null && addonDetails?.all_frequently_asked_questions != null) {
                faq_container.visibility = VISIBLE
                val faq = Gson().fromJson<List<AllFrequentlyAskedQuestion>>(
                    addonDetails?.all_frequently_asked_questions,
                    object : TypeToken<List<AllFrequentlyAskedQuestion>>() {}.type
                )
                faqAdapter.addupdates(faq)
                faqAdapter.notifyDataSetChanged()
            }
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
                    .into(addon_icon)

                Glide.with(this).load(addonDetails!!.feature_banner)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fitCenter()
                    .into(details_image_bg)

                if (addonDetails!!.secondary_images.isNullOrEmpty())
                else {
                    val objectType = object : TypeToken<ArrayList<String>>() {}.type
                    var secondaryImages = Gson().fromJson<ArrayList<String>>(
                        addonDetails!!.secondary_images,
                        objectType
                    )
                    if (secondaryImages != null && secondaryImages.count() > 0) {
                        addUpdateSecondaryImage(secondaryImages)
                    }
                }
                if (addonDetails!!.target_business_usecase != null) {
                    title_top_1.visibility = View.VISIBLE
                    title_top_1.text = addonDetails!!.target_business_usecase
                } else {
                    title_top_1.visibility = View.INVISIBLE
                }
                title_top.text = addonDetails!!.name
                title_appbar.text = addonDetails!!.name
                title_bottom3.text = addonDetails!!.description
                pack_title.text = "Packs with ${addonDetails!!.name}"
                tv_how_to_use_title.text ="How To Use " + addonDetails!!.name
                if (addonDetails!!.total_installs.isNullOrEmpty() || addonDetails!!.total_installs.equals(
                        "--"
                    )
                ) {
                    title_bottom2.text = "Less than 100 businesses have added this"
                } else {
                    title_bottom2.text = "Used by " + addonDetails!!.total_installs + " businesses"
                }

                loadCostToButtons()
                if (learnMoreLink != null) {
                    widgetLearnMore.text = learnMoreLink.link_description
                    widgetLearnMoreLink = learnMoreLink.link
                    widgetLearnMore.visibility = View.VISIBLE
                } else {
                    widgetLearnMore.visibility = View.GONE
                }
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
            }
        })

        viewModel.bundleResult().observe(this, Observer {
            if (it != null) {
                for (singleBundle in it) {
                    if (singleBundle.included_features != null) {
                        val temp = Gson().fromJson<List<IncludedFeature>>(
                            singleBundle.included_features!!,
                            object : TypeToken<List<IncludedFeature>>() {}.type
                        )
                        for (item in temp) {
                            if (item.feature_code.equals(singleWidgetKey)) {
                                bundlesList.add(singleBundle)
                                break
                            }
                        }
                    }
                }
                //update recyclerview
                addUpdatePacks(bundlesList)
            }
        })

        viewModel.cartResult().observe(this, Observer {
            cart_list = it
            itemInCartStatus = false
            packageItem = false
            if (cart_list != null && cart_list!!.size > 0) {
                badge121.visibility = View.VISIBLE
                for (item in cart_list!!) {
                    if (item.item_type.equals("bundles")) {
                        if (bundlesList.size > 0) {
                            for (singleBundle in bundlesList) {
                                if (singleBundle.bundle_id.equals(item.item_id)) {
                                    val includedFeatures = Gson().fromJson<List<IncludedFeature>>(
                                        singleBundle.included_features,
                                        object : TypeToken<List<IncludedFeature>>() {}.type
                                    )
                                    for (singleFeature in includedFeatures) {
                                        if (singleFeature.feature_code.equals(addonDetails!!.feature_code)) {
                                            itemInCartStatus = true
                                            packageItem = true
//                                            loadCostToButtons()
                                            break
                                        }
                                    }
                                    break
                                }
                            }
                        }

                    } else {
                        if (item.feature_code == singleWidgetKey) {
                            addedToCart = true
                            if (bottom_box.visibility == VISIBLE) {
                                add_item_to_cart.background = ContextCompat.getDrawable(
                                    this,
                                    R.drawable.added_to_cart_grey
                                )
                                add_item_to_cart.setTextColor(getResources().getColor(R.color.tv_color_BB))
                                add_item_to_cart.text = getString(R.string.added_to_cart)
                            } else {
                                add_item_to_cart_new.background = ContextCompat.getDrawable(
                                    this,
                                    R.drawable.added_to_cart_grey
                                )
                                add_item_to_cart_new.setTextColor(getResources().getColor(R.color.tv_color_BB))
                                add_item_to_cart_new.text = getString(R.string.added_to_cart)
                            }
                            itemInCartStatus = true
                            break
                        }
                    }
                }
                if (!addedToCart) {
                    addedToCart = false
                }
                badgeNumber = cart_list!!.size
                badge121.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
                if (!itemInCartStatus) {
                    loadCostToButtons()
                }
            } else {
                badgeNumber = 0
                addedToCart = false
                badge121.visibility = View.GONE
                itemInCartStatus = false
                loadCostToButtons()

                //clear coupon Applyed in cart if the cart is empty
                prefs.storeApplyedCouponDetails(null)
            }
        })

        viewModel.addonsError().observe(this, Observer
        {
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
        viewModel.numberLoader().observe(this, Observer
        {
            if (it) {

                dialogCard.show(
                    this.supportFragmentManager,
                    FindingNumberLoaderBottomSheet::class.java.name
                )
            } else {
                dialogCard.dismiss()
            }
        })
    }

    fun addItemToCart() {
        if (!itemInCartStatus) {
            if (addonDetails != null) {
                when {
                    addonDetails?.boost_widget_key?.equals("IVR")!! || addonDetails?.boost_widget_key?.equals(
                        "CALL TRACKER"
                    )!! -> {
                        loadNumberList()
                    }
                    addonDetails?.boost_widget_key?.equals("DOMAINPURCHASE")!! -> {
                        goToDomainSelection()
                    }
                    addonDetails?.boost_widget_key?.equals("STAFFPROFILE")!! -> {
                        val args = Bundle()
                        args.putString("addonDetails", Gson().toJson(addonDetails))
                        args.putBoolean("addedToCart", addedToCart)
                        staffManagementBottomSheet.arguments = args
                        staffManagementBottomSheet.show(
                            supportFragmentManager,
                            StaffManagementBottomSheet::class.java.name
                        )
                    }
                    else -> {
                        makeFlyAnimation(addon_icon)
                        prefs.storeCartOrderInfo(null)
                        viewModel.addItemToCart1(addonDetails!!, this,null)
                        val event_attributes: HashMap<String, Any> = HashMap()
                        addonDetails!!.name?.let { it1 ->
                            event_attributes.put(
                                "Addon Name",
                                it1
                            )
                        }
                        event_attributes.put("Addon Price", addonDetails!!.price)
                        event_attributes.put(
                            "Addon Discounted Price",
                            getDiscountedPrice(
                                addonDetails!!.price,
                                addonDetails!!.discount_percent
                            )
                        )
                        event_attributes.put(
                            "Addon Discount %",
                            addonDetails!!.discount_percent
                        )
                        event_attributes.put("Addon Validity", 1)
                        event_attributes.put(
                            "Addon Feature Key",
                            addonDetails!!.boost_widget_key
                        )
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

                        Constants.CART_VALUE = badgeNumber


                        add_item_to_cart.background = ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.grey_button_click_effect
                        )
                        add_item_to_cart.setTextColor(getResources().getColor(R.color.tv_color_BB))
                        add_item_to_cart.text = getString(R.string.added_to_cart)
                        itemInCartStatus = true
                        makeFlyAnimation(addon_icon)
                        Glide.with(this).load(addonDetails!!.primary_image)
                            .into(image1222)
                    }
                }
            }
        } else {
            if (addonDetails != null && addonDetails?.boost_widget_key?.equals("DOMAINPURCHASE")!!) {
                goToDomainSelection()
            }
        }
    }

    fun goToDomainSelection() {
        val intent = Intent(
            applicationContext,
            CustomDomainActivity::class.java
        )
        intent.putExtra("expCode", experienceCode)
        intent.putExtra("fpid", fpid)
        intent.putExtra("bundleData", Gson().toJson(addonDetails))
        intent.putExtra(
            "AddonDiscountedPrice",
            getDiscountedPrice(addonDetails!!.price, addonDetails!!.discount_percent)
        )
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.getCartItems()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    fun initializeSecondaryImage() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

    }


    fun initializePackageRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        pack_recycler.apply {
            layoutManager = gridLayoutManager
        }
    }

    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        how_to_use_recycler.apply {
            layoutManager = gridLayoutManager
            how_to_use_recycler.adapter = howToUseAdapter
        }
    }

    private fun initializeFAQRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        faq_recycler.apply {
            layoutManager = gridLayoutManager
            faq_recycler.adapter = faqAdapter
        }
    }

    private fun initializeViewPager() {
        benefits_viewpager.adapter = benefitAdaptor
        benefits_indicator.setViewPager2(benefits_viewpager)
        benefits_viewpager.offscreenPageLimit = 1

//        benefits_viewpager.setPageTransformer(SimplePageTransformerSmall())
        benefits_viewpager.setPageTransformer(BenifitsPageTransformerFullWidth(this))

//        val itemDecoration = HorizontalMarginItemDecoration(
//            applicationContext,
//            R.dimen.viewpager_current_item_horizontal_margin,
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        benefits_viewpager.addItemDecoration(itemDecoration)
    }


    private fun initializeCustomerViewPager() {
        what_our_customer_viewpager.adapter = reviewAdaptor
        what_our_customer_indicator.setViewPager2(what_our_customer_viewpager)
        what_our_customer_viewpager.offscreenPageLimit = 1

        what_our_customer_viewpager.setPageTransformer(ZoomOutPageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        what_our_customer_viewpager.addItemDecoration(itemDecoration)
    }


    fun addUpdatePacks(list: ArrayList<BundlesModel>) {
        if (list.size > 0) {
            featurePacksAdapter.addupdates(list, addonDetails?.name ?: "")
            pack_recycler.adapter = featurePacksAdapter
            featurePacksAdapter.notifyDataSetChanged()
            pack_container.visibility = View.VISIBLE
        } else {
            pack_container.visibility = View.GONE
        }
    }

    fun addUpdateSecondaryImage(list: ArrayList<String>) {
        secondaryImagesAdapter.addUpdates(list)
        secondaryImagesAdapter.notifyDataSetChanged()
    }

    fun loadCostToButtons() {
        try {

            //if the View is opened from package then hide button, price, discount and Cart icon
            if (intent.extras != null && intent.extras!!.containsKey("packageView")) {
                imageViewCart121.visibility = View.INVISIBLE
                if (bottom_box.visibility == VISIBLE) {
                    add_item_to_cart.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart.setTextColor(getResources().getColor(R.color.tv_color_BB))
//                    if(packageItem)
//                        add_item_to_cart.text = "ITEM BELONGS TO THE PACK IN CART"
//                    else {
                    add_item_to_cart.text = "ITEM BELONG TO PACKAGE"
                    add_item_to_cart.isEnabled = false
//                    }
                } else {
                    add_item_to_cart_new.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.grey_button_click_effect
                    )
                    add_item_to_cart_new.setTextColor(getResources().getColor(R.color.tv_color_BB))
//                    if(packageItem)
//                        add_item_to_cart.text = "ITEM BELONGS TO THE PACK IN CART"
//                    else {
                    add_item_to_cart.text = "ITEM BELONG TO PACKAGE"
                    add_item_to_cart_new.isEnabled = false
//                    }
                }
                return
            }

            if (addonDetails!!.is_premium) {
                if (bottom_box.visibility == VISIBLE) {
                    add_item_to_cart.visibility = View.VISIBLE
                    add_item_to_cart.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.cta_button_click_effect
                    )
                    add_item_to_cart.setTextColor(Color.WHITE)
                } else {
                    add_item_to_cart_new.visibility = View.VISIBLE
                    add_item_to_cart_new.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.cta_button_click_effect
                    )
                    add_item_to_cart_new.setTextColor(Color.WHITE)
                }

                val discount = 100 - addonDetails!!.discount_percent
                val paymentPrice = priceCalculatorForYear(
                    (discount * addonDetails!!.price) / 100.0,
                    addonDetails!!.widget_type ?: "",
                    this
                )
                //hide or show MRP price
                val originalCost = priceCalculatorForYear(
                    addonDetails!!.price,
                    addonDetails!!.widget_type ?: "",
                    this
                )
                if (paymentPrice != originalCost) {
                    mrp_price.visibility = View.VISIBLE
                    spannableString(originalCost)
                } else {
                    mrp_price.text =
                        yearlyOrMonthlyOrEmptyValidity(addonDetails!!.widget_type ?: "", this)
                    mrpPrice.visibility = View.INVISIBLE
                }

                price.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                    .format(paymentPrice) + yearlyOrMonthlyOrEmptyValidity(
                    addonDetails!!.widget_type ?: "", this
                )
                final_price.text =
                    "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(paymentPrice)
            } else {
                add_item_to_cart.visibility = View.GONE
                price.visibility = View.GONE
                bottom_box.visibility = View.GONE
                bottom_box_only_btn.visibility = GONE
                add_item_to_cart_new.visibility = GONE
                mrpPrice.text = "Free Forever"
            }
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
            SentryController.captureException(e)
        }
    }

    fun spannableString(value: Double) {
        val origCost = SpannableString(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(value) + yearlyOrMonthlyOrEmptyValidity(
                addonDetails!!.widget_type ?: "",
                this
            )
        )

        origCost.setSpan(
            StrikethroughSpan(),
            0,
            origCost.length,
            0
        )
        mrp_price.text = origCost
        mrpPrice.text = origCost
    }

    fun loadData() {
        try {
            viewModel.loadAddonsFromDB(singleWidgetKey!!)
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
        try {
            viewModel.getAllPackages()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
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


    fun featureEdgeCase() {
        val edgeState = "AutoRenewalOn"
        when (edgeState) {
            "ActionRequired" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
                edge_case_title.setText("Action Required")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.red))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_red,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("You need to take action to activate this feature.")
                edge_case_desc.setText("There is an internal error inside Boost 360. We are working to resolve this issue.")
            }
            "SomethingWentWrong!" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
                edge_case_title.setText("Something went wrong!")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.red))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_red,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("There is an internal error inside Boost 360. We are working to resolve this issue.")

            }
            "active" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
                edge_case_title.setText("Feature is currently active")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.green))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText(
                    "Feature validity expiring on Aug 23, 2021. You\n" +
                            "can extend validity by renewing it for a\n" +
                            "longer duration."
                )

            }
            "expired" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
                edge_case_title.setText("Feature expired on Aug 23, 2021")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.red))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_red,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText(
                    "You need to renew this feature to continue\n" +
                            "using a custom domain. Your domain may be\n" +
                            "lost if you don’t renew it."
                )
            }
            "Syncing" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_skyblue_white_bg)
                edge_case_title.setText("Syncing information")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.light_blue2))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_sync_blue,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("We are working on syncing your information for this feature. It may take some time to get updated. Contact support for help.")
            }
            "AutoRenewalOn" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
                edge_case_title.setText("Auto renewal is turned on")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.green))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("We are working on syncing your information for this feature. It may take some time to get updated. Contact support for help.")
            }
            "AddedToCart" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_orange_white_bg)
                edge_case_title.setText("Feature is currently in cart. ")
                edge_case_title.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.common_text_color
                    )
                )
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_cart_black,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("Feature is currently in cart. ")
            }
            "PartOfPackage" -> {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
                edge_case_title.setText("Feature is part of “Online Classic”")
                edge_case_title.setTextColor(ContextCompat.getColor(this, R.color.green))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("")
            }
        }
    }

    override fun onPackageClicked(item: Bundles?) {

        val event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
        item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
        item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }

        event_attributes.put("Discount %", item!!.overall_discount_percent)
        event_attributes.put("Package Identifier", item!!._kid)
        item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
        WebEngageController.trackEvent(FEATURE_PACKS_CLICKED, ADDONS_MARKETPLACE, event_attributes)

        val packagePopup = PackagePopUpFragement(this, this)
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        args.putString("cartList", Gson().toJson(cart_list))
        packagePopup.arguments = args
        packagePopup.show(supportFragmentManager, "PACKAGE_POPUP")
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

    private fun getDiscountedPrice(price: Double, discountPercent: Int): Double {
        return price - ((discountPercent / 100) * price)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

    override fun onAddonsClicked(item: FeaturesModel) {
    }

    override fun onRefreshCart() {
        viewModel.getCartItems()
    }

    override fun onPackageClicked(item: Bundles?, imageView: ImageView?) {
        if (item != null) {
            prefs.storeAddedPackageDesc(item.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in item.included_features) {
                itemIds.add(i.feature_code)
            }
            if (imageView != null)
                makeFlyAnimation(imageView)

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
                                if (item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1

                            for (singleItem in it) {
                                for (item in item!!.included_features) {
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

                            if (item!!.overall_discount_percent > 0)
                                offeredBundlePrice = RootUtil.round(
                                    originalBundlePrice - (originalBundlePrice * item!!.overall_discount_percent / 100),
                                    2
                                )
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
                                    null,
                                    ""
                                )
                            )
                            val event_attributes: java.util.HashMap<String, Any> =
                                java.util.HashMap()
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
                            event_attributes.put(
                                "Discount %",
                                item!!.overall_discount_percent
                            )
                            item!!.min_purchase_months?.let { it1 ->
                                event_attributes.put(
                                    "Validity",
                                    it1
                                )
                            }
                            WebEngageController.trackEvent(
                                ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                ADDONS_MARKETPLACE,
                                event_attributes
                            )
                            badgeNumber = badgeNumber + 1
                            Log.v("badgeNumber321", " " + badgeNumber)
                            Constants.CART_VALUE = badgeNumber
                            viewModel.getCartItems()
                        },
                        {
                            it.printStackTrace()

                        }
                    )
            )


        }
    }

}