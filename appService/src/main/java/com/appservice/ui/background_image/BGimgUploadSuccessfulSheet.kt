package com.appservice.ui.background_image

import android.view.View
import com.appservice.R
import com.appservice.base.startWebViewPageLoad
import com.appservice.databinding.BottomSheetServiceCreatedSuccessfullyBinding
import com.appservice.databinding.BsheetBgImgUploadSuccessBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.constants.PackageNames
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.getDomainName
import com.framework.utils.IntentUtils
import com.framework.utils.copyToClipBoard

enum class TypeSuccess {
  CLOSE, VISIT_WEBSITE
}

class BGimgUploadSuccessfulSheet : BaseBottomSheetDialog<BsheetBgImgUploadSuccessBinding, BaseViewModel>() {

  var onClicked: (value: String) -> Unit = { }
  var isEdit: Boolean = false
  override fun getLayout(): Int {
    return R.layout.bsheet_bg_img_upload_success
  }

  fun setData(isEdit: Boolean) {
    this.isEdit = isEdit
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.civCancel, binding?.visitWebsite,binding?.btnCopyMessage,binding?.btnFb
      ,binding?.btnWhatsapp,binding?.btnLinkedin,binding?.btnTwitter,binding?.btnMail)
    isCancelable = false

  }

  override fun onClick(v: View) {
    super.onClick(v)
    val sessionManager = UserSessionManager(requireActivity())
    val url = sessionManager.getDomainName()?:""
    when (v) {
      binding?.civCancel -> {
        dismiss()
        onClicked(TypeSuccess.CLOSE.name)
      }
      binding?.visitWebsite->{
        baseActivity.startWebViewPageLoad(sessionManager,url)
      }
      binding?.btnFb->{
        IntentUtils.shareText(requireActivity(),url,PackageNames.FACEBOOK)
      }
      binding?.btnLinkedin->{
        IntentUtils.shareText(requireActivity(),url,PackageNames.LINKEDIN)

      }
      binding?.btnTwitter->{
        IntentUtils.shareText(requireActivity(),url,PackageNames.TWITTER)

      }
      binding?.btnWhatsapp->{
        IntentUtils.shareText(requireActivity(),url,PackageNames.WHATSAPP)

      }
      binding?.btnMail->{
        IntentUtils.composeEmail(requireActivity(),url)
      }
      binding?.btnCopyMessage->{
        copyToClipBoard(url)
      }
//      binding?.visitWebsite -> onClicked(TypeSuccess.VISIT_WEBSITE.name)
    }
  }
}