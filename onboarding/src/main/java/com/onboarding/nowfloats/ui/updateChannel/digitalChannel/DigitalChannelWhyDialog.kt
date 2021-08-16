package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
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
      binding?.clickHelp -> callHelpLineNumber()
      binding?.confirm -> this.dismiss()
    }
  }

  private fun callHelpLineNumber() {
    try {
      val intent = Intent(Intent.ACTION_CALL)
      intent.data = Uri.parse("tel:18601231233")
      if (ContextCompat.checkSelfPermission(
          baseActivity,
          Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
      ) {
        baseActivity.startActivity(intent)
      } else requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
    } catch (e: ActivityNotFoundException) {
      showLongToast(getString(R.string.error_in_your_phone_call))
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