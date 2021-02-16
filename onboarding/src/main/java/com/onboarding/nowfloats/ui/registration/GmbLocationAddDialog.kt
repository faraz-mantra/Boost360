package com.onboarding.nowfloats.ui.registration

import android.view.View
import android.view.ViewGroup
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogDigitalChannelWhyBinding
import com.onboarding.nowfloats.databinding.DialogGmbLocationAddBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getName
import com.onboarding.nowfloats.utils.openWebPage

class GmbLocationAddDialog : BaseDialogFragment<DialogGmbLocationAddBinding, BaseViewModel>() {


  override fun getLayout(): Int {
    return R.layout.dialog_gmb_location_add
  }

  override fun onCreateView() {
    isCancelable = false
    binding?.container?.post {
      (binding?.container?.fadeIn(200L)?.mergeWith(binding?.imageCard?.fadeIn(200L)))
          ?.andThen(binding?.title?.fadeIn(50L)?.mergeWith(binding?.subTitle?.fadeIn(80L))
              ?.mergeWith(binding?.desc?.fadeIn(100L)))?.andThen(binding?.confirm?.fadeIn(50L))
          ?.andThen(binding?.laterBtn?.fadeIn(50L))?.subscribe()
    }
    setOnClickListener(binding?.confirm, binding?.laterBtn)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.confirm -> baseActivity.openWebPage(getString(R.string.google_business_url))
    }
    this.dismiss()
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