package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import com.framework.CustomTypefaceSpan
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.framework.webengageconstant.BUSINESS_WEBSITE_VIEW_CLICKED
import com.framework.webengageconstant.REGISTRATION_COMPLETE
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.builder.BottomDialog
import com.onboarding.nowfloats.bottomsheet.builder.imagePicker
import com.onboarding.nowfloats.bottomsheet.builder.title
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationCompleteBinding
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.getBitmap
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.onboarding.nowfloats.model.uploadfile.UploadFileProfileRequest
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.ui.webview.WebViewActivity
import com.onboarding.nowfloats.utils.WebEngageController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.util.*

class RegistrationCompleteFragment :
  BaseRegistrationFragment<FragmentRegistrationCompleteBinding>() {

  private var businessImage: File? = null
  private var profileImage: File? = null
  private var selectedChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  var isProfileImage: Boolean? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationCompleteFragment {
      val fragment = RegistrationCompleteFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  @ExperimentalStdlibApi
  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(
      binding?.menuView,
      binding?.websiteBtnClick,
      binding?.skipDashboard,
      binding?.businessClick
    )
    setSetSelectedChannels(channels)
    val personName = (pref?.getString(PreferenceConstant.PERSON_NAME, "") ?: "").capitalizeWords()
    binding?.congratsText?.text =
      resources.getString(R.string.congratulations_new).plus("\n$personName").trim()
    requestFloatsModel?.contactInfo?.businessName?.let {
      binding?.businessName?.text = it
      binding?.businessNameInitial?.text = it.firstOrNull()?.toUpperCase()?.toString()
    }
    setBusinessName()
    setBusinessImage()

    binding?.imageRiya?.post {
      binding?.imageRiya?.fadeIn(300L)?.andThen(binding?.congratsText?.fadeIn(50L))
        ?.andThen(binding?.businessText?.fadeIn(50L))?.andThen(binding?.tagImage?.fadeIn(100L))
        ?.andThen(binding?.cardView?.fadeIn(100L))?.andThen(binding?.businessName?.fadeIn(50L))
        ?.andThen(binding?.domain?.fadeIn(50L))?.andThen(binding?.settingUpChannels?.fadeIn(30L))
        ?.andThen(binding?.selectedChannels?.fadeIn(50L))
        ?.andThen(binding?.websiteBtnClick?.fadeIn(30L))
        ?.andThen(binding?.skipDashboard?.fadeIn(0L))?.andThen { initLottieAnimation() }
        ?.subscribe()
    }
  }

  private fun setBusinessImage() {
    val imageUrl = takeIf { requestFloatsModel?.businessUrl.isNullOrEmpty() }?.let {
      requestFloatsModel?.channelAccessTokens?.map { it.profilePicture }
        ?.firstOrNull { it?.isNotEmpty() == true }
        ?: return
    } ?: requestFloatsModel?.businessUrl
    binding?.businessNameInitial?.gone()
    binding?.businessImage?.visible()
    imageUrl?.let { baseActivity.glideLoad(binding?.businessImage, it) }
//        requestFloatsModel?.profileUrl?.let { baseActivity.glideLoad(binding?.profileImage, it) }
  }

  private fun initLottieAnimation() {
    binding?.lottieAnimation?.setAnimation(R.raw.lottie_anim_congratulation)
    binding?.lottieAnimation?.repeatCount = 0
    startCheckAnimation()
  }

  private fun startCheckAnimation() {
    binding?.lottieAnimation?.let {
      if (it.isAnimating) it.pauseAnimation()
      else it.playAnimation()
    }
  }

  private fun setBusinessName() {
    var title = requestFloatsModel?.categoryDataModel?.category_Name ?: return
    title = title.replace('\n', ' ')
    val regular = getFont(R.font.regular) ?: return
    val semiBold = getFont(R.font.semi_bold) ?: return

    val spannableStringBuilder =
      SpannableStringBuilder(resources.getString(R.string.your) + " $title " + resources.getString(R.string.business_setup_boost))
    WebEngageController.setCategory(requestFloatsModel?.categoryDataModel?.experience_code)

    spannableStringBuilder.setSpan(
      CustomTypefaceSpan("", regular),
      0,
      4,
      Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    spannableStringBuilder.setSpan(
      CustomTypefaceSpan("", semiBold),
      5,
      5 + title.length,
      Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    spannableStringBuilder.setSpan(
      CustomTypefaceSpan("", regular),
      5 + title.length + 1,
      spannableStringBuilder.length,
      Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )

    binding?.businessText?.text = spannableStringBuilder
  }

  private fun setSetSelectedChannels(list: ArrayList<ChannelModel>) {
    binding?.domainTxt?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)
      ?.let { binding?.domainTxt?.setPaintFlags(it) }
    binding?.domainTxt?.text =
      "${requestFloatsModel?.contactInfo?.domainName?.toLowerCase(Locale.ROOT)}.nowfloats.com"
    val itemSize = ConversionUtils.dp2px(48f)
    var spanCount =
      (ScreenUtils.instance.getWidth(baseActivity) - ConversionUtils.dp2px(96f)) / itemSize
    if (spanCount == 0) {
      spanCount = 1
    } else if (spanCount > list.size) {
      spanCount = list.size
    }
    val selectedItems = list.map {
      it.recyclerViewType = RecyclerViewItemType.SMALL_SELECTED_CHANNEL_ITEM.getLayout(); it
    }
    selectedChannelsAdapter =
      binding?.selectedChannels?.setGridRecyclerViewAdapter(baseActivity, spanCount, selectedItems)
    selectedChannelsAdapter?.notifyDataSetChanged()
    if (selectedItems.isEmpty()) binding?.settingUpChannels?.gone()
    else binding?.settingUpChannels?.visible()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.menuView -> showMenuLogout(v)
      binding?.businessClick -> openImagePicker(false)
      binding?.websiteBtnClick -> {
        try {
          requestFloatsModel?.fpTag?.let {
            WebEngageController.trackEvent(
              BUSINESS_WEBSITE_VIEW_CLICKED,
              REGISTRATION_COMPLETE,
              it
            )
          }
          val bundle = Bundle()
          bundle.putString(
            IntentConstant.DOMAIN_URL.name,
            "${requestFloatsModel?.contactInfo?.domainName?.toLowerCase(Locale.ROOT)}.nowfloats.com"
          )
          navigator?.startActivity(WebViewActivity::class.java, bundle)
        } catch (e: Exception) {
          Log.e(RegistrationCompleteFragment::class.java.name, e.localizedMessage)
        }
      }
      binding?.skipDashboard -> {
        try {
          setDataLogin()
          val intent =
            Intent(baseActivity, Class.forName("com.boost.presignin.ui.LoaderActivity"))
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
          val loginBundle = Bundle()
          loginBundle.putBoolean("fromLogin", true)
          intent.putExtras(loginBundle)
          baseActivity.startActivity(intent)
          baseActivity.finish()
          NavigatorManager.clearStackAndFormData()
        } catch (e: ClassNotFoundException) {
          e.printStackTrace()
        }
      }
    }
  }

  private fun openImagePicker(isProfileImage: Boolean) {
    this.isProfileImage = isProfileImage
    BottomDialog.builder(baseActivity) {
      expandable = true
      peekHeightProportion = .4f
      title(resources.getString(R.string.choose_an_action), round = true)
      imagePicker("") { dialog, type, _ ->
        dialog.dismiss()
        if (type == 1 || type == 2) {
          ImagePicker.Builder(baseActivity)
            .mode(takeIf { type == 2 }?.let { ImagePicker.Mode.CAMERA }
              ?: ImagePicker.Mode.GALLERY)
            .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
            .enableDebuggingMode(true).build()
        }
      }
    }

  }

  private fun showMenuLogout(view: View) {
    var popup: PopupMenu? = null
    popup = PopupMenu(baseActivity, view)
    popup.inflate(R.menu.menu_facebook_profile)
    popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
      when (item!!.itemId) {
        R.id.menu_logout -> {
          NavigatorManager.clearStackAndFormData()
          showShortToast(getString(R.string.str_logout))
        }
      }
      true
    })
    popup.show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
      if (mPaths.isNotEmpty()) {
        if (this.isProfileImage != null && this.isProfileImage!!) {
//                    profileImage = File(mPaths[0])
//                    profileImage?.getBitmap()?.let { binding?.profileImage?.setImageBitmap(it) }
//                    uploadImageProfileLogo()
        } else {
          businessImage = File(mPaths[0])
          binding?.businessNameInitial?.gone()
          binding?.businessImage?.visible()
          businessImage?.getBitmap()?.let { binding?.businessImage?.setImageBitmap(it) }
          uploadImageBusinessLogo()
        }
      }
    }
  }

  private fun uploadImageBusinessLogo() {
    if (businessImage != null) {
      showProgress(resources.getString(R.string.uploading_business_image))
      viewModel?.putUploadImageBusiness(getRequestBusinessDate(businessImage!!))
        ?.observeOnce(viewLifecycleOwner, Observer {
          hideProgress()
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            showLongToast(resources.getString(R.string.business_image_uploaded))
            requestFloatsModel?.businessUrl = it.stringResponse
            updateInfo()
          } else showLongToast(it.message())
        })
    }
  }

  private fun getRequestBusinessDate(businessImage: File): UploadFileBusinessRequest {
    val responseBody =
      RequestBody.create("image/png".toMediaTypeOrNull(), businessImage.readBytes())
    val fileName = takeIf { businessImage.name.isNullOrEmpty().not() }?.let { businessImage.name }
      ?: "BUSINESS_${requestFloatsModel?.contactInfo?.domainName}.png"
    return UploadFileBusinessRequest(
      clientId,
      requestFloatsModel?.floatingPointId,
      UploadFileBusinessRequest.Type.SINGLE.name,
      fileName,
      responseBody
    )
  }

  private fun uploadImageProfileLogo() {
    if (profileImage != null) {
      showProgress(resources.getString(R.string.uploading_profile_image))
      viewModel?.putUploadImageProfile(getRequestProfileData(profileImage!!))
        ?.observeOnce(viewLifecycleOwner, Observer {
          hideProgress()
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            showLongToast(getString(R.string.profile_image_uploaded))
            requestFloatsModel?.profileUrl = it.stringResponse
            updateInfo()
          } else showLongToast(it.message)
        })
    }
  }

  private fun getRequestProfileData(profileImage: File): UploadFileProfileRequest {
    val responseBody = RequestBody.create("image/png".toMediaTypeOrNull(), profileImage.readBytes())
    val fileName = takeIf { profileImage.name.isNullOrEmpty().not() }?.let { profileImage.name }
      ?: "PROFILE_${requestFloatsModel?.contactInfo?.domainName}.png"
    return UploadFileProfileRequest(clientId, userProfileId, fileName, responseBody)
  }
}