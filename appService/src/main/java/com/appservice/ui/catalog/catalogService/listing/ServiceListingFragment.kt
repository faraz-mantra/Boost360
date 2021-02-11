package com.appservice.ui.catalog.catalogService.listing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentServiceListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.CatalogServiceContainerActivity
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.ui.model.*
import com.appservice.viewmodel.ServiceViewModel
import com.framework.extensions.observeOnce
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_service_detail.*
import kotlinx.android.synthetic.main.recycler_item_service_timing.*
import java.util.*
import kotlin.collections.ArrayList

class ServiceListingFragment : AppBaseFragment<FragmentServiceListingBinding, ServiceViewModel>(), RecyclerItemClickListener {

  private lateinit var searchView: SearchView
  private val list: ArrayList<ItemsItem> = arrayListOf()
  private val copylist: ArrayList<ItemsItem> = arrayListOf()
  private var adapterService: AppBaseRecyclerViewAdapter<ItemsItem>? = null
  private var targetMap: Target? = null

  private var isNonPhysicalExperience: Boolean? = null
  private var currencyType: String? = "INR"
  private var fpId: String? = null
  private var fpTag: String? = null
  private var clientId: String? = null
  private var externalSourceId: String? = null
  private var applicationId: String? = null
  private var userProfileId: String? = null

  override fun getLayout(): Int {
    return R.layout.fragment_service_listing
  }


  override fun getViewModelClass(): Class<ServiceViewModel> {
    return ServiceViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    getBundleData()
    setServiceListing()
    setOnClickListener(binding?.cbAddService, binding?.serviceListingEmpty?.cbAddService)
  }

  private fun getBundleData() {
    isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
    currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name) ?: "INR"
    fpId = arguments?.getString(IntentConstant.FP_ID.name)
    fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    clientId = arguments?.getString(IntentConstant.CLIENT_ID.name)
    externalSourceId = arguments?.getString(IntentConstant.EXTERNAL_SOURCE_ID.name)
    applicationId = arguments?.getString(IntentConstant.APPLICATION_ID.name)
    userProfileId = arguments?.getString(IntentConstant.USER_PROFILE_ID.name)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_service_listing, menu)
