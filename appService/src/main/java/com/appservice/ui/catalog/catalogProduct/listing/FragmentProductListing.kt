package com.appservice.ui.catalog.catalogProduct.listing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.base.getSingleProductTaxonomyFromServiceCode
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentProductListingBinding
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.CatalogProductCountResponse
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.model.serviceProduct.service.ItemsItem
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.appservice.viewmodel.ProductViewModel
import com.framework.constants.SupportVideoType
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.ContentSharing.Companion.shareProduct
import com.framework.utils.isHotelProfile
import com.framework.utils.isRestaurantProfile
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.*
import java.util.*


class FragmentProductListing : AppBaseFragment<FragmentProductListingBinding, ProductViewModel>(), RecyclerItemClickListener, AppOnZeroCaseClicked {

  private var product: CatalogProduct? = null
  private val list: ArrayList<CatalogProduct> = arrayListOf()
  private val finalList: ArrayList<CatalogProduct> = arrayListOf()
  private var adapterProduct: AppBaseRecyclerViewAdapter<CatalogProduct>? = null
  private var isNonPhysicalExperience: Boolean? = null
  private var currencyType: String? = "INR"
  private var fpId: String? = null
  private var fpTag: String? = null
  private var externalSourceId: String? = null
  private var applicationId: String? = null
  private var userProfileId: String? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var fragmentZeroCase: AppFragmentZeroCase? = null
  private var isZeroCaseCalled = true

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var skip: Int = PaginationScrollListener.PAGE_START
  private var isLastPageD = false

  override fun getLayout(): Int {
    return R.layout.fragment_product_listing
  }

  override fun getViewModelClass(): Class<ProductViewModel> {
    return ProductViewModel::class.java
  }

  companion object {
    fun newInstance(fpId: String?, fpTag: String?, externalSourceId: String?, applicationId: String?, userProfileId: String?): FragmentProductListing {
      val bundle = Bundle()
      bundle.putString(IntentConstant.CURRENCY_TYPE.name, "INR")
      bundle.putString(IntentConstant.FP_ID.name, fpId)
      bundle.putString(IntentConstant.FP_TAG.name, fpTag)
      bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
      bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name, externalSourceId)
      bundle.putString(IntentConstant.APPLICATION_ID.name, applicationId)
      val productListingFragment = FragmentProductListing()
      productListingFragment.arguments = bundle
      return productListingFragment
    }

    private const val STORAGE_CODE = 120
    var defaultShareGlobal = true
    var shareType = 2
    var shareProduct: ItemsItem? = null
    fun newInstance(): FragmentProductListing {
      return FragmentProductListing()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    getBundleData()
    sessionLocal = UserSessionManager(requireActivity())
    WebEngageController.trackEvent(PRODUCT_CATALOGUE_LIST, PAGE_VIEW, EVENT_VALUE_MANAGE_CONTENT);
    layoutManagerN = LinearLayoutManager(baseActivity)
    layoutManagerN?.let { scrollPagingListener(it) }
    setOnClickListener(binding?.cbAddProduct)
    getProductListing(isFirst = true, skipBy = skip)
    this.fragmentZeroCase = AppRequestZeroCaseBuilder(getZerocaseType(), this, baseActivity).getRequest().build()
    addFragment(containerID = binding?.childContainer?.id, fragmentZeroCase, false)

  }

  fun getZerocaseType(): AppZeroCases {
    if (isHotelProfile(sessionLocal.fP_AppExperienceCode)) {
      return AppZeroCases.ROOMS_LISTING
    } else if (isRestaurantProfile(sessionLocal.fP_AppExperienceCode)) {
      return AppZeroCases.RESTAURANT_SERVICES
    } else {
      return AppZeroCases.PRODUCT
    }
  }


