package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.os.Bundle
import android.view.View
import com.framework.base.BaseDialogFragment
import com.framework.extensions.gone
import com.framework.extensions.underlineText
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.databinding.DialogDigitalChannelInfoBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.ui.webview.WebViewActivity

class DigitalChannelInfoDialog : BaseDialogFragment<DialogDigitalChannelInfoBinding, BaseViewModel>() {

  private var channelModel: ChannelModel? = null

  override fun getLayout(): Int {
    return R.layout.dialog_digital_channel_info
  }

  override fun onCreateView() {
    binding?.container?.post {
      val fabObservable = (binding?.container?.fadeIn(300L)?.mergeWith(binding?.imageCard?.fadeIn(300L)))
          ?.andThen(binding?.title?.fadeIn(100L)?.mergeWith(binding?.desc?.fadeIn(100L)))
          ?.andThen(binding?.confirm?.fadeIn(50L))
      if (channelModel != null && channelModel!!.isGoogleChannel()) {
        binding?.disconnectBtn?.gone()
        fabObservable?.subscribe()
      } else {
        binding?.disconnectBtn?.visible()
        fabObservable?.andThen(binding?.disconnectBtn?.fadeIn(50L))?.subscribe()
      }
      if (channelModel != null && channelModel!!.isWhatsAppChannel()) {
        binding?.title?.text = channelModel?.channelActionData?.active_whatsapp_number?.takeIf { it.isNotEmpty() }?.let { it } ?: channelModel?.getName()
      } else {
        binding?.title?.text = channelModel?.channelAccessToken?.userAccountName?.takeIf { it.isNotEmpty() }?.let { it } ?: channelModel?.getName()
        if (channelModel != null && (channelModel!!.isFacebookShop() || channelModel!!.isFacebookPage())) {
          val profilePicture = FacebookGraphManager.getProfilePictureUrl(channelModel?.channelAccessToken?.userAccountId ?: "")
          binding?.picture?.let { baseActivity.glideLoad(it, profilePicture, R.drawable.ic_user3) }
        }
      }

      binding?.desc?.text = channelModel?.moreDesc
      binding?.image?.setImageDrawable(channelModel?.getDrawable(activity))
      binding?.title?.underlineText(0, (binding?.title?.text ?: "").length - 1)
    }
    setOnClickListener(binding?.confirm, binding?.disconnectBtn, binding?.title)
  }

  fun setChannels(channelModel: ChannelModel?) {
    this.channelModel = channelModel
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.confirm -> this.dismiss()
      binding?.disconnectBtn -> disConnectChannel()
      binding?.title -> {
        openBrowser()
      }
    }
  }

  private fun openBrowser() {
    var url: String? = null
    if (channelModel != null) {
      if (channelModel!!.isTwitterChannel() && channelModel?.channelAccessToken?.userAccountName.isNullOrEmpty().not()) {
        url = "https://twitter.com/${channelModel?.channelAccessToken?.userAccountName?.trim()}"
      } else if (channelModel!!.isFacebookPage() && channelModel?.channelAccessToken?.userAccountId.isNullOrEmpty().not()) {
        url = "https://www.facebook.com/${channelModel?.channelAccessToken?.userAccountId}"
      }
    }
    url?.let {
      val bundle = Bundle()
      bundle.putString(IntentConstant.DOMAIN_URL.name, url)
      navigator?.startActivity(WebViewActivity::class.java, bundle)
      this.dismiss()
    }
  }

  private fun disConnectChannel() {
    showLongToast("Coming Soon...")
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogTheme
  }

  override fun getWidth(): Int? {
    return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(32f)
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }
}