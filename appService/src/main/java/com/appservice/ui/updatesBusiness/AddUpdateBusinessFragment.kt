package com.appservice.ui.updatesBusiness

import android.content.*
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.AddUpdateBusinessFragmentBinding
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.utils.getBitmap
import com.appservice.viewmodel.UpdatesViewModel
import com.framework.extensions.*
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.pref.*
import com.framework.pref.Key_Preferences.PREF_KEY_TWITTER_LOGIN
import com.framework.pref.Key_Preferences.PREF_NAME_TWITTER
import com.framework.utils.hasHTMLTags
import com.framework.utils.showKeyBoard
import com.framework.views.customViews.CustomTextView
import com.framework.webengageconstant.*
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.updateChannel.startFragmentChannelActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

const val isFirstTimeSendToSubscriber = "isFirstTimeSendToSubscriber"
const val msgPost = "msg_post"
const val imagePost = "image_post"

class AddUpdateBusinessFragment : AppBaseFragment<AddUpdateBusinessFragmentBinding, UpdatesViewModel>() {

  private val REQ_CODE_SPEECH_INPUT = 122
  private var isUpdate: Boolean = false
  private var toSubscribers = false
  private var fbStatusEnabled = false
  private var twitterSharingEnabled = false
  private var fbPageStatusEnable = false
  private var updateFloat: UpdateFloat? = null
  private var postImagePath: String? = null
  private val postImage: File?
    get() {
      return if (postImagePath.isNullOrEmpty().not()) File(postImagePath) else null
    }

