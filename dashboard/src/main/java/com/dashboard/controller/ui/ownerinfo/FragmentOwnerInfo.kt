package com.dashboard.controller.ui.ownerinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentOwnerInfoBinding
import com.dashboard.model.*
import com.dashboard.utils.WebEngageController
import com.dashboard.viewmodel.OwnersViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.pref.UserSessionManager
import com.framework.utils.convertObjToString
import com.framework.webengageconstant.*
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class FragmentOwnerInfo : AppBaseFragment<FragmentOwnerInfoBinding, OwnersViewModel>() {

  private var imageUrl: String? = null
  private var ownersDataResponse: OwnersDataResponse? = null
  private var requestAddOwnersInfo: RequestAddOwnersInfo? = null
  private var ownerImage: File? = null
  private var isEdit: Boolean? = null
  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return R.layout.fragment_owner_info
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): FragmentOwnerInfo {
      val fragment = FragmentOwnerInfo()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getViewModelClass(): Class<OwnersViewModel> {
    return OwnersViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    WebEngageController.trackEvent(OWNER_INFO_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.btnSaveDetails, binding?.clearImage, binding?.btnChangeImage, binding?.imageAddBtn)
    hitGetOwnersInfo()
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSaveDetails -> if (isValid()) uploadProfileImage()
      binding?.imageAddBtn, binding?.btnChangeImage -> openImagePicker()
      binding?.clearImage -> clearImage()
    }
  }

  private fun addUpdateOwnersInfo() {
    if (isEdit == true) updateOwnersInfo() else hitAddOwnersInfo()
  }

  private fun updateOwnersInfo() {
    showProgress()
    WebEngageController.trackEvent(OWNER_INFO_UPDATE, CLICK, NO_EVENT_VALUE)
    val query = "{_id:'" + ownersDataResponse?.data?.firstOrNull()?.id + "'}"
    val actionData = ActionData(binding?.ctfDescription?.text.toString(), binding?.ctfOwnerName?.text.toString(), binding?.ctfDesignation?.text.toString(), Profileimage(url = imageUrl ?: "", description = ""))
    val updateValue = "{\$set :" + convertObjToString(actionData) + "}"
    val request = UpdateOwnersDataRequest(query = query, updateValue = updateValue)
    viewModel?.updateOwnersData(request = request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.isSuccess()) {
        showShortToast(getString(R.string.updated_owners_data))
      } else showShortToast(getString(R.string.error_adding_owners_data))
      hideProgress()
    })
  }

  private fun hitAddOwnersInfo() {
    showProgress()
    WebEngageController.trackEvent(OWNER_INFO_ADD, CLICK, NO_EVENT_VALUE)
    this.requestAddOwnersInfo?.actionData?.profileimage?.url = this.imageUrl ?: ""
    viewModel?.addOwnersData(request = requestAddOwnersInfo!!)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        showShortToast(getString(R.string.owners_data_added_successfully))
      } else showShortToast(getString(R.string.error_adding_owners_data))
      hideProgress()
    })
  }

  private fun uploadProfileImage() {
    if (ownerImage != null) {
      val fileNew = takeIf { ownerImage?.name.isNullOrEmpty().not() }?.let { ownerImage?.name } ?: "owner_${Date()}.jpg"
      val requestProfile = ownerImage?.asRequestBody("image/*".toMediaTypeOrNull())
      val body = requestProfile?.let { MultipartBody.Part.createFormData("file", fileNew, it) }
      showProgress()
      viewModel?.uploadImageProfile(assetFileName = fileNew, file = body)?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) {
          this.imageUrl = it.parseStringResponse() ?: ""
          addUpdateOwnersInfo()
        } else showShortToast(getString(R.string.error_uploading_image))
      })
    } else addUpdateOwnersInfo()
  }

  private fun hitGetOwnersInfo() {
    showProgress(getString(R.string.loading))
    viewModel?.getOwnersData(fpTag = session?.fpTag)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        ownersDataResponse = (it as? OwnersDataResponse) ?: OwnersDataResponse()
        updatePreviousData(ownersDataResponse!!)
      } else {
        showShortToast(getString(R.string.error_getting_owners_info))
      }
    })
  }

  private fun updatePreviousData(ownersDataResponse: OwnersDataResponse) {
    val data = ownersDataResponse.data?.firstOrNull()
    isEdit = data != null
    binding?.ctfDesignation?.setText(data?.title)
    binding?.ctfOwnerName?.setText(data?.name)
    binding?.ctfDescription?.setText(data?.ourStory)
    if (data?.profileimage?.url.isNullOrEmpty().not()) {
      this.imageUrl = data?.profileimage?.url
      binding?.imageAddBtn?.gone()
      binding?.clearImage?.visible()
      binding?.civOwnerImage?.visible()
      binding?.btnChangeImage?.visible()
      binding?.civOwnerImage?.let {
        Picasso.get().load(data?.profileimage?.url ?: "").placeholder(R.drawable.placeholder_image_n).into(it)
      }
    }
    if (isEdit == true) binding?.btnSaveDetails?.text = getString(R.string.update_details)
  }

  private fun goBack() {
    baseActivity.finishAfterTransition()
  }

  private fun clearImage() {
    binding?.imageAddBtn?.visible()
    binding?.clearImage?.gone()
    binding?.btnChangeImage?.gone()
    binding?.civOwnerImage?.gone()
    ownerImage = null
    ownersDataResponse?.data?.firstOrNull()?.profileimage?.url = null
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }


  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity)
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
      .scale(800, 800)
      .enableDebuggingMode(true).build()
  }

  private fun isValid(): Boolean {
    val designation = binding?.ctfDesignation?.text.toString()
    val name = binding?.ctfOwnerName?.text.toString()
    val description = binding?.ctfDescription?.text.toString()
    if (ownersDataResponse?.data?.firstOrNull()?.profileimage?.url == null && ownerImage == null) {
      showLongToast(getString(R.string.add_owner_image))
      return false
    }
    if (name.isEmpty()) {
      showLongToast(getString(R.string.please_enter_name))
      return false
    } else if (designation.isEmpty()) {
      showLongToast(getString(R.string.please_enter_designation))
      return false
    }
    this.requestAddOwnersInfo = RequestAddOwnersInfo(session?.fpTag, ActionData(ourStory = description, name = name, title = designation, Profileimage()))
    return true
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
      if (mPaths.isNotEmpty()) {
        ownerImage = File(mPaths[0])
        binding?.imageAddBtn?.gone()
        binding?.clearImage?.visible()
        binding?.civOwnerImage?.visible()
        binding?.btnChangeImage?.visible()
        activity?.glideLoad(binding?.civOwnerImage, ownerImage?.path)
      }
    }
  }
}