package com.onboarding.nowfloats.ui.channel

import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogChannelBottomSheetBinding

@Deprecated("not in use")
class ChannelBottomSheetDialog : BaseBottomSheetDialog<DialogChannelBottomSheetBinding, BaseViewModel>() {

  private var list = ArrayList<ChannelModel>()
  var onDoneClicked: () -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.dialog_channel_bottom_sheet
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setChannels(list: ArrayList<ChannelModel>) {
    this.list.clear()
    this.list.addAll(list)
  }

  override fun onCreateView() {
    binding?.channelBottomSheet?.setChannels(baseActivity, list)
    binding?.channelBottomSheet?.onDoneClicked = onDoneClicked
  }

  fun scrollToTop() {
    binding?.channelBottomSheet?.scrollToTop()
  }

  override fun getMarginTop(): Int {
    return ConversionUtils.dp2px(resources.getDimension(R.dimen.toolbar_size) / 2)
  }
}