package com.boost.cart.ui.packages

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.cart.adapter.CompareItemAdapter
import com.boost.cart.adapter.PackageAdaptor
import com.boost.cart.base_class.BaseFragment
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.cart.ui.home.CartFragment
import com.boost.cart.utils.CircleAnimationUtil
import com.boost.cart.utils.Constants
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.WebEngageController
import com.bumptech.glide.Glide
import com.framework.utils.RootUtil
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.package_fragment_layout.*
import kotlinx.android.synthetic.main.package_fragment_layout.badge121
import kotlinx.android.synthetic.main.package_fragment_layout.offer_price
import kotlinx.android.synthetic.main.package_fragment_layout.package_addons_recycler
import kotlinx.android.synthetic.main.package_fragment_layout.package_back
import kotlinx.android.synthetic.main.package_fragment_layout.package_cart_icon
import kotlinx.android.synthetic.main.package_fragment_layout.package_profile_image
import kotlinx.android.synthetic.main.package_fragment_layout.package_submit
import kotlinx.android.synthetic.main.package_fragment_layout.package_title
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PackageFragmentNew : BaseFragment() {

    lateinit var root: View

    lateinit var packageAdaptor: PackageAdaptor
    lateinit var packageAdapter: CompareItemAdapter

    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var badgeNumber = 0
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0

    var packageInCartStatus = false
    lateinit var prefs: SharedPrefs

    private lateinit var viewModel: PackageViewModel
    companion object {
        fun newInstance() = PackageFragmentNew()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.package_fragment_layout, container, false)

        val jsonString = requireArguments().getString("bundleData")
        Log.v("jsonString"," "+ jsonString)
        bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)
        packageAdaptor = PackageAdaptor((activity as CartActivity), ArrayList(), Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type))
        packageAdapter = CompareItemAdapter((activity as CartActivity), ArrayList())
        prefs = SharedPrefs(activity as CartActivity)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PackageViewModel::class.java)

        loadData()
        initMvvm()
