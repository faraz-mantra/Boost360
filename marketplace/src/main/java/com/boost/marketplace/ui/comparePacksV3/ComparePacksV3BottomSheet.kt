package com.boost.marketplace.ui.comparePacksV3

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.CartActivity
import com.boost.cart.adapter.SimplePageTransformerSmall
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.PacksV3BenefitsViewPagerAdapter
import com.boost.marketplace.R
import com.boost.marketplace.databinding.Comparepacksv3PopupBinding
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.pref.UserSessionManager
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ComparePacksV3BottomSheet :
    BaseBottomSheetDialog<Comparepacksv3PopupBinding, ComparePacksViewModel>() {

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

        binding?.buyPack?.text = "Buy " + bundleData.name

        binding?.addonsCountTv?.text =
            addonsSize.toString() + " PREMIUM FEATURES ideal for small businesses that want to get started with online sales."

        binding?.closeBtn?.setOnClickListener {
            dismiss()
        }

        initializeViewPager()

        if (bundleData.benefits != null) {
            binding?.benefitsContainer12?.visibility=View.VISIBLE
            benefitAdaptor.addupdates(bundleData.benefits!!)
            benefitAdaptor.notifyDataSetChanged()
            initializeViewPager()
        }

        initMvvm()

        binding?.buyPack?.setOnClickListener {
            if (bundleData != null) {
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

        }


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
            viewModel?.getAllFeaturesFromDB()
            viewModel?.getCartItems()
            viewModel?.loadPackageUpdates()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {

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
                    binding?.buyPack?.setText("Buy ${bundleData.name}")
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

    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsViewpager?.let { binding?.benefitsIndicator?.setViewPager2(it) }
        binding?.benefitsViewpager?.offscreenPageLimit = 1
        binding?.benefitsViewpager?.setPageTransformer(SimplePageTransformerSmall())

        val itemDecoration = HorizontalMarginItemDecoration(
            requireContext(),
            R.dimen.viewpager_next_item_visible
        )
        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

    }

}