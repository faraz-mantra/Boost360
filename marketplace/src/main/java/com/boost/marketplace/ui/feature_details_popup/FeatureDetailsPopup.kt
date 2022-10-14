package com.boost.marketplace.ui.feature_details_popup

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.CartActivity
import com.boost.cart.ui.home.CartViewModel
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.interfaces.MarketPlacePopupListener
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_details_popup.*
import kotlinx.android.synthetic.main.layout_details_popup.view.*
import kotlinx.android.synthetic.main.view_select_website.*
import kotlinx.android.synthetic.main.view_select_website.view.*
import kotlinx.android.synthetic.main.view_select_website.view.selectWebsiteSubmit
import kotlinx.android.synthetic.main.view_selected_number.*
import kotlinx.android.synthetic.main.view_selected_website.view.*

class FeatureDetailsPopup(val listener: MarketPlacePopupListener, val homeListener: CompareListener, val addonsListener: AddonsListener) : DialogFragment() {
    private var domainName: String? = null
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
    var bundleData: Bundles? = null
    lateinit var prefs: SharedPrefs
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    lateinit var singleAddon: FeaturesModel
    lateinit var viewModel: CartViewModel
    var cartList: List<CartModel>? = null
    val sameAddonsInCart = java.util.ArrayList<String>()
    val addonsListInCart = java.util.ArrayList<String>()

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(com.boost.cart.R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.layout_details_popup, container, false)
        viewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)
        prefs = SharedPrefs(requireActivity())
        fpid = requireArguments().getString("fpid")
        isDeepLink = requireArguments().getBoolean("isDeepLink", false)
        deepLinkViewType = requireArguments().getString("deepLinkViewType") ?: ""
        deepLinkDay = requireArguments().getString("deepLinkDay")?.toIntOrNull() ?: 7
        experienceCode = requireArguments().getString("expCode")
        screenType = requireArguments().getString("screenType")
        fpName = requireArguments().getString("fpName")
        fpid = requireArguments().getString("fpid")
        fpTag = requireArguments().getString("fpTag")
        email = requireArguments().getString("email")
        mobileNo = requireArguments().getString("mobileNo")
        profileUrl = requireArguments().getString("profileUrl")
        accountType = requireArguments().getString("accountType")
        isOpenCardFragment = requireArguments().getBoolean("isOpenCardFragment", false)
        isOpenHomeFragment = requireArguments().getBoolean("isComingFromOrderConfirm", false)
        isOpenAddOnsFragment =
            requireArguments().getBoolean("isComingFromOrderConfirmActivation", false)
        widgetFeatureCode = requireArguments().getString("buyItemKey")
        userPurchsedWidgets =
            requireArguments().getStringArrayList("userPurchsedWidgets") ?: ArrayList()

        val jsonString = requireArguments().getString("bundleData")
        bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)

        view.riv_close_bottomSheet.setOnClickListener {
            dismiss()
        }

        // Default layout to open
        view.select_website_layout.visibility = View.VISIBLE
        view.select_website_layout.selectWebsiteIwillDoItLater.text = "Skip & continue to cart"
        view.selectWebsiteIwillDoItLater.setOnClickListener {
            hideAllLayout()
            val intent = Intent(
                activity,
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
            dismiss()
            //  select_domain_layout.visibility = View.VISIBLE
        }

//        selectDomainIwillDoItLater.setOnClickListener {
//            hideAllLayout()
//            no_selection_layout.visibility = View.VISIBLE
//        }

        view.selectWebsiteSubmit.setOnClickListener {
            hideAllLayout()
            view.selected_website_layout.visibility = View.VISIBLE
            view.selectedWebsiteContinueButton.text = "continue to cart"
            view.tv_empty_selected_website.text = domainName
            view.tv_explore_select_website1.setOnClickListener {
                exploreDomainOptions()
            }
            listener.featureDetailsPopup(domainName!!)
        }

//        selectedNumberContinue.setOnClickListener {
//            hideAllLayout()
//            review_selection_layout.visibility = View.VISIBLE
//        }

        view.selectedWebsiteContinueButton.setOnClickListener {
            // Onclick of continue button
            // hideAllLayout()
            addToCart()
            //   select_domain_layout.visibility = View.VISIBLE
            //   fpid?.let { viewModel?.loadNumberList(it, clientId) }
        }

        view.tv_explore_select_website.setOnClickListener {
            exploreDomainOptions()
        }

        loadData()
        initView()
        initMvvm()
        return view
    }

    private fun initView() {
        if (bundleData != null) {
            prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in bundleData!!.included_features) {
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
                            if (it.isNotEmpty()) {
                                for (singleItem in it) {
                                    if (singleItem.feature_code == "DOMAINPURCHASE") {
                                        singleAddon = singleItem
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

    private fun addToCart() {
        if (bundleData != null) {
            prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in bundleData!!.included_features) {
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
                            if (cartList != null) {
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

                            if (sameAddonsInCart.size > 0) {
                                val removeFeatureBottomSheet =
                                    RemoveFeatureBottomSheet(homeListener, addonsListener, null)
                                val args = Bundle()
                                args.putStringArrayList("addonNames", sameAddonsInCart)
                                args.putStringArrayList("addonsListInCart", addonsListInCart)
                                args.putString("packageDetails", Gson().toJson(bundleData))
                                removeFeatureBottomSheet.arguments = args
                                removeFeatureBottomSheet.show(
                                    childFragmentManager,
                                    RemoveFeatureBottomSheet::class.java.name
                                )
                            } else {
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

                                //remove other bundle and add existing bundle to cart
                                removeOtherBundlesAndAddExistingBundle(addonsListInCart, bundleData!!, offeredBundlePrice, originalBundlePrice)

                            }
                        },
                        {
                            it.printStackTrace()
                        }
                    )
            )
        }

        val intent = Intent(
            activity,
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
        dismiss()
    }

    fun removeOtherBundlesAndAddExistingBundle(addonsListInCart: List<String>, bundle: Bundles, offerBundlePrice: Double, originalBundlePrice: Double ){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
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
            }
            .doOnError {
                Toast.makeText(requireContext(), "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
            }
            .subscribe()
    }

    override fun onResume() {
        super.onResume()
        //clear previous existing data
        sameAddonsInCart.clear()
        addonsListInCart.clear()
    }

    private fun loadData() {
        try {
            val pref = activity?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
            fpTag?.let { DomainRequest(Constants.clientid, it) }
                ?.let { viewModel.getSuggestedDomains(it) }
            viewModel.getCartItems()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {

        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            cartList = it
        })

        viewModel.updateCustomDomainsResultResult().observe(this) {
            for (singleDomain in it.domains) {
                if (singleDomain.isAvailable) {
                    domainName = singleDomain.name
                    tv_empty_select_website.text = singleDomain.name
                    break
                }
            }

        }

        viewModel.updatesLoader.observe(this) {
            if (it) {
                mainll.visibility = View.GONE
                shimmer_anim.visibility = View.VISIBLE

            } else {
                mainll.visibility = View.VISIBLE
                shimmer_anim.visibility = View.GONE
            }
        }

        viewModel.getCallTrackingDetails().observe(this) {
            if (it != null) {

                System.out.println("numberList" + it)
                val selectedNum = it[0]
                tv_empty_select_number.text = selectedNum
                //                val dialogCard = SelectNumberBottomSheet()
                //                val bundle = Bundle()

                //                val content = SpannableString("Claim the above number\n@ ${numberprice}")
                //                content.setSpan(
                //                    StyleSpan(Typeface.BOLD),
                //                    0,
                //                    22,
                //                    0
                //                )
                //                claim_button.setText(content)
                //                claim_button.setOnClickListener {
                //                    if (selectedNum != null) {
                //                        if (!itemInCartStatus) {
                //                            if (addonDetails != null) {
                //                                prefs.storeCartOrderInfo(null)
                //                                viewModel!!.addItemToCart1(addonDetails!!, this, selectedNum)
                //                                val event_attributes: HashMap<String, Any> = HashMap()
                //                                addonDetails!!.name?.let { it1 ->
                //                                    event_attributes.put(
                //                                        "Addon Name",
                //                                        it1
                //                                    )
                //                                }
                //                                event_attributes.put("Addon Price", addonDetails!!.price)
                //                                event_attributes.put(
                //                                    "Addon Discounted Price",
                //                                    getDiscountedPrice(
                //                                        addonDetails!!.price,
                //                                        addonDetails!!.discount_percent
                //                                    )
                //                                )
                //                                event_attributes.put(
                //                                    "Addon Discount %",
                //                                    addonDetails!!.discount_percent
                //                                )
                //                                event_attributes.put("Addon Validity", 1)
                //                                event_attributes.put(
                //                                    "Addon Feature Key",
                //                                    addonDetails!!.boost_widget_key
                //                                )
                //                                addonDetails!!.target_business_usecase?.let { it1 ->
                //                                    event_attributes.put(
                //                                        "Addon Tag",
                //                                        it1
                //                                    )
                //                                }
                //                                WebEngageController.trackEvent(
                //                                    ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
                //                                    ADDONS_MARKETPLACE,
                //                                    event_attributes
                //                                )
                //                                itemInCartStatus = true
                //                            }
                //                        }
                //
                //                    } else {
                //                        Toasty.error(
                //                            this,
                //                            "Number not available, please select other",
                //                            Toast.LENGTH_SHORT
                //                        ).show()
                //                    }
                //                    val intent = Intent(this, CartActivity::class.java)
                //                    intent.putExtra("fpid", fpid)
                //                    intent.putExtra("expCode", experienceCode)
                //                    intent.putExtra("isDeepLink", isDeepLink)
                //                    intent.putExtra("deepLinkViewType", deepLinkViewType)
                //                    intent.putExtra("deepLinkDay", deepLinkDay)
                //                    intent.putExtra("isOpenCardFragment", isOpenCardFragment)
                //                    intent.putExtra("accountType", accountType)
                //                    intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
                //                    if (email != null) {
                //                        intent.putExtra("email", email)
                //                    } else {
                //                        intent.putExtra("email", "ria@nowfloats.com")
                //                    }
                //                    if (mobileNo != null) {
                //                        intent.putExtra("mobileNo", mobileNo)
                //                    } else {
                //                        intent.putExtra("mobileNo", "9160004303")
                //                    }
                //                    intent.putExtra("profileUrl", profileUrl)
                //                    startActivity(intent)
                //                }
                //                choose_different_value.setText("Pick another number")
            }
        }
    }

    fun exploreDomainOptions() {
        try {
            val intent = Intent(
                activity,
                Class.forName("com.boost.marketplace.ui.details.domain.CustomDomainActivity")
            )
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("fpid", fpid)
            intent.putExtra("bundleData", Gson().toJson(singleAddon))
            intent.putExtra("domainSelectionForCart", true)
            startActivity(intent)
            //  dismiss()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun hideAllLayout() {
        select_domain_layout.visibility = View.GONE
        review_selection_layout.visibility = View.GONE
        select_website_layout.visibility = View.GONE
        no_selection_layout.visibility = View.GONE
        selected_website_layout.visibility = View.GONE
        selected_number_layout.visibility = View.GONE
    }

}