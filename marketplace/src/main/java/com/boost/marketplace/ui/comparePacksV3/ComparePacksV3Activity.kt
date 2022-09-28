package com.boost.marketplace.ui.comparePacksV3

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.data.api_model.packageAddonsCompares.AddonsPacksIn
import com.boost.dbcenterapi.data.api_model.packageAddonsCompares.PackageAddonsCompares
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.*
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksv3Binding
import com.boost.marketplace.interfaces.*
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.boost.marketplace.ui.feature_details_popup.FeatureDetailsPopup
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feature_details.*
import java.util.*


class ComparePacksV3Activity :
    AppBaseActivity<ActivityComparePacksv3Binding, ComparePacksViewModel>(), PacksV3listener,
    PacksV3FooterListener,AddonsListenerV3, CompareListener, AddonsListener, MarketPlacePopupListener {

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
    var packageInCartStatus = false
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    var featureCount = 0
    var cartCount = 0
    lateinit var prefs: SharedPrefs
    var featuresHashMap: MutableMap<String?, FeaturesModel> = HashMap<String?, FeaturesModel>()
    lateinit var progressDialog: ProgressDialog
    private var annualPlan = false
    var selectedBundle: Bundles? = null
    var itemInCart = false

    val sameAddonsInCart = ArrayList<String>()
    val addonsListInCart = ArrayList<String>()

    //  var listItem=ArrayList<Bundles>()
    var upgradeList: ArrayList<Bundles>? = null
    private var cartItems = listOf<CartModel>()
    lateinit var howToUseAdapter: PacksV3HowToUseAdapter
    lateinit var faqAdapter: PacksFaqAdapter
    lateinit var packsv3Adapter: PacksV3Adapter
    lateinit var packsv3footerAdapter: PacksV3FooterAdapter
    lateinit var packsv3pricingAdapter: PacksV3PricingAdapter
    lateinit var packsAddonsAdapter: PacksAddonsV3Adapter

    companion object {
        fun newInstance() = ComparePacksV3Activity()
    }

    override fun getLayout(): Int {
        return R.layout.activity_compare_packsv3
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getIntExtra("deepLinkDay", 7)
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
        prefs = SharedPrefs(this)
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)
        howToUseAdapter = PacksV3HowToUseAdapter(this, ArrayList())
        faqAdapter = PacksFaqAdapter(this, ArrayList())
        packsv3Adapter = PacksV3Adapter(ArrayList(), this, this)
        packsv3footerAdapter = PacksV3FooterAdapter(ArrayList(), this, this)
        packsv3pricingAdapter = PacksV3PricingAdapter(ArrayList(), this)
        packsAddonsAdapter = PacksAddonsV3Adapter(ArrayList(), this,this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

      loadData()
        initMvvm()
        initializeHowToUseRecycler()
        initializeFAQRecycler()
        initializePacksAddonsRecycler()
        initializePacksV3Recycler()
        initializePacksV3FooterRecycler()
        initializePacksV3PricingRecycler()


        //Add to cart..
        binding?.buyPack?.setOnClickListener {

            val dialogCard = FeatureDetailsPopup(this)
            val args = Bundle()
            args.putString("expCode", experienceCode)
            args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
            args.putString("bundleData", Gson().toJson(selectedBundle))
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
            this.supportFragmentManager.let { dialogCard.show(it, com.boost.cart.ui.popup.FeatureDetailsPopup::class.java.name) }

            /*if (selectedBundle != null) {
                prefs.storeAddedPackageDesc(selectedBundle!!.desc ?: "")

                val itemIds = arrayListOf<String>()
                for (i in selectedBundle!!.included_features) {
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
                                    val removeFeatureBottomSheet = RemoveFeatureBottomSheet(this, this, null)
                                    val args = Bundle()
                                    args.putStringArrayList("addonNames", sameAddonsInCart)
                                    args.putStringArrayList("addonsListInCart", addonsListInCart)
                                    args.putString("packageDetails", Gson().toJson(selectedBundle!!))
                                    removeFeatureBottomSheet.arguments = args
                                    removeFeatureBottomSheet.show(supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
                                }else {
                                var bundleMonthlyMRP = 0.0
                                val minMonth: Int =
                                    if (!prefs.getYearPricing() && selectedBundle!!.min_purchase_months != null && selectedBundle!!.min_purchase_months!! > 1) selectedBundle!!.min_purchase_months!! else 1

                                for (singleItem in it) {
                                    for (item in selectedBundle!!.included_features) {
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

                                if (selectedBundle!!.overall_discount_percent > 0)
                                    offeredBundlePrice = RootUtil.round(
                                        originalBundlePrice - (originalBundlePrice * selectedBundle!!.overall_discount_percent / 100),
                                        2
                                    )
                                else
                                    offeredBundlePrice = originalBundlePrice

                                //clear cartOrderInfo from SharedPref to requestAPI again
                                prefs.storeCartOrderInfo(null)

                                    //remove other bundle and add existing bundle to cart
                                    removeOtherBundlesAndAddExistingBundle(addonsListInCart)

                                val event_attributes: java.util.HashMap<String, Any> =
                                    java.util.HashMap()
                                selectedBundle!!.name?.let { it1 ->
                                    event_attributes.put(
                                        "Package Name",
                                        it1
                                    )
                                }
                                selectedBundle!!.target_business_usecase?.let { it1 ->
                                    event_attributes.put(
                                        "Package Tag",
                                        it1
                                    )
                                }
                                event_attributes.put("Package Price", originalBundlePrice)
                                event_attributes.put("Discounted Price", offeredBundlePrice)
                                event_attributes.put(
                                    "Discount %",
                                    selectedBundle!!.overall_discount_percent
                                )
                                selectedBundle!!.min_purchase_months?.let { it1 ->
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
            }*/
        }

        binding?.packageBack?.setOnClickListener {
            finish()
        }

        binding?.packageMenuIcon?.setOnClickListener {

            val popup = PopupMenu(this, binding?.packageMenuIcon)
            popup.getMenuInflater().inflate(R.menu.compare_packs_menu, popup.getMenu())
            val menuOpts = popup.menu
            if (prefs.getYearPricing()) {
                menuOpts.getItem(0).setTitle(R.string.switch_to_monthly_pricing)
            } else {
                menuOpts.getItem(0).setTitle(R.string.switch_to_yearly_pricing)
            }
            popup.setForceShowIcon(true)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.pricing_switch -> {
                        val menuOpts1 = popup.menu
                        if (prefs.getYearPricing()) {
                            menuOpts1.getItem(1).setTitle(R.string.switch_to_monthly_pricing)
                            prefs.storeYearPricing(false)
                        } else {
                            menuOpts1.getItem(1).setTitle(R.string.switch_to_yearly_pricing)
                            prefs.storeYearPricing(true)
                        }
                        prefs.storeCartValidityMonths("1")
                        packsv3pricingAdapter.notifyDataSetChanged()
                        packsv3footerAdapter.notifyDataSetChanged()
                    }
                    R.id.help_section -> {
//                        val videoshelp = HelpVideosBottomSheet()
//                        val args = Bundle()
//                        videoshelp.arguments = args
//                        videoshelp.show(
//                            this.supportFragmentManager,
//                            HelpVideosBottomSheet::class.java.name
//                        )
                    }
                }

                true
            })
            popup.show() //showing popup menu
        }
        
    }

    fun removeOtherBundlesAndAddExistingBundle(addonsListInCart: List<String>){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                viewModel.addItemToCartPackage1(
                    CartModel(
                        selectedBundle!!._kid,
                        null,
                        null,
                        selectedBundle!!.name,
                        "",
                        selectedBundle!!.primary_image!!.url,
                        offeredBundlePrice.toDouble(),
                        originalBundlePrice.toDouble(),
                        selectedBundle!!.overall_discount_percent,
                        1,
                        if (!prefs.getYearPricing() && selectedBundle!!.min_purchase_months != null) selectedBundle!!.min_purchase_months!! else 1,
                        "bundles",
                        null,
                        ""
                    )
                )
                viewModel.getCartItems()
            }
            .doOnError {
                Toast.makeText(this, "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
                viewModel.getCartItems()
            }
            .subscribe()
    }

    override fun onResume() {
        super.onResume()
//        loadData()
        if(upgradeList!=null) {
            viewModel.getCartItems()
        }
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
            viewModel.getAllFeaturesFromDB()
            refreshViewPager = true
            viewModel.loadPackageUpdates()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {
        viewModel.getAllFeatures().observe(this, {
            Log.v("AllFeaturesResultValue", Gson().toJson(it))
            featuresList = it
        })

        viewModel.cartResult().observe(this, Observer {
            cartList = it
            itemInCart = false
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {
                // packsv3footerAdapter.updateCartItem(cartList!!)
                if (refreshViewPager) {
                    refreshViewPager = false
                    packsv3footerAdapter.updateCartItem(cartList!!)
                }

                if (cartList?.size!! > 0) {
                    if (cartList != null) {
                        for (singleCartItem in cartList!!) {
                            if (singleCartItem.item_id.equals(selectedBundle!!._kid)) {
                                itemInCart = true
                                break
                            }
                        }
                    }
                }
                if (!itemInCart) {
                    binding?.buyPack?.setTextColor(this.resources.getColor(R.color.white))
                    binding?.buyPack?.background = ContextCompat.getDrawable(
                        this.applicationContext,
                        R.drawable.ic_cart_continue_bg
                    )
                    var originalText = selectedBundle?.name
                    originalText = originalText?.lowercase(Locale.getDefault())
                    binding?.buyPack?.text = "Buy " + originalText
                  //  binding?.buyPack?.setText("Buy ${selectedBundle!!.name}".toLowerCase())
                    binding?.buyPack?.isClickable = true
                } else {
                    binding?.buyPack?.background = ContextCompat.getDrawable(
                        this.applicationContext,
                        R.drawable.ic_packsv3_added_to_cart_bg
                    )
                    binding?.buyPack?.setTextColor(
                        this.getResources().getColor(R.color.tv_color_BB)
                    )
                    binding?.buyPack?.setText(this.getString(R.string.added_to_cart))
                    binding?.buyPack?.isClickable = false
                }

                cartCount = cartList!!.size
//                badgeNumber = cartList!!.size
//                badge121.setText(badgeNumber.toString())
//                badge121.visibility = View.VISIBLE
//                Log.v("badgeNumber", " " + badgeNumber)
            } else {
                cartCount = 0
                binding?.buyPack?.setTextColor(this.resources.getColor(R.color.white))
                binding?.buyPack?.background = ContextCompat.getDrawable(
                    this.applicationContext,
                    R.drawable.ic_cart_continue_bg
                )
                var originalText = selectedBundle?.name
                originalText = originalText?.lowercase(Locale.getDefault())
                binding?.buyPack?.text = "Buy " + originalText
                //  binding?.buyPack?.setText("Buy ${selectedBundle!!.name}".toLowerCase())
                binding?.buyPack?.isClickable = true
                //badgeNumber = 0
                //   badge121.visibility = View.GONE
                packageInCartStatus = false
                if (refreshViewPager) {
                    refreshViewPager = false
                    packsv3footerAdapter.updateCartItem(arrayListOf())
                }
            }
        })

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            if (it != null){
                binding?.packsData?.visibility=View.VISIBLE
                binding?.shimmerViewPacksv3?.visibility=View.GONE
            }
            else{
                binding?.packsData?.visibility=View.GONE
                binding?.shimmerViewPacksv3?.visibility=View.VISIBLE
            }
            viewModel.getCartItems()
            if (it != null && it.size > 0) {
                val listItem = arrayListOf<Bundles>()
                for (item in it) {
                    Log.v("allBundleResultValue", " " + item.name)
                    val temp = Gson().fromJson<List<IncludedFeature>>(
                        item.included_features,
                        object : TypeToken<List<IncludedFeature>>() {}.type
                    )
                    val faq = Gson().fromJson<List<FrequentlyAskedQuestion>>(
                        item.frequently_asked_questions,
                        object : TypeToken<List<FrequentlyAskedQuestion>>() {}.type
                    )
                    val steps = Gson().fromJson<List<HowToActivate>>(
                        item.how_to_activate,
                        object : TypeToken<List<HowToActivate>>() {}.type
                    )
                    val benefits = if(item.benefits != null) Gson().fromJson<List<String>>(
                        item.benefits!!,
                        object : TypeToken<List<String>>() {}.type
                    ) else arrayListOf()
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
                            null, steps, null, faq, benefits, item.desc ?: ""
                        )
                    )
                }
                if (listItem.size > 0) {
                    updatePackageRecycler(listItem)
                    updatePackageAddons(listItem)
                    updatePackageFooterRecycler(listItem)
                    updatePackagePricingRecycler(listItem)
                    // updateHowToUseRecycler(listItem.steps)
                    this.selectedBundle = listItem.get(0)

                    if ( listItem.get(1).frequently_asked_questions != null) {
                       binding?.faqContainer?.visibility = View.VISIBLE
                        updateFAQRecycler(listItem.get(1).frequently_asked_questions!!)
//                        faqAdapter.addupdates(faq)
//                        faqAdapter.notifyDataSetChanged()
                    }
            //        listItem.get(1).frequently_asked_questions?.let { it1 -> updateFAQRecycler(it1) }
            //        listItem.get(1).how_to_activate?.let { it1 -> updateHowToUseRecycler(it1) }

                    if ( listItem.get(1).how_to_activate != null) {
                        binding?.howToUseContainer?.visibility = View.VISIBLE
                        updateHowToUseRecycler(listItem.get(1).how_to_activate!!)
//                        faqAdapter.addupdates(faq)
//                        faqAdapter.notifyDataSetChanged()
                    }

                    binding?.howToUseTitleLayout?.setOnClickListener {
                        if (binding?.packsHowToUseRecycler?.visibility == View.VISIBLE) {
                            how_to_use_arrow.setImageResource(R.drawable.ic_arrow_down_gray)
                            binding?.packsHowToUseRecycler?.visibility = View.GONE
                        } else {
                            how_to_use_arrow.setImageResource(R.drawable.packs_arrow_up)
                            binding?.packsHowToUseRecycler?.visibility = View.VISIBLE
                        }
                    }
                    upgradeList = listItem

                }
            }
        })

        viewModel.addedToCartResult().observe(this, Observer {
            if(it){
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
        })
    }

    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.packsHowToUseRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.packsHowToUseRecycler?.adapter = howToUseAdapter
        }
    }

    fun updateHowToUseRecycler(list: List<HowToActivate>) {
        Log.v("updatePackageViewPager", " " + list.size)
        howToUseAdapter.addupdates(list)
    }

    private fun initializeFAQRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.packsFaqRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.packsFaqRecycler?.adapter = faqAdapter
        }
    }

    fun updateFAQRecycler(list: List<FrequentlyAskedQuestion>) {
        Log.v("updatePackageViewPager", " " + list.size)
        faqAdapter.addupdates(list)
    }


    private fun initializePacksV3Recycler() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding?.packsv3recycler?.apply {
            layoutManager = linearLayoutManager
            binding?.packsv3recycler?.adapter = packsv3Adapter
        }
    }

    fun updatePackageRecycler(list: List<Bundles>) {
        Log.v("updatePackageViewPager", " " + list.size)
        packsv3Adapter.addupdates(list)
    }

    private fun initializePacksV3FooterRecycler() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding?.packsv3footerrecycler?.apply {
            layoutManager = linearLayoutManager
            binding?.packsv3footerrecycler?.adapter = packsv3footerAdapter
        }
    }

    fun updatePackageFooterRecycler(list: List<Bundles>) {
        Log.v("updatePackageViewPager", " " + list.size)
        packsv3footerAdapter.addupdates(list)
        binding?.buyPack?.setTextColor(this.resources.getColor(R.color.white))
        binding?.buyPack?.background = ContextCompat.getDrawable(
            this.applicationContext,
            R.drawable.ic_cart_continue_bg
        )
        var originalText = list.get(0).name
        originalText = originalText?.lowercase(Locale.getDefault())
        binding?.buyPack?.text = "Buy " + originalText
       // binding?.buyPack?.setText("Buy ${list.get(0).name}".toLowerCase())
        binding?.buyPack?.isClickable = true
    }

    private fun initializePacksV3PricingRecycler() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding?.packsPriceRecycler?.apply {
            layoutManager = linearLayoutManager
            binding?.packsPriceRecycler?.adapter = packsv3pricingAdapter
        }
    }

    fun updatePackagePricingRecycler(list: List<Bundles>) {
        Log.v("updatePackageViewPager", " " + list.size)
        packsv3pricingAdapter.addupdates(list)
    }


    private fun initializePacksAddonsRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.addonsRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.addonsRecycler?.adapter = packsAddonsAdapter
        }
    }

    fun updatePackageAddons(bundleList: ArrayList<Bundles>) {
        val addonsListTemp = ArrayList<PackageAddonsCompares>()
        val totalList = ArrayList<String>()
        //get total listing for all the packages
        for(singleBundle in bundleList){
            for (singleFeatureCode in singleBundle.included_features) {
                if(!totalList.contains(singleFeatureCode.feature_code)){
                    totalList.add(singleFeatureCode.feature_code)
                }
            }
        }
        //create skeleton for all addons
        for(singleAllList in totalList){
            for (singleFeature in featuresList!!) {
                if(singleAllList.equals(singleFeature.feature_code)){
                    val temp = ArrayList<AddonsPacksIn>()
                    for(singleBundle in bundleList){
                        temp.add(AddonsPacksIn(
                            singleBundle.name ?: "", false
                        ))
                    }
                    addonsListTemp.add(
                        PackageAddonsCompares(
                            title = singleFeature.name ?: "",
                            featureCode = singleAllList,
                            packsAvailableIn = temp
                        )
                    )
                }
            }
        }
        //setting status true only for particular package
        bundleList.forEachIndexed { index, element ->
            for (singleFeatureCode in element.included_features) {
                for ((index1, singleCompareFeature) in addonsListTemp.withIndex()) {
                    if (singleFeatureCode.feature_code.equals(singleCompareFeature.featureCode)){
                        val packAvaiList = addonsListTemp.get(index1).packsAvailableIn
                            packAvaiList.set(index,
                                AddonsPacksIn(
                                    element.name ?: "", true
                                )
                            )
                            addonsListTemp.get(index1).packsAvailableIn = packAvaiList
                    }
                }
            }
        }
        val addonsList = ArrayList<PackageAddonsCompares>()
        //arrange the list in descending order
        var totalSize = bundleList.size
        bundleList.forEachIndexed { index, element ->
            for (singleItem in addonsListTemp) {
                var tempSize = 0
                for (singleStatus in singleItem.packsAvailableIn) {
                    if (singleStatus.packageStatus) {
                        tempSize += 1
                    }
                }
                if (totalSize == tempSize) {
                    addonsList.add(singleItem)
                }
            }
            totalSize -= 1
        }
        packsAddonsAdapter.addupdates(addonsList)
    }

    override fun onPackageClickedV3(item: Bundles?, image: ImageView?) {
        val dialogCard = ComparePacksV3BottomSheet(this)
        val args = Bundle()
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
        args.putString("expCode",experienceCode)
        args.putString("bundleData", Gson().toJson(item))
        args.putDouble("price", offeredBundlePrice)
        args.putDouble("price1", originalBundlePrice)
        if (item != null) {
            args.putInt("addons", item.included_features.size)
        }
        dialogCard.arguments = args
        dialogCard.show(this.supportFragmentManager, ComparePacksV3BottomSheet::class.java.name)
    }


    override fun onSelectedPack(selectedBundle: Bundles, itemList1: List<CartModel>?) {

        itemInCart = false
        this.selectedBundle = selectedBundle
       // binding?.buyPack?.text = "Buy" + selectedBundle.name?.toLowerCase()
        var originalText = selectedBundle.name
        originalText = originalText?.lowercase(Locale.getDefault())
        binding?.buyPack?.text = "Buy " + originalText
//        selectedBundle.how_to_activate?.let { updateHowToUseRecycler(it) }
//        howToUseAdapter.notifyDataSetChanged()

        if ( selectedBundle.how_to_activate != null) {
            binding?.howToUseContainer?.visibility = View.VISIBLE
            updateHowToUseRecycler(selectedBundle.how_to_activate!!)
            howToUseAdapter.addupdates(selectedBundle.how_to_activate!!)
            howToUseAdapter.notifyDataSetChanged()
        }

        if ( selectedBundle.frequently_asked_questions != null) {
            binding?.faqContainer?.visibility = View.VISIBLE
            updateFAQRecycler(selectedBundle.frequently_asked_questions!!)
                        faqAdapter.addupdates(selectedBundle.frequently_asked_questions!!)
                        faqAdapter.notifyDataSetChanged()
        }
     //   selectedBundle?.frequently_asked_questions?.let { updateFAQRecycler(it) }
    //    faqAdapter.notifyDataSetChanged()

        if (cartList?.size!! > 0) {
            if (cartList != null) {
                for (singleCartItem in cartList!!) {
                    if (singleCartItem.item_id.equals(selectedBundle!!._kid)) {
                        itemInCart = true
                        break
                    }
                }
            }
        }

        if (!itemInCart) {
            binding?.buyPack?.setTextColor(this.resources.getColor(R.color.white))
            binding?.buyPack?.background = ContextCompat.getDrawable(
                this.applicationContext,
                R.drawable.ic_cart_continue_bg
            )
            var originalText = selectedBundle.name
            originalText = originalText?.lowercase(Locale.getDefault())
            binding?.buyPack?.text = "Buy " + originalText
          //  binding?.buyPack?.setText("Buy ${selectedBundle!!.name}".toLowerCase())
            binding?.buyPack?.isClickable = true
        } else {
            binding?.buyPack?.background = ContextCompat.getDrawable(
                this.applicationContext,
                R.drawable.ic_packsv3_added_to_cart_bg
            )
            binding?.buyPack?.setTextColor(
                this.getResources().getColor(R.color.tv_color_BB)
            )
            binding?.buyPack?.setText(this.getString(R.string.added_to_cart))
            binding?.buyPack?.isClickable = false
        }
    }

    override fun onPaidAddonsClicked(item: String) {
        val packagePopup = ComparePacksV3AddonsPopUpFragment( )
        val args = Bundle()

        args.putString("fpid", fpid)
        args.putString("expCode", experienceCode)
        args.putString("isDeepLink", isDeepLink.toString())
        args.putInt("deepLinkDay", deepLinkDay)
        args.putString("isOpenCardFragment", isOpenCardFragment.toString())
        args.putString("accountType", accountType)
        args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
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
//        args.putString("addons", Gson().toJson(featuresList))
        args.putString("featureCode", item)
        args.putDouble("price", offeredBundlePrice)
        args.putDouble("price1", originalBundlePrice)
        packagePopup.arguments = args
        packagePopup.show(supportFragmentManager, "PACKAGE_POPUP")
    }

    override fun onAddonsClicked(item: FeaturesModel) {
    }

    override fun onRefreshCart() {
        viewModel.getCartItems()
    }

    override fun onPackageClicked(item: Bundles?, image: ImageView?) {
        viewModel.addItemToCartPackage1(
            CartModel(
                selectedBundle!!._kid,
                null,
                null,
                selectedBundle!!.name,
                "",
                selectedBundle!!.primary_image!!.url,
                offeredBundlePrice.toDouble(),
                originalBundlePrice.toDouble(),
                selectedBundle!!.overall_discount_percent,
                1,
                if (!prefs.getYearPricing() && selectedBundle!!.min_purchase_months != null) selectedBundle!!.min_purchase_months!! else 1,
                "bundles",
                null,
                ""
            )
        )
        viewModel.getCartItems()
    }

    override fun featureDetailsPopup(domain: String) {
        prefs.storeSelectedDomainName(domain)
    }
}
