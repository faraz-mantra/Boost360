package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.framework.models.BaseViewModel
import com.framework.utils.PreferencesUtils
import com.nowfloats.facebook.FacebookLoginHelper
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookPermissions
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesResponse
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessFacebookShopBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.haveTwitterChannels
import com.onboarding.nowfloats.model.channel.haveWhatsAppChannels
import com.onboarding.nowfloats.model.channel.isFacebookShop
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter

class RegistrationBusinessFacebookShopFragment : BaseRegistrationFragment<FragmentRegistrationBusinessFacebookShopBinding, BaseViewModel>(),
    FacebookLoginHelper, FacebookGraphManager.GraphRequestUserAccountCallback {

    private val callbackManager = CallbackManager.Factory.create()
    private var facebookChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    private val channelAccessToken = ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.Facebookshop)

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): RegistrationBusinessFacebookShopFragment {
            val fragment = RegistrationBusinessFacebookShopFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        registerFacebookLoginCallback(this, callbackManager)
        binding?.facebookChannels?.post {
            (binding?.facebookChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn(1000L)))
                ?.andThen(binding?.title?.fadeIn(200L))?.andThen(binding?.subTitle?.fadeIn(200L))
                ?.andThen(binding?.linkFacebook?.fadeIn(200L))
                    ?.andThen(binding?.skip?.fadeIn(100L))?.subscribe()
        }
        setOnClickListener(binding?.skip, binding?.linkFacebook)
        setSetSelectedFacebookChannels(channels)
    }

    private fun setSetSelectedFacebookChannels(list: ArrayList<ChannelModel>) {
        val selectedItems = list.filter { it.isFacebookShop() }.map { it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it }
        facebookChannelsAdapter = binding?.facebookChannels?.setGridRecyclerViewAdapter(baseActivity, selectedItems.size, selectedItems)
        facebookChannelsAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.skip -> {
                when {
                    channels.haveTwitterChannels() -> {
                        gotoTwitterDetails()
                    }
                    channels.haveWhatsAppChannels() -> {
                        gotoWhatsAppCallDetails()
                    }
                    else -> {
                        gotoBusinessApiCallDetails()
                    }
                }
            }
            binding?.linkFacebook -> loginWithFacebook(this, listOf(FacebookPermissions.public_profile))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFacebookLoginSuccess(result: LoginResult?) {
        showShortToast(result?.toString())
        val accessToken = result?.accessToken ?: return
        PreferencesUtils.instance.saveFacebookUserToken(accessToken.token)
        PreferencesUtils.instance.saveFacebookUserId(accessToken.userId)
        FacebookGraphManager.requestUserPages(accessToken, this)
    }

    override fun onFacebookLoginCancel() {
        showShortToast("cancel")
    }

    override fun onFacebookLoginError(error: FacebookException?) {
        showShortToast(error?.localizedMessage)
    }

    override fun onCompleted(type: FacebookGraphRequestType, facebookGraphResponse: BaseFacebookGraphResponse?) {
        val graphUserPagesResponse = facebookGraphResponse as? FacebookGraphUserPagesResponse
        val pages = graphUserPagesResponse?.data ?: return

        if (pages.size > 1) return showShortToast("Select only one page")

        val page = pages.firstOrNull() ?: return
        page.profilePicture = FacebookGraphManager.getPageProfilePictureUrl(page.id ?: "")

        channelAccessToken.userAccessTokenKey = AccessToken.getCurrentAccessToken().token
        channelAccessToken.userAccountId = AccessToken.getCurrentAccessToken().userId
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}