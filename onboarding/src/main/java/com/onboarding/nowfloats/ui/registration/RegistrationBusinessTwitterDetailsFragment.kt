package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.nowfloats.twitter.TwitterLoginHelper
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
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient

class RegistrationBusinessTwitterDetailsFragment : BaseRegistrationFragment<FragmentRegistrationBusinessTwitterDetailsBinding, BaseViewModel>(),
        TwitterLoginHelper {

    private val twitterAuthClient = TwitterAuthClient()
    private var selectedChannel: List<ChannelModel>? = null
    private val channelAccessToken = ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.Twitter)
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
            (binding?.twitterChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn(1000L)))
                    ?.andThen(binding?.title?.fadeIn(200L))?.andThen(binding?.subTitle?.fadeIn(200L))
                    ?.andThen(binding?.linkTwitter?.fadeIn(200L))
                    ?.andThen(binding?.skip?.fadeIn(100L))?.subscribe()
        }
        setOnClickListener(binding?.skip, binding?.linkTwitter)
        setSelectedTwitterChannels(channels)
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
                twitterAuthClient.cancelAuthorize()
                loginWithTwitter(baseActivity, twitterAuthClient)
            }
        }
    }

    private fun gotoNextScreen() {
        if (channels.haveWhatsAppChannels()) {
            gotoWhatsAppCallDetails()
        } else gotoBusinessApiCallDetails()
    }

    override fun onTwitterLoginSuccess(result: Result<TwitterSession>?) {
        if (result == null) return
        channelAccessToken.userAccountId = result.data?.id?.toString()
        channelAccessToken.userAccessTokenKey = result.data?.authToken?.token
        channelAccessToken.userAccountName = result.data?.userName
        setProfileDetails(binding?.twitterSuccess)
    }

    private fun setProfileDetails(bindingSuccess: SuccessSocialLayoutBinding?) {
        bindingSuccess?.let { twitterSuccess ->
            binding?.skip?.gone()
            twitterSuccess.maimView.visible()
            twitterSuccess.disconnect.setOnClickListener { disconnectTwitter(twitterSuccess) }
            binding?.subTitle?.text = resources.getString(R.string.twitter_allows_digital_business_boost)
            binding?.linkTwitter?.text = resources.getString(R.string.save_continue)
            twitterSuccess.profileTitle.text = channelAccessToken.userAccountName
            selectedChannel?.let { channels ->
                if (channels.isNotEmpty()) {
                    channels[0].getDrawable(baseActivity)?.let { img -> twitterSuccess.channelType.setImageDrawable(img) }
                }
            }
//          getting userData setImage
//            it.profile_image?.let { it1 ->
//                baseActivity.glideLoad(it.profileImage, it1, R.drawable.ic_user3)
//            }
        }
    }

    private fun disconnectTwitter(twitterSuccess: SuccessSocialLayoutBinding) {
        binding?.skip?.visible()
        twitterSuccess.maimView.gone()
        binding?.subTitle?.text = resources.getString(R.string.twitter_acocunt_business_Skip)
        binding?.linkTwitter?.text = resources.getString(R.string.do_you_already_have_a_twitter_profile)
    }

    override fun onTwitterLoginFailure(exception: TwitterException?) {
        showShortToast(exception?.localizedMessage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}