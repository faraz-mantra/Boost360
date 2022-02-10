package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.NetworkUtils
import com.framework.webengageconstant.DIGITAL_CHANNELS
import com.framework.webengageconstant.FAILED
import com.framework.webengageconstant.TWITTER_CONNECTED
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
import com.onboarding.nowfloats.utils.WebEngageController
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient

class RegistrationBusinessTwitterDetailsFragment :
  BaseRegistrationFragment<FragmentRegistrationBusinessTwitterDetailsBinding>(), TwitterLoginHelper,
  TwitterUserHelper {

  private val twitterAuthClient = TwitterAuthClient()
  private var selectedChannel: List<ChannelModel>? = null
  private var channelAccessToken =
    ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.twitter.name.toLowerCase())
  private var twitterChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var isShowProfile = false

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
    checkIsUpdate()
    binding?.twitterChannels?.post {
      (binding?.twitterChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn()))
        ?.andThen(binding?.title?.fadeIn(100L))?.andThen(binding?.subTitle?.fadeIn(100L))
        ?.doOnComplete {
          if (isShowProfile) setProfileDetails(
            channelAccessToken.userAccountName,
            channelAccessToken.profilePicture
          )
        }?.andThen(binding?.linkTwitter?.fadeIn(500L))
        ?.subscribe()
    }
    setOnClickListener(binding?.linkTwitter)
    setSelectedTwitterChannels(channels)
    setSavedData()
  }

  private fun checkIsUpdate() {
    if (requestFloatsModel?.isUpdate == true) {
      requestFloatsModel?.channelAccessTokens?.forEach {
        if (it.type == ChannelAccessToken.AccessTokenType.twitter.name) {
          channelAccessToken = it
          isShowProfile = true
        }
      }
    }
  }

  override fun setSavedData() {
    val channelAccessToken =
      requestFloatsModel?.channelAccessTokens?.firstOrNull { it.getType() == channelAccessToken.getType() }
        ?: return
    setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
    requestFloatsModel?.channelAccessTokens?.remove(channelAccessToken)
    this.channelAccessToken = channelAccessToken
  }

  private fun setSelectedTwitterChannels(list: ArrayList<ChannelModel>) {
    selectedChannel = list.filter { it.isTwitterChannel() }.map {
      it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it
    }
    selectedChannel?.let { channels ->
      twitterChannelsAdapter =
        binding?.twitterChannels?.setGridRecyclerViewAdapter(baseActivity, channels.size, channels)
      twitterChannelsAdapter?.notifyDataSetChanged()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
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

  private fun gotoNextScreen(isSkip: Boolean = false) {
    if (channelAccessToken.isLinked() && isSkip.not()) requestFloatsModel?.channelAccessTokens?.add(
      channelAccessToken
    )

    if (channels.haveWhatsAppChannels()) gotoWhatsAppCallDetails() else gotoBusinessApiCallDetails()
  }

  override fun onTwitterLoginSuccess(result: Result<TwitterSession>?) {
    if (result == null) return
    channelAccessToken.userAccountId = result.data?.id?.toString()
    channelAccessToken.userAccessTokenKey = result.data?.authToken?.token
    channelAccessToken.userAccessTokenSecret = result.data?.authToken?.secret
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
    requestFloatsModel?.fpTag?.let {
      WebEngageController.trackEvent(
        TWITTER_CONNECTED,
        DIGITAL_CHANNELS,
        it
      )
    }
    val binding = binding?.twitterSuccess ?: return
    binding.maimView.visible()
    binding.maimView.alpha = 1F
    binding.disconnect.setOnClickListener { disconnectTwitter(binding) }
    this.binding?.title?.text = resources.getString(R.string.twitter_connected)
    this.binding?.subTitle?.text =
      resources.getString(R.string.twitter_allows_digital_business_boost)
    this.binding?.linkTwitter?.text = resources.getString(R.string.view_updated_channels)
    binding.profileTitle.text = name
    selectedChannel?.let { channels ->
      if (channels.isNotEmpty()) {
        channels[0].getDrawable(baseActivity)
          ?.let { img -> binding.channelType.setImageDrawable(img) }
      }
    }
    if (profilePicture?.isNotBlank() == true) {
      baseActivity.glideLoad(binding.profileImage, profilePicture, R.drawable.ic_user3)
    }
  }

  private fun disconnectTwitter(twitterSuccess: SuccessSocialLayoutBinding) {
    twitterSuccess.maimView.gone()
    this.binding?.title?.text = resources.getString(R.string.do_you_already_have_a_twitter_profile)
    binding?.subTitle?.text = resources.getString(R.string.twitter_account_business_Skip)
    binding?.linkTwitter?.text = resources.getString(R.string.do_you_already_have_a_twitter_profile)
    channelAccessToken.clear()
  }

  override fun onTwitterLoginFailure(exception: TwitterException?) {
    WebEngageController.trackEvent(TWITTER_CONNECTED, DIGITAL_CHANNELS, FAILED)
    showShortToast(exception?.localizedMessage)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    twitterAuthClient.onActivityResult(requestCode, resultCode, data)
  }

  override fun updateInfo() {
    requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.twitter }
    super.updateInfo()
  }

}