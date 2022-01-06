package com.boost.marketplace.ui.Compare_Plans

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksBinding
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.Adapters.ParentCompareItemAdapter
import com.boost.marketplace.infra.api.models.test.ViewpagerData
import com.boost.marketplace.infra.api.models.test.getDatas
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_LOADED
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_compare_packs.*

import org.json.JSONObject

class ComparePacksActivity: AppBaseActivity<ActivityComparePacksBinding, ComparePacksViewModel>(),
    RecyclerItemClickListener {

    lateinit var packageAdaptor: ParentCompareItemAdapter

    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var badgeNumber = 0
    var offeredBundlePrice = 0
    var originalBundlePrice = 0
    var featureCount = 0
    var cartCount = 0

    var packageInCartStatus = false
    lateinit var prefs: SharedPrefs
    var featuresHashMap:MutableMap<String?, FeaturesModel> = HashMap<String?, FeaturesModel>()


    lateinit var upgradeList: ArrayList<Bundles>


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

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        packageAdaptor = ParentCompareItemAdapter(ArrayList())
//        WebEngageController.trackEvent(ADDONS_MARKETPLACE_COMPARE_PACKAGE_LOADED, PAGE_VIEW, NO_EVENT_VALUE)
        prefs = SharedPrefs(this)

        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)
//        initializeBannerViewPager()
        loadData()
        initMvvm()
//        (activity as UpgradeActivity)setBackListener(this)
//        package_viewpager.setPageTransformer(SimplePageTransformer())

//        val itemDecoration = HorizontalMarginItemDecoration(
//            requireContext(),
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        package_viewpager.addItemDecoration(itemDecoration)
//        shimmer_view_compare.startShimmer()
//        if(requireArguments().containsKey("showCartIcon")){
//            package_cart_icon.visibility = View.INVISIBLE
//        }


//        ss.setSpan(clickableSpan, 143, 157, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        upgradeTextBottom.setText(ss)
//        upgradeTextBottom.setMovementMethod(LinkMovementMethod.getInstance())
//        upgradeTextBottom.setHighlightColor(resources.getColor(R.color.common_text_color))

//        package_back.setOnClickListener {
//            (activity as UpgradeActivity).popFragmentFromBackStack()
//        }

//        package_cart_icon.setOnClickListener {
//            val intent = Intent(
//                requireContext(),
//                CartActivity::class.java
//            )
//            intent.putExtra("fpid", (activity as UpgradeActivity).fpid)
//            intent.putExtra("expCode", (activity as UpgradeActivity).experienceCode)
//            intent.putExtra("isDeepLink", (activity as UpgradeActivity).isDeepLink)
//            intent.putExtra("deepLinkViewType", (activity as UpgradeActivity).deepLinkViewType)
//            intent.putExtra("deepLinkDay", (activity as UpgradeActivity).deepLinkDay)
//            intent.putExtra("isOpenCardFragment", (activity as UpgradeActivity).isOpenCardFragment)
//            intent.putExtra(
//                "accountType",
//                (activity as UpgradeActivity).accountType
//            )
//            intent.putStringArrayListExtra(
//                "userPurchsedWidgets",
//                (activity as UpgradeActivity).userPurchsedWidgets
//            )
//            if ((activity as UpgradeActivity).email != null) {
//                intent.putExtra("email", (activity as UpgradeActivity).email)
//            } else {
//                intent.putExtra("email", "ria@nowfloats.com")
//            }
//            if ((activity as UpgradeActivity).mobileNo != null) {
//                intent.putExtra("mobileNo", (activity as UpgradeActivity).mobileNo)
//            } else {
//                intent.putExtra("mobileNo", "9160004303")
//            }
//            intent.putExtra("profileUrl", (activity as UpgradeActivity).profileUrl)
//            startActivity(intent)
//        }
       initializePackageViewPager()

    }

    private fun loadData() {
        val pref = this.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
//        var code: String = (this).experienceCode!!
//        if (!code.equals("null", true)) {
//            viewModel.setCurrentExperienceCode(code, fpTag!!)
//        }
//        /*    if(bundleData!!.included_features != null) {
//                val itemIds = arrayListOf<String>()
//                for (item in bundleData!!.included_features) {
//                    itemIds.add(item.feature_code)
//                }
//                viewModel.loadUpdates(itemIds)
//            } else {
//                //TODO: Load the widget_keys associated with Bundle from db
//                viewModel.getAssociatedWidgetKeys(bundleData!!._kid)
//            }*/
//
        viewModel.loadPackageUpdates()
        viewModel.getCartItems()
    }


    private fun initMvvm() {


      //  initializeFreeAddonsRecyclerView()
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
//                Constants.COMPARE_CART_COUNT = cartList!!.size
                cartCount = cartList!!.size
                badgeNumber = cartList!!.size
                binding?.badge121?.setText(badgeNumber.toString())
                binding?.badge121?.visibility = View.VISIBLE
                Log.v("badgeNumber", " "+ badgeNumber)
//                }
            } else {
//                Constants.COMPARE_CART_COUNT = 0
                cartCount = 0
                badgeNumber = 0
                binding?.badge121?.visibility = View.GONE
                packageInCartStatus = false



            }
