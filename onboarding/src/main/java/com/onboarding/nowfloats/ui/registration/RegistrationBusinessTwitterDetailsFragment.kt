package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.NetworkUtils
import com.nowfloats.twitter.TwitterLoginHelper
import com.nowfloats.twitter.TwitterUserHelper
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessTwitterDetailsBinding
import com.onboarding.nowfloats.databinding.SuccessSocialLayoutBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.haveWhatsAppChannels
import com.onboarding.nowfloats.model.channel.isTwitterChannel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.clear
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.model.channel.request.isLinked
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.ui.InternetErrorDialog
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient

class RegistrationBusinessTwitterDetailsFragment : BaseRegistrationFragment<FragmentRegistrationBusinessTwitterDetailsBinding>(),
        TwitterLoginHelper, TwitterUserHelper {

  private val twitterAuthClient = TwitterAuthClient()
  private var selectedChannel: List<ChannelModel>? = null
  private var channelAccessToken = ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.Twitter.name.toLowerCase())
  private var twitterChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null


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
    binding?.twitterChannels?.post {
      (binding?.twitterChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn()))
              ?.andThen(binding?.title?.fadeIn(100L))?.andThen(binding?.subTitle?.fadeIn(100L))
              ?.andThen(binding?.linkTwitter?.fadeIn(50L))
              ?.andThen(binding?.skip?.fadeIn(0L))?.subscribe()
    }
    setOnClickListener(binding?.skip, binding?.linkTwitter)
    setSelectedTwitterChannels(channels)

    setSavedData()
  }

  override fun setSavedData() {
    val channelAccessToken = requestFloatsModel?.channelAccessTokens
            ?.firstOrNull { it.getType() == channelAccessToken.getType() } ?: return
    setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
    requestFloatsModel?.channelAccessTokens?.remove(channelAccessToken)
    this.channelAccessToken = channelAccessToken
  }

  private fun setSelectedTwitterChannels(list: ArrayList<ChannelModel>) {
    selectedChannel = list.filter { it.isTwitterChannel() }.map {
      it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it
    }
    selectedChannel?.let { channels ->
      twitterChannelsAdapter = binding?.twitterChannels?.setGridRecyclerViewAdapter(baseActivity, channels.size, channels)
      twitterChannelsAdapter?.notifyDataSetChanged()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.skip -> gotoNextScreen()
      binding?.linkTwitter -> {
        if (channelAccessToken.isLinked()) {
          gotoNextScreen()
        } else if (!NetworkUtils.isNetworkConnected()) {
          InternetErrorDialog().show(parentFragmentManager, InternetErrorDialog::class.java.name)
        } else {
          twitterAuthClient.cancelAuthorize()
          loginWithTwitter(baseActivity, twitterAuthClient)
        }
      }
    }
  }

  private fun gotoNextScreen() {
    if (channelAccessToken.isLinked()) {
      requestFloatsModel?.channelAccessTokens?.add(channelAccessToken)
    }
    if (channels.haveWhatsAppChannels()) {
      gotoWhatsAppCallDetails()
    } else gotoBusinessApiCallDetails()
  }

  override fun onTwitterLoginSuccess(result: Result<TwitterSession>?) {
    if (result == null) return
    channelAccessToken.userAccountId = result.data?.id?.toString()
    channelAccessToken.userAccessTokenKey = result.data?.authToken?.token
    channelAccessToken.userAccountName = result.data?.userName

    getUserDetails(result.data) { userDetails, error ->
      if (error != null) showShortToast(error.localizedMessage)
      else {
        channelAccessToken.profilePicture = userDetails?.data?.profileImageUrl
        setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
      }
    }
  }

  override fun setProfileDetails(name: String?, profilePicture: String?) {
    val binding = binding?.twitterSuccess ?: return
    this.binding?.skip?.gone()
    binding.maimView.visible()
    binding.disconnect.setOnClickListener { disconnectTwitter(binding) }
    this.binding?.title?.text = resources.getString(R.string.twitter_connected)
    this.binding?.subTitle?.text = resources.getString(R.string.twitter_allows_digital_business_boost)
    this.binding?.linkTwitter?.text = resources.getString(R.string.save_continue)
    binding.profileTitle.text = name
    selectedChannel?.let { channels ->
      if (channels.isNotEmpty()) {
        channels[0].getDrawable(baseActivity)?.let { img -> binding.channelType.setImageDrawable(img) }
      }
    }
    if (profilePicture?.isNotBlank() == true) {
      baseActivity.glideLoad(binding.profileImage, profilePicture, R.drawable.ic_user3)
    }
  }

  private fun disconnectTwitter(twitterSuccess: SuccessSocialLayoutBinding) {
    binding?.skip?.visible()
    twitterSuccess.maimView.gone()
    binding?.subTitle?.text = resources.getString(R.string.twitter_account_business_Skip)
    binding?.linkTwitter?.text = resources.getString(R.string.do_you_already_have_a_twitter_profile)
    channelAccessToken.clear()
  }

  override fun onTwitterLoginFailure(exception: TwitterException?) {
    showShortToast(exception?.localizedMessage)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    twitterAuthClient.onActivityResult(requestCode, resultCode, data)
  }

    override fun updateInfo() {
    requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.Twitter }
        super.updateInfo()
  }
}