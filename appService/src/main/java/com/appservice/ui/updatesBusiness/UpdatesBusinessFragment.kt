package com.appservice.ui.updatesBusiness

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.BusinesUpdateListFragmentBinding
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.model.ItemsItem
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.ContentSharing.Companion.shareUpdates
import com.inventoryorder.model.ordersdetails.OrderItem
import java.util.*

class UpdatesBusinessFragment : AppBaseFragment<BusinesUpdateListFragmentBinding, UpdatesViewModel>(), RecyclerItemClickListener {

  private val STORAGE_CODE = 120

  private var session: UserSessionManager? = null
  private var adapterUpdate: AppBaseRecyclerViewAdapter<UpdateFloat>? = null
  private val listFloat: ArrayList<UpdateFloat> = arrayListOf()

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var offSet: Int = PaginationScrollListener.PAGE_START
  private var isLastPageD = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): UpdatesBusinessFragment {
      val fragment = UpdatesBusinessFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.busines_update_list_fragment
  }

  override fun getViewModelClass(): Class<UpdatesViewModel> {
    return UpdatesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    showProgress()
    scrollPagingListener()
    listUpdateApi(offSet = offSet)
  }

  private fun scrollPagingListener() {
    binding?.rvUpdates?.apply {
      addOnScrollListener(object : PaginationScrollListener(layoutManager as? LinearLayoutManager) {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          super.onScrolled(recyclerView, dx, dy)
          if (dy > 0 && binding?.btnAdd?.visibility == View.VISIBLE) binding?.btnAdd?.hide()
          else if (dy < 0 && binding?.btnAdd?.visibility != View.VISIBLE) binding?.btnAdd?.show()
        }

        override fun loadMoreItems() {
          if (!isLastPageD) {
            isLoadingD = true
            adapterUpdate?.addLoadingFooter(UpdateFloat().getLoaderItem())
            offSet = listFloat.size - 1
            listUpdateApi(offSet = offSet)
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
  }

  private fun listUpdateApi(offSet: Int) {
    hitApi(viewModel?.getMessageUpdates(getRequestUpdate(offSet)), R.string.latest_update_data_not_found)
  }

  override fun onSuccess(it: BaseResponse) {
    super.onSuccess(it)
    removeLoader()
    val data = it as? BusinessUpdateResponse
    if (data?.floats.isNullOrEmpty().not()) {
      listFloat.addAll(data?.floats!!)
      isLastPageD = (listFloat.size == data.totalCount ?: 0)
      if (adapterUpdate == null) {
        binding?.rvUpdates?.apply {
          adapterUpdate = AppBaseRecyclerViewAdapter(baseActivity, listFloat, this@UpdatesBusinessFragment)
          this.adapter = adapterUpdate
        }
      } else adapterUpdate?.notifyDataSetChanged()

    } else showShortToast(getString(R.string.latest_update_data_not_found))
    hideProgress()
  }

  override fun onFailure(it: BaseResponse) {
    super.onFailure(it)
    removeLoader()
  }

  private fun getRequestUpdate(skipBy: Int = 0): HashMap<String?, String?> {
    val map = HashMap<String?, String?>()
    map["clientId"] = clientId
    map["skipBy"] = skipBy.toString()
    map["fpId"] = session?.fPID ?: ""
    return map
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterUpdate?.removeLoadingFooter()
    }
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal, RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal,
      RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal -> shareUpdate(item, actionType)
      RecyclerViewActionType.UPDATE_BUSINESS_CLICK.ordinal -> {
      }
    }
  }

  private fun shareUpdate(item: BaseRecyclerViewItem?, actionType: Int) {
    val float = item as? UpdateFloat ?: return
    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      showDialog(baseActivity, "Storage Permission", "To share service image, we need storage permission.") { _: DialogInterface?, _: Int ->
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_CODE)
      }
      return
    }
    val subDomain = if (isService(session?.fP_AppExperienceCode)) "all-services" else "all-products"
    when (actionType) {
      RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal -> {
        shareUpdates(baseActivity, float.message ?: "", float.url, session?.getDomainName() + "/" + subDomain, session?.userPrimaryMobile ?: "", true, false, float.imageUri)
      }
      RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal -> {
        shareUpdates(baseActivity, float.message ?: "", float.url, session?.getDomainName() + "/" + subDomain, session?.userPrimaryMobile ?: "", false, false, float.imageUri)
      }
      RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal -> {
        shareUpdates(baseActivity, float.message ?: "", float.url, session?.getDomainName() + "/" + subDomain, session?.userPrimaryMobile ?: "", false, true, float.imageUri)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
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