package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.NetworkUtils
import com.framework.utils.PreferencesUtils
import com.framework.webengageconstant.DIGITAL_CHANNELS
import com.framework.webengageconstant.FACEBOOK_SHOP_DISCONNECTED
import com.nowfloats.facebook.FacebookLoginHelper
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookPermissions
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import com.nowfloats.facebook.models.userDetails.FacebookGraphUserDetailsResponse
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessFacebookShopBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.haveTwitterChannels
import com.onboarding.nowfloats.model.channel.haveWhatsAppChannels
import com.onboarding.nowfloats.model.channel.isFacebookShop
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.clear
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.model.channel.request.isLinked
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.ui.InternetErrorDialog
import com.onboarding.nowfloats.utils.WebEngageController

class RegistrationBusinessFacebookShopFragment :
  BaseRegistrationFragment<FragmentRegistrationBusinessFacebookShopBinding>(),
  FacebookLoginHelper, FacebookGraphManager.GraphRequestUserAccountCallback {

  private val callbackManager = CallbackManager.Factory.create()
  private var facebookChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var channelAccessToken =
    ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.facebookshop.name.toLowerCase())
  private var isShowProfile = false

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
    checkIsUpdate()
    binding?.facebookChannels?.post {
      (binding?.facebookChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn()))
        ?.andThen(binding?.title?.fadeIn(100L))?.andThen(binding?.subTitle?.fadeIn(100L))
        ?.doOnComplete {
          if (isShowProfile) setProfileDetails(
            channelAccessToken.userAccountName,
            channelAccessToken.profilePicture
          )
        }?.andThen(binding?.linkFacebook?.fadeIn(500L))
        ?.andThen(binding?.skip?.fadeIn(100L))?.subscribe()
    }
    setOnClickListener(binding?.skip, binding?.linkFacebook)
    setSetSelectedFacebookChannels(channels)
    setSavedData()
  }

  private fun checkIsUpdate() {
    if (requestFloatsModel?.isUpdate == true) {
      requestFloatsModel?.channelAccessTokens?.forEach {
        if (it.type == ChannelAccessToken.AccessTokenType.facebookshop.name) {
          channelAccessToken = it
          channelAccessToken.profilePicture =
            FacebookGraphManager.getProfilePictureUrl(it.userAccountId ?: "")
          isShowProfile = true
        }
      }
    }
  }

  override fun setSavedData() {
    val channelAccessToken = requestFloatsModel?.channelAccessTokens
      ?.firstOrNull { it.getType() == channelAccessToken.getType() } ?: return
    setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
    requestFloatsModel?.channelAccessTokens?.remove(channelAccessToken)
    this.channelAccessToken = channelAccessToken
  }

  private fun setSetSelectedFacebookChannels(list: ArrayList<ChannelModel>) {
    val selectedItems = list.filter { it.isFacebookShop() }
      .map { it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it }
    facebookChannelsAdapter = binding?.facebookChannels?.setGridRecyclerViewAdapter(
      baseActivity,
      selectedItems.size,
      selectedItems
    )
    facebookChannelsAdapter?.notifyDataSetChanged()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.skip -> gotoNextScreen(true)
      binding?.linkFacebook -> {
        if (channelAccessToken.isLinked()) {
          gotoNextScreen()
        } else if (!NetworkUtils.isNetworkConnected()) {
          InternetErrorDialog().show(parentFragmentManager, InternetErrorDialog::class.java.name)
        } else loginWithFacebook(
          this,
          listOf(
            FacebookPermissions.email,
            FacebookPermissions.public_profile,
            FacebookPermissions.read_insights,
            FacebookPermissions.pages_show_list,
            FacebookPermissions.pages_manage_cta,
            FacebookPermissions.ads_management,
            FacebookPermissions.pages_read_engagement,
            FacebookPermissions.pages_manage_posts,
            FacebookPermissions.pages_read_user_content,
            FacebookPermissions.pages_manage_metadata,
            FacebookPermissions.catalog_management
          )
        )
      }
    }
  }

  private fun gotoNextScreen(isSkip: Boolean = false) {
    if (channelAccessToken.isLinked() && isSkip.not()) requestFloatsModel?.channelAccessTokens?.add(
      channelAccessToken
    )
    when {
      channels.haveTwitterChannels() -> gotoTwitterDetails()
      channels.haveWhatsAppChannels() -> gotoWhatsAppCallDetails()
      else -> gotoBusinessApiCallDetails()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  override fun onFacebookLoginSuccess(result: LoginResult?) {
    val accessToken = result?.accessToken ?: return
    PreferencesUtils.instance.saveFacebookUserToken(accessToken.token)
    PreferencesUtils.instance.saveFacebookUserId(accessToken.userId)
    FacebookGraphManager.requestUserPages(accessToken, this)
    FacebookGraphManager.requestUserPublicDetails(accessToken, accessToken.userId, this)
  }

  override fun onFacebookLoginCancel() {
    showShortToast(resources.getString(R.string.cancelled))
  }

  override fun onFacebookLoginError(error: FacebookException?) {
    showShortToast(error?.localizedMessage)
  }

  override fun onCompleted(
    type: FacebookGraphRequestType,
    facebookGraphResponse: BaseFacebookGraphResponse?
  ) {
    when (type) {
      FacebookGraphRequestType.USER_PAGES -> onFacebookPagesFetched(facebookGraphResponse as? FacebookGraphUserPagesResponse)
      FacebookGraphRequestType.USER_DETAILS -> onFacebookDetailsFetched(facebookGraphResponse as? FacebookGraphUserDetailsResponse)
    }
  }

  private fun onFacebookDetailsFetched(response: FacebookGraphUserDetailsResponse?) {
  }

  private fun onFacebookPagesFetched(response: FacebookGraphUserPagesResponse?) {
    val pages = response?.data ?: return
//        if (pages.size > 1) return showShortToast(resources.getString(R.string.select_one_page))
    val page = pages.firstOrNull() ?: return
    channelAccessToken.userAccessTokenKey = AccessToken.getCurrentAccessToken().token
    channelAccessToken.userAccountId = page.id
    channelAccessToken.profilePicture = FacebookGraphManager.getProfilePictureUrl(page.id ?: "")
    channelAccessToken.userAccountName = page.name
    setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
  }

  override fun setProfileDetails(name: String?, profilePicture: String?) {
    requestFloatsModel?.fpTag?.let {
      WebEngageController.trackEvent(
        FACEBOOK_SHOP_DISCONNECTED,
        DIGITAL_CHANNELS,
        it
      )
    }
    val binding = binding?.facebookPageSuccess ?: return
    this.binding?.skip?.gone()
    binding.maimView.visible()
    binding.maimView.alpha = 1F
    binding.disconnect.setOnClickListener { disconnectFacebookPage() }
    this.binding?.title?.text = resources.getString(R.string.facebook_shop_connected)
    this.binding?.subTitle?.text =
      resources.getString(R.string.facebook_shop_allows_digital_business_boost)
    this.binding?.linkFacebook?.text = resources.getString(R.string.save_continue)
    binding.profileTitle.text = name
    binding.channelType.setImageResource(R.drawable.ic_facebook_shop_n)
    val profilePicture = profilePicture
    if (profilePicture?.isNotBlank() == true) {
      baseActivity.glideLoad(binding.profileImage, profilePicture, R.drawable.ic_user3)
    }
  }

  private fun disconnectFacebookPage() {
    logoutFacebook()
    requestFloatsModel?.fpTag?.let {
      WebEngageController.trackEvent(
        FACEBOOK_SHOP_DISCONNECTED,
        DIGITAL_CHANNELS,
        it
      )
    }
    binding?.skip?.visible()
    binding?.facebookPageSuccess?.maimView?.gone()
    this.binding?.title?.text = resources.getString(R.string.shop_section_on_your_fb_page)
    binding?.subTitle?.text = resources.getString(R.string.facebook_page_connect_later_Skip)
    binding?.linkFacebook?.text = resources.getString(R.string.sync_facebook_page)
    channelAccessToken.clear()
  }

  override fun updateInfo() {
    requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.facebookshop }
    super.updateInfo()
  }
}