//            viewModel.getCartItems()
            /*if (viewModel.allBundleResult.value != null) {


                var list = viewModel.allBundleResult.value!!
                if (list.size > 0) {
                    val listItem = arrayListOf<Bundles>()
                    for (item in list) {
                        Log.v("allBundleResultValue"," "+ item.name)
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
//                        packageAdaptor.addupdatesNew(listItem)
                        packageAdaptor.addupdates(listItem)
                        packageAdaptor.notifyDataSetChanged()
                    }
                }
            }*/
//            viewModel.getCartItems()
        })

        viewModel.getSpecificFeature().observe(this, Observer {
            if(it != null){
                for(items in it){
//                    Log.v("featuresHashMap", " "+ items)
                    featuresHashMap.put(items.target_business_usecase, items)
                }

                featureCount++
//                if(shimmer_view_compare.isShimmerStarted) {
//                    shimmer_view_compare.stopShimmer()
//                    shimmer_view_compare.visibility = View.GONE
//                }
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
            /*if (it) {
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }*/
        })

        viewModel.cartResultBack().observe(this, Observer {
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
                binding?.badge121?.setText(badgeNumber.toString())
                binding?.badge121?.visibility = View.VISIBLE
                Log.v("badgeNumber", " "+ badgeNumber)
//                }
            } else {
                badgeNumber = 0
                binding?.badge121?.visibility = View.GONE
                packageInCartStatus = false



            }
            viewModel.getCartItems()
            Log.v("COMPARE_BACK_VALUE"," "+ Constants.COMPARE_BACK_VALUE + " cartCount: "+  cartCount +
                    " COMPARE_CART_COUNT: "+Constants.COMPARE_CART_COUNT + " CART_VALUE: "+ Constants.CART_VALUE)
            if(Constants.COMPARE_BACK_VALUE == 1 /*&& cartCount != Constants.COMPARE_CART_COUNT*/){
//                Constants.COMPARE_BACK_VALUE = 0
                if (viewModel.allBundleResult.value != null) {


                    var list = viewModel.allBundleResult.value!!
                    if (list.size > 0) {
                        val listItem = arrayListOf<Bundles>()
                        for (item in list) {
                            Log.v("allBundleResultValue"," "+ item.name)
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
                            updatePackageViewPager(listItem)
//                        packageAdaptor.addupdates(listItem)
//                        packageAdaptor.notifyDataSetChanged()
                        }
                    }
                }else{
                      viewModel.loadPackageUpdates()
                }
                viewModel.getCartItemsBack()
            }


        })
    }

//    override fun onBackPressed() {
//        Log.v("onBackPressed", " "+ Constants.COMPARE_BACK_VALUE)
//        /*if (::viewModel.isInitialized) {
////            viewModel.getCartItems()
//            viewModel.getCartItemsBack()
//        }*/
//    }



    fun updatePackageViewPager(list: List<Bundles>) {

       // initializeFreeAddonsRecyclerView()
        //    initializePackageViewPager()
        //   package_viewpager.offscreenPageLimit = list.size
        Log.v("updatePackageViewPager"," "+ list.size)
        packageAdaptor.addupdates(list)
        packageAdaptor.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
     //  upgradeTextBottom.visibility = View.VISIBLE
        if (list.size > 1) {
            binding?.packageIndicator2?.visibility = View.VISIBLE
        } else {
            binding?.packageIndicator2?.visibility = View.INVISIBLE
        }
    }



