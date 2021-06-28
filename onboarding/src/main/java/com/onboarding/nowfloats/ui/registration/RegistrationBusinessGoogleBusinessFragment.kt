package com.onboarding.nowfloats.ui.registration

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import auth.google.GoogleLoginHelper
import auth.google.constants.GoogleGraphPath
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.NetworkUtils
import com.framework.webengageconstant.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessGoogleBinding
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.clear
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.model.channel.request.isLinkedGoogleBusiness
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthResponse
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthTokenRequest
import com.onboarding.nowfloats.model.googleAuth.location.LocationNew
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.rest.response.AccountLocationResponse
import com.onboarding.nowfloats.ui.InternetErrorDialog
import com.onboarding.nowfloats.utils.WebEngageController

class RegistrationBusinessGoogleBusinessFragment : BaseRegistrationFragment<FragmentRegistrationBusinessGoogleBinding>(), GoogleLoginHelper {

  private var channelAccessToken = ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.googlemybusiness.name.toLowerCase())
  private var googleChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var isShowProfile = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessGoogleBusinessFragment {
      val fragment = RegistrationBusinessGoogleBusinessFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(GOOGLE_MY_BUSINESS_PAGE_LOAD, GOOGLE_MY_BUSINESS, NO_EVENT_VALUE)
    checkIsUpdate()
    binding?.googleChannels?.post {
      (binding?.googleChannels?.fadeIn()?.mergeWith(binding?.viewBusiness?.fadeIn()))
          ?.andThen(binding?.title?.fadeIn(100L))?.andThen(binding?.subTitle?.fadeIn(100L))
          ?.doOnComplete {
            if (isShowProfile) setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
          }?.andThen(binding?.linkGoogle?.fadeIn(500L))
          ?.andThen(binding?.skip?.fadeIn(100L))?.subscribe()
    }
    setOnClickListener(binding?.skip, binding?.linkGoogle)
    setSetSelectedGoogleChannels(channels)
    setSavedData()
  }

  private fun checkIsUpdate() {
    if (requestFloatsModel?.isUpdate == true) {
      requestFloatsModel?.channelAccessTokens?.forEach {
        if (it.type == ChannelAccessToken.AccessTokenType.googlemybusiness.name) {
          channelAccessToken = it
          channelAccessToken.profilePicture = ""
          isShowProfile = true
        }
      }
    }
  }

  override fun setSavedData() {
    val channelAccessToken = requestFloatsModel?.channelAccessTokens?.firstOrNull { it.getType() == channelAccessToken.getType() } ?: return
    setProfileDetails(channelAccessToken.userAccountName, channelAccessToken.profilePicture)
    requestFloatsModel?.channelAccessTokens?.remove(channelAccessToken)
    this.channelAccessToken = channelAccessToken
  }

  private fun setSetSelectedGoogleChannels(list: ArrayList<ChannelModel>) {
    val selectedItems = list.filter { it.isGoogleBusinessChannel() }.map { it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it }
    googleChannelsAdapter = binding?.googleChannels?.setGridRecyclerViewAdapter(baseActivity, selectedItems.size, selectedItems)
    googleChannelsAdapter?.notifyDataSetChanged()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.skip -> {
        WebEngageController.trackEvent(GOOGLE_MY_BUSINESS_CLICK_TO_SKIP, GOOGLE_MY_BUSINESS, NO_EVENT_VALUE)
        gotoNextScreen(true)
      }
      binding?.linkGoogle -> {
        if (channelAccessToken.isLinkedGoogleBusiness()) {
          gotoNextScreen()
        } else if (!NetworkUtils.isNetworkConnected()) {
          InternetErrorDialog().show(parentFragmentManager, InternetErrorDialog::class.java.name)
        } else {
          WebEngageController.trackEvent(GOOGLE_MY_BUSINESS_CLICK_TO_CONNECT, GOOGLE_MY_BUSINESS, NO_EVENT_VALUE)
          googleLoginCallback(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
        }
      }
    }
  }

  private fun gotoNextScreen(isSkip: Boolean = false) {
    if (channelAccessToken.isLinkedGoogleBusiness() && isSkip.not()) requestFloatsModel?.channelAccessTokens?.add(channelAccessToken)
    when {
      channels.haveFacebookPage() -> gotoFacebookPage()
      channels.haveFacebookShop() -> gotoFacebookShop()
      channels.haveTwitterChannels() -> gotoTwitterDetails()
      channels.haveWhatsAppChannels() -> gotoWhatsAppCallDetails()
      else -> gotoBusinessApiCallDetails()
    }
  }

  override fun setProfileDetails(name: String?, profilePicture: String?) {
    val binding = binding?.googlePageSuccess ?: return
    this.binding?.skip?.gone()
    binding.maimView.visible()
    binding.maimView.alpha = 1F
    binding.disconnect.setOnClickListener { disconnectGooglePage() }
    this.binding?.title?.text = resources.getString(R.string.google_page_connected)
    this.binding?.subTitle?.text = resources.getString(R.string.google_allows_digital_business_boost)
    this.binding?.linkGoogle?.text = resources.getString(R.string.save_continue)
    binding.profileTitle.text = name
    binding.channelType.setImageResource(R.drawable.ic_google_business_n)
    if (profilePicture?.isNotBlank() == true) {
      baseActivity.glideLoad(binding.profileImage, profilePicture, R.drawable.ic_user3)
    }
  }

  private fun disconnectGooglePage() {
    WebEngageController.trackEvent(GOOGLE_MY_BUSINESS_CLICK_TO_DISCONNECT, GOOGLE_MY_BUSINESS, NO_EVENT_VALUE)
    logoutGoogle(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
    binding?.skip?.visible()
    binding?.googlePageSuccess?.maimView?.gone()
    this.binding?.title?.text = resources.getString(R.string.does_your_business_already_have_a_google_page)
    binding?.subTitle?.text = resources.getString(R.string.google_page_connect_later_Skip)
    binding?.linkGoogle?.text = resources.getString(R.string.sync_google_page)
    channelAccessToken.clear()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == GoogleGraphPath.RC_SIGN_IN) handleGoogleSignInResult(data)
  }


  override fun onGoogleLoginSuccess(result: GoogleSignInAccount?) {
    showProgress()
    val userId = result?.id
    val request = GoogleAuthTokenRequest(GoogleGraphPath.SERVER_CLIENT_ID, GoogleGraphPath.SERVER_CLIENT_SECRET, result?.serverAuthCode)
    viewModel?.getGoogleAuthToken(request)?.observeOnce(viewLifecycleOwner, Observer {
      val response = it as? GoogleAuthResponse
      if (response != null && response.access_token.isNullOrEmpty().not()) {
        viewModel?.getAccountLocationsGMB(response.getAuth(), userId)?.observeOnce(viewLifecycleOwner, Observer { it1 ->
          hideProgress()
          val responseLocation = it1 as? AccountLocationResponse
          if ((it1.status == 200 || it1.status == 201 || it1.status == 202) && responseLocation?.locations.isNullOrEmpty().not()) {
            selectLocation(result, response, responseLocation?.locations)
          } else {
            logoutGoogle(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
            GmbLocationAddDialog().show(parentFragmentManager, GmbLocationAddDialog::class.java.name)
          }
        })
      } else {
        hideProgress()
        logoutGoogle(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
        onGoogleLoginError(ApiException(Status(400, "Auth token getting error.")))
      }
    })

  }

  private fun selectLocation(result: GoogleSignInAccount?, responseAuth: GoogleAuthResponse?, locations: List<LocationNew>?) {
    val singleItems = ArrayList<String>()
    locations?.forEach { it.locationName?.let { it1 -> singleItems.add(it1) } }
    var checkedItem = 0
    AlertDialog.Builder(baseActivity, R.style.DialogTheme).setTitle(getString(R.string.select_the_location_on_map))
        .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
          dialog.dismiss()
          val data = locations?.firstOrNull { singleItems[checkedItem] == it.locationName }
          requestFloatsModel?.fpTag?.let { WebEngageController.trackEvent(GOOGLE_MY_BUSINESS_AND_GOOGLE_MAPS_CONNECTED, DIGITAL_CHANNELS, it) }
          setDataGoogle(result, responseAuth, data)
          setProfileDetails(data?.locationName?.capitalizeWords(), result?.photoUrl?.toString())
        }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
          logoutGoogle(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
          dialog.dismiss()
        }
        .setSingleChoiceItems(singleItems.toTypedArray(), checkedItem) { _, position ->
          checkedItem = position
        }.setCancelable(false).show()
  }

  private fun setDataGoogle(result: GoogleSignInAccount?, responseAuth: GoogleAuthResponse?, data: LocationNew?) {
    val res = ChannelTokenResponse(responseAuth?.access_token, responseAuth?.token_type, responseAuth?.expires_in, responseAuth?.refresh_token)
    channelAccessToken.token_expiry = responseAuth?.getExpiryDate()
    channelAccessToken.invalid = false
    channelAccessToken.token_response = res
    channelAccessToken.refresh_token = responseAuth?.refresh_token
    channelAccessToken.userAccessTokenKey = responseAuth?.access_token
    channelAccessToken.userAccountId = result?.id
    channelAccessToken.userAccountName = result?.displayName
    channelAccessToken.LocationId = data?.name //TODO name refer location id
    channelAccessToken.LocationName = data?.locationName
    channelAccessToken.verified_location = null
  }

  override fun onGoogleLoginError(error: ApiException?) {
    showLongToast(getString(R.string.google_login_error))
  }

  override fun updateInfo() {
    requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.googlemybusiness }
    super.updateInfo()
  }
}