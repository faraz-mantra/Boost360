package com.onboarding.nowfloats.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.framework.webengageconstant.*
import com.nowfloats.facebook.FacebookLoginHelper
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookGraphRequestType.USER_DETAILS
import com.nowfloats.facebook.constants.FacebookGraphRequestType.USER_PAGES
import com.nowfloats.facebook.constants.FacebookPermissions
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import com.nowfloats.facebook.models.userDetails.FacebookGraphUserDetailsResponse
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessFacebookPageBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.ui.InternetErrorDialog
import com.onboarding.nowfloats.utils.WebEngageController

class RegistrationBusinessFacebookPageFragment :
  BaseRegistrationFragment<FragmentRegistrationBusinessFacebookPageBinding>(),
  FacebookLoginHelper, FacebookGraphManager.GraphRequestUserAccountCallback {

  private var channelAccessToken =
    ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.facebookpage.name.toLowerCase())

  private val callbackManager = CallbackManager.Factory.create()
  private var facebookChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var isShowProfile = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessFacebookPageFragment {
      val fragment = RegistrationBusinessFacebookPageFragment()
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
        ?.subscribe()
    }
    setOnClickListener( binding?.linkFacebook)
    setSetSelectedFacebookChannels(channels)
    setSavedData()
  }

  private fun checkIsUpdate() {
    if (requestFloatsModel?.isUpdate == true) {
      requestFloatsModel?.channelAccessTokens?.forEach {
        if (it.type == ChannelAccessToken.AccessTokenType.facebookpage.name) {
          channelAccessToken = it
          channelAccessToken.profilePicture =
            FacebookGraphManager.getProfilePictureUrl(it.userAccountId ?: "")
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

  private fun setSetSelectedFacebookChannels(list: ArrayList<ChannelModel>) {
    val selectedItems = list.filter { it.isFacebookPage() }
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

      binding?.linkFacebook -> {
        //linking facebook profile Yes Event Tracker.
        WebEngageController.trackEvent(LINKING_FACEBOOK_PROFILE, BUTTON, YES)
        if (channelAccessToken.isLinked()) {
          gotoNextScreen()
        } else if (!NetworkUtils.isNetworkConnected()) {
          InternetErrorDialog().show(parentFragmentManager, InternetErrorDialog::class.java.name)
        } else loginWithFacebook(
          this, listOf(
            FacebookPermissions.email,
            FacebookPermissions.public_profile,
            FacebookPermissions.read_insights,
            FacebookPermissions.pages_show_list,
            FacebookPermissions.pages_manage_cta,
            FacebookPermissions.ads_management,
            FacebookPermissions.pages_read_engagement,
            FacebookPermissions.pages_manage_posts,
            FacebookPermissions.pages_read_user_content,
            FacebookPermissions.pages_manage_metadata
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
      channels.haveInstagram() -> gotoInstagram()
      channels.haveFacebookShop() -> gotoFacebookShop()
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
    WebEngageController.trackEvent(FACEBOOK_CONNECTED, DIGITAL_CHANNELS, FAILED)
    showShortToast(error?.localizedMessage)
  }

  override fun onCompleted(
    type: FacebookGraphRequestType,
    facebookGraphResponse: BaseFacebookGraphResponse?
  ) {
    when (type) {
      USER_PAGES -> onFacebookPagesFetched(facebookGraphResponse as? FacebookGraphUserPagesResponse)
      USER_DETAILS -> onFacebookDetailsFetched(facebookGraphResponse as? FacebookGraphUserDetailsResponse)
    }
  }

  private fun onFacebookDetailsFetched(response: FacebookGraphUserDetailsResponse?) {

  }

  private fun onFacebookPagesFetched(response: FacebookGraphUserPagesResponse?) {
    val pages = response?.data ?: return
//        if (pages.size > 1) return showShortToast(resources.getString(R.string.select_one_page))
    val page = pages.firstOrNull() ?: return
    Log.d("UserToken", "" + AccessToken.getCurrentAccessToken().token);
    Log.d("PageToken", "" + page.access_token);
    channelAccessToken.userAccessTokenKey = page.access_token
    channelAccessToken.userAccountId = page.id
    channelAccessToken.profilePicture = FacebookGraphManager.getProfilePictureUrl(page.id ?: "")
    channelAccessToken.userAccountName = page.name
    setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
  }

  override fun setProfileDetails(name: String?, profilePicture: String?) {
    requestFloatsModel?.fpTag?.let {
      WebEngageController.trackEvent(
        FACEBOOK_PAGE_CONNECTED,
        DIGITAL_CHANNELS,
        it
      )
    }
    val binding = binding?.facebookPageSuccess ?: return
    binding.maimView.visible()
    binding.maimView.alpha = 1F
    binding.disconnect.setOnClickListener { disconnectFacebookPage() }
    this.binding?.title?.text = resources.getString(R.string.facebook_page_connected)
    this.binding?.subTitle?.text =
      resources.getString(R.string.facebook_page_allows_digital_business_boost)
    this.binding?.linkFacebook?.text = resources.getString(R.string.view_updated_channels)
    binding.profileTitle.text = name
    binding.channelType.setImageResource(R.drawable.ic_facebook_page_n)
    if (profilePicture?.isNotBlank() == true) {
      baseActivity.glideLoad(binding.profileImage, profilePicture, R.drawable.ic_user3)
    }
  }

  private fun disconnectFacebookPage() {
    logoutFacebook()
    binding?.facebookPageSuccess?.maimView?.gone()
    this.binding?.title?.text = resources.getString(R.string.do_you_already_have_a_facebook_page)
    binding?.subTitle?.text = resources.getString(R.string.facebook_page_connect_later_Skip)
    binding?.linkFacebook?.text = resources.getString(R.string.sync_facebook_page)
    channelAccessToken.clear()
    requestFloatsModel?.fpTag?.let {
      WebEngageController.trackEvent(
        FACEBOOK_PAGE_DISCONNECTED,
        DIGITAL_CHANNELS,
        it
      )
    }
  }

  override fun updateInfo() {
    requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.facebookpage }
    super.updateInfo()
  }
}