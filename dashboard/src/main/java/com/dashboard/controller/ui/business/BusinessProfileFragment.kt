package com.dashboard.controller.ui.business

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.business.bottomsheet.BusinessCategoryBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessDescriptionBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessFeaturedBottomSheet
import com.dashboard.controller.ui.business.bottomsheet.BusinessNameBottomSheet
import com.dashboard.controller.ui.business.model.BusinessProfileModel
import com.dashboard.controller.ui.business.model.BusinessProfileUpdateRequest
import com.dashboard.controller.ui.business.model.UpdatesItem
import com.dashboard.controller.ui.website_theme.dialog.WebViewDialog
import com.dashboard.databinding.FragmentBusinessProfileBinding
import com.dashboard.utils.WebEngageController
import com.dashboard.utils.startBusinessAddress
import com.dashboard.utils.startBusinessContactInfo
import com.dashboard.utils.startDigitalChannel
import com.dashboard.viewmodel.BusinessProfileViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.models.firestore.FirestoreManager
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_ADDRESS
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_DESCRIPTION
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_LogoUrl
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.framework.views.customViews.CustomImageView
import com.framework.webengageconstant.*
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.getConnectedChannel
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.visibleChannels
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.util.*

class BusinessProfileFragment : AppBaseFragment<FragmentBusinessProfileBinding, BusinessProfileViewModel>() {

