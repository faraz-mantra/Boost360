package com.appservice.ui.catalog.catalogProduct.listing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentProductListingBinding
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.model.ItemsItem
import com.appservice.viewmodel.ProductViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.ContentSharing.Companion.shareProduct
import com.squareup.picasso.Target
import java.util.*

class FragmentProductListing : AppBaseFragment<FragmentProductListingBinding, ProductViewModel>(), RecyclerItemClickListener {
    override fun getLayout(): Int {
        return R.layout.fragment_product_listing
    }

    private var product: CatalogProduct? = null
    private val TAG = "FragmentProductListing"
    private val list: ArrayList<CatalogProduct> = arrayListOf()
    private val finalList: ArrayList<CatalogProduct> = arrayListOf()
    private var adapterProduct: AppBaseRecyclerViewAdapter<CatalogProduct>? = null
    private var targetMap: Target? = null
    private var isNonPhysicalExperience: Boolean? = null
    private var currencyType: String? = "INR"
    private var fpId: String? = null
    private var fpTag: String? = null
    private var externalSourceId: String? = null
    private var applicationId: String? = null
    private var userProfileId: String? = null
    private var layoutManagerN: LinearLayoutManager? = null

    /* Paging */
    private var isLoadingD = false
    private var TOTAL_ELEMENTS = 0
    private var limit: Int = PaginationScrollListener.PAGE_SIZE
    private var isLastPageD = false
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
        layoutManagerN = LinearLayoutManager(baseActivity)
        layoutManagerN?.let { scrollPagingListener(it) }
        getProductListing(isFirst = true, skipBy = limit)
        setOnClickListener(binding?.cbAddService, binding?.productListingEmpty?.cbAddService)
    }


    private fun getBundleData() {
        isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
        currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name) ?: "₹"
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
                    Log.i(TAG, "scroll limit: " + limit)
                    TOTAL_ELEMENTS = finalList.size
                    Log.i(TAG, "scroll total elements: " + TOTAL_ELEMENTS)
                    getProductListing(skipBy = limit)
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
        viewModel?.getProductListing(fpTag, clientId, skipBy)?.observeOnce(viewLifecycleOwner, {
            if (it.isSuccess()) {
                setProductDataItems((it.anyResponse as? ArrayList<CatalogProduct>), isFirst)
            } else if (isFirst) showShortToast(it.message())
            if (isFirst) hideProgress()
        })
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_product_listing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_product_configuration -> {
                startFragmentActivity(FragmentType.ECOMMERCE_SETTINGS)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun setProductDataItems(resultProduct: ArrayList<CatalogProduct>?, isFirstLoad: Boolean) {
        if (isFirstLoad) finalList.clear()
        when {
            resultProduct.isNullOrEmpty().not() -> {
              removeLoader()
              setEmptyView(View.GONE)
              Log.i(TAG, "response size: " + resultProduct?.size)
              finalList.addAll(resultProduct!!)
              limit = finalList.size
              list.clear()
              list.addAll(finalList)
              isLastPageD = (finalList.size == TOTAL_ELEMENTS)
              Log.i(TAG, "islastpage: " + isLastPageD)
              setAdapterNotify()
                var fpDetails = sessionLocal.getFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB)
                if (fpDetails.isNullOrEmpty()) fpDetails = "Products"
                setToolbarTitle("$fpDetails (${limit})")
            //            (parentFragment as FragmentProductHome).setTabTitle("Products (${limit})", 0)

          }
          isFirstLoad -> setEmptyView(View.VISIBLE)
            resultProduct.isNullOrEmpty().not() -> {
              list.clear()
              list.addAll(resultProduct!!)
              setAdapterNotify()
          }
          else -> removeLoader()
        }
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
        binding?.productListingEmpty?.root?.visibility = visibility
        if (visibility == View.VISIBLE) setListingView(View.GONE) else setListingView(View.VISIBLE)
        setEmptyView()
    }

    private fun setListingView(visibility: Int) {
        binding?.baseRecyclerView?.visibility = visibility
        binding?.llActionButtons?.visibility = visibility
    }

    private fun setEmptyView() {
        val spannableString = SpannableString(resources.getString(R.string.yout_dont_have_product_added_to_your_catalog))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showShortToast("video link")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length.minus(11), spannableString.length, 0)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.black_4a4a4a)), spannableString.length.minus(11), spannableString.length, 0)
        spannableString.setSpan(UnderlineSpan(), spannableString.length.minus(11), spannableString.length, 0)
        binding?.productListingEmpty?.ctvAddServiceSubheading?.text = spannableString
        binding?.productListingEmpty?.ctvAddServiceSubheading?.movementMethod = LinkMovementMethod.getInstance()
        binding?.productListingEmpty?.ctvAddServiceSubheading?.highlightColor = resources.getColor(android.R.color.transparent)
    }

    private fun sendBundleData(itemsItem: CatalogProduct?): Bundle {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, itemsItem)
        bundle.putBoolean(
            IntentConstant.NON_PHYSICAL_EXP_CODE.name, isNonPhysicalExperience
                ?: false
        )
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
            showDialog(
                requireActivity(), getString(R.string.storage_permission), getString(R.string.to_share_the_image_we_need_storage_permission)
            ) { _: DialogInterface?, _: Int -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_CODE) }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareProduct(product?.Name, product?.Price.toString(), product?.ProductUrl, sessionLocal.userPrimaryMobile, product?.ImageUri,
            isWhatsApp = false, isService = false, isFb = false, activity = requireActivity())
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
            binding?.cbAddService, binding?.productListingEmpty?.cbAddService -> {
                startFragmentActivity(FragmentType.PRODUCT_DETAIL_VIEW, bundle = sendBundleData(null), isResult = true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
            if (isRefresh) {
                this.limit = PaginationScrollListener.PAGE_SIZE
                getProductListing(isFirst = true, skipBy = limit)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var fpDetails = sessionLocal.getFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB)
        if (fpDetails.isNullOrEmpty()) fpDetails = "Products"
        setToolbarTitle("$fpDetails (${limit})")
    }
    override fun showProgress(title: String?, cancelable: Boolean?) {
        binding?.progress?.visible()
    }

    override fun hideProgress() {
        binding?.progress?.gone()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        this. product = item as? CatalogProduct

        when (actionType) {
            RecyclerViewActionType.PRODUCT_ITEM_CLICK.ordinal -> {
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, product)
                startFragmentActivity(FragmentType.PRODUCT_DETAIL_VIEW, bundle, clearTop = false, isResult = true)
            }
            RecyclerViewActionType.PRODUCT_DATA_SHARE_CLICK.ordinal -> {

                //fb
                shareProduct(product?.Name, product?.Price.toString(), product?.ProductUrl, sessionLocal.userPrimaryMobile, product?.ImageUri,
                    isWhatsApp = false, isService = false, isFb = false, activity = requireActivity())


            }
            RecyclerViewActionType.PRODUCT_WHATS_APP_SHARE.ordinal -> {
                shareProduct(product?.Name, product?.Price.toString(), product?.ProductUrl, sessionLocal.userPrimaryMobile, product?.ImageUri,
                    isWhatsApp = true, isService = false, isFb = true, activity = requireActivity())
            }

        }
    }
}