  private fun getBundleData() {
    isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
    currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name) ?: "INR"
    fpId = arguments?.getString(IntentConstant.FP_ID.name)
    fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    externalSourceId = arguments?.getString(IntentConstant.EXTERNAL_SOURCE_ID.name)
    applicationId = arguments?.getString(IntentConstant.APPLICATION_ID.name)
    userProfileId = arguments?.getString(IntentConstant.USER_PROFILE_ID.name)
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.baseRecyclerView?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          adapterProduct?.addLoadingFooter(CatalogProduct().getLoaderItem())
          getProductListing(skipBy = finalList.size)
        }
      }

      override val totalPageCount: Int
        get() = TOTAL_ELEMENTS
      override val isLastPage: Boolean
        get() = isLastPageD
      override val isLoading: Boolean
        get() = isLoadingD
    })
  }

  private fun getProductListing(isFirst: Boolean = false, skipBy: Int? = null) {
    if (isFirst) showProgress()
    viewModel?.getProductListingCount(fpTag, clientId, skipBy)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        val data = it as? CatalogProductCountResponse
        setProductDataItems(data?.Products, data?.TotalCount, isFirst)
      } else if (isFirst) showShortToast(it.message())
      if (isFirst) hideProgress()
    }
  }

  private fun setProductDataItems(resultProduct: ArrayList<CatalogProduct>?, totalCount: Int?, isFirstLoad: Boolean) {
    onServiceAddedOrUpdated(resultProduct?.size ?: 0)
    if (isFirstLoad) {
      finalList.clear()
      if (resultProduct.isNullOrEmpty().not()) {
        setEmptyView(View.GONE)
        setListProduct(resultProduct, totalCount)
      } else setEmptyView(View.VISIBLE)
    } else if (resultProduct.isNullOrEmpty().not()) {
      removeLoader()
      setListProduct(resultProduct, totalCount)
    }
  }

  private fun setListProduct(resultProduct: ArrayList<CatalogProduct>?, totalCount: Int?) {
    finalList.addAll(resultProduct!!)
    skip = finalList.size
    list.clear()
    list.addAll(finalList)
    TOTAL_ELEMENTS = totalCount ?: 0
    isLastPageD = (finalList.size == TOTAL_ELEMENTS)
    setAdapterNotify()

    setToolbarTitle("${sessionLocal.getListingTitle()} ${if (TOTAL_ELEMENTS > 0) "(${TOTAL_ELEMENTS})" else ""}".capitalizeUtil())
  }


  private fun onServiceAddedOrUpdated(count: Int) {
    val instance = FirestoreManager
    instance.getDrScoreData()?.metricdetail?.number_products_added = count
    instance.updateDocument()
  }


  private fun setAdapterNotify() {
    if (adapterProduct == null) {
      adapterProduct = AppBaseRecyclerViewAdapter(baseActivity, list, this@FragmentProductListing)
      binding?.baseRecyclerView?.layoutManager = layoutManagerN
      binding?.baseRecyclerView?.adapter = adapterProduct
      adapterProduct?.runLayoutAnimation(binding?.baseRecyclerView)
    } else adapterProduct?.notifyDataSetChanged()
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterProduct?.removeLoadingFooter()
    }
  }

  private fun setEmptyView(visibility: Int) {
    when (visibility) {
      View.GONE -> {
        isZeroCaseCalled = false
        binding?.mainlayout?.visible()
        binding?.childContainer?.gone()
      }
      View.VISIBLE -> {
        isZeroCaseCalled = true
        binding?.mainlayout?.gone()
        binding?.childContainer?.visible()
      }
    }
    setHasOptionsMenu(true)
    baseActivity.invalidateOptionsMenu()
    if (visibility == View.VISIBLE) setListingView(View.GONE) else setListingView(View.VISIBLE)
  }

  private fun setListingView(visibility: Int) {
    binding?.baseRecyclerView?.visibility = visibility
    binding?.cbAddProduct?.visibility = visibility
  }

  private fun sendBundleData(itemsItem: CatalogProduct?): Bundle {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, itemsItem)
    bundle.putBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name, isNonPhysicalExperience ?: false)
    bundle.putString(IntentConstant.CURRENCY_TYPE.name, currencyType)
    bundle.putString(IntentConstant.FP_ID.name, fpId)
    bundle.putString(IntentConstant.FP_TAG.name, fpTag)
    bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
    bundle.putString(IntentConstant.CLIENT_ID.name, clientId)
    bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name, externalSourceId)
    bundle.putString(IntentConstant.APPLICATION_ID.name, applicationId)
    return bundle
  }

  private fun checkStoragePermission(): Boolean {
    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      showDialog(requireActivity(), getString(R.string.storage_permission), getString(R.string.to_share_the_image_we_need_storage_permission))
      { _: DialogInterface?, _: Int -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_CODE) }
      return false
    }
    return true
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      shareProduct(
        product?.Name, product?.Price.toString(), product?.ProductUrl, sessionLocal.userPrimaryMobile, product?.ImageUri,
        isWhatsApp = false, isService = false, isFb = false, activity = requireActivity()
      )
    }
  }


  fun showDialog(mContext: Context?, title: String?, msg: String?, listener: DialogInterface.OnClickListener) {
    val builder = AlertDialog.Builder(mContext!!)
    builder.setTitle(title).setMessage(msg).setPositiveButton("Ok") { dialog, which ->
      dialog.dismiss()
      listener.onClick(dialog, which)
    }
    builder.create().show()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.cbAddProduct -> addProduct()
    }
  }

  private fun addProduct() {
    WebEngageController.trackEvent(CLICKED_ON_PRODUCTS_CATALOGUE_ADD_NEW, CLICK, NO_EVENT_VALUE);
    startFragmentActivity(FragmentType.PRODUCT_DETAIL_VIEW, bundle = sendBundleData(null), isResult = true)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
      if (isRefresh) {
        this.skip = PaginationScrollListener.PAGE_START
        getProductListing(isFirst = true, skipBy = skip)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    setToolbarTitle("${sessionLocal.getListingTitle()} ${if (TOTAL_ELEMENTS > 0) "(${TOTAL_ELEMENTS})" else ""}".capitalizeUtil())
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    this.product = item as? CatalogProduct
    when (actionType) {
      RecyclerViewActionType.PRODUCT_ITEM_CLICK.ordinal -> {
        WebEngageController.trackEvent(CLICKED_ON_PRODUCTS_CATALOGUE_ITEM, CLICK, NO_EVENT_VALUE);
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, product)
        startFragmentActivity(FragmentType.PRODUCT_DETAIL_VIEW, bundle, clearTop = false, isResult = true)
      }
      RecyclerViewActionType.PRODUCT_DATA_SHARE_CLICK.ordinal -> {
        shareProduct(
          product?.Name, "${product?.getDiscountedPrice()}", product?.ProductUrl, sessionLocal.userPrimaryMobile, product?.ImageUri,
          isWhatsApp = false, isService = false, isFb = false, activity = requireActivity()
        )
      }
      RecyclerViewActionType.PRODUCT_WHATS_APP_SHARE.ordinal -> {
        shareProduct(
          product?.Name, "${product?.getDiscountedPrice()}", product?.ProductUrl, sessionLocal.userPrimaryMobile, product?.ImageUri,
          isWhatsApp = true, isService = false, isFb = false, activity = requireActivity()
        )
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_product_listing, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu) {
    super.onPrepareOptionsMenu(menu)
    menu.findItem(R.id.action_product_configuration).isVisible = isZeroCaseCalled.not()
    menu.findItem(R.id.menu_item_help).isVisible = isZeroCaseCalled
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_product_configuration -> {
        startFragmentActivity(FragmentType.ECOMMERCE_SETTINGS)
        return true
      }
      R.id.menu_item_help -> {
        startActivity(
          Intent(baseActivity, Class.forName("com.onboarding.nowfloats.ui.supportVideo.SupportVideoPlayerActivity"))
            .putExtra(com.onboarding.nowfloats.constant.IntentConstant.SUPPORT_VIDEO_TYPE.name, SupportVideoType.PRODUCT_CATALOGUE.value)
        )
        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun primaryButtonClicked() {
    addProduct()
  }

  override fun secondaryButtonClicked() {
    startFragmentActivity(FragmentType.ECOMMERCE_SETTINGS)
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {
  }
}

fun UserSessionManager.getListingTitle(): String? {
  var fpDetails = getFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB)
  if (fpDetails.isNullOrEmpty()) fpDetails = getSingleProductTaxonomyFromServiceCode(fP_AppExperienceCode)
  return fpDetails
}
