package com.boost.upgrades.ui.compare

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.SimplePageTransformer
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.CompareListener
import com.boost.upgrades.interfaces.HomeListener
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.HorizontalMarginItemDecoration
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.compare_all_packages.*
import kotlinx.android.synthetic.main.compare_package_fragment.*
import kotlinx.android.synthetic.main.compare_package_fragment.badge121
import kotlinx.android.synthetic.main.compare_package_fragment.package_back
import kotlinx.android.synthetic.main.compare_package_fragment.package_cart_icon
import kotlinx.android.synthetic.main.compare_package_fragment.package_indicator
import kotlinx.android.synthetic.main.compare_package_fragment.package_indicator2
import kotlinx.android.synthetic.main.compare_package_fragment.package_viewpager
import kotlinx.android.synthetic.main.home_fragment.*
import org.json.JSONArray
import org.json.JSONObject


class ComparePackageFragment : BaseFragment(), CompareListener {

    lateinit var root: View

//    lateinit var packageAdaptor: CompareAdapter
//    lateinit var packageAdaptor: ParentItemAdapter
    lateinit var packageAdaptor: ParentCompareItemAdapter

    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var badgeNumber = 0
    var offeredBundlePrice = 0
    var originalBundlePrice = 0
    var featureCount = 0

    var packageInCartStatus = false
    lateinit var prefs: SharedPrefs
    var featuresHashMap:MutableMap<String?, FeaturesModel> = HashMap<String?, FeaturesModel>()
    lateinit var upgradeList: ArrayList<Bundles>
    lateinit var progressDialog: ProgressDialog

    private lateinit var viewModel: ComparePackageViewModel
    companion object {
        fun newInstance() = ComparePackageFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.compare_all_packages, container, false)

//        val jsonString = arguments!!.getString("bundleData")
//        bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)
//        val feature_item = root.findViewById<RecyclerView>(R.id.feature_item)
//        val layoutManager = LinearLayoutManager(
//                context)
//        feature_item.setLayoutManager(layoutManager)
//        packageAdaptor = CompareAdapter((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type))
//        packageAdaptor = ParentItemAdapter(ArrayList(), (activity as UpgradeActivity))
        val layoutManager = LinearLayoutManager(
                context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        packageAdaptor = ParentCompareItemAdapter(ArrayList(),(activity as UpgradeActivity),this)
        prefs = SharedPrefs(activity as UpgradeActivity)
        progressDialog = ProgressDialog(requireContext())
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ComparePackageViewModel::class.java)
//        initializeBannerViewPager()
        loadData()
        initMvvm()



        if(arguments!!.containsKey("showCartIcon")){
            package_cart_icon.visibility = View.INVISIBLE
        }



