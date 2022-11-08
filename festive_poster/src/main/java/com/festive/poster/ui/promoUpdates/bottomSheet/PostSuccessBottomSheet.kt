package com.festive.poster.ui.promoUpdates.bottomSheet

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.festive.poster.R
import com.festive.poster.databinding.BsheetPostSuccessBinding
import com.festive.poster.ui.promoUpdates.PromoUpdatesActivity
import com.festive.poster.utils.WebEngageController
import com.framework.base.BaseBottomSheetDialog
import com.framework.constants.PackageNames
import com.framework.models.BaseViewModel
import com.framework.utils.IntentUtils
import com.framework.utils.loadFromFile
import com.framework.utils.shareAsImage
import com.framework.webengageconstant.Promotional_Update_Instagram_Share_Click
import com.framework.webengageconstant.Promotional_Updates_More_Share_Click
import com.framework.webengageconstant.Promotional_Updates_Post_Success_Loaded
import com.framework.webengageconstant.Promotional_Updates_WhatsApp_Share_Click
import java.io.File

class PostSuccessBottomSheet : BaseBottomSheetDialog<BsheetPostSuccessBinding, BaseViewModel>() {

  private var caption: String? = null
  private var posterImgPath: String? = null

  companion object {
    val IK_POSTER = "IK_POSTER"
    val IK_CAPTION = "IK_CAPTION"

    @JvmStatic
    fun newInstance(posterImgPath: String?, caption: String?): PostSuccessBottomSheet {
      val bundle = Bundle().apply {}
      bundle.putString(IK_POSTER, posterImgPath)
      bundle.putString(IK_CAPTION, caption)

      val fragment = PostSuccessBottomSheet()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.bsheet_post_success
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(Promotional_Updates_Post_Success_Loaded)
    posterImgPath = arguments?.getString(IK_POSTER)
    caption = arguments?.getString(IK_CAPTION)

    setOnClickListener(binding?.ivWhatsapp, binding?.ivInstagram, binding?.ivOther, binding?.ivClosePostSuccess)

    binding!!.ivPosterIcon.loadFromFile(posterImgPath, false)
    binding?.ivClosePostSuccess?.setOnClickListener {
      dismiss()
    }
    binding!!.cardBigAnim.isVisible = posterImgPath == null
    binding!!.cardSmallAnim.isVisible = posterImgPath != null
    binding!!.ivPosterCard.isVisible = posterImgPath != null

  }

  override fun onClick(v: View) {
    super.onClick(v)
    if (posterImgPath != null) {
      val imgFile = File(posterImgPath!!)

      when (v) {
        binding?.ivWhatsapp -> {
          WebEngageController.trackEvent(Promotional_Updates_WhatsApp_Share_Click)
          imgFile.shareAsImage(requireActivity(), PackageNames.WHATSAPP, caption)

        }
        binding?.ivInstagram -> {
          WebEngageController.trackEvent(Promotional_Update_Instagram_Share_Click)
          imgFile.shareAsImage(requireActivity(), PackageNames.INSTAGRAM, caption)

        }
        binding?.ivOther -> {
          WebEngageController.trackEvent(Promotional_Updates_More_Share_Click)
          imgFile.shareAsImage(requireActivity(), null, caption)
        }

        binding?.ivClosePostSuccess -> {
          dismiss()
        }
      }
    } else {
      when (v) {
        binding?.ivWhatsapp -> {
          IntentUtils.shareText(requireActivity(), caption ?: "", PackageNames.WHATSAPP)

        }
        binding?.ivInstagram -> {
          IntentUtils.shareText(requireActivity(), caption ?: "", PackageNames.INSTAGRAM)
        }
        binding?.ivOther -> {
          IntentUtils.shareText(requireActivity(), caption ?: "")

        }

        binding?.ivClosePostSuccess -> {
          dismiss()
        }
      }
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    val intent = Intent(requireActivity(), PromoUpdatesActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    super.onDismiss(dialog)
  }
}