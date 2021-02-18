package com.boost.upgrades.ui.marketplace_offers

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
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.PackageAdaptor
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.marketplaceoffer_fragment.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MarketPlaceOfferFragment : BaseFragment() {

    lateinit var root: View

    lateinit var packageAdaptor: PackageAdaptor

    var marketOffersData: MarketPlaceOffers? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var badgeNumber = 0
    var offeredBundlePrice = 0
    var originalBundlePrice = 0

    var packageInCartStatus = false
    lateinit var prefs: SharedPrefs

    private lateinit var viewModel: MarketPlaceOfferViewModel
    companion object {
        fun newInstance() = MarketPlaceOfferFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.marketplaceoffer_fragment, container, false)

        val jsonString = arguments!!.getString("marketOffersData")
        marketOffersData = Gson().fromJson<MarketPlaceOffers>(jsonString, object : TypeToken<MarketPlaceOffers>() {}.type)
        Log.v("bundleData"," "+ marketOffersData?.coupon_code)
        packageAdaptor = PackageAdaptor((activity as UpgradeActivity), ArrayList(), Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type))
        prefs = SharedPrefs(activity as UpgradeActivity)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketPlaceOfferViewModel::class.java)

        loadData()
        initMvvm()

        package_title.setText(marketOffersData!!.title)

        if(arguments!!.containsKey("showCartIcon")){
            package_cart_icon.visibility = View.INVISIBLE
            package_submit.visibility = View.GONE
        }

        if(marketOffersData!!.image != null && !marketOffersData!!.image!!.url.isNullOrEmpty()){
            Glide.with(this).load(marketOffersData!!.image!!.url).into(package_profile_image)
        } else {
            package_profile_image.setImageResource(R.drawable.rectangle_copy_18)
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

    }

    private fun loadData() {

    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {

    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
//            viewModel.getCartItems()
        }
    }

    fun spannableString(value: Int, minMonth: Int) {
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
        orig_cost.setText(origCost)
    }



}
