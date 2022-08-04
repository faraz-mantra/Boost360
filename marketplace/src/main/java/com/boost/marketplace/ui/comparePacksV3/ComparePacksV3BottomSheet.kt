package com.boost.marketplace.ui.comparePacksV3

import android.app.Application
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.adapter.BenifitsPageTransformer
import com.boost.cart.adapter.SimplePageTransformerSmall
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.PacksV3BenefitsViewPagerAdapter
import com.boost.marketplace.R
import com.boost.marketplace.databinding.Comparepacksv3PopupBinding
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.bumptech.glide.Glide
import com.framework.base.BaseBottomSheetDialog
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feature_details.*

class ComparePacksV3BottomSheet: BaseBottomSheetDialog<Comparepacksv3PopupBinding, ComparePacksViewModel>() {

    lateinit var bundleData: Bundles
   // lateinit var cartData: List<CartModel>
    lateinit var prefs: SharedPrefs
    var offeredBundlePrice :Double = 0.0
    var originalBundlePrice :Double = 0.0
    var addonsSize :Int = 0
    lateinit var benefitAdaptor: PacksV3BenefitsViewPagerAdapter

    override fun getLayout(): Int {
        return R.layout.comparepacksv3_popup
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {

        dialog.behavior.isDraggable = true

        bundleData = Gson().fromJson<Bundles>(
            requireArguments().getString("bundleData"),
            object : TypeToken<Bundles>() {}.type
        )

      //  cartData = Gson().fromJson<List<CartModel>?>(requireArguments().getString("cartData"),object : TypeToken<List<CartModel>?>() {}.type)

        addonsSize = requireArguments().getInt("addons")
        offeredBundlePrice = requireArguments().getDouble("price")
        originalBundlePrice = requireArguments().getDouble("price")
        benefitAdaptor = PacksV3BenefitsViewPagerAdapter(ArrayList())
        prefs = SharedPrefs(baseActivity)
        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)

        binding?.packageTitle?.text=bundleData.name

        binding?.packageProfileImage?.let {
            Glide.with(this).load(bundleData.primary_image!!.url)
                .into(it)
        }

        if (bundleData.overall_discount_percent > 0) {
            offeredBundlePrice = RootUtil.round(originalBundlePrice - (originalBundlePrice * bundleData.overall_discount_percent / 100.0), 2)
            binding?.packDiscountTv?.visibility = View.VISIBLE
            binding?.packDiscountTv?.setText(bundleData.overall_discount_percent.toString() + "% SAVING") //= "${bundleData.overall_discount_percent}" +" % SAVING"

        } else {
            offeredBundlePrice = originalBundlePrice
            binding?.packDiscountTv?.visibility = View.VISIBLE
        }

        binding?.buyPack?.text = "Buy " + bundleData.name

        binding?.addonsCountTv?.text= addonsSize.toString()+" PREMIUM FEATURES ideal for small businesses that want to get started with online sales."

        binding?.closeBtn?.setOnClickListener {
            dismiss()
        }

        initializeViewPager()

        if (bundleData.benefits != null && bundleData.benefits!!.size > 0){
            benefitAdaptor.addupdates(bundleData.benefits!!)
            benefitAdaptor.notifyDataSetChanged()
            binding?.benefitContainer?.visibility = View.VISIBLE
        }else{
            binding?.benefitContainer?.visibility = View.GONE
        }

        binding?.buyPack?.setOnClickListener {
            if (bundleData != null) {
                prefs.storeAddedPackageDesc(bundleData.desc ?: "")

                val itemIds = arrayListOf<String>()
                for (i in bundleData.included_features) {
                    itemIds.add(i.feature_code)
                }

                CompositeDisposable().add(
                    AppDatabase.getInstance(Application())!!
                        .featuresDao()
                        .getallFeaturesInList(itemIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
//                                            featuresList = it
                                var bundleMonthlyMRP = 0.0
                                val minMonth: Int =
                                    if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                                for (singleItem in it) {
                                    for (item in bundleData!!.included_features) {
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

                                if (bundleData!!.overall_discount_percent > 0)
                                    offeredBundlePrice = RootUtil.round(
                                        originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100),
                                        2
                                    )
                                else
                                    offeredBundlePrice = originalBundlePrice

                                //clear cartOrderInfo from SharedPref to requestAPI again
                                prefs.storeCartOrderInfo(null)
                                viewModel!!.addItemToCartPackage1(
                                    CartModel(
                                        bundleData!!._kid,
                                        null,
                                        null,
                                        bundleData!!.name,
                                        "",
                                        bundleData!!.primary_image!!.url,
                                        offeredBundlePrice.toDouble(),
                                        originalBundlePrice.toDouble(),
                                        bundleData!!.overall_discount_percent,
                                        1,
                                        if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null) bundleData!!.min_purchase_months!! else 1,
                                        "bundles",
                                        null,
                                        ""
                                    )
                                )
                                val event_attributes: java.util.HashMap<String, Any> =
                                    java.util.HashMap()
                                bundleData!!.name?.let { it1 ->
                                    event_attributes.put(
                                        "Package Name",
                                        it1
                                    )
                                }
                                bundleData!!.target_business_usecase?.let { it1 ->
                                    event_attributes.put(
                                        "Package Tag",
                                        it1
                                    )
                                }
                                event_attributes.put("Package Price", originalBundlePrice)
                                event_attributes.put("Discounted Price", offeredBundlePrice)
                                event_attributes.put(
                                    "Discount %",
                                    bundleData!!.overall_discount_percent
                                )
                                bundleData!!.min_purchase_months?.let { it1 ->
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
                            },
                            {
                                it.printStackTrace()

                            }

                        )
                )
            }
            binding?.buyPack?.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_packsv3_added_to_cart_bg
            )
            binding?.buyPack?.setTextColor(
                this.getResources().getColor(R.color.tv_color_BB)
            )
            binding?.buyPack?.setText(this.getString(R.string.added_to_cart))
            binding?.buyPack?.isClickable = false


        }
    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsViewpager?.let { binding?.benefitsIndicator?.setViewPager2(it) }
        binding?.benefitsViewpager?.offscreenPageLimit = 1
        binding?.benefitsViewpager?.setPageTransformer(BenifitsPageTransformer(requireActivity()))

//        val itemDecoration = HorizontalMarginItemDecoration(
//            requireContext(),
//            R.dimen.viewpager_previous_benifits,
//            R.dimen.viewpager_next_benifits
//        )
//        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

//        binding?.benefitsViewpager?.setPageTransformer(SimplePageTransformerSmall())
//
//        val itemDecoration = HorizontalMarginItemDecoration(
//            requireContext(),
//            R.dimen.viewpager_current_item_horizontal_margin,
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)

    }

}