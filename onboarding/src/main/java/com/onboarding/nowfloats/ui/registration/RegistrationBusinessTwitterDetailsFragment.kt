package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.nowfloats.twitter.TwitterLoginHelper
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.haveWhatsAppChannels
import com.onboarding.nowfloats.model.channel.isTwitterChannel
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessTwitterDetailsBinding
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton

class RegistrationBusinessTwitterDetailsFragment : BaseRegistrationFragment<FragmentRegistrationBusinessTwitterDetailsBinding>(),
  TwitterLoginHelper {

  private val channelAccessToken = ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.Twitter)

  private var twitterChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var twitterButton: TwitterLoginButton? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessTwitterDetailsFragment {
      val fragment = RegistrationBusinessTwitterDetailsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

    override fun onCreateView() {
        super.onCreateView()
        twitterButton = TwitterLoginButton(context)
        registerTwitterLogin()
        binding?.twitterChannels?.post {
            (binding?.twitterChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn(1000L)))
                ?.andThen(binding?.title?.fadeIn(200L))?.andThen(binding?.subTitle?.fadeIn(200L))
                ?.andThen(binding?.linkTwitter?.fadeIn(200L))
                ?.andThen(binding?.next?.fadeIn(100L))?.subscribe()
        }
      setOnClickListener(binding?.next, binding?.linkTwitter)
      setSelectedTwitterChannels(channels)
    }

  private fun setSelectedTwitterChannels(list: ArrayList<ChannelModel>) {
    val selectedItems = list.filter { it.isTwitterChannel() }.map {
      it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it
    }
    twitterChannelsAdapter = binding?.twitterChannels?.setGridRecyclerViewAdapter(
      baseActivity,
      selectedItems.size,
      selectedItems
    )
    twitterChannelsAdapter?.notifyDataSetChanged()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.next -> if (channels.haveWhatsAppChannels()) {
        gotoWhatsAppCallDetails()
      } else gotoBusinessApiCallDetails()
      binding?.linkTwitter -> {
        getTwitterLoginButton()?.performClick()
      }
    }
  }

  override fun getTwitterLoginButton(): TwitterLoginButton? {
    return twitterButton
  }

  override fun onTwitterLoginSuccess(result: Result<TwitterSession>?) {
    if (result == null) return
    channelAccessToken.userAccountId = result.data?.id?.toString()
    channelAccessToken.userAccessTokenKey = result.data?.authToken?.token
    channelAccessToken.userAccountName = result.data?.userName

    gotoWhatsAppCallDetails()
  }

  override fun onTwitterLoginFailure(exception: TwitterException?) {
    showShortToast(exception?.localizedMessage)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    getTwitterLoginButton()?.onActivityResult(requestCode, resultCode, data)
  }
}