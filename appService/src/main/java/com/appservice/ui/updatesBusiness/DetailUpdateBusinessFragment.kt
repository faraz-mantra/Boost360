package com.appservice.ui.updatesBusiness

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.DetailBusinessFragmentBinding
import com.appservice.holder.BASE_IMAGE_URL
import com.appservice.model.updateBusiness.DeleteBizMessageRequest
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.utils.FileUtils
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.pref.clientId
import com.framework.utils.ContentSharing
import com.framework.utils.setNoDoubleClickListener
import com.framework.webengageconstant.DELETE
import com.framework.webengageconstant.EVENT_NAME_UPDATE_DELETE
import com.framework.webengageconstant.EVENT_NAME_UPDATE_DETAIL_PAGE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject

class DetailUpdateBusinessFragment :
  AppBaseFragment<DetailBusinessFragmentBinding, UpdatesViewModel>() {

  private val STORAGE_CODE = 120

  private var updateFloat: UpdateFloat? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): DetailUpdateBusinessFragment {
      val fragment = DetailUpdateBusinessFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.detail_business_fragment
  }

  override fun getViewModelClass(): Class<UpdatesViewModel> {
    return UpdatesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(EVENT_NAME_UPDATE_DETAIL_PAGE, PAGE_VIEW, sessionLocal.fpTag)
    updateFloat = arguments?.getSerializable(IntentConstant.OBJECT_DATA.name) as? UpdateFloat
    if (updateFloat != null) loadData(updateFloat!!) else baseActivity.finish()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.ic_menu_delete_new, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.id_delete) apiDeleteUpdateBusiness()
    return super.onOptionsItemSelected(item)
  }

  private fun loadData(data: UpdateFloat) {
    getBizMessage(data)
    binding?.mainImageView?.visible()
    if (data.imageUri?.contains("deal.png") == true) {
      binding?.mainImageView?.gone()
    } else if (data.imageUri?.contains("BizImages") == true) {
      val baseName = BASE_IMAGE_URL + data.imageUri
      activity?.glideLoad(binding?.mainImageView!!, baseName, R.drawable.placeholder_image_n)
    } else if (data.imageUri?.contains("/storage/emulated") == true || data.imageUri?.contains("/mnt/sdcard") == true) {
      activity?.let { FileUtils(it).getBitmap(data.imageUri, 720) }
    } else if (data.imageUri.isNullOrEmpty().not()) {
      activity?.glideLoad(
        binding?.mainImageView!!,
        data.imageUri ?: "",
        R.drawable.placeholder_image_n
      )
    } else binding?.mainImageView?.gone()

    binding?.headingTextView?.text = data.message
    binding?.dateTextView?.text = data.getDateValue()
    binding?.shareData?.setNoDoubleClickListener({
      shareUpdate(
        RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal,
        updateFloat
      )
    })
    binding?.shareWhatsapp?.setNoDoubleClickListener({
      shareUpdate(
        RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal,
        updateFloat
      )
    })
    binding?.shareFacebook?.setNoDoubleClickListener({
      shareUpdate(
        RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal,
        updateFloat
      )
    })
  }

  private fun getBizMessage(data: UpdateFloat) {
    viewModel?.getBizWebMessage(data.id, clientId)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        try {
          val json: JSONObject? =
            JSONObject(it.parseStringResponse() ?: "").getJSONObject("targetFloat")
          val keyword: JSONArray? = json?.getJSONArray("_keywords")
          val jsonObj = keyword.toString()
          val indexOfOpenBracket: Int = jsonObj.indexOf("[")
          val indexOfLastBracket: Int = jsonObj.lastIndexOf("]")
          val tags: String = jsonObj.substring(indexOfOpenBracket + 1, indexOfLastBracket)
            .replace(",".toRegex(), "\\|").replace("\"", " ")
          binding?.messagetag?.text = tags.substring(1)
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    })
  }

  private fun shareUpdate(actionType: Int, float: UpdateFloat?) {
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
    val subDomain =
      if (isService(sessionLocal.fP_AppExperienceCode)) "all-services" else "all-products"
    when (actionType) {
      RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal -> {
        ContentSharing.shareUpdates(
          baseActivity,
          float?.message ?: "",
          float?.url,
          sessionLocal.getDomainName() + "/" + subDomain,
          sessionLocal.userPrimaryMobile ?: "",
          true,
          false,
          float?.imageUri
        )
      }
      RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal -> {
        ContentSharing.shareUpdates(
          baseActivity,
          float?.message ?: "",
          float?.url,
          sessionLocal.getDomainName() + "/" + subDomain,
          sessionLocal.userPrimaryMobile ?: "",
          false,
          false,
          float?.imageUri
        )
      }
      RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal -> {
        ContentSharing.shareUpdates(
          baseActivity,
          float?.message ?: "",
          float?.url,
          sessionLocal.getDomainName() + "/" + subDomain,
          sessionLocal.userPrimaryMobile ?: "",
          false,
          true,
          float?.imageUri
        )
      }
    }
  }


  private fun apiDeleteUpdateBusiness() {
    AlertDialog.Builder(baseActivity)
      .setCancelable(false)
      .setTitle(R.string.are_you_sure_want_to_delete)
      .setPositiveButton(R.string.delete_) { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
        showProgress()
        WebEngageController.trackEvent(EVENT_NAME_UPDATE_DELETE, DELETE, sessionLocal.fpTag)
        viewModel?.deleteBizMessageUpdate(
          DeleteBizMessageRequest(
            clientId = clientId,
            dealId = updateFloat?.id
          )
        )?.observeOnce(viewLifecycleOwner, {
          if (it.isSuccess()) {
            showShortToast(getString(R.string.successfully_deleted))
            onBackResult()
          } else showShortToast(getString(R.string.error_delete_failed_try_again))
          hideProgress()
        })
      }.setNegativeButton(R.string.cancel, null)
      .show()
  }

  private fun onBackResult() {
    val intent = Intent()
    intent.putExtra(IntentConstant.IS_UPDATED.name, true)
    baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
    baseActivity.finish()
  }
}