//        initializeRecycler()
        initializePackageRecycler()

        package_title.setText(bundleData!!.name)
        parent_item_title.setText(bundleData!!.desc)

        if(requireArguments().containsKey("showCartIcon")){
            package_cart_icon.visibility = View.INVISIBLE
            package_submit.visibility = View.GONE
        }

        if(bundleData!!.primary_image != null && !bundleData!!.primary_image!!.url.isNullOrEmpty()){
            Glide.with(this).load(bundleData!!.primary_image!!.url).into(package_profile_image)
            Glide.with(this).load(bundleData!!.primary_image!!.url).into(package_profile_image_copy_new)
        } else {
            package_profile_image.setImageResource(R.drawable.rectangle_copy_18)
        }

        package_back.setOnClickListener {
            (activity as CartActivity).popFragmentFromBackStack()
        }

        package_cart_icon.setOnClickListener {
            (activity as CartActivity).addFragment(
                    CartFragment.newInstance(),
                    Constants.CART_FRAGMENT
            )
        }

        package_submit.setOnClickListener {
            if (!packageInCartStatus) {
                if (bundleData != null) {

                    //clear cartOrderInfo from SharedPref to requestAPI again
                    prefs.storeCartOrderInfo(null)
                    prefs.storeAddedPackageDesc(bundleData?.desc!!)

                    makeFlyAnimation(package_profile_image_copy_new)


                    viewModel.addItemToCart(CartModel(
                            bundleData!!._kid,
                            null,
                            null,
                            bundleData!!.name,
                            "",
                            bundleData!!.primary_image!!.url,
                            offeredBundlePrice,
                            originalBundlePrice,
                            bundleData!!.overall_discount_percent,
                            1,
                            if (bundleData!!.min_purchase_months != null) bundleData!!.min_purchase_months!! else 1,
                            "bundles",
                            null
                    ))
                    val event_attributes: HashMap<String, Any> = HashMap()
                    bundleData!!.name?.let { it1 -> event_attributes.put("Package Name", it1) }
                    bundleData!!.target_business_usecase?.let { it1 -> event_attributes.put("Package Tag", it1) }
                    event_attributes.put("Package Price", originalBundlePrice)
                    event_attributes.put("Discounted Price", offeredBundlePrice)
                    event_attributes.put("Discount %", bundleData!!.overall_discount_percent)
                    bundleData!!.min_purchase_months?.let { it1 -> event_attributes.put("Validity", it1) }
                    WebEngageController.trackEvent(ADDONS_MARKETPLACE_PACKAGE_ADDED_TO_CART, ADDONS_MARKETPLACE, event_attributes)
                    packageInCartStatus = true
                    package_submit.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.added_to_cart_grey
                    )
                    package_submit.setTextColor(Color.parseColor("#bbbbbb"))
                    package_submit.setText(getString(R.string.added_to_cart))
                    badgeNumber = badgeNumber + 1
//                    badge121.setText(badgeNumber.toString())
//                    badge121.visibility = View.VISIBLE
                    Constants.CART_VALUE = badgeNumber
                }
            }
        }
    }

    private fun loadData() {
        if(bundleData!!.included_features != null) {
            val itemIds = arrayListOf<String>()
            for (item in bundleData!!.included_features) {
                itemIds.add(item.feature_code)
            }
            viewModel.loadUpdates(itemIds)
        } else {
            Log.v("getkeyWidget1"," "+ bundleData!!._kid)
            //TODO: Load the widget_keys associated with Bundle from db
            viewModel.getAssociatedWidgetKeys(bundleData!!._kid)
        }

        viewModel.getCartItems()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {
        viewModel.getUpgradeResult().observe(this, Observer {
            if (it.size > 0) {
                featuresList = it
                var bundleMonthlyMRP = 0.0
                val minMonth:Int = if (bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1
                for (singleItem in it) {
                    for (item in bundleData!!.included_features) {
                        if (singleItem.feature_code == item.feature_code) {
                            bundleMonthlyMRP += RootUtil.round((singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),2)
                        }
                    }
                }

                offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                originalBundlePrice = (bundleMonthlyMRP * minMonth)

                if(bundleData!!.overall_discount_percent > 0)
                    offeredBundlePrice = RootUtil.round(originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent/100),2)
                else
                    offeredBundlePrice = originalBundlePrice

                if (minMonth > 1) {
                    offer_price.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice) + "/" + bundleData!!.min_purchase_months + "mths")
                    if (offeredBundlePrice != originalBundlePrice) {
                        spannableString(originalBundlePrice, minMonth)
//                        orig_cost.visibility = View.VISIBLE
                    } else {
//                        orig_cost.visibility = View.GONE
                    }
//                    updateRecycler(it,bundleData!!.min_purchase_months!!)
                    updatePackageRecycler(it,bundleData!!.min_purchase_months!!)
                } else {
                    offer_price.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice) + "/mth")
                    if (offeredBundlePrice != originalBundlePrice) {
                        spannableString(originalBundlePrice, 1)
//                        orig_cost.visibility = View.VISIBLE
                    } else {
//                        orig_cost.visibility = View.GONE
                    }
//                    updateRecycler(it,1)
                    updatePackageRecycler(it,1)
                }
//                package_count.setText(featuresList!!.size.toString())
//                if(bundleData!!.target_business_usecase.isNullOrEmpty()){
//                    package_use_case_layout.visibility = View.GONE
//                } else{
//                    package_use_case_layout.visibility = View.VISIBLE
//                    package_use_case.setText(bundleData!!.target_business_usecase)
//
//                }
                var event_attributes: java.util.HashMap<String, Any> = java.util.HashMap()
                event_attributes.put("Package Name", bundleData!!.name!!)
                WebEngageController.trackEvent(ADDONS_MARKETPLACE_PACKAGE_BUNDLE_LOADED, PAGE_VIEW, event_attributes,"")
            }
        })

        viewModel.cartResult().observe(this, Observer {
            cartList = it
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {
                if (bundleData != null) {
                    for (item in it) {
                        if (item.item_id.equals(bundleData!!._kid)) {
                            packageInCartStatus = true
                            package_submit.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.added_to_cart_grey
                            )
                            package_submit.setTextColor(Color.parseColor("#bbbbbb"))
                            package_submit.setText(getString(R.string.added_to_cart))
                            break
                        }
                    }
                    badgeNumber = cartList!!.size
                    badge121.setText(badgeNumber.toString())
                    badge121.visibility = View.VISIBLE

                    if(!packageInCartStatus){
                        package_submit.visibility = View.VISIBLE
                        package_submit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.orange_button_click_effect
                        )
                        package_submit.setTextColor(Color.WHITE)
                        package_submit.setText("Add Package to cart")
                    }
                }
            } else {
                badgeNumber = 0
                badge121.visibility = View.GONE
                packageInCartStatus = false

                package_submit.visibility = View.VISIBLE
                package_submit.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.orange_button_click_effect
                )
                package_submit.setTextColor(Color.WHITE)
                package_submit.setText("Add Package to cart")
            }
        })

        viewModel.getBundleWidgetKeys().observe(this, Observer {
            if(it != null){
                val itemIds = arrayListOf<String>()
                for (item in it) {
                    itemIds.add(item)
                }
                viewModel.loadUpdates(itemIds)
            }
        })
    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
            viewModel.getCartItems()
        }
    }

    fun spannableString(value: Double, minMonth: Int) {
        val origCost: SpannableString
        if (minMonth > 1) {
            origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/" + minMonth + "mths")
        } else {
            origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/mth")
        }
        origCost.setSpan(
                StrikethroughSpan(),
                0,
                origCost.length,
                0
        )
//        orig_cost.setText(origCost)
    }

    fun updateRecycler(list: List<FeaturesModel>, minMonth: Int) {
        packageAdaptor.addupdates(list, minMonth)
        packageAdaptor.notifyDataSetChanged()
    }

    fun updatePackageRecycler(list: List<FeaturesModel>, minMonth: Int) {
        packageAdapter.addupdates(list, minMonth)
        packageAdapter.notifyDataSetChanged()
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        package_addons_recycler.apply {
            layoutManager = gridLayoutManager
        }
        package_addons_recycler.adapter = packageAdaptor
    }

    private fun initializePackageRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        package_addons_recycler.apply {
            layoutManager = gridLayoutManager
        }
        package_addons_recycler.adapter = packageAdapter
    }

    private fun makeFlyAnimation(targetView: ImageView) {

        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(600)
            .setDestView(package_cart_icon).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                viewModel.getCartItems()

                }


                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()

    }


}