  private val mSharedPreferences: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PREF_NAME_TWITTER, Context.MODE_PRIVATE)
    }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddUpdateBusinessFragment {
      val fragment = AddUpdateBusinessFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.add_update_business_fragment
  }

  override fun getViewModelClass(): Class<UpdatesViewModel> {
    return UpdatesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    updateFloat = arguments?.getSerializable(IntentConstant.OBJECT_DATA.name) as? UpdateFloat
    isUpdate = updateFloat != null && updateFloat!!.id.isNullOrEmpty().not()
    WebEngageController.trackEvent(EVENT_NAME_UPDATE_CREATE, PAGE_VIEW, sessionLocal.fpTag)
    setOnClickListener(
      binding?.btnCamera,
      binding?.btnEditPhoto,
      binding?.btnFpStatus,
      binding?.btnFpPageStatus,
      binding?.btnGoogleVoice,
      binding?.btnRemovePhoto,
      binding?.btnSubscription,
      binding?.btnTwitter
    )
    binding?.edtDesc?.afterTextChanged { str ->
      if (isUpdate.not()) sessionLocal.storeFPDetails(
        msgPost,
        str
      )
    }
    localDataView()
    baseActivity.onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          if (isUpdate.not() && binding?.edtDesc?.text.isNullOrEmpty()
              .not() || postImage != null
          ) onBackPress() else baseActivity.finish()
        }
      })
    initializeSocial()
    binding?.edtDesc?.post { baseActivity.showKeyBoard(binding?.edtDesc) }
  }

  private fun localDataView() {
    if (isUpdate.not()) {
      binding?.edtDesc?.setText(sessionLocal.getFPDetails(msgPost))
      postImagePath = sessionLocal.getFPDetails(imagePost)
      val bitmap = postImage?.getBitmap()
      if (bitmap != null) {
        binding?.imageView?.visible()
        binding?.postImage?.setImageBitmap(bitmap)
      } else binding?.imageView?.invisible()
    } else {
      binding?.edtDesc?.setText(updateFloat?.message ?: "")
      if (updateFloat?.imageUri.isNullOrEmpty().not()) {
        binding?.imageView?.visible()
        activity?.glideLoad(
          binding?.postImage!!,
          updateFloat?.imageUri ?: "",
          R.drawable.placeholder_image_n
        )
      } else binding?.imageView?.invisible()
    }
  }


  private fun initializeSocial() {
    if (sessionLocal.facebookName.isNullOrEmpty()
        .not() && (sessionLocal.getIntDetails("fbStatus") == 1 || sessionLocal.getIntDetails("fbStatus") == 3)
    ) {
      fbStatusEnabled = true
      binding?.btnFpStatus?.setTintColor(
        ContextCompat.getColor(
          baseActivity,
          if (fbStatusEnabled) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
        )
      )
    }
    if (sessionLocal.facebookPage.isNullOrEmpty()
        .not() && sessionLocal.getIntDetails("fbPageStatus") == 1
    ) {
      fbPageStatusEnable = true
      binding?.btnFpPageStatus?.setTintColor(
        ContextCompat.getColor(
          baseActivity,
          if (fbPageStatusEnable) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
        )
      )
    }
    if (mSharedPreferences?.getBoolean(PREF_KEY_TWITTER_LOGIN, false) == true) {
      twitterSharingEnabled = true
      binding?.btnTwitter?.setTintColor(
        ContextCompat.getColor(
          baseActivity,
          if (twitterSharingEnabled) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
        )
      )
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnEditPhoto, binding?.btnCamera -> openImagePicker()

      binding?.btnRemovePhoto -> {
        if (postImagePath.isNullOrEmpty().not()) {
          postImagePath = null
          sessionLocal.storeFPDetails(imagePost, "")
        }
        updateFloat?.imageUri = null
        binding?.postImage?.setImageBitmap(null)
        binding?.imageView?.gone()
      }
      binding?.btnSubscription -> {
        if (!sessionLocal.getBooleanDetails(isFirstTimeSendToSubscriber)) {
          AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
            .setTitle(R.string.send_to_subscribers)
            .setMessage(R.string.unable_to_send_website_updates_to_subscribers)
            .setPositiveButton(R.string.enable) { _: DialogInterface, _: Int ->
              WebEngageController.trackEvent(
                SUBSCRIBER_SHARING_ACTIVATED,
                HAS_CLICKED_SUBSCRIBER_SHARING_ON,
                sessionLocal.fpTag
              )
              sessionLocal.storeBooleanDetails(isFirstTimeSendToSubscriber, true)
              toSubscribers = toSubscribers.not()
              binding?.btnSubscription?.setTintColor(
                ContextCompat.getColor(
                  baseActivity,
                  if (toSubscribers) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
                )
              )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
        } else {
          toSubscribers = toSubscribers.not()
          binding?.btnSubscription?.setTintColor(
            ContextCompat.getColor(
              baseActivity,
              if (toSubscribers) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
            )
          )
        }
      }

      binding?.btnGoogleVoice -> promptSpeechInput()
      binding?.btnFpStatus -> {
        if (sessionLocal.facebookName.isNullOrEmpty()
            .not() && (sessionLocal.getIntDetails("fbStatus") == 1 || sessionLocal.getIntDetails("fbStatus") == 3)
        ) {
          fbStatusEnabled = fbStatusEnabled.not()
          binding?.btnFpStatus?.setTintColor(
            ContextCompat.getColor(
              baseActivity,
              if (fbStatusEnabled) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
            )
          )
          showShortToast(getString(if (fbStatusEnabled) R.string.fb_enabled else R.string.fb_disabled))
        } else baseActivity.startDigitalChannel(sessionLocal)
      }
      binding?.btnFpPageStatus -> {
        if (sessionLocal.facebookPage.isNullOrEmpty()
            .not() && sessionLocal.getIntDetails("fbPageStatus") == 1
        ) {
          fbPageStatusEnable = fbPageStatusEnable.not()
          binding?.btnFpPageStatus?.setTintColor(
            ContextCompat.getColor(
              baseActivity,
              if (fbPageStatusEnable) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
            )
          )
          if (fbPageStatusEnable) WebEngageController.trackEvent(
            FB_PAGE_SHARING_ACTIVATED,
            HAS_CLICKED_FB_PAGE_SHARING_ON,
            sessionLocal.fpTag
          )
          showShortToast(getString(if (fbPageStatusEnable) R.string.facebook_page_enabled else R.string.facebook_page_disabled))
        } else baseActivity.startDigitalChannel(sessionLocal)
      }
      binding?.btnTwitter -> {
        if (mSharedPreferences?.getBoolean(PREF_KEY_TWITTER_LOGIN, false) == true) {
          twitterSharingEnabled = twitterSharingEnabled.not()
          binding?.btnTwitter?.setTintColor(
            ContextCompat.getColor(
              baseActivity,
              if (twitterSharingEnabled) R.color.ButtoncolorAccent else R.color.grey_A1A1A1
            )
          )
          if (twitterSharingEnabled) WebEngageController.trackEvent(
            TWITTER_SHARING_ACTIVATED,
            HAS_CLICKED_TWITTER_SHARING_ON,
            sessionLocal.fpTag
          )
          showShortToast(getString(if (twitterSharingEnabled) R.string.twitter_enabled else R.string.twitter_disabled))
        } else baseActivity.startDigitalChannel(sessionLocal)
      }
    }
  }

  private fun promptSpeechInput() {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(
      RecognizerIntent.EXTRA_LANGUAGE_MODEL,
      RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
    try {
      startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
    } catch (a: ActivityNotFoundException) {
      showShortToast(getString(R.string.speech_not_supported))
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@AddUpdateBusinessFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as? List<String>
      if (mPaths.isNullOrEmpty().not()) {
        postImagePath = mPaths!![0]
        binding?.imageView?.visible()
        postImage?.getBitmap().let { binding?.postImage?.setImageBitmap(it) }
        if (isUpdate.not()) sessionLocal.storeFPDetails(imagePost, postImagePath)
        WebEngageController.trackEvent(ADDED_PHOTO_IN_UPDATE, ADDED_PHOTO, sessionLocal.fpTag)
      }
    } else if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == AppCompatActivity.RESULT_OK) {
      val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) ?: return
      binding?.edtDesc?.append(result[0].toString() + ". ")
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_save_post, menu)
    val item: MenuItem? = menu.findItem(R.id.menu_item_post)
    item?.actionView?.findViewById<CustomTextView>(R.id.button_post)?.setOnClickListener {
      if (isValid() && isUpdate.not()) saveUpdatePost()
    }
  }

  private fun isValid(): Boolean {
    if (binding?.edtDesc?.text.isNullOrEmpty()) {
      showShortToast(getString(R.string.null_string_exception))
      return false
    }
    if (sessionLocal.iSEnterprise == "false" && hasHTMLTags(
        binding?.edtDesc?.text?.toString() ?: ""
      )
    ) {
      showShortToast(getString(R.string.html_tags_exception))
      return false
    }
    return true
  }

  private fun saveUpdatePost() {
    showProgress()
    WebEngageController.trackEvent(POST_AN_UPDATE, EVENT_LABEL_NULL, sessionLocal.fpTag)
    var socialShare = ""
    if (fbStatusEnabled) socialShare += "FACEBOOK."
    if (fbPageStatusEnable) socialShare += "FACEBOOK_PAGE."
    if (twitterSharingEnabled) socialShare += "TWITTER."
    val merchantId = if (sessionLocal.iSEnterprise == "true") null else sessionLocal.fPID
    val parentId = if (sessionLocal.iSEnterprise == "true") sessionLocal.fPParentId else null
    val isPictureMessage = postImage != null
    val request = PostUpdateTaskRequest(
      clientId,
      binding?.edtDesc?.text?.toString(),
      isPictureMessage,
      merchantId,
      parentId,
      toSubscribers,
      socialShare
    )
    viewModel?.putBizMessageUpdate(request)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess() && it.stringResponse.isNullOrEmpty().not()) {
        if (isPictureMessage) {
          val bodyImage = getRequestBizImage(postImage)
          val s_uuid = UUID.randomUUID().toString().replace("-", "")
          viewModel?.putBizImageUpdate(
            clientId, "sequential", s_uuid, 1, 1,
            socialShare, it.stringResponse, toSubscribers, bodyImage
          )?.observeOnce(viewLifecycleOwner, { it1 ->
            if (it1.isSuccess()) {
              successResult()
            } else showShortToast("Image uploading error, please try again.")
            hideProgress()
          })
        } else {
          successResult()
          hideProgress()
        }
      } else {
        showShortToast("Post updating error, please try again.")
        hideProgress()
      }
    })
  }

  private fun successResult() {
    showShortToast("Post update successfully!")
    clearLocalData()
    onBackResult()
  }

  private fun onBackResult() {
    val intent = Intent()
    intent.putExtra(IntentConstant.IS_UPDATED.name, true)
    baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
    baseActivity.finish()
  }

  private fun getRequestBizImage(postImage: File?): RequestBody {
    val responseBody = postImage?.readBytes()?.let { it.toRequestBody("image/png".toMediaTypeOrNull(), 0, it.size) }
    val fileName = takeIf { postImage?.name.isNullOrEmpty().not() }?.let { postImage?.name }
      ?: "biz_message_${Date().time}.png"
    return responseBody!!
  }

  fun onBackPress() {
    AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
      .setCancelable(false)
      .setMessage(R.string.do_you_want_to_save_this_update_as_draft)
      .setPositiveButton(R.string.save) { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
        baseActivity.finish()
      }.setNegativeButton(R.string.delete_) { dialog: DialogInterface, _: Int ->
        clearLocalData()
        dialog.dismiss()
        WebEngageController.trackEvent(POST_AN_UPDATE_CANCLED, CLICKED_CANCEL, sessionLocal.fpTag)
        baseActivity.finish()
      }.show()
  }

  private fun clearLocalData() {
    sessionLocal.storeFPDetails(msgPost, "")
    sessionLocal.storeFPDetails(imagePost, "")
  }
}