//        val searchItem = menu.findItem(R.id.action_search)
//        if (searchItem != null) {
//            searchView = searchItem.actionView as SearchView
//            val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
//            searchAutoComplete?.setHintTextColor(getColor(R.color.white_70))
//            searchAutoComplete?.setTextColor(getColor(R.color.white))
//            searchView.setIconifiedByDefault(true)
//            searchView.clearFocus()
//            searchView.queryHint = getString(R.string.search_services)
//            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    return false
//                }
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    startFilter(newText)
//                    return false
//                }
//
//            })
//        }
  }

  private fun startFilter(query: String?) {
    val secondCopyList = ArrayList<ItemsItem>()
    if (query.isNullOrEmpty().not()) {
      viewModel?.getSearchListings(fpTag = UserSession.fpTag, fpId = UserSession.fpId, searchString = query)?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          val data = (it as ServiceSearchListingResponse).result?.data as ArrayList<ItemsItem>
          secondCopyList.removeAll(data)
          secondCopyList.addAll(data)
          adapterService?.notify(secondCopyList)
        } else adapterService?.notify(copylist)
      })

    } else if (copylist.isNullOrEmpty().not()) {
      adapterService?.notify(copylist)
    }
  }

  private fun setServiceListing() {
    showProgress()
    viewModel?.getServiceListing(ServiceListingRequest(arrayListOf(), "ALL", UserSession.fpTag))?.observeOnce(lifecycleOwner = viewLifecycleOwner, Observer {
      hideProgress()
      when (it.isSuccess()) {
        true -> {
          val map = (it as ServiceListingResponse).result?.map { resultItem -> (resultItem as ResultItem).services?.items }
          if (map.isNullOrEmpty().not()) {
            list.clear()
            copylist.clear()
            map?.forEach { list1 -> list1?.forEach { itemsItem -> list.add(itemsItem!!) } }
            copylist.addAll(list)
            (requireActivity() as CatalogServiceContainerActivity).getToolbar()?.title = "${resources.getString(R.string.services)} (${list.size})"
            adapterService = AppBaseRecyclerViewAdapter(activity = baseActivity, list = list, itemClickListener = this@ServiceListingFragment)
            binding?.baseRecyclerView?.adapter = adapterService
            setEmptyView(View.GONE)
          } else setEmptyView(View.VISIBLE)
        }
        else -> setEmptyView(View.VISIBLE)
      }
    })
  }

  private fun setListingView(visibility: Int) {
    binding?.baseRecyclerView?.visibility = visibility
    binding?.llActionButtons?.visibility = visibility
  }

  private fun setEmptyView(visibility: Int) {
    binding?.serviceListingEmpty?.root?.visibility = visibility
    setEmptyView()
    if (visibility == View.VISIBLE) setListingView(View.GONE) else setListingView(View.VISIBLE)
  }

  private fun setEmptyView() {
    val spannableString = SpannableString(resources.getString(R.string.you_don_t_have_any_service_added_to_your_digital_catalog_as_of_yet_watch_video))
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
    binding?.serviceListingEmpty?.ctvAddServiceSubheading?.text = spannableString
    binding?.serviceListingEmpty?.ctvAddServiceSubheading?.movementMethod = LinkMovementMethod.getInstance()
    binding?.serviceListingEmpty?.ctvAddServiceSubheading?.highlightColor = resources.getColor(android.R.color.transparent)

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) {
      startFragmentActivity(FragmentType.SERVICE_DETAIL_VIEW, sendBundleData(item as? ItemsItem), false, isResult = true)
    }
    if (actionType == RecyclerViewActionType.SERVICE_WHATS_APP_SHARE.ordinal) {
      shareProduct = item as ItemsItem
      if (checkStoragePermission())
        share(defaultShareGlobal, 1, shareProduct)
    }
    if (actionType == RecyclerViewActionType.SERVICE_DATA_SHARE_CLICK.ordinal) {
      shareProduct = item as ItemsItem
      if (checkStoragePermission())
        share(defaultShareGlobal, 0, shareProduct)
    }
  }

  private fun sendBundleData(itemsItem: ItemsItem?): Bundle {
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
      showDialog(requireActivity(), "Storage Permission", "To share the image we need storage permission."
      ) { _: DialogInterface?, _: Int -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_CODE) }
      return false
    }
    return true
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      if (!defaultShareGlobal && shareType != 2 && shareProduct != null) {
        share(defaultShareGlobal, shareType, shareProduct)
      }
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

  fun share(defaultShare: Boolean, type: Int, product: ItemsItem?) {
    showProgress("Sharing...")
    if (isOnline(baseActivity)) {
      val shareText = String.format("*%s* %s\n*%s* %s\n\n-------------\n%s\n\nfor more details visit: %s",
          product?.name?.trim { it <= ' ' }, product?.description, "${product?.currency}${product?.discountedPrice}",
          "${product?.currency}${product?.price}", product?.description?.trim { it <= ' ' }, product?.category?.trim { it <= ' ' })
      val target: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
          targetMap = null
          try {
            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val view = View(activity)
            view.draw(Canvas(mutableBitmap))
            val path = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, mutableBitmap, "boost_360", "")
            val uri = Uri.parse(path)
            shareTextService(defaultShare, type, uri, shareText)
          } catch (e: OutOfMemoryError) {
            showShortToast(getString(R.string.image_size_is_large))
          } catch (e: Exception) {
            showShortToast(getString(R.string.image_not_able_to_share))
          }
          hideProgress()
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
          hideProgress()
          targetMap = null
          showShortToast(getString(R.string.failed_to_download_image))
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }
      }
      if (product?.image.isNullOrEmpty().not()) {
        targetMap = target
        Picasso.get().load(product?.image ?: "").into(target)
      } else {
        shareTextService(defaultShare, type, null, shareText)
        hideProgress()
      }
    } else {
      hideProgress()
      showShortToast(getString(R.string.can_not_share_image_offline))
    }
  }

  private fun shareTextService(defaultShare: Boolean, type: Int, uri: Uri?, shareText: String) {
    val share = Intent(Intent.ACTION_SEND)
    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    share.putExtra(Intent.EXTRA_TEXT, shareText)
    uri?.let { share.putExtra(Intent.EXTRA_STREAM, uri) }
    share.type = if (uri != null) "image/*" else "text/plain"
    if (share.resolveActivity(requireActivity().packageManager) != null) {
      if (!defaultShare) {
        if (type == 0) {
          share.setPackage(getString(R.string.facebook_package))
        } else if (type == 1) {
          share.setPackage(getString(R.string.whats_app_package))
        }
      }
      startActivityForResult(Intent.createChooser(share, resources.getString(R.string.share_updates)), 1)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.cbAddService, binding?.serviceListingEmpty?.cbAddService -> {
        startFragmentActivity(FragmentType.SERVICE_DETAIL_VIEW, bundle = sendBundleData(null), isResult = true)
      }
    }
  }

  private fun openSortingBottomSheet() {
    val sortSheet = SortAndFilterBottomSheet()
    sortSheet.onClicked = { }
    sortSheet.show(this@ServiceListingFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  companion object {
    fun newInstance(): ServiceListingFragment {
      return ServiceListingFragment()
    }

    private const val STORAGE_CODE = 120
    var defaultShareGlobal = true
    var shareType = 2
    var shareProduct: ItemsItem? = null
  }

  fun isOnline(context: Activity): Boolean {
    var status = false
    try {
      val connectivityManager = context
          .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      var netInfo = connectivityManager.getNetworkInfo(0)
      if (netInfo != null
          && netInfo.state == NetworkInfo.State.CONNECTED) {
        status = true
      } else {
        netInfo = connectivityManager.getNetworkInfo(1)
        if (netInfo != null
            && netInfo.state == NetworkInfo.State.CONNECTED) status = true
      }
      if (!status) {
        sandbarNoInternet(context)
      }
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
      sandbarNoInternet(context)
      return false
    }
    return status
  }

  private fun sandbarNoInternet(context: Activity) {
    val snackBar = Snackbar.make(context.findViewById(android.R.id.content), context.getString(R.string.noInternet), Snackbar.LENGTH_LONG)
    snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_negative_color))
    snackBar.show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
      if (isRefresh) setServiceListing()
    }
  }
}


