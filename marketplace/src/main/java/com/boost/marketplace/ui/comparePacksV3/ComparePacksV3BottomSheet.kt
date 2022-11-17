package com.boost.marketplace.ui.comparePacksV3

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.CartActivity
import com.boost.cart.adapter.BenifitsPageTransformer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.data.api_model.mycurrentPlanV3.MyPlanV3
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.PacksV3BenefitsViewPagerAdapter
import com.boost.marketplace.R
import com.boost.marketplace.databinding.Comparepacksv3PopupBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.interfaces.MarketPlacePopupListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.boost.marketplace.ui.feature_details_popup.FeatureDetailsPopup
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ComparePacksV3BottomSheet(val activityListener: ComparePacksV3Activity, val homeListener: CompareListener, val addonsListener: AddonsListener) :
    BaseBottomSheetDialog<Comparepacksv3PopupBinding, ComparePacksViewModel>(),
    MarketPlacePopupListener {

    lateinit var bundleData: Bundles
    var experienceCode: String? = null
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var fpid: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var userPurchsedWidgets = java.util.ArrayList<String>()
    var cartList: List<CartModel>? = null
    var packageInCartStatus = false
    var itemInCart = false
    lateinit var prefs: SharedPrefs
    var offeredBundlePrice: Double = 0.0
    var originalBundlePrice: Double = 0.0
    var addonsSize: Int = 0
    var cartCount = 0
    lateinit var benefitAdaptor: PacksV3BenefitsViewPagerAdapter
    val callTrackingHelpBottomSheet = CallTrackingHelpBottomSheet()
    var allowPackageToCart = true
    var myPlanV3: MyPlanV3? = null

    val sameAddonsInCart = ArrayList<String>()
    val addonsListInCart = ArrayList<String>()

    private var purchasedDomainType: String? = null
    private var purchasedDomainName: String? = null

    override fun getLayout(): Int {
        return R.layout.comparepacksv3_popup
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {

        dialog.behavior.isDraggable = true

        bundleData = Gson().fromJson<Bundles>(
            requireArguments().getString("bundleData"),
            object : TypeToken<Bundles>() {}.type
        )

        experienceCode = requireArguments().getString("expCode")
        fpid = requireArguments().getString("fpid")
        isDeepLink = requireArguments().getBoolean("isDeepLink", false)
        deepLinkViewType = requireArguments().getString("deepLinkViewType") ?: ""
        deepLinkDay = requireArguments().getString("deepLinkDay")?.toIntOrNull() ?: 7
        email = requireArguments().getString("email")
        mobileNo = requireArguments().getString("mobileNo")
        profileUrl = requireArguments().getString("profileUrl")
        accountType = requireArguments().getString("accountType")
        isOpenCardFragment = requireArguments().getBoolean("isOpenCardFragment", false)
        userPurchsedWidgets =
            requireArguments().getStringArrayList("userPurchsedWidgets") ?: java.util.ArrayList()
        addonsSize = requireArguments().getInt("addons")
        offeredBundlePrice = requireArguments().getDouble("price")
        originalBundlePrice = requireArguments().getDouble("price")
        benefitAdaptor = PacksV3BenefitsViewPagerAdapter(ArrayList())
        prefs = SharedPrefs(baseActivity)
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)

        binding?.packageTitle?.text = bundleData.name

        binding?.packageProfileImage?.let {
            Glide.with(this).load(bundleData.primary_image!!.url)
                .into(it)
        }

        if (bundleData.overall_discount_percent > 0) {
            offeredBundlePrice = RootUtil.round(
                originalBundlePrice - (originalBundlePrice * bundleData.overall_discount_percent / 100.0),
                2
            )
            binding?.packDiscountTv?.visibility = View.VISIBLE
            binding?.packDiscountTv?.setText( bundleData.overall_discount_percent.toString() + "% SAVING") //= "${bundleData.overall_discount_percent}" +" % SAVING"

        } else {
            offeredBundlePrice = originalBundlePrice
            binding?.packDiscountTv?.visibility = View.VISIBLE
        }

        var originalText = bundleData.name
        originalText = originalText?.lowercase(Locale.getDefault())
        binding?.buyPack?.text = "Buy " + originalText
      //  binding?.buyPack?.text = "Buy " + bundleData.name

        binding?.addonsCountTv?.text =
            addonsSize.toString() + " PREMIUM FEATURES ideal for small businesses that want to get started with online sales."

        binding?.closeBtn?.setOnClickListener {
            dismiss()
        }

        initializeViewPager()

        if (bundleData.benefits != null && bundleData.benefits!!.size > 0){
            benefitAdaptor.addupdates(bundleData.benefits!!)
            benefitAdaptor.notifyDataSetChanged()
            binding?.benefitContainer?.visibility = View.VISIBLE
        }else{
            binding?.benefitContainer?.visibility = View.GONE
        }

        initMvvm()

        binding?.buyPack?.setOnClickListener {

//            if(!allowPackageToCart){
//                val arg = Bundle()
//                arg.putBoolean("allowPackageToCart", allowPackageToCart)
//                callTrackingHelpBottomSheet.arguments = arg
//                fragmentManager?.let { it1 ->
//                    callTrackingHelpBottomSheet.show(
//                        it1,
//                        CallTrackingHelpBottomSheet::class.java.name
//                    )
//                }
//                return@setOnClickListener
//            }

            if (purchasedDomainType.isNullOrEmpty() || purchasedDomainName?.contains("null") == true) {                    // show Popup
                prefs.storeCartOrderInfo(null)

                val bundlesModel = BundlesModel(
                    bundleData._kid,
                    bundleData.name,
                    if (bundleData.min_purchase_months != null && bundleData.min_purchase_months!! > 1) bundleData.min_purchase_months!! else 1,
                    bundleData.overall_discount_percent,
                    if (bundleData.primary_image != null) bundleData.primary_image!!.url else null,
                    Gson().toJson(bundleData.included_features),
                    bundleData.target_business_usecase,
                    Gson().toJson(bundleData.exclusive_to_categories),
                    if (bundleData.frequently_asked_questions != null && bundleData.frequently_asked_questions!!.isNotEmpty()) Gson().toJson(
                        bundleData.frequently_asked_questions
                    ) else null,
                    if (bundleData.how_to_activate != null && bundleData.how_to_activate!!.isNotEmpty()) Gson().toJson(
                        bundleData.how_to_activate
                    ) else null,
                    if (bundleData.testimonials != null && bundleData.testimonials!!.isNotEmpty()) Gson().toJson(
                        bundleData.testimonials
                    ) else null,
                    if (bundleData.benefits != null && bundleData.benefits!!.isNotEmpty()) Gson().toJson(
                        bundleData.benefits
                    ) else null,
                    bundleData.desc,
                )

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
                val benefits = if(bundleData.benefits != null) Gson().fromJson<List<String>>(
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

                val dialogCard = FeatureDetailsPopup(this, homeListener, addonsListener)
                val args = Bundle()
                args.putString("expCode", experienceCode)
                args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
                args.putString("bundleData", Gson().toJson(bundle))
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
                requireActivity().supportFragmentManager.let { dialogCard.show(it, com.boost.cart.ui.popup.FeatureDetailsPopup::class.java.name) }
            } else {
                prefs.storeAddedPackageDesc(bundleData.desc ?: "")

                val itemIds = arrayListOf<String>()
                for (i in bundleData.included_features) {
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
                                    val removeFeatureBottomSheet = RemoveFeatureBottomSheet(activityListener, activityListener, null)
                                    val args = Bundle()
                                    args.putStringArrayList("addonNames", sameAddonsInCart)
                                    args.putStringArrayList("addonsListInCart", addonsListInCart)
                                    args.putString("packageDetails", Gson().toJson(bundleData!!))
                                    removeFeatureBottomSheet.arguments = args
                                    removeFeatureBottomSheet.show(requireActivity().supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                    dismiss()
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

                                    if (bundleData!!.overall_discount_percent > 0)
                                        offeredBundlePrice =
                                            originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100)

                                    else
                                        offeredBundlePrice = originalBundlePrice

                                    //clear cartOrderInfo from SharedPref to requestAPI again
                                    prefs.storeCartOrderInfo(null)
                                    //remove other bundle and add existing bundle to cart
                                    removeOtherBundlesAndAddExistingBundle(addonsListInCart)

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

    fun removeOtherBundlesAndAddExistingBundle(addonsListInCart: List<String>){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                viewModel!!.addItemToCartPackage1(
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
                viewModel?.getCartItems()
            }
            .doOnError {
                Toast.makeText(requireContext(), "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
                viewModel?.getCartItems()
            }
            .subscribe()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val pref = context?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
        val code: String =
            if (experienceCode.isNullOrEmpty()) experienceCode!! else UserSessionManager(
                requireContext()
            ).fP_AppExperienceCode!!
        if (!code.equals("null", true)) {
            viewModel?.setCurrentExperienceCode(code, fpTag!!)
        }
        try {
//            fpid?.let {
//                viewModel?.myPlanV3Status(
//                    it,
//                    "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
//                )
//            }
            getAlreadyPurchasedDomain()
            viewModel?.getCartItems()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {

//        viewModel?.myplanResultV3()?.observe(this, androidx.lifecycle.Observer {
//            allowPackageToCart = true
//            if(it!=null) {
//                binding?.shimmerViewDomainv3?.visibility=View.GONE
//                binding?.container1?.visibility=View.VISIBLE
//                myPlanV3 = it
//                getAllowPackageToCart(bundleData)
//            } else{
//                binding?.shimmerViewDomainv3?.visibility=View.VISIBLE
//                binding?.container1?.visibility=View.GONE
//            }
//        })

        viewModel?.PurchasedDomainResponse()?.observe(this) {
            if(it!=null) {
                binding?.shimmerViewDomainv3?.visibility=View.GONE
                binding?.container1?.visibility=View.VISIBLE
                purchasedDomainName = it.domainName
                purchasedDomainType = it.domainType
                if(it.domainName != null && it.domainType != null) {
                    if(!(it.domainName.contains("null") || it.domainType.contains("null"))) {
                        prefs.storeDomainOrderType(1)
                        prefs.storeSelectedDomainName(it.domainName + it.domainType)
                    }
                }
            }else{
                binding?.shimmerViewDomainv3?.visibility=View.VISIBLE
                binding?.container1?.visibility=View.GONE
            }

        }

        viewModel?.cartResult()?.observe(this, Observer {
            cartList = it
            itemInCart = false
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {

                if (cartList?.size!! > 0) {
                    if (cartList != null) {
                        for (singleCartItem in cartList!!) {
                            if (singleCartItem.item_id.equals(bundleData._kid)) {
                                itemInCart = true
                                break
                            }
                        }
                    }
                }
                if (!itemInCart) {
                    binding?.buyPack?.setTextColor(this.resources.getColor(R.color.white))
                    binding?.buyPack?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_cart_continue_bg
                    )
                    var originalText = bundleData.name
                    originalText = originalText?.lowercase(Locale.getDefault())
                    binding?.buyPack?.text = "Buy " + originalText
                   // binding?.buyPack?.setText("Buy ${bundleData.name}")
                    binding?.buyPack?.isClickable = true
                } else {
                    binding?.buyPack?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_packsv3_added_to_cart_bg
                    )
                    binding?.buyPack?.setTextColor(
                        this.getResources().getColor(R.color.tv_color_BB)
                    )
                    binding?.buyPack?.setText(this.getString(R.string.added_to_cart))
                    binding?.buyPack?.isClickable = false
                }

                cartCount = cartList!!.size

            } else {
                cartCount = 0
            }
        })

        viewModel!!.addedToCartResult().observe(this, Observer {
            if(it){
                val intent = Intent(
                    requireContext(),
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
        })

    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsViewpager?.let { binding?.benefitsIndicator?.setViewPager2(it) }
        binding?.benefitsViewpager?.offscreenPageLimit = 1
        binding?.benefitsViewpager?.setPageTransformer(BenifitsPageTransformer(requireActivity()))

//        val itemDecoration = HorizontalMarginItemDecoration(
//            requireContext(),
//            R.dimen.viewpager_previous_benifits,
//            R.dimen.viewpager_next_benifits
//        )
//        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

//        binding?.benefitsViewpager?.setPageTransformer(SimplePageTransformerSmall())
//
//        val itemDecoration = HorizontalMarginItemDecoration(
//            requireContext(),
//            R.dimen.viewpager_current_item_horizontal_margin,
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

    }

    override fun featureDetailsPopup(domain: String) {
        prefs.storeSelectedDomainName(domain)
    }

    override fun featureDetailsPopup1(vmn: String) {
        prefs.storeSelectedVMNName(vmn)
    }

    private fun getAlreadyPurchasedDomain() {
        val pref = context?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
        val auth = context?.let { UserSessionManager(it).getAccessTokenAuth()?.barrierToken() } ?: ""
        viewModel?.getAlreadyPurchasedDomain(
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

}