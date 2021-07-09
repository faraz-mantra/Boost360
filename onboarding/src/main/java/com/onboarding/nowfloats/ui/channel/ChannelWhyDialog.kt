package com.onboarding.nowfloats.ui.channel

import android.view.View
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogChannelWhyConfirmBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getName

class ChannelWhyDialog : BaseDialogFragment<DialogChannelWhyConfirmBinding, BaseViewModel>() {

    private var channelModel: ChannelModel? = null

    override fun getLayout(): Int {
        return R.layout.dialog_channel_why_confirm
    }

  override fun onCreateView() {
        binding?.container?.post {
            (binding?.container?.fadeIn(300L)?.mergeWith(binding?.imageCard?.fadeIn(300L)))
                    ?.andThen(binding?.title?.fadeIn(100L)?.mergeWith(binding?.desc?.fadeIn(100L)))
                    ?.andThen(binding?.confirm?.fadeIn(50L))?.subscribe()
            binding?.title?.text = channelModel?.getName()
            binding?.desc?.text = channelModel?.moreDesc
            binding?.image?.setImageDrawable(channelModel?.getDrawable(activity))
        }
        setOnClickListener(binding?.confirm)
    }

    fun setChannels(channelModel: ChannelModel?) {
        this.channelModel = channelModel
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.confirm -> this.dismiss()
        }
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