fun AppCompatActivity.startDigitalChannel(session: UserSessionManager, channelType: String = "") {
  try {
    WebEngageController.trackEvent(DIGITAL_CHANNEL_PAGE_CLICK, CLICK, TO_BE_ADDED)
    val bundle = Bundle()
    session.setHeader(WA_KEY)
    bundle.putString(UserSessionManager.KEY_FP_ID, session.fPID)
    bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.fpTag)
    bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.fP_AppExperienceCode)
    bundle.putBoolean(Key_Preferences.IS_UPDATE, true)
    bundle.putString(
      Key_Preferences.BUSINESS_NAME,
      session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
    )
    bundle.putString(
      Key_Preferences.CONTACT_NAME,
      session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME)
    )
    var imageUri = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    bundle.putString(Key_Preferences.BUSINESS_IMAGE, imageUri)
    bundle.putString(
      Key_Preferences.BUSINESS_TYPE,
      session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)
    )

    val city = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
    val country = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
    bundle.putString(
      Key_Preferences.LOCATION,
      if (city.isNullOrEmpty().not() && country.isNullOrEmpty()
          .not()
      ) "$city, $country" else "$city$country"
    )
    bundle.putString(Key_Preferences.WEBSITE_URL, session.getDomainName(false))
    bundle.putString(Key_Preferences.PRIMARY_NUMBER, session.userPrimaryMobile)
    bundle.putString(Key_Preferences.PRIMARY_EMAIL, session.fPEmail)
    bundle.putString(
      com.onboarding.nowfloats.constant.IntentConstant.CHANNEL_TYPE.name,
      channelType
    )
    startFragmentChannelActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle)
  } catch (e: Exception) {
    e.printStackTrace()
  }
}