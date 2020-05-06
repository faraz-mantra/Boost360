package com.boost.upgrades.ui.packages

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.PackageAdaptor
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.package_fragment.*

class PackageFragment : BaseFragment() {

    lateinit var root: View

    lateinit var packageAdaptor: PackageAdaptor

    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var mrpPrice = 0.0
    var grandTotal = 0.0
    var badgeNumber = 0

    var packageInCartStatus = false

    companion object {
        fun newInstance() = PackageFragment()
    }

    private lateinit var viewModel: PackageViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.package_fragment, container, false)

        val jsonString = arguments!!.getString("bundleData")
        bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)

        packageAdaptor = PackageAdaptor(ArrayList(), Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type))

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PackageViewModel::class.java)

        loadData()
        initMvvm()
        initializeRecycler()

        package_title.setText(bundleData!!.name)

        Glide.with(this).load(R.drawable.back_beau)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(back_image)

        package_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        package_cart_icon.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    CartFragment.newInstance(),
                    Constants.CART_FRAGMENT
            )
        }

        package_submit.setOnClickListener {
            if (!packageInCartStatus) {
                if (bundleData != null) {
                    viewModel.addItemToCart(CartModel(
                            bundleData!!._kid,
                            bundleData!!.name,
                            "",
                            bundleData!!.primary_image!!.url,
                            grandTotal,
                            mrpPrice,
                            bundleData!!.overall_discount_percent,
                            1,
                            if (bundleData!!.min_purchase_months != null) bundleData!!.min_purchase_months!! else 1,
                            "bundles",
                            null
                    ))
                    packageInCartStatus = true
                    package_submit.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.added_to_cart_grey
                    )
                    package_submit.setTextColor(Color.parseColor("#bbbbbb"))
                    package_submit.setText(getString(R.string.added_to_cart))
                    badgeNumber = badgeNumber + 1
                    badge121.setText(badgeNumber.toString())
                    badge121.visibility = View.VISIBLE
                    Constants.CART_VALUE = badgeNumber
                }
            }
        }
    }

    private fun loadData() {
        val itemIds = arrayListOf<String>()
        for (item in bundleData!!.included_features) {
            itemIds.add(item.feature_code)
        }
        viewModel.loadUpdates(itemIds)
        viewModel.getCartItems()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {
        viewModel.getUpgradeResult().observe(this, Observer {
            if (it.size > 0) {
                featuresList = it
                val minMonth:Int = if (bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1
                for (singleItem in it) {
                    for (item in bundleData!!.included_features) {
                        if (singleItem.boost_widget_key == item.feature_code) {
                            val total = (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0))
                            grandTotal += total
                            mrpPrice += singleItem.price
                        }
                    }
                }
                if (minMonth > 1) {
                    val offeredPrice = grandTotal * minMonth
                    offer_price.setText("₹" + offeredPrice + "/" + bundleData!!.min_purchase_months + "month")
                    if (grandTotal != mrpPrice) {
                        spannableString(mrpPrice, minMonth)
                        orig_cost.visibility = View.VISIBLE
                    } else {
                        orig_cost.visibility = View.GONE
                    }
                    updateRecycler(it,bundleData!!.min_purchase_months!!)
                } else {
                    offer_price.setText("₹" + grandTotal + "/month")
                    if (grandTotal != mrpPrice) {
                        spannableString(mrpPrice, 1)
                        orig_cost.visibility = View.VISIBLE
                    } else {
                        orig_cost.visibility = View.GONE
                    }
                    updateRecycler(it,1)
                }
                package_count.setText(featuresList!!.size.toString())
            }
        })
        viewModel.cartResult().observe(this, Observer {
            cartList = it
            packageInCartStatus = false
            if (cartList != null && cartList!!.size > 0) {
                if (bundleData != null) {
                    for (item in it) {
                        if (item.boost_widget_key.equals(bundleData!!._kid)) {
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
                    if (!packageInCartStatus) {
                        package_submit.visibility = View.VISIBLE
                        package_submit.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.orange_button_click_effect
                        )
                        package_submit.setTextColor(Color.WHITE)
                        package_submit.setText("Add '" + bundleData!!.name + "' to cart")
                    }
                }
            } else {
                badgeNumber = 0
                badge121.visibility = View.GONE
                packageInCartStatus = false
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
            val originalCost = value * minMonth
            origCost = SpannableString("₹" + originalCost + "/" + minMonth + "month")
        } else {
            origCost = SpannableString("₹" + value + "/month")
        }
        origCost.setSpan(
                StrikethroughSpan(),
                0,
                origCost.length,
                0
        )
        orig_cost.setText(origCost)
    }

    fun updateRecycler(list: List<FeaturesModel>, minMonth: Int) {
        packageAdaptor.addupdates(list, minMonth)
        packageAdaptor.notifyDataSetChanged()
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        package_addons_recycler.apply {
            layoutManager = gridLayoutManager
        }
        package_addons_recycler.adapter = packageAdaptor
    }

}