//    fun initializeFreeAddonsRecyclerView() {
//
//
//
//        val list = arrayListOf<Bundles>()
//            upgradeList = list
//
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
////        layoutManager.isMeasurementCacheEnabled = false
//        val parentItemAdapter = ParentCompareItemAdapter(list)
////        package_viewpager.adapter = parentItemAdapter
//        binding?.packageViewpager?.adapter = packageAdaptor
//     //   binding?.packageViewpager?.offscreenPageLimit = 2
////        package_viewpager.offscreenPageLimit = upgradeList.size
//        package_indicator2.setViewPager2(package_viewpager)
//
//        if (upgradeList.size > 2) {
////            package_viewpager.setPageTransformer(SimplePageTransformer())
////
////                val itemDecoration = HorizontalMarginItemDecoration(
////                        this,
////                        R.dimen.viewpager_current_item_horizontal_margin2
////                )
////            package_viewpager.addItemDecoration(itemDecoration)
//        }
//
//        // removed for viewpager//
//        /*feature_item
//                .setAdapter(parentItemAdapter)
//        feature_item
//                .setLayoutManager(layoutManager)*/
//        //  initializePackageViewPager()
//
//    }


    private fun initializePackageViewPager() {
        binding?.packageViewpager?.adapter = packageAdaptor
//        package_viewpager.offscreenPageLimit = 4
        binding?.packageIndicator2?.setViewPager2(package_viewpager)

//        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position)
//        }
        val itemDecoration = HorizontalMarginItemDecoration(applicationContext, R.dimen.viewpager_current_item_horizontal_margin)
        binding?.packageViewpager!!.addItemDecoration(itemDecoration)



    }

//    override fun onPackageClicked(item: Bundles?,imageView: ImageView) {
//        if (!packageInCartStatus) {
//            if (item != null) {
//                prefs.storeAddedPackageDesc(item.desc!!)
//
//                val itemIds = arrayListOf<String>()
//                for(i in item.included_features){
//                    itemIds.add(i.feature_code)
//                }
//                makeFlyAnimation(imageView)
//
//                CompositeDisposable().add(
//                    AppDatabase.getInstance(requireActivity().application)!!
//                        .featuresDao()
//                        .getallFeaturesInList(itemIds)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                            {
////                                            featuresList = it
//                                var bundleMonthlyMRP = 0
//                                val minMonth:Int = if (item!!.min_purchase_months != null && item!!.min_purchase_months!! > 1) item!!.min_purchase_months!! else 1
//
//                                for (singleItem in it) {
//                                    for (item in item!!.included_features) {
//                                        if (singleItem.feature_code == item.feature_code) {
//                                            bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
//                                        }
//                                    }
//                                }
//
//                                offeredBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
//                                originalBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
//
//                                if(item!!.overall_discount_percent > 0)
//                                    offeredBundlePrice = originalBundlePrice - (originalBundlePrice * item!!.overall_discount_percent/100)
//                                else
//                                    offeredBundlePrice = originalBundlePrice
//
//                                //clear cartOrderInfo from SharedPref to requestAPI again
//                                prefs.storeCartOrderInfo(null)
//                                viewModel.addItemToCartPackage1(CartModel(
//                                    item!!._kid,
//                                    null,
//                                    null,
//                                    item!!.name,
//                                    "",
//                                    item!!.primary_image!!.url,
//                                    offeredBundlePrice.toDouble(),
//                                    originalBundlePrice.toDouble(),
//                                    item!!.overall_discount_percent,
//                                    1,
//                                    if (item!!.min_purchase_months != null) item!!.min_purchase_months!! else 1,
//                                    "bundles",
//                                    null
//                                ))
//                                val event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
//                                item!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
//                                item!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
//                                event_attributes.put("Package Price", originalBundlePrice)
//                                event_attributes.put("Discounted Price", offeredBundlePrice)
//                                event_attributes.put("Discount %", item!!.overall_discount_percent)
//                                item!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
//                                WebEngageController.trackEvent(ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART, ADDONS_MARKETPLACE, event_attributes)
//                                badgeNumber = badgeNumber + 1
//                                Log.v("badgeNumber321", " "+ badgeNumber)
//                                Constants.CART_VALUE = badgeNumber
////                                            viewModel.getCartItems()
//                            },
//                            {
//                                it.printStackTrace()
//
//                            }
//                        )
//                )
//
//
//            }
//        }
//    }

//    override fun onLearnMoreClicked(item: Bundles?) {
//        val packageFragment = PackageFragment.newInstance()
//        val args = Bundle()
//        args.putString("bundleData", Gson().toJson(item))
//        packageFragment.arguments = args
//        (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
//    }

//    override fun backComparePress() {
//        if(prefs.getCompareState() == 1){
//            prefs.storeCompareState(0)
//            viewModel.loadPackageUpdates()
//        }
//    }

//    private fun makeFlyAnimation(targetView: ImageView) {
//
//        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(600)
//            .setDestView(package_cart_icon).setAnimationListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(animation: Animator) {}
//                override fun onAnimationEnd(animation: Animator) {
//                    viewModel.getCartItems()
//
//
//                }
//
//                override fun onAnimationCancel(animation: Animator) {}
//                override fun onAnimationRepeat(animation: Animator) {}
//            }).startAnimation()
//
//    }








    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {


    }

}