        package_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        package_cart_icon.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    CartFragment.newInstance(),
                    Constants.CART_FRAGMENT
            )
        }
        initializePackageViewPager()

    }

    private fun loadData() {
        val pref = activity!!.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        var code: String = (activity as UpgradeActivity).experienceCode!!
        if (!code.equals("null", true)) {
            viewModel.setCurrentExperienceCode(code, fpTag!!)
        }
    /*    if(bundleData!!.included_features != null) {
            val itemIds = arrayListOf<String>()
            for (item in bundleData!!.included_features) {
                itemIds.add(item.feature_code)
            }
            viewModel.loadUpdates(itemIds)
        } else {
            //TODO: Load the widget_keys associated with Bundle from db
            viewModel.getAssociatedWidgetKeys(bundleData!!._kid)
        }*/

        viewModel.loadPackageUpdates()
        viewModel.getCartItems()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {

        viewModel.getUpgradeResult().observe(this, Observer {
            if (it.size > 0) {
                featuresList = it
                var bundleMonthlyMRP = 0

                for (singleItem in it) {

                    Log.v("getUpgradeResult", " "+ singleItem.feature_id + " "+ singleItem.boost_widget_key + " "+singleItem.name)
                    /*for (item in bundleData!!.included_features) {
                        if (singleItem.feature_code == item.feature_code) {
                            bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
                        }
                    }*/
                }


            }
        })


//        initializeFreeAddonsRecyclerView()
        viewModel.cartResult().observe(this, Observer {
            cartList = it
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {
//                if (bundleData != null) {
                  /*  for (item in it) {
                        if (item.item_id.equals(bundleData!!._kid)) {
                            packageInCartStatus = true

                            break
                        }
                    }*/
                    badgeNumber = cartList!!.size
                    badge121.setText(badgeNumber.toString())
                    badge121.visibility = View.VISIBLE

                    if(!packageInCartStatus){


                    }
//                }
            } else {
                badgeNumber = 0
                badge121.visibility = View.GONE
                packageInCartStatus = false



            }
//            viewModel.getCartItems()
            if (viewModel.allBundleResult.value != null) {


                var list = viewModel.allBundleResult.value!!
                if (list.size > 0) {
                    val listItem = arrayListOf<Bundles>()
                    for (item in list) {
                        val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                        listItem.add(Bundles(
                                item.bundle_id,
                                temp,
                                item.min_purchase_months,
                                item.name,
                                item.overall_discount_percent,
                                PrimaryImage(item.primary_image),
                                item.target_business_usecase,
                                Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                                null,item.desc
                        ))
                    }
                    if (list.size > 0) {
//                        updatePackageViewPager(listItem)
                        packageAdaptor.addupdates(listItem)
                        packageAdaptor.notifyDataSetChanged()
                    }
                }
            }
        })

        viewModel.getSpecificFeature().observe(this, Observer {
            if(it != null){
                for(items in it){
//                    Log.v("featuresHashMap", " "+ items)
                    featuresHashMap.put(items.target_business_usecase, items)
                }

                featureCount++
                updatePackageViewPager(upgradeList)
//                updateBannerViewPager(upgradeList, it as ArrayList<FeaturesModel>)
            }
            val obj = JSONObject(featuresHashMap as Map<*, *>)
//            val array = JSONArray(obj.toString())
            Log.v("featuresHashMap", " "+ obj)
//            Log.v("featuresHashMap", " "+ featureCount)
            if(featuresHashMap.size == 0){

            }
        })

        viewModel.getBundleWidgetKeys().observe(this, Observer {
            if(it != null){
                val itemIds = arrayListOf<String>()
                for (item in it) {
                    itemIds.add(item)
                    Log.v("getBundleWidgetKeys", " " + item)
                }

//                viewModel.loadUpdates(itemIds)
            }
        })

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            val list = arrayListOf<Bundles>()
            for (item in it) {
                val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                list.add(Bundles(
                        item.bundle_id,
                        temp,
                        item.min_purchase_months,
                        item.name,
                        item.overall_discount_percent,
                        PrimaryImage(item.primary_image),
                        item.target_business_usecase,
                        Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
                        null,
                        item.desc))
            }
            if (list.size > 0) {

                for(items in list){
                    Log.v("getkeyWidget"," "+ items.name +" "+items.included_features.size)
                    val itemIds = arrayListOf<String>()
                    for (item in  items!!.included_features) {

                        itemIds.add(item.feature_code)
//                        viewModel.getFeatureValues(item.feature_code)
                    }
                    Log.v("getkeyWidget123"," "+ itemIds.size)
                    viewModel.getFeatureValues(itemIds)
//                    viewModel.loadUpdates(itemIds)
//                    viewModel.getAssociatedWidgetKeys(items._kid)
//                    viewModel.getAssociatedWidgetKeys("5f6a2d66663bb00001e2b1d7")
                }
//                package_layout.visibility = View.VISIBLE
                upgradeList = list
//                updatePackageViewPager(list)
            } else {
//                package_layout.visibility = View.GONE
            }
        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
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

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
            viewModel.getCartItems()
        }
    }



    fun updatePackageViewPager(list: List<Bundles>) {

        initializeFreeAddonsRecyclerView()
//        initializePackageViewPager()
//        package_viewpager.offscreenPageLimit = list.size
        Log.v("updatePackageViewPager"," "+ list.size)
        packageAdaptor.addupdates(list)
        packageAdaptor.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
//        upgradeText.visibility = View.VISIBLE
        if (list.size > 1) {
            package_indicator2.visibility = View.VISIBLE
        } else {
            package_indicator2.visibility = View.INVISIBLE
        }
    }

    fun updateBannerViewPager(list: List<Bundles>) {
//        package_viewpager.offscreenPageLimit = list.size
        packageAdaptor.addupdates(list)
        packageAdaptor.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
        if (list.size > 1) {
            if (list.size > 2) {
                package_viewpager.setPageTransformer(SimplePageTransformer())

                val itemDecoration = HorizontalMarginItemDecoration(
                        requireContext(),
                        R.dimen.viewpager_current_item_horizontal_margin
                )
                package_viewpager.addItemDecoration(itemDecoration)
            }
//            banner_indicator.visibility = View.VISIBLE
        } else {
//            banner_indicator.visibility = View.INVISIBLE
        }
    }


    fun initializeFreeAddonsRecyclerView() {

        val layoutManager = LinearLayoutManager(
                context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
//        layoutManager.isMeasurementCacheEnabled = false
        val parentItemAdapter = ParentCompareItemAdapter(upgradeList,(activity as UpgradeActivity),this)
//        package_viewpager.adapter = parentItemAdapter
        package_viewpager.adapter = packageAdaptor
        package_viewpager.offscreenPageLimit = 2
//        package_viewpager.offscreenPageLimit = upgradeList.size
        package_indicator2.setViewPager2(package_viewpager)

        if (upgradeList.size > 2) {
//            package_viewpager.setPageTransformer(SimplePageTransformer())

//                val itemDecoration = HorizontalMarginItemDecoration(
//                        requireContext(),
//                        R.dimen.viewpager_current_item_horizontal_margin2
//                )
//            package_viewpager.addItemDecoration(itemDecoration)
        }

        // removed for viewpager//
        /*feature_item
                .setAdapter(parentItemAdapter)
        feature_item
                .setLayoutManager(layoutManager)*/
//        initializePackageViewPager()

    }


    private fun initializePackageViewPager() {
        package_viewpager.adapter = packageAdaptor
//        package_viewpager.offscreenPageLimit = 4
        package_indicator2.setViewPager2(package_viewpager)

    }

    override fun onPackageClicked(item: Bundles?) {
        if (!packageInCartStatus) {
            if (item != null) {

                val itemIds = arrayListOf<String>()
                for(i in item.included_features){
                    itemIds.add(i.feature_code)
                }

                CompositeDisposable().add(
                        AppDatabase.getInstance(requireActivity().application)!!
                                .featuresDao()
                                .getallFeaturesInList(itemIds)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            featuresList = it
                                            var bundleMonthlyMRP = 0
                                            val minMonth:Int = if (item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1

                                            for (singleItem in it) {
                                                for (item in item!!.included_features) {
                                                    if (singleItem.feature_code == item.feature_code) {
                                                        bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
                                                    }
                                                }
                                            }

                                            offeredBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
                                            originalBundlePrice = (bundleMonthlyMRP * minMonth).toInt()

                                            if(item!!.overall_discount_percent > 0)
                                                offeredBundlePrice = originalBundlePrice - (originalBundlePrice * item!!.overall_discount_percent/100)
                                            else
                                                offeredBundlePrice = originalBundlePrice

                                            //clear cartOrderInfo from SharedPref to requestAPI again
                                            prefs.storeCartOrderInfo(null)
                                            viewModel.addItemToCartPackage(CartModel(
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
                                                    null
                                            ))
                                            val event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
                                            item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
                                            item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
                                            event_attributes.put("Package Price", originalBundlePrice)
                                            event_attributes.put("Discounted Price", offeredBundlePrice)
                                            event_attributes.put("Discount %", item!!.overall_discount_percent)
                                            item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
                                            WebEngageController.trackEvent("ADDONS_MARKETPLACE Package added to cart", "ADDONS_MARKETPLACE", event_attributes)
                                            badgeNumber = badgeNumber + 1

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

    override fun onLearnMoreClicked(item: Bundles?) {
        val packageFragment = PackageFragment.newInstance()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        packageFragment.arguments = args
        (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
    }
}
