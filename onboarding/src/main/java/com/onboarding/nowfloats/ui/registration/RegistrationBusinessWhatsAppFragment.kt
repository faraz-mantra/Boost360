package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.view.View
import com.framework.extensions.getDrawable
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessWhatsappBinding
import com.onboarding.nowfloats.extensions.afterTextChanged
import com.onboarding.nowfloats.extensions.drawableEnd
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.isWhatsAppChannel
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.channel.request.isLinked
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter

class RegistrationBusinessWhatsAppFragment : BaseRegistrationFragment<FragmentRegistrationBusinessWhatsappBinding, BaseViewModel>() {

    private var whatsAppData = ChannelActionData()
    private var whatsAppAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): RegistrationBusinessWhatsAppFragment {
            val fragment = RegistrationBusinessWhatsAppFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        binding?.whatsappChannels?.post {
            (binding?.whatsappChannels?.fadeIn(1000L)?.mergeWith(binding?.viewBusiness?.fadeIn()))
                ?.andThen(binding?.title?.fadeIn(200L))?.andThen(binding?.subTitle?.fadeIn(200L))
                ?.andThen(
                    binding?.edtView?.fadeIn()?.mergeWith(binding?.confirmBtn?.fadeIn(200L, 0.3F))
                )
                ?.andThen(binding?.skip?.fadeIn(100L))?.subscribe()
        }
        setOnClickListener(binding?.confirmBtn, binding?.skip)
        setSetSelectedWhatsAppChannel(channels)
        binding?.number?.afterTextChanged { checkValidNumber(it) }
    }

    private fun checkValidNumber(phoneNumber: String?) {
        phoneNumber?.let { it1 ->
            binding?.number?.drawableEnd = takeIf { it1.length == 10 }?.let {
                resources.getDrawable(baseActivity, R.drawable.ic_valid)
            }
            binding?.confirmBtn?.alpha = takeIf { it1.length == 10 }?.let { 1f } ?: 0.3f
            binding?.skip?.text =
                resources.getString(takeIf { it1.length == 10 }?.let { R.string.skip } ?: R.string.i_don_t_have_one_will_do_later)
        }
        if (binding?.confirmBtn?.alpha == 1f) {
            whatsAppData.active_whatsapp_number = binding?.number?.text?.toString()
        }
    }

    private fun setSetSelectedWhatsAppChannel(list: ArrayList<ChannelModel>) {
        val selectedItems = ArrayList(list.filter { it.isWhatsAppChannel() }.map {
            it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it
        })

        whatsAppAdapter = binding?.whatsappChannels?.setGridRecyclerViewAdapter(
            baseActivity,
            selectedItems.size,
            selectedItems
        )
        whatsAppAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v) {
            binding?.confirmBtn -> {
                if (binding?.number?.length() == 10) gotoBusinessApiCallDetails()
            }
            binding?.skip -> gotoBusinessApiCallDetails()
        }
    }

    override fun gotoBusinessApiCallDetails() {
        if (whatsAppData.isLinked()) {
            requestFloatsModel?.channelActionDatas?.add(whatsAppData)
        }
        super.gotoBusinessApiCallDetails()
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}