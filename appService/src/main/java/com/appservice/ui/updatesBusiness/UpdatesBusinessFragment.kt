package com.appservice.ui.updatesBusiness

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.BusinesUpdateListFragmentBinding
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.base.BaseResponse
import com.framework.constants.SupportVideoType
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.ContentSharing.Companion.shareUpdates
import com.framework.utils.startPromotionUpdates
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.EVENT_NAME_UPDATE_PAGE
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.UPDATE
import java.util.*

@Deprecated(" use PastUpdatesLisitngFragment")
class UpdatesBusinessFragment : AppBaseFragment<BusinesUpdateListFragmentBinding, UpdatesViewModel>(), RecyclerItemClickListener,AppOnZeroCaseClicked {

  private val STORAGE_CODE = 120

  private var adapterUpdate: AppBaseRecyclerViewAdapter<UpdateFloat>? = null
  private val listFloat: ArrayList<UpdateFloat> = arrayListOf()

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0
  private var offSet: Int = PaginationScrollListener.PAGE_START
  private var isLastPageD = false
  private lateinit var zeroCaseFragment: AppFragmentZeroCase

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
    WebEngageController.trackEvent(EVENT_NAME_UPDATE_PAGE, PAGE_VIEW, sessionLocal.fpTag)
    WebEngageController.trackEvent(UPDATE, PAGE_VIEW, NO_EVENT_VALUE)
    showProgress()
    scrollPagingListener()
    listUpdateApi(offSet = offSet)
    binding?.btnAdd?.setOnClickListener {
      primaryButtonClicked()
    }
    this.zeroCaseFragment = AppRequestZeroCaseBuilder(AppZeroCases.LATEST_NEWS_UPADATES, this, baseActivity).getRequest().build()
    addFragment(containerID = binding?.childContainer?.id, zeroCaseFragment,false)
  }

  private fun nonEmptyView() {
    setHasOptionsMenu(true)
    binding?.mainlayout?.visible()
    binding?.childContainer?.gone()
  }

  private fun emptyView() {
    setHasOptionsMenu(false)
    binding?.mainlayout?.gone()
    binding?.childContainer?.visible()

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
    nonEmptyView()
    hitApi(viewModel?.getMessageUpdates(sessionLocal.getRequestUpdate(offSet)), R.string.latest_update_data_not_found)

  }

  override fun onSuccess(it: BaseResponse) {
    super.onSuccess(it)
    removeLoader()
    val data = it as? BusinessUpdateResponse
    if (data?.floats.isNullOrEmpty().not()) {
      nonEmptyView()
      listFloat.addAll(data?.floats!!)
      isLastPageD = (listFloat.size == data.totalCount ?: 0)
      if (adapterUpdate == null) {
        binding?.rvUpdates?.apply {
          adapterUpdate =
            AppBaseRecyclerViewAdapter(baseActivity, listFloat, this@UpdatesBusinessFragment)
          this.adapter = adapterUpdate
        }
      } else adapterUpdate?.notifyDataSetChanged()

    } else if (listFloat.isEmpty()) emptyView()
    onBusinessUpdateAddedOrUpdated(data?.floats?.size?:0)
    hideProgress()
  }

  override fun onFailure(it: BaseResponse) {
    super.onFailure(it)
    emptyView()
    hideProgress()
    removeLoader()
  }

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      adapterUpdate?.removeLoadingFooter()
    }
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    showSimmer(true)
  }

  override fun hideProgress() {
    showSimmer(false)
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal, RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal,
      RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal -> shareUpdate(item, actionType)
      RecyclerViewActionType.UPDATE_BUSINESS_CLICK.ordinal -> {
        val float = item as? UpdateFloat ?: return
        startUpdateFragmentActivity(
          FragmentType.DETAIL_UPDATE_BUSINESS_FRAGMENT,
          Bundle().apply { putSerializable(IntentConstant.OBJECT_DATA.name, float) },
          isResult = true
        )
      }
    }
  }

  private fun shareUpdate(item: BaseRecyclerViewItem?, actionType: Int) {
    val float = item as? UpdateFloat ?: return
    if (ActivityCompat.checkSelfPermission(
        baseActivity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_DENIED
    ) {
      showDialog(
        baseActivity,
        "Storage Permission",
        "To share service image, we need storage permission."
      ) { _: DialogInterface?, _: Int ->
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_CODE)
      }
      return
    }
    val subDomain = if (isService(sessionLocal.fP_AppExperienceCode)) "all-services" else "all-products"
    when (actionType) {
      RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal -> {
        shareUpdates(
          baseActivity,
          float.message ?: "",
          float.url,
          sessionLocal.getDomainName() + "/" + subDomain,
          sessionLocal.userPrimaryMobile ?: "",
          true,
          false,
          float.imageUri
        )
      }
      RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal -> {
        shareUpdates(
          baseActivity,
          float.message ?: "",
          float.url,
          sessionLocal.getDomainName() + "/" + subDomain,
          sessionLocal.userPrimaryMobile ?: "",
          false,
          false,
          float.imageUri
        )
      }
      RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal -> {
        shareUpdates(
          baseActivity,
          float.message ?: "",
          float.url,
          sessionLocal.getDomainName() + "/" + subDomain,
          sessionLocal.userPrimaryMobile ?: "",
          false,
          true,
          float.imageUri
        )
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
      if (isRefresh) {
        showProgress()
        listFloat.clear()
        offSet = PaginationScrollListener.PAGE_START
        listUpdateApi(offSet = offSet)
      }
    }
  }

  override fun primaryButtonClicked() {
    if (FirebaseRemoteConfigUtil.featureUpdateStudioSelectedUsers(sessionLocal.fpTag)){
      baseActivity.startPromotionUpdates()
    } else
      startUpdateFragmentActivity(FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT, isResult = true)
  }

  override fun secondaryButtonClicked() {
    //showShortToast(getString(R.string.coming_soon))
    startActivity(Intent(baseActivity, Class.forName("com.onboarding.nowfloats.ui.supportVideo.SupportVideoPlayerActivity"))
      .putExtra(com.onboarding.nowfloats.constant.IntentConstant.SUPPORT_VIDEO_TYPE.name, SupportVideoType.LATEST_UPDATES.value))
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {
  }

  open fun onBusinessUpdateAddedOrUpdated(count: Int) {
    val instance = FirestoreManager
    if (instance.getDrScoreData() != null && instance.getDrScoreData()?.metricdetail != null) {
      instance.getDrScoreData()?.metricdetail?.number_updates_posted = count
      instance.updateDocument()
    }
  }

  private fun showSimmer(isSimmer: Boolean) {
    binding?.root?.apply {
      if (isSimmer) {
        binding?.progressSimmer?.parentShimmerLayout?.visible()
        binding?.progressSimmer?.parentShimmerLayout?.startShimmer()
        binding?.rvUpdates?.gone()
      } else {
        binding?.rvUpdates?.visible()
        binding?.progressSimmer?.parentShimmerLayout?.gone()
        binding?.progressSimmer?.parentShimmerLayout?.stopShimmer()
      }
    }
  }
}

fun showDialog(mContext: Context?, title: String?, msg: String?, listener: DialogInterface.OnClickListener) {
  val builder = AlertDialog.Builder(ContextThemeWrapper(mContext!!, R.style.CustomAlertDialogTheme))
  builder.setTitle(title).setMessage(msg).setPositiveButton("Ok") { dialog, which ->
    dialog.dismiss()
    listener.onClick(dialog, which)
  }
  builder.create().show()
}



fun UserSessionManager.getRequestUpdate(skipBy: Int = 0): HashMap<String?, String?> {
  val map = HashMap<String?, String?>()
  map["clientId"] = clientId
  map["skipBy"] = skipBy.toString()
  map["fpId"] = this.fPID ?: ""
  return map
}