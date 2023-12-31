package com.festive.poster.ui.festivePoster

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.festive.poster.R
import com.festive.poster.base.AppBaseBottomSheetFragment
import com.framework.constants.PosterKeys
import com.festive.poster.databinding.BsheetCustomizePosterBinding
import com.festive.poster.utils.MarketPlaceUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.changeColorOfSubstring
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.analytics.SentryController
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.UserProfileDataResult
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.framework.pref.getDomainName
import com.framework.utils.ValidationUtils
import com.framework.webengageconstant.FESTIVAL_POSTER_UPDATE_INFO
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class CustomizePosterSheet : AppBaseBottomSheetFragment<BsheetCustomizePosterBinding, FestivePosterViewModel>() {

  private val TAG = "CustomizePosterSheet"
  private var path: String? = null
  private val RC_IMAGE_PCIKER = 422
  private var sharedViewModel: FestivePosterSharedViewModel? = null
  private var packTag: String? = null
  private var isAlreadyPurchased: Boolean = false
  private var creatorName: String? = null
  private var session: UserSessionManager? = null
  private var imageUrl:String?=null





  companion object {
    val BK_TAG = "BK_TITLE"
    val BK_IS_PURCHASED = "BK_IS_PURCHASED"
    val BK_CREATOR_NAME = "BK_CREATOR_NAME"

    @JvmStatic
    fun newInstance(tag: String, isAlreadyPurchased: Boolean, creatorName: String? = null): CustomizePosterSheet {
      val bundle = Bundle().apply {
        putString(BK_TAG, tag)
        putBoolean(BK_IS_PURCHASED, isAlreadyPurchased)
        putString(BK_CREATOR_NAME, creatorName)
      }
      val fragment = CustomizePosterSheet()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.bsheet_customize_poster
  }

  override fun getViewModelClass(): Class<FestivePosterViewModel> {
    return FestivePosterViewModel::class.java
  }

  override fun onCreateView() {
    session = UserSessionManager(baseActivity)
    packTag = arguments?.getString(BK_TAG)
    isAlreadyPurchased = arguments?.getBoolean(BK_IS_PURCHASED) == true
    creatorName = arguments?.getString(BK_CREATOR_NAME)
    sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
    setupUIColor()
    setUserDetails()
    setOnClickListener(binding?.ivCancel, binding?.uploadSelfie, binding?.tvUpdateInfo, binding?.imgEdit)
  }

  private fun setupUIColor() {
    changeColorOfSubstring(R.string.your_name_star, R.color.colorAccent, "*", binding?.tvYourNameVw!!)
    changeColorOfSubstring(R.string.display_email_id, R.color.colorAccent, "*", binding?.tvDisplayEmailVw!!)
    changeColorOfSubstring(R.string.display_whatsapp_number, R.color.colorAccent, "*", binding?.tvDisplayWhatsappVw!!)
    changeColorOfSubstring(R.string.website_address_aestrick, R.color.colorAccent, "*", binding?.tvWebsiteAddressVw!!)
  }

  private fun setUserDetails() {
    val keys = sharedViewModel?.selectedPoster?.keys

    val name = if (keys?.find { it.name== PosterKeys.user_name }?.custom!=null){
      keys.find { it.name== PosterKeys.user_name }?.custom
    }else{
      session?.userProfileName ?: session?.fpTag
    }

    val website = if (keys?.find { it.name== PosterKeys.business_website }?.custom!=null){
      keys.find { it.name== PosterKeys.business_website }?.custom
    }else{
      getDomainName()
    }

    val whatsapp = if (keys?.find { it.name== PosterKeys.user_contact }?.custom!=null){
      keys.find { it.name== PosterKeys.user_contact }?.custom
    }else{
      session?.userPrimaryMobile ?: session?.fPPrimaryContactNumber
    }

    val email = if (keys?.find { it.name== PosterKeys.business_email }?.custom!=null){
      keys.find { it.name== PosterKeys.business_email }?.custom
    }else{
      session?.userProfileEmail ?: session?.fPEmail
    }
    keys?.find { it.name== PosterKeys.user_image }?.custom?.let { url->
        imageUrl=url
        Glide.with(requireActivity()).load(url).into(binding?.ivUserImg!!)
        showUserImage()
    }

    binding?.etName?.setText(name)
    binding?.etWhatsapp?.setText(whatsapp)
    binding?.etWebsite?.setText(website)
    binding?.etEmail?.setText(email)

  }

  private fun getDomainName(): String? {
    return if(session?.getDomainName(true)?.startsWith("www.") == true) session?.getDomainName(true)
    else "www.${session?.getDomainName(true)}"
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.ivCancel -> dismiss()
      binding?.uploadSelfie -> ImagePicker.with(this).cropSquare().start(RC_IMAGE_PCIKER)
      binding?.tvUpdateInfo -> {
        Log.i(TAG, "path: $path")
        if (validation()) {
          WebEngageController.trackEvent(FESTIVAL_POSTER_UPDATE_INFO, event_value = HashMap())
          submitDetails()
        }
      }
      binding?.imgEdit -> ImagePicker.with(this).cropSquare().start(RC_IMAGE_PCIKER)
    }
  }

  private fun submitDetails() {

    uploadImageAndSaveData()
  }

  private fun uploadImageAndSaveData() {
    showProgress()
    if (imageUrl==null){
      val imgFile = File(path)
      viewModel?.uploadProfileImage(
        clientId2, session?.userProfileId,imgFile.name,
        imgFile.asRequestBody("image/*".toMediaTypeOrNull()))?.observe(viewLifecycleOwner) {

        if (it.isSuccess()) {
          Log.i(TAG, "uploadImage: success ${Gson().toJson(it)}")
          imageUrl = it.stringResponse
          val profileData = UserProfileDataResult.getMerchantProfileDetails()
          if (profileData != null) {
            profileData.ImageUrl = imageUrl
            UserProfileDataResult.saveMerchantProfileDetails(profileData)
          }
          saveKeyValue()
        } else {
          hideProgress()
        }
      }
    }else{
      saveKeyValue()
    }


  }

  private fun saveKeyValue() {

    val map = hashMapOf(
      PosterKeys.user_name to binding?.etName?.text.toString(),
      PosterKeys.business_website to binding?.etWebsite?.text.toString(),
      PosterKeys.business_email to binding?.etEmail?.text.toString(),
      PosterKeys.business_name to session?.fPName,
      PosterKeys.user_contact to binding?.etWhatsapp?.text.toString(),
      PosterKeys.user_image to imageUrl)

    val templateIds = ArrayList<String>()
    if (sharedViewModel?.selectedPoster==null){
      hideProgress()
      return
    }
    if (isAlreadyPurchased){
      templateIds.add(sharedViewModel?.selectedPoster?.id!!)
    }else{
      sharedViewModel?.selectedPosterPack?.posterList?.forEach {
        it.id?.let { it1 -> templateIds.add(it1) }
      }
    }



    var countApiCallSuccess =0
    var totalApiCall=0

    viewModel?.saveKeyValue(session?.fPID,session?.fpTag,templateIds,map)?.observe(viewLifecycleOwner) {
      hideProgress()

      totalApiCall++
      if (it.isSuccess()) {

        countApiCallSuccess++
        if (countApiCallSuccess == templateIds.size) {
          Log.i(TAG, "saveKeyValue: success")
          showShortToast(getString(R.string.poster_updated_successfully))
          navigateToNextFragment()
        }

      }
      if (totalApiCall == templateIds.size && countApiCallSuccess != templateIds.size) {
        showLongToast("Unable to update info")
      }

      Log.i(TAG, "saveKeyValue: totalCalls $totalApiCall successcall $countApiCallSuccess")

    }
  }

  private fun navigateToNextFragment() {
    sharedViewModel?.keyValueSaved?.value=null
    if (isAlreadyPurchased||creatorName== PosterListFragment::class.java.name){
      dismiss()
    }else{
      PosterPaymentSheetV2.newInstance().show(parentFragmentManager, PosterPaymentSheetV2::class.java.name)
    }
    dismiss()
  }



  private fun validation(): Boolean {
    if (path.isNullOrEmpty()&&imageUrl.isNullOrEmpty()) {
      showLongToast(getString(R.string.please_upload_image))
      return false
    }
    if (binding?.etName?.text?.toString().isNullOrEmpty()) {
      showLongToast(getString(R.string.please_enter_name))
      return false
    }
    if (binding?.etEmail?.text?.toString().isNullOrEmpty()) {
      showLongToast(getString(R.string.please_enter_email))
      return false
    }
    if (ValidationUtils.isEmailValid(binding?.etEmail?.text.toString()).not()) {
      showLongToast(getString(R.string.please_enter_valid_email))
      return false
    }
    if (binding?.etWhatsapp?.text?.toString().isNullOrEmpty()) {
      showLongToast(getString(R.string.please_enter_whatsapp))
      return false
    }
    if (ValidationUtils.isMobileNumberValid(binding?.etWhatsapp?.text.toString()).not()) {
      showLongToast(getString(R.string.please_enter_valid_whatsapp))
      return false
    }

    if (binding?.etWhatsapp?.text?.toString().isNullOrEmpty()) {
      showLongToast(getString(R.string.please_enter_whatsapp))
      return false
    }
    return true
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK && requestCode == RC_IMAGE_PCIKER) {
      openCropFragment(data)
    }
  }

  private fun openCropFragment(data: Intent?) {
    val uri = data?.data!!
    path = getTempFile(requireContext(), uri)?.path
    imageUrl=null
    binding?.ivUserImg?.setImageURI(uri)
    showUserImage()

  }

  private fun showUserImage() {
    binding?.layoutImage?.visible()
    binding?.layoutNoImage?.gone()

  }

  private fun getTempFile(context: Context, uri: Uri): File? {
    try {
      val destination = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
      val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
      val fileDescriptor = parcelFileDescriptor?.fileDescriptor ?: return null
      val src = FileInputStream(fileDescriptor).channel
      val dst = FileOutputStream(destination).channel
      dst.transferFrom(src, 0, src.size())
      src.close()
      dst.close()
      return destination
    } catch (ex: IOException) {
      SentryController.captureException(ex)

      ex.printStackTrace()
    }
    return null
  }
}