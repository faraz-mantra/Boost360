package com.boost.marketplace.ui.Compare_Plans

import android.animation.Animator
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.adapter.SimplePageTransformer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.CircleAnimationUtil
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.ParentCompareItemAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.utils.RootUtil
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_compare_packs.*
import org.json.JSONObject
import java.util.*

class ComparePacksActivity : AppBaseActivity<ActivityComparePacksBinding, ComparePacksViewModel>(),
    CompareListener, AddonsListener,
    RecyclerItemClickListener {

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
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var userPurchsedWidgets = ArrayList<String>()
    private var widgetFeatureCode: String? = null
    var isOpenHomeFragment: Boolean = false
    var isOpenAddOnsFragment: Boolean = false
    var refreshViewPager: Boolean = false


    lateinit var packageAdaptor: ParentCompareItemAdapter

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

    companion object {
        fun newInstance() = ComparePacksActivity()
    }

    override fun getLayout(): Int {
        return R.layout.activity_compare_packs
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

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
        isOpenHomeFragment = intent.getBooleanExtra("isComingFromOrderConfirm", false)
        isOpenAddOnsFragment = intent.getBooleanExtra("isComingFromOrderConfirmActivation", false)
        widgetFeatureCode = intent.getStringExtra("buyItemKey")
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        packageAdaptor = ParentCompareItemAdapter(ArrayList(), this, this, this)
        shimmer_view_compare.startShimmer()
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
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        loadData()
        initMvvm()

        package_back.setOnClickListener {
            onBackPressed()
        }

        package_cart_icon.setOnClickListener {
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
        initializePackageViewPager()
    }

    private fun loadData() {
        val pref = this.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        val code: String =
            if (experienceCode.isNullOrEmpty()) experienceCode!! else UserSessionManager(this).fP_AppExperienceCode!!
        if (!code.equals("null", true)) {
            viewModel.setCurrentExperienceCode(code, fpTag!!)
        }
        try {
            refreshViewPager = true
            viewModel.getCartItems()
            viewModel.loadPackageUpdates()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {
        viewModel.cartResult().observe(this, Observer {
            cartList = it
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {
//                val cartBundleIds = arrayListOf<String>()
//                for (item in it) {
//                    if (item.item_type.equals("bundles")) {
//                        cartBundleIds.add(item.item_id)
//                    }
//                }
                if(refreshViewPager){
                    refreshViewPager = false
                    packageAdaptor.updateCartItem(cartList!!)
                }

                cartCount = cartList!!.size
                badgeNumber = cartList!!.size
                badge121.setText(badgeNumber.toString())
                badge121.visibility = View.VISIBLE
                Log.v("badgeNumber", " " + badgeNumber)
            } else {
                cartCount = 0
                badgeNumber = 0
                badge121.visibility = View.GONE
                packageInCartStatus = false
                if(refreshViewPager) {
                    refreshViewPager = false
                    packageAdaptor.updateCartItem(arrayListOf())
                }
            }
        })

        viewModel.getSpecificFeature().observe(this, Observer {
            if (it != null) {
                for (items in it) {
                    featuresHashMap.put(items.target_business_usecase, items)
                }

                featureCount++
                if (shimmer_view_compare.isShimmerStarted) {
                    shimmer_view_compare.stopShimmer()
                    shimmer_view_compare.visibility = View.GONE
                }
                updatePackageViewPager(upgradeList)
            }
            val obj = JSONObject(featuresHashMap as Map<*, *>)
            Log.v("featuresHashMap", " " + obj)
            if (featuresHashMap.size == 0) {

            }
        })

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            if (it != null && it.size > 0) {
                val listItem = arrayListOf<Bundles>()
                for (item in it) {
                    Log.v("allBundleResultValue", " " + item.name)
                    val temp = Gson().fromJson<List<IncludedFeature>>(
                        item.included_features,
                        object : TypeToken<List<IncludedFeature>>() {}.type
                    )
                    val benefits = Gson().fromJson<List<String>>(
                        item.benefits?:"",
                        object : TypeToken<List<String>>() {}.type
                    )
                    listItem.add(
                        Bundles(
                            item.bundle_id,
                            temp,
                            if(!prefs.getYearPricing()) item.min_purchase_months else 1,
                            item.name,
                            item.overall_discount_percent,
                            PrimaryImage(item.primary_image),
                            item.target_business_usecase,
                            Gson().fromJson<List<String>>(
                                item.exclusive_to_categories,
                                object : TypeToken<List<String>>() {}.type
                            ),
                            null, null,null,null,null,item.desc ?: ""
                        )
                    )
                }
                if (listItem.size > 0) {
                    updatePackageViewPager(listItem)
                    for (items in listItem) {
                        Log.v("getkeyWidget", " " + items.name + " " + items.included_features.size)
                        val itemIds = arrayListOf<String>()
                        for (item in items.included_features!!) {
                            itemIds.add(item.feature_code)
                        }
                        Log.v("getkeyWidget123", " " + itemIds.size)
                        viewModel.getFeatureValues(itemIds)
                        upgradeList = listItem
                    }
                }
            }
        })
    }


    fun updatePackageViewPager(list: List<Bundles>) {

        package_viewpager.offscreenPageLimit = 1
        Log.v("updatePackageViewPager", " " + list.size)
        packageAdaptor.addupdates(list)
        if (list.size > 1) {
            package_indicator2.visibility = View.VISIBLE
        } else {
            package_indicator2.visibility = View.INVISIBLE
        }
    }

    fun initializePackageViewPager() {
        package_viewpager.adapter = packageAdaptor
        package_indicator2.setViewPager2(package_viewpager)

        package_viewpager.setPageTransformer(SimplePageTransformer())

        val itemDecoration = com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        package_viewpager.addItemDecoration(itemDecoration)

    }

    override fun onPackageClicked(item: Bundles?, imageView: ImageView?) {
        if (item != null) {
            prefs.storeAddedPackageDesc(item.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in item.included_features) {
                itemIds.add(i.feature_code)
            }
            if(imageView!=null)
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
                                if (!prefs.getYearPricing() && item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1

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
                                    if (!prefs.getYearPricing() && item!!.min_purchase_months != null) item!!.min_purchase_months!! else 1,
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
                                    if(!prefs.getYearPricing()) it1 else 1
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
//                                            viewModel.getCartItems()
                        },
                        {
                            it.printStackTrace()

                        }
                    )
            )


        }
    }

    private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(this).setTargetView(targetView)
            .setMoveDuration(600)
            .setDestView(package_cart_icon)
            .setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    package_viewpager.currentItem = 0
                    viewModel.getCartItems()
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()

    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
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
        intent.putExtra("itemId", item.boost_widget_key)
        startActivity(intent)
    }

    override fun onRefreshCart() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                package_viewpager.currentItem = 0
                refreshViewPager = true
                viewModel.getCartItems()
            }
        }, 1000)
    }

    override fun onResume() {
        super.onResume()
        try {
            package_viewpager.currentItem = 0
            if (cartCount > 0) {
                refreshViewPager = true
                viewModel.getCartItems()
            }
//            else{
//                package_viewpager.currentItem = 0
//            }
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }
}





