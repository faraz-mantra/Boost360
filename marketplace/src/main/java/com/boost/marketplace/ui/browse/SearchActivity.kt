package com.boost.marketplace.ui.browse

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.CompareItemAdapter
import com.boost.marketplace.R
import com.boost.marketplace.adapter.PackageRecyclerAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivitySearchBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.boost.marketplace.ui.popup.PackagePopUpFragement
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART
import com.framework.webengageconstant.FEATURE_PACKS_CLICKED
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppBaseActivity<ActivitySearchBinding, SearchViewModel>(), HomeListener,
    CompareListener, AddonsListener {

    lateinit var featureAdaptor: CompareItemAdapter
    lateinit var packageAdaptor: PackageRecyclerAdapter
    lateinit var progressDialog: ProgressDialog

    var allFeatures: ArrayList<FeaturesModel> = arrayListOf()
    var allBundles: ArrayList<Bundles> = arrayListOf()
    var userPurchsedWidgets = ArrayList<String>()

    var singleWidgetKey: String? = null
    var badgeNumber = 0
    var addonDetails: FeaturesModel? = null
    var cart_list: List<CartModel>? = null
    var itemInCartStatus = false
    var widgetLearnMoreLink: String? = null
    lateinit var prefs: SharedPrefs

    var experienceCode: String? = null
    var fpid: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0

    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7

    override fun getLayout(): Int {
        return R.layout.activity_search
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()

        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getIntExtra("deepLinkDay", 7)
        experienceCode = intent.getStringExtra("expCode")
        fpid = intent.getStringExtra("fpid")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: java.util.ArrayList()
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()

        featureAdaptor = CompareItemAdapter(allFeatures,this,this)
        progressDialog = ProgressDialog(this)
        prefs = SharedPrefs(this)
        packageAdaptor = PackageRecyclerAdapter(allBundles, this, this)
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        initView()
        initMvvm()

    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        viewModel.setApplicationLifecycle(application, this)
        viewModel.loadAllPackagesFromDB()
        viewModel.loadAddonsFromDB()

        back_arrow.setOnClickListener {
            finish()
        }

        clear_text.setOnClickListener {
            search_value.setText("")
        }

        val tempString = SpannableString("Pack(s) having add-on")
        tempString.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
        package_title.setText(tempString)

        search_value.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!=null && p0.length>3){
                    val tempString = SpannableString("Pack(s) having add-on named `"+p0.toString()+"`")
                    tempString.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                    package_title.setText(tempString)
                    updateAllItemBySearchValue(p0.toString())
                } else{
                    val tempString = SpannableString("Pack(s) having add-on")
                    tempString.setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                    package_title.setText(tempString)
                    updateRecyclerViewItems(allFeatures,allBundles)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun initMvvm() {
        viewModel.addonsResult().observe(this, Observer {
            if(it!=null) {
                allFeatures = it as ArrayList<FeaturesModel>
                updateRecyclerViewItems(allFeatures, null)
            }
        })
        viewModel.bundleResult().observe(this, Observer {

            for(item in it){
                val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                allBundles.add(Bundles(
                    item.bundle_id,
                    temp,
                    item.min_purchase_months,
                    item.name,
                    item.overall_discount_percent,
                    PrimaryImage(item.primary_image),
                    item.target_business_usecase,
                    Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                    null,null,null,null,null,item.desc
                ))
                Log.e("loop",Gson().toJson(allBundles))
            }
            updateRecyclerViewItems(null, allBundles)
        })
        viewModel.addonsError().observe(this, Observer {

        })
        viewModel.addonsLoader().observe(this, Observer {
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

    fun updateAllItemBySearchValue(searchValue: String){
        var searchFeatures: ArrayList<FeaturesModel> = arrayListOf()
        var searchBundles: ArrayList<Bundles> = arrayListOf()

        for(singleFeature in allFeatures){
            if(singleFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
                searchFeatures.add(singleFeature)
            }
        }
        for(singleBundle in allBundles){
            var addedState = false
            for(singleFeatureBundle in singleBundle.included_features){
                for(singleFeature in allFeatures){
                    if((singleFeatureBundle.feature_code.equals(singleFeature.boost_widget_key) && singleFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ) ||(singleBundle.name?.lowercase()?.indexOf(searchValue.lowercase())!=-1)){
                        searchBundles.add(singleBundle)
                        addedState = true
                        break
                    }
                }
                //skip bundle loop if already added
                if(addedState){
                    break
                }
            }
        }
        updateRecyclerViewItems(searchFeatures, searchBundles)
    }

    fun updateRecyclerViewItems(featuresList: ArrayList<FeaturesModel>?, bundleList: ArrayList<Bundles>?){
        if(featuresList!=null) {
            if (featuresList.size > 0) {
                featureAdaptor.addupdates(featuresList, 1)
                feature_recyclerview.adapter = featureAdaptor
                featureAdaptor.notifyDataSetChanged()
                feature_recyclerview.visibility = View.VISIBLE
                addons_title.visibility = View.VISIBLE
            } else {
                feature_recyclerview.visibility = View.GONE
                addons_title.visibility = View.GONE
            }
        }
        if(bundleList != null) {
            if (bundleList.size > 0) {
                packageAdaptor.addupdates(allBundles)
                package_recyclerview.adapter = packageAdaptor
                packageAdaptor.notifyDataSetChanged()
                package_recyclerview.visibility = View.VISIBLE
                package_title.visibility = View.VISIBLE
            } else {
                package_recyclerview.visibility = View.GONE
                package_title.visibility = View.GONE
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
        packagePopup.show(supportFragmentManager,"PACKAGE_POPUP")

//        val intent = Intent(applicationContext, ComparePacksActivity::class.java)
//        intent.putExtra("bundleData", Gson().toJson(item))
//        intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
//        startActivity(intent)



    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
        //not in use
    }

    override fun onShowHidePromoBannerIndicator(status: Boolean) {
        //not in use
    }

    override fun onPartnerZoneClicked(item: PartnerZone?) {
        //not in use
    }

    override fun onShowHidePartnerZoneIndicator(status: Boolean) {
        //not in use
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
        //not in use
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
        //not in use
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        //not in use
    }

    override fun onPackageAddToCart(item: Bundles?, image: ImageView) {
        //not in use
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

//                                                            startActivity(intent)


//                intent.putExtra("itemId", it.feature_code)
//                startActivity(intent)
        // intent.putExtra("itemId", item!!.cta_feature_key)
        startActivity(intent)
    }

    override fun onRefreshCart() {
        viewModel.loadAllPackagesFromDB()
    }

    override fun onPackageClicked(item: Bundles?, imageView: ImageView?) {
        if (item != null) {
            prefs.storeAddedPackageDesc(item.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in item.included_features) {
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
//                                            viewModel.getCartItems()
                        },
                        {
                            it.printStackTrace()

                        }
                    )
            )


        }
    }
}