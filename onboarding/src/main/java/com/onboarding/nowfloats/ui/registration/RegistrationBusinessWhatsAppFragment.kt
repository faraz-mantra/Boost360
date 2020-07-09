package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.view.View
import com.framework.extensions.getDrawable
import com.framework.utils.ValidationUtils
import com.framework.utils.showKeyBoard
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

class RegistrationBusinessWhatsAppFragment : BaseRegistrationFragment<FragmentRegistrationBusinessWhatsappBinding>() {

  private var whatsAppData: ChannelActionData = ChannelActionData()
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
    setSavedData()
    var confirmButtonAlpha = 0.3f
    if (ValidationUtils.isMobileNumberValid(binding?.number?.text?.toString() ?: "")) {
      confirmButtonAlpha = 1f
    }
    binding?.whatsappChannels?.post {
      (binding?.whatsappChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn())
          ?.doOnComplete { setSetSelectedWhatsAppChannel(channels) })
          ?.andThen(binding?.title?.fadeIn(100L))?.andThen(binding?.subTitle?.fadeIn(100L))
          ?.andThen(binding?.edtView?.fadeIn(100))
          ?.andThen(binding?.whatsappEntransactional?.fadeIn(100)?.mergeWith(binding?.confirmBtn?.fadeIn(400L, confirmButtonAlpha)))
          ?.andThen(binding?.skip?.fadeIn(50L))?.doOnComplete {
            baseActivity.showKeyBoard(binding?.number)
          }?.subscribe()
    }
    setOnClickListener(binding?.confirmBtn, binding?.skip)

    binding?.number?.afterTextChanged { checkValidNumber(it) }
  }

  override fun setSavedData() {
    val whatsAppData = requestFloatsModel?.channelActionDatas?.firstOrNull() ?: return
    requestFloatsModel?.channelActionDatas?.remove(whatsAppData)
    this.whatsAppData = whatsAppData
    binding?.number?.setText(whatsAppData.active_whatsapp_number)
    binding?.confirmBtn?.post {
      onNumberValid(ValidationUtils.isMobileNumberValid(binding?.number?.text?.toString() ?: ""), 0F)
    }
  }

  private fun checkValidNumber(phoneNumber: String?) {
    val number = phoneNumber ?: return
    val isNumberValid = ValidationUtils.isMobileNumberValid(number)
    onNumberValid(isNumberValid)
  }

  private fun onNumberValid(isNumberValid: Boolean, alpha: Float = 1F) {
    if (isNumberValid) {
      binding?.number?.drawableEnd = resources.getDrawable(baseActivity, R.drawable.ic_valid)
      binding?.confirmBtn?.alpha = alpha
      binding?.skip?.text = resources.getString(R.string.skip)
      whatsAppData.active_whatsapp_number = binding?.number?.text?.toString()
    } else {
      binding?.number?.drawableEnd = null
      binding?.confirmBtn?.alpha = 0.3f
      binding?.skip?.text = resources.getString(R.string.i_don_t_have_one_will_do_later)
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
      binding?.confirmBtn -> if (binding?.number?.length() == 10) gotoBusinessApiCallDetails()
      binding?.skip -> {
        updateInfo()
        gotoBusinessApiCallDetails()
      }
    }
  }

  override fun gotoBusinessApiCallDetails() {
    if (whatsAppData.isLinked()) {
      requestFloatsModel?.channelActionDatas?.add(whatsAppData)
      requestFloatsModel?.whatsappEntransactional = binding?.whatsappEntransactional?.isChecked
    }
    super.gotoBusinessApiCallDetails()
  }

  override fun updateInfo() {
    super.updateInfo()
    requestFloatsModel?.channelActionDatas?.clear()
    whatsAppData.active_whatsapp_number = ""
  }
}