  private var businessImage: File? = null
  private var targetMap: Target? = null
  private var businessProfileModel = BusinessProfileModel();
  private var businessProfileUpdateRequest: BusinessProfileUpdateRequest? = null
  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return R.layout.fragment_business_profile
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BusinessProfileFragment {
      val fragment = BusinessProfileFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getViewModelClass(): Class<BusinessProfileViewModel> {
    return BusinessProfileViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(requireContext())
    WebEngageController.trackEvent(BUSINESS_PROFILE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(
      binding?.ctvWhatsThis, binding?.ctvBusinessName, binding?.ctvBusinessCategory, binding?.clBusinessDesc,
      binding?.imageAddBtn, binding?.btnChangeImage, binding?.btnSavePublish, binding?.openBusinessAddress,
      binding?.openBusinessChannels, binding?.openBusinessContact, binding?.openBusinessWebsite,
    )
    setImage(session?.getFPDetails(GET_FP_DETAILS_LogoUrl)!!)
  }

  override fun onResume() {
    super.onResume()
    setData()
  }

  private fun uploadBusinessLogo(businessLogoImage: File) {
    WebEngageController.trackEvent(BUSINESS_LOGO_IMAGE_CLICK, FILE_LINK, NO_EVENT_VALUE)
    showProgress(getString(R.string.uploading_image))
    var s_uuid = UUID.randomUUID().toString()
    s_uuid = s_uuid.replace("-", "")
    viewModel?.putUploadBusinessLogo(
      clientId2, fpId = FirestoreManager.fpId, reqType = "sequential", reqId = s_uuid,
      totalChunks = "1", currentChunkNumber = "1", file = RequestBody.create("image/png".toMediaTypeOrNull(), businessLogoImage.readBytes())
    )?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        session?.storeFPDetails(GET_FP_DETAILS_LogoUrl, it.parseStringResponse()?.replace("\\", "")?.replace("\"", ""))
        showSnackBarPositive(requireActivity(), getString(R.string.business_image_uploaded))
      } else showSnackBarNegative(requireActivity(), it.message)
      hideProgress()
    })
  }

  private fun setData() {
    binding?.btnSavePublish?.isEnabled = false
    binding?.ctvBusinessName?.text = session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME)
    onBusinessNameAddedOrUpdated(session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME).isNullOrEmpty().not())
    binding?.ctvBusinessNameCount?.text = "${session?.fPName?.length}/40"
    binding?.ctvWebsite?.text = "${session?.getDomainName()}"
    binding?.ctvBusinessDesc?.text = session?.getFPDetails(GET_FP_DETAILS_DESCRIPTION)
    onBusinessDescAddedOrUpdated(session?.getFPDetails(GET_FP_DETAILS_DESCRIPTION).isNullOrEmpty().not())
    binding?.ctvBusinessAddress?.text = session?.getFPDetails(GET_FP_DETAILS_ADDRESS)
    if (session?.getFPDetails(GET_FP_DETAILS_ADDRESS).isNullOrEmpty()) {
      binding?.containerBusinessAddress?.gone()
    } else {
      binding?.containerBusinessAddress?.visible()
    }
    var str = ""
    if (session?.userPrimaryMobile.isNullOrEmpty().not()) str += "• +91 ${session?.userPrimaryMobile} (VMN)"
    if (session?.fPPrimaryContactNumber.isNullOrEmpty().not()) str += "\n• +91 ${session?.fPPrimaryContactNumber}"
    if ((session?.userProfileEmail ?: session?.fPEmail).isNullOrEmpty().not()) str += "\n• ${session?.userProfileEmail ?: session?.fPEmail}"
    str += "\n• ${session?.getDomainName() ?: ""}"
    binding?.ctvBusinessContacts?.text = str.trimMargin()

    setDataToModel()
    setImageGrayScale()
    setConnectedChannels()
  }

  private fun setConnectedChannels() {
    visibleChannels(binding?.containerChannels!!)
    getConnectedChannel().apply {
      if (this.isEmpty()) {
        binding?.ctvConnected?.gone()
        binding?.containerChannels?.gone()
      } else {
        binding?.ctvConnected?.visible()
        binding?.containerChannels?.visible()
      }

      if (this.size == 5) {
        binding?.ctvNotConnected?.gone()
        binding?.containerChannelsDisabled?.gone()
      } else {
        binding?.ctvNotConnected?.visible()
        binding?.containerChannelsDisabled?.apply {
          visible()
          grayScaleDisabledChannels(this)
        }
      }
    }
  }

  private fun grayScaleDisabledChannels(containerChannels: LinearLayout) {
    for (it in containerChannels.children) {
      val customImageView = it as? CustomImageView
      val tag = customImageView?.tag
      if (getConnectedChannel().contains(tag)) {
        customImageView?.gone()
      } else {
        customImageView?.visible()
      }
    }
    if (containerChannels.childCount == 0) binding?.ctvNotConnected?.gone() else binding?.ctvNotConnected?.visible()
  }

  private fun setDataToModel() {
    val businessDesc = binding?.ctvBusinessDesc?.text.toString()
    val businessName = binding?.ctvBusinessName?.text.toString()
    businessProfileModel.businessDesc = businessDesc
    businessProfileModel.businessName = businessName

  }

  private fun setImageGrayScale() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(matrix)
    binding?.civFbPageDisabled?.colorFilter = filter
    binding?.civFbShopDisabled?.colorFilter = filter
    binding?.civWhatsappBusinessDisabled?.colorFilter = filter
    binding?.civTwitterDisabled?.colorFilter = filter
    binding?.civGooogleBusinessDisabled?.colorFilter = filter
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.ctvWhatsThis -> {
        openDialogForInformation()
      }
      binding?.ctvBusinessName -> {
        WebEngageController.trackEvent(EDIT_BUSINESS_NAME_CLICK, CLICK, NO_EVENT_VALUE)
        openBusinessNameDialog()
      }
      binding?.ctvBusinessCategory -> {
        WebEngageController.trackEvent(BUSINESS_CATEGORY_CLICK, CLICK, NO_EVENT_VALUE)

        openBusinessCategoryBottomSheet()
      }
      binding?.clBusinessDesc -> {
        WebEngageController.trackEvent(EDIT_BUSINESS_DESCRIPTION_CLICK, CLICK, NO_EVENT_VALUE)
        showBusinessDescDialog()
      }
      binding?.imageAddBtn, binding?.btnChangeImage -> openImagePicker()
      binding?.imageAddBtn, binding?.btnSavePublish -> if (isValid()) updateFpDetails()
      binding?.openBusinessAddress -> {
        WebEngageController.trackEvent(BUSINESS_ADDRESS_PAGE, CLICK, NO_EVENT_VALUE)
        baseActivity.startBusinessAddress(session)
      }
      binding?.openBusinessChannels -> {
        WebEngageController.trackEvent(CONNECTED_CHANNELS_PAGE_CLICK, CLICK, NO_EVENT_VALUE)
        baseActivity.startDigitalChannel(session!!)
      }
      binding?.openBusinessContact -> {
        WebEngageController.trackEvent(BUSINESS_CONTACTS_PAGE_CLICK, CLICK, NO_EVENT_VALUE)
        baseActivity.startBusinessContactInfo(session)
      }
      binding?.openBusinessWebsite -> {
        WebEngageController.trackEvent(WEB_VIEW_PAGE, CLICK, NO_EVENT_VALUE)
        openWebViewDialog(session?.rootAliasURI!!, session?.fpTag!!)
      }

    }
  }

  private fun openWebViewDialog(url: String, title: String) {
    WebViewDialog().apply {
      setData(url, title)
      show(this@BusinessProfileFragment.parentFragmentManager, WebViewDialog::javaClass.name)
    }
  }

  fun isValid(): Boolean {
    if (businessProfileModel.businessName.isNullOrEmpty() || businessProfileModel.businessName?.length ?: 0 <= 2) {
      showLongToast(getString(R.string.please_enter_valid_business_name))
      return false
    } else if (businessProfileModel.businessDesc.isNullOrEmpty()) {
      showLongToast(getString(R.string.please_enter_valid_business_description))
      return false
    }
    return true
  }

  private fun updateFpDetails() {
    WebEngageController.trackEvent(BUSINESS_PROFILE_UPDATE, CLICK, NO_EVENT_VALUE)
    showProgress()
    val updateItemList = arrayListOf<UpdatesItem>()
    if (session?.getFPDetails(GET_FP_DETAILS_BUSINESS_NAME) != binding?.ctvBusinessName?.text.toString()) {
      updateItemList.add(UpdatesItem(key = "NAME", value = businessProfileModel.businessName))
    }
    if (session?.getFPDetails(GET_FP_DETAILS_DESCRIPTION) != binding?.ctvBusinessDesc?.text.toString()) {
      updateItemList.add(UpdatesItem(key = "DESCRIPTION", value = businessProfileModel.businessDesc))
    }
    businessProfileUpdateRequest = BusinessProfileUpdateRequest(session?.fpTag, clientId2, updateItemList)
    viewModel?.updateBusinessProfile(businessProfileUpdateRequest!!)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        binding?.btnSavePublish?.isEnabled = false
        val response = it?.parseStringResponse()
        if (response?.contains("NAME") == true) {
          onBusinessNameAddedOrUpdated(true)
          showSnackBarPositive(requireActivity(), getString(R.string.business_name_published_successfully))
        }
        if (response?.contains("DESCRIPTION") == true) {
          onBusinessDescAddedOrUpdated(true)
          showSnackBarPositive(requireActivity(), getString(R.string.business_description_published_successfully))
        }
        session?.storeFPDetails(GET_FP_DETAILS_DESCRIPTION, binding?.ctvBusinessDesc?.text.toString())
        session?.storeFPDetails(GET_FP_DETAILS_BUSINESS_NAME, binding?.ctvBusinessName?.text.toString())
      } else {
        showShortToast(baseActivity.getString(R.string.error_updating_business))
      }
      hideProgress()
    })
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@BusinessProfileFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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

  private fun showBusinessDescDialog() {
    val businessDescDialog = BusinessDescriptionBottomSheet()
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.BUSINESS_DETAILS.name, businessProfileModel)
    businessDescDialog.arguments = bundle
    businessDescDialog.onClicked = {
      binding?.btnSavePublish?.isEnabled = true
      binding?.ctvBusinessDesc?.text = it.businessDesc
    }
    businessDescDialog.show(parentFragmentManager, BusinessDescriptionBottomSheet::javaClass.name)
  }

  private fun openBusinessCategoryBottomSheet() {
    val businessCategoryBottomSheet = BusinessCategoryBottomSheet()
    businessCategoryBottomSheet.show(parentFragmentManager, BusinessCategoryBottomSheet::javaClass.name)
  }

  private fun openBusinessNameDialog() {
    val businessNameBottomSheet = BusinessNameBottomSheet()
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.BUSINESS_DETAILS.name, businessProfileModel)
    businessNameBottomSheet.arguments = bundle
    businessNameBottomSheet.onClicked = {
      binding?.btnSavePublish?.isEnabled = true
      binding?.ctvBusinessName?.text = it
      binding?.ctvBusinessNameCount?.text = "${it.length}/40"
    }
    businessNameBottomSheet.show(parentFragmentManager, BusinessNameBottomSheet::javaClass.name)
  }

  private fun openDialogForInformation() {
    val businessFeaturedBottomSheet = BusinessFeaturedBottomSheet()
    businessFeaturedBottomSheet.show(parentFragmentManager, BusinessFeaturedBottomSheet::javaClass.name)
  }

  private fun setImage(imageUri: String) {
    val target: Target = object : Target {
      override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        targetMap = null
        try {
          val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
          bindImage(mutableBitmap)
        } catch (e: OutOfMemoryError) {
        } catch (e: Exception) {
        }
      }

      override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
        binding?.businessImage?.gone()
        binding?.btnChangeImage?.gone()
        binding?.imageAddBtn?.visible()
        businessImage = null
        binding?.ctvWhatsThis?.compoundDrawables?.get(0)?.setTint(getColor(R.color.blue_4A90E2))
        binding?.ctvWhatsThis?.setTextColor(ColorStateList.valueOf(getColor(R.color.blue_4A90E2)))
        targetMap = null
      }

      override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
      }
    }
    if (imageUri.isEmpty().not()) {
      targetMap = target
      Picasso.get().load(imageUri).placeholder(R.drawable.placeholder_image_n).into(target)
      binding?.imageAddBtn?.gone()
      binding?.btnChangeImage?.visible()
      binding?.businessImage?.visible()
    } else {
      binding?.businessImage?.gone()
      binding?.btnChangeImage?.gone()
      binding?.imageAddBtn?.visible()
      businessImage = null
      binding?.ctvWhatsThis?.compoundDrawables?.get(0)?.setTint(getColor(R.color.blue_4A90E2))
      binding?.ctvWhatsThis?.setTextColor(ColorStateList.valueOf(getColor(R.color.blue_4A90E2)))
    }
  }

  private fun bindImage(mutableBitmap: Bitmap?) {
    binding?.businessImage?.setImageBitmap(mutableBitmap)
    binding?.ctvWhatsThis?.compoundDrawables?.get(0)?.setTint(getColor(R.color.white))
    binding?.ctvWhatsThis?.setTextColor(ColorStateList.valueOf(getColor(R.color.white)))
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as? List<String>
      if (mPaths.isNullOrEmpty().not()) {
        this.businessImage = File(mPaths!![0])
        bindImage(businessImage?.getBitmap())
        uploadBusinessLogo(businessImage!!)
      }
    }
  }

  fun File.getBitmap(): Bitmap? {
    return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path), 800, 800)
  }

  private fun onBusinessDescAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    if (instance.getDrScoreData()!!.metricdetail == null) return
    instance.getDrScoreData()!!.metricdetail!!.boolean_add_business_description = isAdded
    instance.updateDocument()
  }

  private fun onBusinessNameAddedOrUpdated(isAdded: Boolean) {
    val instance = FirestoreManager
    if (instance.getDrScoreData()!!.metricdetail == null) return
    instance.getDrScoreData()!!.metricdetail!!.boolean_add_business_name = isAdded
    instance.updateDocument()
  }
}

