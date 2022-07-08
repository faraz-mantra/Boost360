package com.boost.marketplace.ui.comparePacksV3

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.Adapters.PacksFaqAdapter
import com.boost.marketplace.Adapters.PacksV3Adapter
import com.boost.marketplace.Adapters.PacksV3HowToUseAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksv3Binding
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ComparePacksV3Activity :
    AppBaseActivity<ActivityComparePacksv3Binding, ComparePacksViewModel>() {

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
    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    var featureCount = 0
    var cartCount = 0
    lateinit var prefs: SharedPrefs
    var featuresHashMap: MutableMap<String?, FeaturesModel> = HashMap<String?, FeaturesModel>()
    lateinit var progressDialog: ProgressDialog

    //  var listItem=ArrayList<Bundles>()
    var upgradeList: ArrayList<Bundles>? = null
    lateinit var howToUseAdapter: PacksV3HowToUseAdapter
    lateinit var faqAdapter: PacksFaqAdapter
    lateinit var packsv3Adapter : PacksV3Adapter
//    lateinit var packsAddonsAdapter: PacksAddonsV3Adapter

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
        prefs = SharedPrefs(this)
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)

        howToUseAdapter = PacksV3HowToUseAdapter(this, ArrayList())
        faqAdapter = PacksFaqAdapter(this, ArrayList())
        packsv3Adapter = PacksV3Adapter(ArrayList(), this)
        //  packsAddonsAdapter= PacksAddonsV3Adapter(featuresList,this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        loadData()
        initMvvm()
        initializeHowToUseRecycler()
        initializeFAQRecycler()
        // initializePacksAddonsRecycler()
        initializePacksV3Recycler()

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

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            if (it != null && it.size > 0) {
                val listItem = arrayListOf<Bundles>()
                for (item in it) {
                    Log.v("allBundleResultValue", " " + item.name)
                    val temp = Gson().fromJson<List<IncludedFeature>>(
                        item.included_features,
                        object : TypeToken<List<IncludedFeature>>() {}.type
                    )
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
                            null, item.desc ?: ""
                        )
                    )
                }
                if (listItem.size > 0) {
                    updatePackageViewPager(listItem)
                  //  upgradeList = listItemut
                  //  loadPacksData()

//                    for (items in listItem) {
//                        Log.v("getkeyWidget", " " + items.name + " " + items.included_features.size)
//                        val itemIds = arrayListOf<String?>()
//                        for (item in items.included_features!!) {
//                            itemIds.add(item.feature_code)
//                        }
//                        Log.v("getkeyWidget123", " " + itemIds.size)
//                        viewModel.getFeatureValues(itemIds)
//                    }
                }
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

    private fun initializeFAQRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.packsFaqRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.packsFaqRecycler?.adapter = faqAdapter
        }
    }

    private fun initializePacksV3Recycler() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding?.packsv3recycler?.apply {
            layoutManager = linearLayoutManager
            binding?.packsv3recycler?.adapter = packsv3Adapter
        }
    }

    fun updatePackageViewPager(list: List<Bundles>) {
        Log.v("updatePackageViewPager", " " + list.size)
        packsv3Adapter.addupdates(list)
    }


//    private fun initializePacksAddonsRecycler() {
//        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
//        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        binding?.addonsRecycler?.apply {
//            layoutManager = gridLayoutManager
//            binding?.addonsRecycler?.adapter = packsAddonsAdapter
//        }
//    }

   // private fun loadPacksData() {
//        Glide.with(this).load(upgradeList?.get(0)?.primary_image!!.url)
//            .into(binding?.packsImage1!!)
//        Glide.with(this).load(upgradeList!![1].primary_image!!.url)
//            .into(binding?.packsImage2!!)
//        Glide.with(this).load(upgradeList!![2].primary_image!!.url)
//            .into(binding?.packsImage3!!)
//
//        binding?.packTitle1?.text = upgradeList?.get(0)?.name
//        binding?.packTitle2?.text = upgradeList?.get(1)?.name
//        binding?.packTitle3?.text = upgradeList?.get(2)?.name
//
//        binding?.footerPack?.text = upgradeList?.get(0)?.name
//        binding?.footerPack2?.text = upgradeList?.get(1)?.name
//        binding?.footerPack3?.text = upgradeList?.get(2)?.name
//
//    }
}