package com.boost.marketplace.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.*
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ActivityMarketplaceBinding
import com.boost.marketplace.infra.api.models.test.TestData
import com.boost.marketplace.infra.api.models.test.getData
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.ui.Marketplace_Offers.MarketPlaceOffersActivity
import com.boost.marketplace.ui.Packs.PacksActivity
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.framework.base.BaseActivity
import com.framework.pref.UserSessionManager
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MarketPlaceActivity : AppBaseActivity<ActivityMarketplaceBinding, MarketPlaceHomeViewModel>(), RecyclerItemClickListener {

  private var adapterMarketPromoBanner: AppBaseRecyclerViewAdapter<TestData>? = null
  private var adapterPack: AppBaseRecyclerViewAdapter<TestData>? = null
  private var adapterVideos :AppBaseRecyclerViewAdapter<TestData>? = null
  private var adapterFeaturesByCategory: AppBaseRecyclerViewAdapter<TestData>? = null
  private var adapterPartnerZone :AppBaseRecyclerViewAdapter<TestData>? = null
  private var adapterTopFeatures: AppBaseRecyclerViewAdapter<TestData>? = null




  var badgeNumber = 0
  var fpRefferalCode: String = ""
  var feedBackLink: String? = null
  lateinit var prefs: SharedPrefs
  var packageInCartStatus = false
  var offeredBundlePrice = 0
  var originalBundlePrice = 0
  var featuresList: List<FeaturesModel>? = null
  private var itemsArrayList : ArrayList<String>? = ArrayList()
  private var packsArrayList : ArrayList<String>? = ArrayList()
  private var itemTypeArrayList :ArrayList<String>? = ArrayList()



  override fun onCreateView() {
    super.onCreateView()
    supportActionBar?.setDisplayShowTitleEnabled(false)
    supportActionBar?.elevation = 0F
    setMarketPlacePromoBannerData()
    setPackData()
    setPartnerData()
    setFeaturesByCategoryData()
    setTopFeaturesData()
    setVideosData()
  }

  override fun getLayout(): Int {
    return R.layout.activity_marketplace
  }

  override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
    return MarketPlaceHomeViewModel::class.java
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.overflowMenu -> {
        Toast.makeText(applicationContext, "Clicked on More Button", Toast.LENGTH_LONG).show()
        true
      }
      R.id.plan_history -> {
        Toast.makeText(applicationContext, "Clicked on Plan History", Toast.LENGTH_LONG).show()
        return true
      }
      R.id.offer_coupons -> {
        Toast.makeText(applicationContext, "Clicked on Offer Coupons", Toast.LENGTH_LONG).show()
        return true
      }
      R.id.help_section -> {
        Toast.makeText(applicationContext, "Clicked on Help Section", Toast.LENGTH_LONG).show()
        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun setMarketPlacePromoBannerData() {
    binding?.mpBannerViewpager?.apply {
        if (adapterMarketPromoBanner == null) {
          adapterMarketPromoBanner = AppBaseRecyclerViewAdapter(this@MarketPlaceActivity, getData(RecyclerViewItemType.PROMO_BANNER.ordinal),this@MarketPlaceActivity )
          offscreenPageLimit = 3
          adapter = adapterMarketPromoBanner
          binding?.mpBannerDotIndicator?.setViewPager2(this)
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        }
      }
  }

  private fun setPackData() {
    binding?.mpPackageViewpager?.apply {
        if (adapterPack == null){
          adapterPack = AppBaseRecyclerViewAdapter(this@MarketPlaceActivity,getData(RecyclerViewItemType.PACKS.ordinal),this@MarketPlaceActivity)
          offscreenPageLimit = 3
          adapter = adapterPack
          binding?.mpPackageIndicator?.setViewPager2(this)
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        }
      }
    }

  private fun setPartnerData(){
    binding?.mpHomePartnerViewpager?.apply {
      if(adapterPartnerZone == null){
        adapterPartnerZone = AppBaseRecyclerViewAdapter(this@MarketPlaceActivity, getData(RecyclerViewItemType.PARTNER.ordinal),this@MarketPlaceActivity)
        offscreenPageLimit = 3
        adapter = adapterPartnerZone
        binding?.mpHomePartnerIndicator?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    }
  }

  private fun setFeaturesByCategoryData(){
    binding?.addonsCategoryRecycler?.apply {
        if(adapterFeaturesByCategory == null){
          adapterFeaturesByCategory = AppBaseRecyclerViewAdapter(this@MarketPlaceActivity,getData(RecyclerViewItemType.FEATURES_BY_CATEGORY.ordinal),this@MarketPlaceActivity)
          adapter = adapterFeaturesByCategory
      }
    }
  }

  private fun setTopFeaturesData(){
    binding?.topFetauresRecycler?.apply {
      if (adapterTopFeatures == null){
        adapterTopFeatures = AppBaseRecyclerViewAdapter(this@MarketPlaceActivity,getData(RecyclerViewItemType.TOP_FEATURES.ordinal),this@MarketPlaceActivity)
        adapter = adapterTopFeatures
      }
    }
  }
  private fun setVideosData(){
    binding?.mpHomeFeaturesVideoRv?.apply {
      if(adapterVideos == null){
        adapterVideos = AppBaseRecyclerViewAdapter(this@MarketPlaceActivity, getData(RecyclerViewItemType.VIDEOS.ordinal),this@MarketPlaceActivity)
        adapter = adapterVideos
      }
    }
  }
//  fun loadData() {
////    val pref = requireActivity().getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
////    val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
////    var code: String = (activity as UpgradeActivity).experienceCode!!
////    if (!code.equals("null", true)) {
////      viewModel.setCurrentExperienceCode(code, fpTag!!)
////    }
//
////    viewModel.loadUpdates(
////      (activity as? UpgradeActivity)?.getAccessToken() ?: "",
////      (activity as UpgradeActivity).fpid!!,
////      (activity as UpgradeActivity).clientid,
////      (activity as UpgradeActivity).experienceCode,
////      (activity as UpgradeActivity).fpTag
////    )
////    viewModel.getAllFeaturesForMarketplace(application,)
//  }

//  private fun initMvvm() {
//
//    viewModel.updatesError().observe(this, androidx.lifecycle.Observer {
//      longToast(requireContext(), "onFailure: " + it)
//    })
//
//    viewModel.getAllAvailableFeatures().observe(this, androidx.lifecycle.Observer {
//      all_recommended_addons.visibility = View.VISIBLE
//      updateRecycler(it)
//      updateAddonCategoryRecycler(it)
//    })
//
//    viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
//      val list = arrayListOf<Bundles>()
//      for (item in it) {
//        val temp = Gson().fromJson<List<IncludedFeature>>(
//          item.included_features,
//          object : TypeToken<List<IncludedFeature>>() {}.type
//        )
//        list.add(
//          Bundles(
//            item.bundle_id,
//            temp,
//            item.min_purchase_months,
//            item.name,
//            item.overall_discount_percent,
//            PrimaryImage(item.primary_image),
//            item.target_business_usecase,
//            Gson().fromJson<List<String>>(
//              item.exclusive_to_categories,
//              object : TypeToken<List<String>>() {}.type
//            ),
//            null, item.desc
//          )
//        )
//      }
//      if (list.size > 0) {
//        if (shimmer_view_package.isShimmerStarted) {
//          shimmer_view_package.stopShimmer()
//          shimmer_view_package.visibility = View.GONE
//        }
//        package_layout.visibility = View.VISIBLE
//        package_compare_layout.visibility = View.VISIBLE
//        updatePackageViewPager(list)
//      } else {
//        if (shimmer_view_package.isShimmerStarted) {
//          shimmer_view_package.stopShimmer()
//          shimmer_view_package.visibility = View.GONE
//        }
//        package_layout.visibility = View.GONE
//        package_compare_layout.visibility = View.GONE
//      }
//    })
//
//    viewModel.getBackAllBundles().observe(this, androidx.lifecycle.Observer {
//      val list = arrayListOf<Bundles>()
//      for (item in it) {
//        val temp = Gson().fromJson<List<IncludedFeature>>(
//          item.included_features,
//          object : TypeToken<List<IncludedFeature>>() {}.type
//        )
//        list.add(
//          Bundles(
//            item.bundle_id,
//            temp,
//            item.min_purchase_months,
//            item.name,
//            item.overall_discount_percent,
//            PrimaryImage(item.primary_image),
//            item.target_business_usecase,
//            Gson().fromJson<List<String>>(
//              item.exclusive_to_categories,
//              object : TypeToken<List<String>>() {}.type
//            ),
//            null, item.desc
//          )
//        )
//      }
//      if (list.size > 0) {
//        package_layout.visibility = View.VISIBLE
//        package_compare_layout.visibility = View.VISIBLE
////                updatePackageViewPager(list)
//        updatePackageBackPressViewPager(list)
//      } else {
//        package_layout.visibility = View.GONE
//        package_compare_layout.visibility = View.GONE
//      }
//    })
//
//    viewModel.getAllFeatureDeals().observe(this, androidx.lifecycle.Observer {
//      if (it.size > 0) {
//        var cartItems: List<CartModel> = arrayListOf()
//        if (viewModel.cartResult.value != null) {
//          cartItems = viewModel.cartResult.value!!
//        }
//        updateFeatureDealsViewPager(it, cartItems)
//      }
//    })
//
//    viewModel.getTotalActiveWidgetCount().observe(this, androidx.lifecycle.Observer {
//      total_active_widget_count.text = it.toString()
//    })
//
//    viewModel.categoryResult().observe(this, androidx.lifecycle.Observer {
//      if (it != null) {
//        if (recommended_features_account_type.paint.measureText(Html.fromHtml(it!!.toLowerCase()).toString()) > recommended_features_account_type.measuredWidth) {
//          recommended_features_account_type.visibility = View.GONE
//          recommended_features_additional_tv.text = Html.fromHtml(it!!.toLowerCase())
//
//        } else {
//          recommended_features_account_type.setText(Html.fromHtml(it!!.toLowerCase()))
//          recommended_features_additional_tv.visibility = View.GONE
//        }
//      }
//
//    })
//
//    viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
//      if (it) {
//        val status = "Loading. Please wait..."
//        progressDialog.setMessage(status)
//        progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
//        progressDialog.show()
//      } else {
//        progressDialog.dismiss()
//      }
//    })
//
//    viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
//      UserSessionManager(requireActivity()).storeIntDetails(Key_Preferences.KEY_FP_CART_COUNT, it.size ?: 0)
//      if (it != null && it.size > 0) {
////                packageInCartStatus = false
//        mp_view_cart_rl.visibility = View.VISIBLE
//        badge.visibility = View.VISIBLE
//        badgeNumber = it.size
//        badge.setText(badgeNumber.toString())
//
//        itemTypeArrayList?.clear()
//        it.forEach {
//          itemTypeArrayList?.add(it.item_type.toString())
//        }
//        if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
//          if (badgeNumber == 1) {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " item waiting in cart"
//          } else {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " items waiting in cart"
//          }
//        } else if (itemTypeArrayList!!.contains("features")) {
//          if (badgeNumber == 1) {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " item waiting in cart"
//          } else {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " items waiting in cart"
//          }
//        } else if (itemTypeArrayList!!.contains("bundles")) {
//          if (badgeNumber == 1) {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " pack waiting in cart"
//          } else {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " packs waiting in cart"
//          }
//
//        }
//        itemsArrayList?.clear()
//        it.forEach {
//          if (it.item_type.equals("features")) {
//            itemsArrayList?.add(it.item_name.toString())
//          }
//        }
//
//        var cartItems = " "
//        if (itemsArrayList != null && itemsArrayList!!.size > 0) {
//          for (items in itemsArrayList!!) {
//            cartItems += items + ", "
//          }
//          cartItems = cartItems.substring(0, cartItems.length - 2)
//          var cartUpdatedItems = ""
//          if (mp_items_name_tv.paint.measureText(cartItems) > 2 * (mp_items_name_tv.measuredWidth)) {
//            val index = itemsArrayList!!.size - 1
//            itemsArrayList!!.removeAt(index)
//            for (updatedItems in itemsArrayList!!) {
//              cartUpdatedItems += updatedItems + ", "
//            }
//            cartUpdatedItems = cartUpdatedItems.substring(0, cartUpdatedItems.length - 2)
//            val displayString = cartUpdatedItems + " + " + (it.size - itemsArrayList!!.size) + " more"
//            var cartUpdatedItems1 = ""
//            if (mp_items_name_tv.paint.measureText(displayString) > 2 * (mp_items_name_tv.measuredWidth)) {
//              val index1 = itemsArrayList!!.size - 1
//              itemsArrayList!!.removeAt(index1)
//              for (updatedItems1 in itemsArrayList!!) {
//                cartUpdatedItems1 += updatedItems1 + ", "
//              }
//              cartUpdatedItems1 = cartUpdatedItems1.substring(0, cartUpdatedItems1.length - 2)
//              val displayString1 = cartUpdatedItems1 + " + " + (it.size - itemsArrayList!!.size) + " more"
//              var cartLatestItems = ""
//              if (mp_items_name_tv.paint.measureText(displayString1) > 2 * (mp_items_name_tv.measuredWidth)) {
//                val latestIndex = itemsArrayList!!.size - 1
//                itemsArrayList!!.removeAt(latestIndex)
//                for (latestItems in itemsArrayList!!) {
//                  cartLatestItems += latestItems + ", "
//                }
//                cartLatestItems = cartLatestItems.substring(0, cartLatestItems.length - 2)
//                val displayString2 = cartLatestItems + " + " + (it.size - itemsArrayList!!.size) + " more"
//                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//                  if (itemsArrayList!!.size == 1) {
//                    val singleFeature = displayString2.replace(",", "")
//                    mp_items_name_tv.text = singleFeature.replace(" ", "\u00A0")
//                  } else {
//                    mp_items_name_tv.text = displayString2.replace(" ", "\u00A0")
//                  }
//
//                }
//              } else {
//                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//                  if (itemsArrayList!!.size == 1) {
//                    val singleFeature1 = displayString1.replace(",", "")
//                    mp_items_name_tv.text = singleFeature1.replace(" ", "\u00A0")
//
//                  } else {
//                    mp_items_name_tv.text = displayString1.replace(" ", "\u00A0")
//                  }
//                }
//              }
//            } else {
//              if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//                if (itemsArrayList!!.size == 1) {
//                  val singleFeature2 = displayString.replace(",", "")
//                  mp_items_name_tv.text = singleFeature2.replace(" ", "\u00A0")
//
//                } else {
//                  mp_items_name_tv.text = displayString.replace(" ", "\u00A0")
//                }
//              }
//            }
//          } else {
//            if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//              if (itemsArrayList!!.size == 1) {
//                val singleFeature3 = cartItems.replace(",", "")
//                mp_items_name_tv.text = singleFeature3.replace(" ", "\u00A0")
//
//              } else {
//                mp_items_name_tv.text = cartItems.replace(" ", "\u00A0")
//              }
//            }
//          }
//
//        }
//        val itemDesc = prefs.getStoreAddedPackageDesc()
//        packsArrayList?.clear()
//        it.forEach { it ->
//          if (it.item_type.equals("bundles"))
//            packsArrayList?.add(it.item_name.toString())
//        }
//        var packCartItems = ""
//        if (packsArrayList != null && packsArrayList!!.size > 0) {
//          for (packs in packsArrayList!!) {
//            packCartItems += packs
//          }
//          if (packsArrayList!!.size == 1) {
//            if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains("features").not()) {
//              mp_items_name_tv.text = itemDesc
//            }
//          } else if (packsArrayList!!.size >= 2) {
//            if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains("features").not()) {
//              mp_items_name_tv.text = "Get a pack that serves the need of your growing business."
//            }
//          }
//        }
//
//        if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
//          if (packsArrayList != null && packsArrayList!!.size > 0 && itemsArrayList != null && itemsArrayList!!.size > 0) {
//            var totalPackItems = ""
//            for (totalPacks in packsArrayList!!) {
//              totalPackItems += totalPacks + " Pack" + ", "
//            }
//            var totalFeatureItems = ""
//            for (totalFeatures in itemsArrayList!!) {
//              totalFeatureItems += totalFeatures + ", "
//            }
//            totalFeatureItems = totalFeatureItems.substring(0, totalFeatureItems.length - 2)
//            mp_items_name_tv.text = totalPackItems + totalFeatureItems
//          }
//        }
//
//        Constants.CART_VALUE = badgeNumber
//      } else {
//        badgeNumber = 0
//        badge.visibility = View.GONE
//        mp_view_cart_rl.visibility = View.GONE
//      }
//      //refresh FeatureDeals adaptor when cart is updated
//      if (viewModel.allFeatureDealsResult.value != null) {
//        val list = viewModel.allFeatureDealsResult.value!!
//        if (list.size > 0) {
//          updateFeatureDealsViewPager(list, it)
//        }
//      }
//
//      /*if (viewModel.allBundleResult.value != null){
//          var list = viewModel.allBundleResult.value!!
//          if (list.size > 0){
//              for (item in list){
//                  it.forEach {
//                      if(it.item_id.equals(item.bundle_id)){
//                          packageInCartStatus = true
//                      }
//                  }
//              }
//          }
//      }*/
//
//      /*if (viewModel.allBundleResult.value != null) {
//
//
//          var list = viewModel.allBundleResult.value!!
//          if (list.size > 0) {
//              val listItem = arrayListOf<Bundles>()
//              for (item in list) {
//                  val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
//                  listItem.add(Bundles(
//                          item.bundle_id,
//                          temp,
//                          item.min_purchase_months,
//                          item.name,
//                          item.overall_discount_percent,
//                          PrimaryImage(item.primary_image),
//                          item.target_business_usecase,
//                          Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
//                          null,item.desc
//                  ))
//              }
//              if (list.size > 0) {
////                        updatePackageViewPager(listItem)
//                  packageViewPagerAdapter.addupdates(listItem)
//                  packageViewPagerAdapter.notifyDataSetChanged()
//              }
//          }
//      }*/
//    })
//
//    viewModel.cartResultBack().observe(this, androidx.lifecycle.Observer {
//      UserSessionManager(requireActivity()).storeIntDetails(Key_Preferences.KEY_FP_CART_COUNT, it.size ?: 0)
//      if (it != null && it.size > 0) {
////                packageInCartStatus = false
//        mp_view_cart_rl.visibility = View.VISIBLE
//        badge.visibility = View.VISIBLE
//        badgeNumber = it.size
//        badge.setText(badgeNumber.toString())
//        itemTypeArrayList?.clear()
//        it.forEach {
//          itemTypeArrayList?.add(it.item_type.toString())
//        }
//        if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
//          if (badgeNumber == 1) {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " item waiting in cart"
//          } else {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " items waiting in cart"
//          }
//        } else if (itemTypeArrayList!!.contains("features")) {
//          if (badgeNumber == 1) {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " item waiting in cart"
//          } else {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " items waiting in cart"
//          }
//        } else if (itemTypeArrayList!!.contains("bundles")) {
//          if (badgeNumber == 1) {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " pack waiting in cart"
//          } else {
//            mp_no_of_cart_items_tv.text = badgeNumber.toString() + " packs waiting in cart"
//          }
//
//        }
//        itemsArrayList?.clear()
//        it.forEach {
//          if (it.item_type.equals("features")) {
//            itemsArrayList?.add(it.item_name.toString())
//          }
//        }
//
//        var cartItems = ""
//        if (itemsArrayList != null && itemsArrayList!!.size > 0) {
//          for (items in itemsArrayList!!) {
//            cartItems += items + ", "
//          }
//          cartItems = cartItems.substring(0, cartItems.length - 2)
//          var cartUpdatedItems = ""
//          if (mp_items_name_tv.paint.measureText(cartItems) > 2 * (mp_items_name_tv.measuredWidth)) {
//            val index = itemsArrayList!!.size - 1
//            itemsArrayList!!.removeAt(index)
//            for (updatedItems in itemsArrayList!!) {
//              cartUpdatedItems += updatedItems + ", "
//            }
//            cartUpdatedItems = cartUpdatedItems.substring(0, cartUpdatedItems.length - 2)
//            val displayString = cartUpdatedItems + " +" + (it.size - itemsArrayList!!.size) + " more"
//            var cartUpdatedItems1 = ""
//            if (mp_items_name_tv.paint.measureText(displayString) > 2 * (mp_items_name_tv.measuredWidth)) {
//              val index1 = itemsArrayList!!.size - 1
//              itemsArrayList!!.removeAt(index1)
//              for (updatedItems1 in itemsArrayList!!) {
//                cartUpdatedItems1 += updatedItems1 + ", "
//              }
//              cartUpdatedItems1 = cartUpdatedItems1.substring(0, cartUpdatedItems1.length - 2)
//              val displayString1 = cartUpdatedItems1 + " +" + (it.size - itemsArrayList!!.size) + " more"
//              var cartLatestItems = ""
//              if (mp_items_name_tv.paint.measureText(displayString1) > 2 * (mp_items_name_tv.measuredWidth)) {
//                val latestIndex = itemsArrayList!!.size - 1
//                itemsArrayList!!.removeAt(latestIndex)
//                for (latestItems in itemsArrayList!!) {
//                  cartLatestItems += latestItems + ", "
//                }
//                cartLatestItems = cartLatestItems.substring(0, cartLatestItems.length - 2)
//                val displayString2 = cartLatestItems + " +" + (it.size - itemsArrayList!!.size) + " more"
//                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//                  if (itemsArrayList!!.size == 1) {
//                    val singleFeature = displayString2.replace(",", "")
//                    mp_items_name_tv.text = singleFeature.replace(" ", "\u00A0")
//                  } else {
//                    mp_items_name_tv.text = displayString2.replace(" ", "\u00A0")
//                  }
//                }
//              } else {
//                if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//                  if (itemsArrayList!!.size == 1) {
//                    val singleFeature1 = displayString1.replace(",", "")
//                    mp_items_name_tv.text = singleFeature1.replace(" ", "\u00A0")
//
//                  } else {
//                    mp_items_name_tv.text = displayString1.replace(" ", "\u00A0")
//                  }
//                }
//              }
//            } else {
//              if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//                if (itemsArrayList!!.size == 1) {
//                  val singleFeature2 = displayString.replace(",", "")
//                  mp_items_name_tv.text = singleFeature2.replace(" ", "\u00A0")
//
//                } else {
//                  mp_items_name_tv.text = displayString.replace(" ", "\u00A0")
//                }
//              }
//            }
//          } else {
//            if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles").not()) {
//              if (itemsArrayList!!.size == 1) {
//                val singleFeature3 = cartItems.replace(",", "")
//                mp_items_name_tv.text = singleFeature3.replace(" ", "\u00A0")
//
//              } else {
//                mp_items_name_tv.text = cartItems.replace(" ", "\u00A0")
//              }
//            }
//          }
//
//
//        }
//        val itemDesc = prefs.getStoreAddedPackageDesc()
//        packsArrayList?.clear()
//        it.forEach { it ->
//          if (it.item_type.equals("bundles"))
//            packsArrayList?.add(it.item_name.toString())
//        }
//        var packCartItems = ""
//        if (packsArrayList != null && packsArrayList!!.size > 0) {
//          for (packs in packsArrayList!!) {
//            packCartItems += packs
//          }
//          if (packsArrayList!!.size == 1) {
//            if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains("features").not()) {
//              mp_items_name_tv.text = itemDesc
//            }
//          } else if (packsArrayList!!.size >= 2) {
//            if (itemTypeArrayList!!.contains("bundles") && itemTypeArrayList!!.contains("features").not()) {
//              mp_items_name_tv.text = "Get a pack that serves the need of your growing business."
//            }
//          }
//        }
//
//        if (itemTypeArrayList!!.contains("features") && itemTypeArrayList!!.contains("bundles")) {
//          if (packsArrayList != null && packsArrayList!!.size > 0 && itemsArrayList != null && itemsArrayList!!.size > 0) {
//            var totalPackItems = ""
//            for (totalPacks in packsArrayList!!) {
//              totalPackItems += totalPacks + " Pack" + ", "
//            }
//            var totalFeatureItems = ""
//            for (totalFeatures in itemsArrayList!!) {
//              totalFeatureItems += totalFeatures + ", "
//            }
//            totalFeatureItems = totalFeatureItems.substring(0, totalFeatureItems.length - 2)
//            mp_items_name_tv.text = totalPackItems + totalFeatureItems
//          }
//        }
//        Constants.CART_VALUE = badgeNumber
//      } else {
//        badgeNumber = 0
//        badge.visibility = View.GONE
//        mp_view_cart_rl.visibility = View.GONE
//      }
//      //refresh FeatureDeals adaptor when cart is updated
//      if (viewModel.allFeatureDealsResult.value != null) {
//        val list = viewModel.allFeatureDealsResult.value!!
//        if (list.size > 0) {
//          updateFeatureDealsViewPager(list, it)
//        }
//      }
//      if (Constants.COMPARE_BACK_VALUE == 1) {
//        Constants.COMPARE_BACK_VALUE = 0
//        if (viewModel.allBundleResult.value != null) {
//
//
//          var list = viewModel.allBundleResult.value!!
//          if (list.size > 0) {
//            val listItem = arrayListOf<Bundles>()
//            for (item in list) {
//              val temp = Gson().fromJson<List<IncludedFeature>>(
//                item.included_features,
//                object : TypeToken<List<IncludedFeature>>() {}.type
//              )
//              listItem.add(
//                Bundles(
//                  item.bundle_id,
//                  temp,
//                  item.min_purchase_months,
//                  item.name,
//                  item.overall_discount_percent,
//                  PrimaryImage(item.primary_image),
//                  item.target_business_usecase,
//                  Gson().fromJson<List<String>>(
//                    item.exclusive_to_categories,
//                    object : TypeToken<List<String>>() {}.type
//                  ),
//                  null, item.desc
//                )
//              )
//            }
//            if (list.size > 0) {
////                        updatePackageViewPager(listItem)
//              packageViewPagerAdapter.addupdates(listItem)
//              packageViewPagerAdapter.notifyDataSetChanged()
//            }
//          }
//        }
//        viewModel.getCartItemsBack()
//      }
//    })
//
//    viewModel.getYoutubeVideoDetails().observe(this, androidx.lifecycle.Observer {
//      Log.e("getYoutubeVideoDetails", it.toString())
//      updateVideosViewPager(it)
//    })
//
//    viewModel.getExpertConnectDetails().observe(this, androidx.lifecycle.Observer {
//      Log.e("getYoutubeVideoDetails", it.toString())
//      val expertConnectDetails = it
//      if (it.is_online) {
//        prefs.storeExpertContact(it.contact_number)
//        callnow_layout.visibility = View.VISIBLE
//        callnow_image.visibility = View.VISIBLE
//        callnow_title.setText(it.line1)
//        callnow_desc.setText(it.line2)
//        call_shedule_layout.visibility = View.GONE
//        callnow_button.setOnClickListener {
//          WebEngageController.trackEvent(
//            ADDONS_MARKETPLACE_EXPERT_SPEAK,
//            CLICK,
//            NO_EVENT_VALUE
//          )
//          val callIntent = Intent(Intent.ACTION_DIAL)
//          callIntent.data = Uri.parse("tel:" + expertConnectDetails.contact_number)
//          startActivity(Intent.createChooser(callIntent, "Call by:"))
//        }
//        mp_talk_expert_tv.setOnClickListener {
//          WebEngageController.trackEvent(
//            ADDONS_MARKETPLACE_WAITING_CART_EXPERT_CALL_CLICKED,
//            EVENT_LABEL_ADDONS_MARKETPLACE_WAITING_CART_EXPERT_CALL_CLICKED,
//            NO_EVENT_VALUE
//          )
//          val callExpertIntent = Intent(Intent.ACTION_DIAL)
//          callExpertIntent.data = Uri.parse("tel:" + expertConnectDetails.contact_number)
//          startActivity(Intent.createChooser(callExpertIntent, "Call by:"))
//        }
//      } else {
//        callnow_layout.visibility = View.GONE
//        callnow_image.visibility = View.GONE
//        call_shedule_layout.visibility = View.VISIBLE
//        call_shedule_title.setText(it.line1)
//        call_shedule_desc.setText(it.line2)
//        if (it.offline_message != null) {
//          val spannableString = SpannableString(it.offline_message)
//          spannableString.setSpan(
//            ForegroundColorSpan(resources.getColor(R.color.navy_blue)),
//            0,
//            18,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//          )
//          call_schedule_timinig.setText(spannableString)
//        }
//      }
//    })
//
//    viewModel.promoBannerAndMarketOfferResult().observe(this, androidx.lifecycle.Observer {
//      if (it.size > 0) {
//        if (shimmer_view_banner.isShimmerStarted) {
//          shimmer_view_banner.stopShimmer()
//          shimmer_view_banner.visibility = View.GONE
//        }
////                updateBannerViewPager(it)
//        banner_layout.visibility = View.VISIBLE
//      } else {
//        if (shimmer_view_banner.isShimmerStarted) {
//          shimmer_view_banner.stopShimmer()
//          shimmer_view_banner.visibility = View.GONE
//        }
//        banner_layout.visibility = View.GONE
//      }
//
//    })
//
//
//    viewModel.getPromoBanners().observe(this, androidx.lifecycle.Observer {
//      Log.e("getPromoBanners", it.toString())
//      if (it.size > 0) {
//        if (shimmer_view_banner.isShimmerStarted) {
//          shimmer_view_banner.stopShimmer()
//          shimmer_view_banner.visibility = View.GONE
//        }
////                checkBannerDetails(it as ArrayList<PromoBanners>)F
////                checkBannerDetailsNew(it as ArrayList<PromoBanners>)
//        updateBannerViewPager(it)
//        banner_layout.visibility = View.VISIBLE
//      } else {
//        if (shimmer_view_banner.isShimmerStarted) {
//          shimmer_view_banner.stopShimmer()
//          shimmer_view_banner.visibility = View.GONE
//        }
//        banner_layout.visibility = View.GONE
//      }
//    })
//
//    viewModel.getPartnerZone().observe(this, androidx.lifecycle.Observer {
//      Log.e("getPartnerZone", it.toString())
//      if (it.size > 0) {
//        updatePartnerViewPager(it)
//        partner_layout.visibility = View.VISIBLE
//      } else {
//        partner_layout.visibility = View.GONE
//      }
//    })
//
//    viewModel.getFeedBackLink().observe(this, androidx.lifecycle.Observer {
//      Log.e("getFeedBackLink", it.toString())
//      feedBackLink = it
//    })
////
////        viewModel.getBundleExxists().observe(this,androidx.lifecycle.Observer{
////            Log.d("getBundleExxists", it.toString())
////        })
//  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    var intent:Intent? = null
    if(actionType == RecyclerViewActionType.MARKETPLACE_PROMO_BANNER_CLICK.ordinal) {
      intent = Intent(this, MarketPlaceOffersActivity::class.java)
    }else if(actionType == RecyclerViewActionType.PACKS_CLICK.ordinal) {
      intent = Intent(this, PacksActivity::class.java)
    }else if(actionType == RecyclerViewActionType.TOP_FEATURES_CLICK.ordinal) {
      intent = Intent(this, FeatureDetailsActivity::class.java)
    }
    startActivity(intent)
  }

}

