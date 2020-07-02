package com.onboarding.nowfloats.ui.registration

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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessGoogleBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.clear
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.model.channel.request.isLinked
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthResponse
import com.onboarding.nowfloats.model.googleAuth.GoogleAuthTokenRequest
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.ui.InternetErrorDialog

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
    val channelAccessToken = requestFloatsModel?.channelAccessTokens
        ?.firstOrNull { it.getType() == channelAccessToken.getType() } ?: return
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
      binding?.skip -> gotoNextScreen()
      binding?.linkGoogle -> {
        if (channelAccessToken.isLinked()) {
          gotoNextScreen()
        } else if (!NetworkUtils.isNetworkConnected()) {
          InternetErrorDialog().show(parentFragmentManager, InternetErrorDialog::class.java.name)
        } else googleLoginCallback(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
      }
    }
  }

  private fun gotoNextScreen() {
    if (channelAccessToken.isLinked()) {
      requestFloatsModel?.channelAccessTokens?.add(channelAccessToken)
    }
    when {
      channels.haveFacebookPage() -> {
        gotoFacebookPage()
      }
      channels.haveFacebookShop() -> {
        gotoFacebookShop()
      }
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
        viewModel?.getAccountListGMB(response.getAuth(), userId)?.observeOnce(viewLifecycleOwner, Observer {
          logoutGoogle(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
          hideProgress()
          showLongToast("Business location not found.")
//          hideProgress()
//          setProfileDetails(result?.displayName, result?.photoUrl?.toString())

        })
      } else {
        hideProgress()
        showLongToast("Google login error.")
        logoutGoogle(baseActivity, GoogleGraphPath.GMB_SIGN_IN)
      }
    })

  }

  override fun onGoogleLoginError(error: ApiException?) {
    showLongToast("Google login error.")
  }

  override fun updateInfo() {
    requestFloatsModel?.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.googlemybusiness }
    super.updateInfo()
  }
}