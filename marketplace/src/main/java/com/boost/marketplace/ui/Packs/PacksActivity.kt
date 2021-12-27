package com.boost.marketplace.ui.Packs

import android.graphics.Color
import android.os.Handler
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityPacksBinding
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.activity_packs.*
import kotlinx.android.synthetic.main.item_packs_list.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

class PacksActivity : AppBaseActivity<ActivityPacksBinding, PacksViewModel,>(),
    RecyclerItemClickListener  {

//    var packageAdapter:AppBaseRecyclerViewAdapter<Bundles>? = null
//
//    var bundleData: Bundles? = null
//    var featuresList: List<FeaturesModel>? = null
//    var cartList: List<CartModel>? = null
//
//    var badgeNumber = 0
//    var offeredBundlePrice = 0
//    var originalBundlePrice = 0
//
//    var packageInCartStatus = false
//    lateinit var prefs: SharedPrefs
//    var minMonth = 1


    override fun getLayout(): Int {
        return R.layout.activity_packs
    }

    override fun getViewModelClass(): Class<PacksViewModel> {
        return PacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        val listData = listOf("Daily Stories",
            "Custom Domain",
            "In-Clinic Appointments",
            "Email Accounts",)
        val listLoop = listOf("Daily Stories",
            "Custom Domain",
            "Email Accounts",)

        binding?.childRecyclerview?.setupAdapter<String>(R.layout.item_packs_list) { adapter, context, list ->
            bind { itemView, position, item ->
                itemView.details.text = item
                itemView.setOnClickListener {

                }
            }
            val layoutManager = GridLayoutManager(this@PacksActivity, 3)
            setLayoutManager(layoutManager)
            fixGridSpan(2)


            submitList(listData)

            Handler().postDelayed({
                listLoop.map { item ->
                    submitItem(item)
                }
            }, 1000)
        }


//        val jsonString = intent.extras?.getString("bundleData")
//        Log.v("jsonString"," "+ jsonString)
//        bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)
//      //  packageAdaptor = PackageAdaptor((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type))
//    //    packageAdapter = CompareItemAdapter((this), ArrayList())
//        prefs = SharedPrefs(this)
//
//       // loadData()
//      //  initMvvm()
////        initializeRecycler()
//        initializePackageRecycler()
////        updateRecycler(featuresList)
////        updatePackageRecycler()
//
//        binding?.packageTitle?.text = bundleData!!.name
//
//
//        if(bundleData!!.primary_image != null && !bundleData!!.primary_image!!.url.isNullOrEmpty()){
//            Glide.with(this).load(bundleData!!.primary_image!!.url).into(package_profile_image)
//            Glide.with(this).load(bundleData!!.primary_image!!.url).into(package_profile_image_compare_new)
//        } else {
//            package_profile_image.setImageResource(R.drawable.rectangle_copy_18)
//        }
//
//
//        binding?.packageAddCartNew?.setOnClickListener {
//            if (!packageInCartStatus) {
//                if (bundleData != null) {
//
//                    //clear cartOrderInfo from SharedPref to requestAPI again
//                    prefs.storeCartOrderInfo(null)
//                    prefs.storeAddedPackageDesc(bundleData?.desc!!)
//
//                 //   makeFlyAnimation(binding!!.packageProfileImageCompareNew)
//
//
//                    viewModel.addItemToCart(CartModel(
//                        bundleData!!._kid,
//                        null,
//                        null,
//                        bundleData!!.name,
//                        "",
//                        bundleData!!.primary_image!!.url,
//                        offeredBundlePrice.toDouble(),
//                        originalBundlePrice.toDouble(),
//                        bundleData!!.overall_discount_percent,
//                        1,
//                        if (bundleData!!.min_purchase_months != null) bundleData!!.min_purchase_months!! else 1,
//                        "bundles",
//                        null
//                    ))
//                    val event_attributes: HashMap<String, Any> = HashMap()
//                    bundleData!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
//                    bundleData!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
//                    event_attributes.put("Package Price", originalBundlePrice)
//                    event_attributes.put("Discounted Price", offeredBundlePrice)
//                    event_attributes.put("Discount %", bundleData!!.overall_discount_percent)
//                    bundleData!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
//                 //   WebEngageController.trackEvent(ADDONS_MARKETPLACE_PACKAGE_ADDED_TO_CART, ADDONS_MARKETPLACE, event_attributes)
//                    packageInCartStatus = true
//                   binding!!.packageAddCartNew.background = ContextCompat.getDrawable(
//                        this,
//                        R.drawable.added_to_cart_grey
//                    )
//                    binding!!.packageAddCartNew.setTextColor(Color.parseColor("#bbbbbb"))
//                    binding!!.packageAddCartNew.text = getString(R.string.added_to_cart)
//                    badgeNumber = badgeNumber + 1
////                    badge121.setText(badgeNumber.toString())
////                    badge121.visibility = View.VISIBLE
//                  //  Constants.CART_VALUE = badgeNumber
//                }
//            }
//        }
    }


//    private fun loadData() {
//        if(bundleData!!.included_features != null) {
//            val itemIds = arrayListOf<String>()
//            for (item in bundleData!!.included_features) {
//                itemIds.add(item.feature_code)
//            }
//            viewModel.loadUpdates(itemIds)
//        } else {
//            Log.v("getkeyWidget1"," "+ bundleData!!._kid)
//            //TODO: Load the widget_keys associated with Bundle from db
//            viewModel.getAssociatedWidgetKeys(bundleData!!._kid)
//        }
//
//        viewModel.getCartItems()
//    }

//    private fun initMvvm() {
//        viewModel.getUpgradeResult().observe(this, Observer {
//            if (it.size > 0) {
//                featuresList = it
//                var bundleMonthlyMRP = 0
//                val minMonth:Int = if (bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1
//                for (singleItem in it) {
//                    for (item in bundleData!!.included_features) {
//                        if (singleItem.feature_code == item.feature_code) {
//                            bundleMonthlyMRP += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt()
//                        }
//                    }
//                }
//
//                offeredBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
//                originalBundlePrice = (bundleMonthlyMRP * minMonth).toInt()
//
//                if(bundleData!!.overall_discount_percent > 0)
//                    offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent/100)
//                else
//                    offeredBundlePrice = originalBundlePrice
//
//                if (minMonth > 1) {
//                    offer_price.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice) + "/" + bundleData!!.min_purchase_months + "mths")
//                    if (offeredBundlePrice != originalBundlePrice) {
//                        spannableString(originalBundlePrice, minMonth)
////                        orig_cost.visibility = View.VISIBLE
//                    } else {
////                        orig_cost.visibility = View.GONE
//                    }
////                    updateRecycler(it,bundleData!!.min_purchase_months!!)
//                    updatePackageRecycler(it,bundleData!!.min_purchase_months!!)
//                } else {
//                    offer_price.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice) + "/mth")
//                    if (offeredBundlePrice != originalBundlePrice) {
//                        spannableString(originalBundlePrice, 1)
////                        orig_cost.visibility = View.VISIBLE
//                    } else {
////                        orig_cost.visibility = View.GONE
//                    }
////                    updateRecycler(it,1)
//                    updatePackageRecycler(it,1)
//                }
////                package_count.setText(featuresList!!.size.toString())
////                if(bundleData!!.target_business_usecase.isNullOrEmpty()){
////                    package_use_case_layout.visibility = View.GONE
////                } else{
////                    package_use_case_layout.visibility = View.VISIBLE
////                    package_use_case.setText(bundleData!!.target_business_usecase)
////
////                }
//                var event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
//                event_attributes.put("Package Name", bundleData!!.name!!)
//                WebEngageController.trackEvent(ADDONS_MARKETPLACE_PACKAGE_BUNDLE_LOADED, PAGE_VIEW, event_attributes,"")
//            }
//        })
//
//        viewModel.cartResult().observe(this, Observer {
//            cartList = it
//            packageInCartStatus = false
//            if (cartList != null && cartList!!.size > 0) {
//                if (bundleData != null) {
//                    for (item in it) {
//                        if (item.item_id.equals(bundleData!!._kid)) {
//                            packageInCartStatus = true
//                            package_submit.background = ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.added_to_cart_grey
//                            )
//                            package_submit.setTextColor(Color.parseColor("#bbbbbb"))
//                            package_submit.setText(getString(R.string.added_to_cart))
//                            break
//                        }
//                    }
//                    badgeNumber = cartList!!.size
//                    badge121.setText(badgeNumber.toString())
//                    badge121.visibility = View.VISIBLE
//
//                    if(!packageInCartStatus){
//                        package_submit.visibility = View.VISIBLE
//                        package_submit.background = ContextCompat.getDrawable(
//                            requireContext(),
//                            R.drawable.orange_button_click_effect
//                        )
//                        package_submit.setTextColor(Color.WHITE)
//                        package_submit.setText("Add Package to cart")
//                    }
//                }
//            } else {
//                badgeNumber = 0
//                badge121.visibility = View.GONE
//                packageInCartStatus = false
//
//                package_submit.visibility = View.VISIBLE
//                package_submit.background = ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.orange_button_click_effect
//                )
//                package_submit.setTextColor(Color.WHITE)
//                package_submit.setText("Add Package to cart")
//            }
//        })
//        viewModel.getBundleWidgetKeys().observe(this, Observer {
//            if(it != null){
//                val itemIds = arrayListOf<String>()
//                for (item in it) {
//                    itemIds.add(item)
//                }
//                viewModel.loadUpdates(itemIds)
//            }
//        })
//    }
//
//    fun spannableString(value: Int, minMonth: Int) {
//        val origCost: SpannableString
//        if (minMonth > 1) {
//            origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/" + minMonth + "mths")
//        } else {
//            origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/mth")
//        }
//        origCost.setSpan(
//            StrikethroughSpan(),
//            0,
//            origCost.length,
//            0
//        )
////        orig_cost.setText(origCost)
//    }
//
//    fun updateRecycler(list: ArrayList<Bundles>, minMonth: Int) {
//        packageAdapter?.addupdates(list, minMonth)
//        packageAdapter?.notifyDataSetChanged()
//    }
//
//    fun updatePackageRecycler(list:  ArrayList<Bundles>, minMonth: Int) {
//        packageAdapter?.addupdates(list, minMonth)
//        packageAdapter?.notifyDataSetChanged()
//    }
//
//
//    private fun initializePackageRecycler() {
//        val gridLayoutManager = GridLayoutManager(this, 3)
//        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        binding?.childRecyclerview?.apply {
//            layoutManager = gridLayoutManager
//        }
//        child_recyclerview.adapter = packageAdapter
//    }
//
////    private fun makeFlyAnimation(targetView: ImageView) {
////
////        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(600)
////            .setDestView(package_cart_icon).setAnimationListener(object : Animator.AnimatorListener {
////                override fun onAnimationStart(animation: Animator) {}
////                override fun onAnimationEnd(animation: Animator) {
////                    viewModel.getCartItems()
////
////                }
////
////
////                override fun onAnimationCancel(animation: Animator) {}
////                override fun onAnimationRepeat(animation: Animator) {}
////            }).startAnimation()
////
////    }
//
    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        TODO("Not yet implemented")
    }


}