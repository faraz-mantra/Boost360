package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.framework.analytics.SentryController
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.framework.utils.makeCall
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogDigitalChannelWhyBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getName

class DigitalChannelWhyDialog :
  BaseDialogFragment<DialogDigitalChannelWhyBinding, BaseViewModel>() {

  private var channelModel: ChannelModel? = null

  override fun getLayout(): Int {
    return R.layout.dialog_digital_channel_why
  }

  override fun onCreateView() {
    binding?.container?.post {
      (binding?.container?.fadeIn(300L)?.mergeWith(binding?.imageCard?.fadeIn(300L)))
        ?.andThen(binding?.title?.fadeIn(100L)?.mergeWith(binding?.desc?.fadeIn(100L)))
        ?.andThen(binding?.confirm?.fadeIn(50L))?.subscribe()
      binding?.title?.text = channelModel?.getName()
      binding?.desc?.text = channelModel?.moreDesc
      binding?.image?.setImageDrawable(channelModel?.getDrawable(activity))
      binding?.image?.makeGreyscale()
    }
    setOnClickListener(binding?.confirm, binding?.dismiss, binding?.clickHelp)
  }

  fun setChannels(channelModel: ChannelModel?) {
    this.channelModel = channelModel
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.dismiss -> this.dismiss()
      binding?.clickHelp ->makeCall(getString(R.string.contact_us_number))
      binding?.confirm -> this.dismiss()
    }
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogTheme
  }

  override fun getWidth(): Int? {
    return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(36f)
  }

  override fun getHeight(): Int? {
    return ViewGroup.LayoutParams.MATCH_PARENT
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }
}