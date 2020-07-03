package com.onboarding.nowfloats.ui.registration

import android.Manifest.permission.CALL_PHONE
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.NetworkUtils
import com.framework.views.DotProgressBar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessApiBinding
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.model.channel.getType
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.ui.InternetErrorDialog
import com.onboarding.nowfloats.ui.updateChannel.startFragmentActivity
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel

class RegistrationBusinessApiFragment : BaseRegistrationFragment<FragmentRegistrationBusinessApiBinding>(), RecyclerItemClickListener {

  private var list = ArrayList<ProcessApiSyncModel>()
  private val connectedChannels = ArrayList<ChannelModel>()
  private var apiProcessAdapter: AppBaseRecyclerViewAdapter<ProcessApiSyncModel>? = null
  private var floatingPointId = ""


  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessApiFragment {
      val fragment = RegistrationBusinessApiFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  @ExperimentalStdlibApi
  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.next, binding?.retry, binding?.supportCustomer)
    binding?.categoryImage?.setImageDrawable(requestFloatsModel?.categoryDataModel?.getImage(baseActivity))
    binding?.categoryImage?.setTintColor(ResourcesCompat.getColor(resources, R.color.white, baseActivity.theme))
    if (requestFloatsModel?.isUpdate == true && requestFloatsModel?.floatingPointId.isNullOrEmpty().not()) {
      floatingPointId = requestFloatsModel?.floatingPointId!!
    }
    setProcessApiSyncModel()
    setApiProcessAdapter(list)
    apiHitBusiness()
  }

  private fun apiHitBusiness() {
    getDotProgress()?.let {
      binding?.textBtn?.gone()
      binding?.errorView?.gone()
      binding?.next?.visible()
      binding?.next?.addView(it)
      if (NetworkUtils.isNetworkConnected()) {
        it.startAnimation()
        putCreateBusinessOnboarding(it)
      } else {
        val dialog = InternetErrorDialog()
        dialog.onRetryTapped = {
          it.startAnimation()
          putCreateBusinessOnboarding(it)
        }
        dialog.show(parentFragmentManager, dialog.javaClass.name)
      }
    }
  }

  private fun setProcessApiSyncModel() {
    val channels = this.channels
    val connectedChannelsAccessTokens = requestFloatsModel?.channelAccessTokens?.map { it.getType() }
    val connectedWhatsApp = requestFloatsModel?.channelActionDatas?.firstOrNull()

    for (channel in channels) {
      val isSelected = when (channel.getType()) {
        ChannelType.G_SEARCH -> true
        ChannelType.FB_PAGE -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.facebookpage)
        ChannelType.G_MAPS -> true
        ChannelType.FB_SHOP -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.facebookshop)
        ChannelType.WAB -> connectedWhatsApp != null
        ChannelType.T_FEED -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.twitter)
        ChannelType.G_BUSINESS -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.googlemybusiness)
        null -> false
      }
      if (isSelected == true) connectedChannels.add(channel)
    }
    list.clear()
    if (requestFloatsModel?.isUpdate == true) {
      if (connectedChannels.isNullOrEmpty()) backToDigitalChannelUpdate()
      else list.addAll(ProcessApiSyncModel().getDataStartUpdate(connectedChannels))
    } else {
      list.addAll(ProcessApiSyncModel().getDataStart(connectedChannels))
    }
    binding?.title?.text = resources.getString(
        if (requestFloatsModel?.isUpdate == false)
          R.string.processing_your_business_information
        else R.string.processing_your_digital_channel
    )
  }

  private fun putCreateBusinessOnboarding(dotProgressBar: DotProgressBar) {
    if (checkFpCreate(dotProgressBar)) return
    val request = getBusinessRequest()
    viewModel?.putCreateBusinessOnboarding(userProfileId, request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        if (it.stringResponse.isNullOrEmpty().not()) {
          connectedChannels.forEach { it1 ->
            it1.status = takeIf { (ChannelType.G_SEARCH == it1.getType() || ChannelType.G_MAPS == it1.getType()) }?.let { ProcessApiSyncModel.SyncStatus.SUCCESS.name }
          }
          floatingPointId = it.stringResponse ?: ""
          apiProcessChannelWhatsApp(dotProgressBar, floatingPointId)
        } else updateError("Floating point return null", it.status, "CREATE")
      } else updateError(it.error?.localizedMessage, it.status, "CREATE")
    })
  }

  private fun checkFpCreate(dotProgressBar: DotProgressBar): Boolean {
    if (floatingPointId.isNotEmpty()) {
      connectedChannels.forEach { it1 ->
        it1.status = takeIf { (ChannelType.G_SEARCH == it1.getType() || ChannelType.G_MAPS == it1.getType()) }?.let { ProcessApiSyncModel.SyncStatus.SUCCESS.name }
      }
      apiProcessChannelWhatsApp(dotProgressBar, floatingPointId)
      return true
    }
    return false
  }

  private fun apiProcessChannelWhatsApp(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.channelActionDatas.isNullOrEmpty().not()) {
      val authorization = auth?.let { it } ?: ""
      val dataRequest = UpdateChannelActionDataRequest(requestFloatsModel?.channelActionDatas?.firstOrNull(), requestFloatsModel?.getWebSiteId())
      viewModel?.postUpdateWhatsappRequest(dataRequest, authorization)
          ?.observeOnce(viewLifecycleOwner, Observer {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              connectedChannels.forEach { it1 ->
                it1.status = takeIf { ChannelType.WAB == it1.getType() }?.let { ProcessApiSyncModel.SyncStatus.SUCCESS.name }
              }
              apiProcessChannelAccessTokens(dotProgressBar, floatingPointId)
            } else {
              connectedChannels.forEach { it1 ->
                it1.status = takeIf { (ChannelType.G_SEARCH == it1.getType() || ChannelType.G_MAPS == it1.getType()).not() }?.let { ProcessApiSyncModel.SyncStatus.ERROR.name }
              }
              updateError(it.error?.localizedMessage, it.status, "CHANNELS")
            }
          })
    } else apiProcessChannelAccessTokens(dotProgressBar, floatingPointId)
  }


  private fun apiProcessChannelAccessTokens(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.channelAccessTokens.isNullOrEmpty().not()) {
      if (clientId != null) {
        var index = 0
        requestFloatsModel?.channelAccessTokens?.forEach { channelAccessToken ->
          var isBreak = false
          viewModel?.updateChannelAccessToken(UpdateChannelAccessTokenRequest(channelAccessToken, clientId!!, floatingPointId))?.observeOnce(viewLifecycleOwner, Observer {
            index += 1
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              if (requestFloatsModel?.channelAccessTokens?.size == index) apiBusinessComplete(dotProgressBar, floatingPointId)
            } else {
              isBreak = true
              connectedChannels.forEach { it1 ->
                it1.status = takeIf { (ChannelType.G_SEARCH == it1.getType() || ChannelType.G_MAPS == it1.getType() || ChannelType.WAB == it1.getType()).not() }?.let { ProcessApiSyncModel.SyncStatus.ERROR.name }
              }
              updateError(it.error?.localizedMessage, it.status, "CHANNELS")
            }
          })
          if (isBreak) return@forEach
        }
      }
    } else apiBusinessComplete(dotProgressBar, floatingPointId)
  }

  private fun updateError(message: String?, status: Int?, type: String) {
    list.clear()
    when (type) {
      "CREATE" -> list.addAll(ProcessApiSyncModel().getDataErrorFP(connectedChannels))
      "CHANNELS" -> {
        if (requestFloatsModel?.isUpdate == true) {
          list.addAll(ProcessApiSyncModel().getDataErrorChannelsUpdate(connectedChannels))
        } else list.addAll(ProcessApiSyncModel().getDataErrorChannels(connectedChannels))
      }
    }
    apiProcessAdapter?.notify(list)
    binding?.errorView?.visible()
    binding?.errorApi?.text = if (message.isNullOrEmpty().not()) "$message: $status" else "Connection error: $status"
    binding?.next?.gone()
  }

  private fun apiBusinessComplete(dotProgressBar: DotProgressBar, floatingPointId: String) {
    binding?.apiRecycler?.post {
      requestFloatsModel?.floatingPointId = floatingPointId
      updateInfo()
      list.clear()
      if (requestFloatsModel?.isUpdate == true) {
        list.addAll(ProcessApiSyncModel().getDataSuccessUpdate(connectedChannels))
      } else list.addAll(ProcessApiSyncModel().getDataSuccess(connectedChannels))
      dotProgressBar.stopAnimation()
      dotProgressBar.removeAllViews()
      binding?.next?.alpha = 1F
      binding?.textBtn?.visibility = View.VISIBLE

      binding?.textBtn?.text = resources.getString(
          if (requestFloatsModel?.isUpdate == true) R.string.digital_channel else R.string.start_your_digital_journey
      )
      binding?.container?.setBackgroundResource(R.drawable.bg_card_blue)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        binding?.category?.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.white)
      binding?.title?.setTextColor(getColor(R.color.white))
      binding?.title?.text = resources.getString(
          if (requestFloatsModel?.isUpdate == false)
            R.string.business_information_completed
          else R.string.digital_channel_completed
      )
      binding?.categoryImage?.setTintColor(getColor(R.color.dodger_blue_two))
      apiProcessAdapter?.notify(list)
    }
  }

  private fun setApiProcessAdapter(list: ArrayList<ProcessApiSyncModel>?) {
    list?.let {
      apiProcessAdapter = AppBaseRecyclerViewAdapter(baseActivity, it, this)
      binding?.apiRecycler?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.apiRecycler?.adapter = apiProcessAdapter
      binding?.apiRecycler?.let { it1 -> apiProcessAdapter?.runLayoutAnimation(it1) }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.next -> if ((binding?.textBtn?.visibility == View.VISIBLE)) {
        //TODO check for update channels
        if (binding?.textBtn?.text == resources.getString(R.string.digital_channel)) {
          backToDigitalChannelUpdate()
        } else gotoRegistrationComplete()
      }
      binding?.supportCustomer -> {
        try {
          val intent = Intent(Intent.ACTION_CALL)
          intent.data = Uri.parse("tel:18601231233")
          if (ContextCompat.checkSelfPermission(baseActivity, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            baseActivity.startActivity(intent)
          } else requestPermissions(arrayOf(CALL_PHONE), 1)
        } catch (e: ActivityNotFoundException) {
          showLongToast("Error in your phone call!")
        }
      }
      binding?.retry -> apiHitBusiness()
    }
  }

  override fun getViewModelClass(): Class<BusinessCreateViewModel> {
    return BusinessCreateViewModel::class.java
  }

  private fun getBusinessRequest(): BusinessCreateRequest {
    val createRequest = BusinessCreateRequest()
    createRequest.autoFillSampleWebsiteData = true
    createRequest.webTemplateId = requestFloatsModel?.categoryDataModel?.webTemplateId
    createRequest.clientId = clientId
    createRequest.tag = requestFloatsModel?.contactInfo?.domainName
    createRequest.name = requestFloatsModel?.contactInfo?.businessName
    createRequest.address = requestFloatsModel?.contactInfo?.address
    createRequest.city = ""
    createRequest.pincode = ""
    createRequest.country = "India"
    createRequest.primaryNumber = requestFloatsModel?.contactInfo?.number
    createRequest.email = requestFloatsModel?.contactInfo?.email
    createRequest.primaryNumberCountryCode = "+91"
    createRequest.uri = ""
    createRequest.fbPageName = requestFloatsModel?.channelAccessTokens?.firstOrNull { it.getType() == ChannelAccessToken.AccessTokenType.facebookpage }?.userAccountName
    createRequest.primaryCategory = requestFloatsModel?.categoryDataModel?.category_key
    createRequest.appExperienceCode = requestFloatsModel?.categoryDataModel?.experience_code
    //TODO: [Ronak] pass the widgetkeys (split by ,)
    return createRequest
  }

  private fun backToDigitalChannelUpdate() {
    val bundle = Bundle()
    bundle.putBoolean(PreferenceConstant.IS_UPDATE, true)
    bundle.putBoolean(PreferenceConstant.IS_START_ACTIVITY, true)
    bundle.putString(PreferenceConstant.GET_FP_EXPERIENCE_CODE, requestFloatsModel?.categoryDataModel?.experienceCode())
    bundle.putString(PreferenceConstant.KEY_FP_ID, requestFloatsModel?.floatingPointId)
    bundle.putString(PreferenceConstant.GET_FP_DETAILS_TAG, requestFloatsModel?.fpTag)
    bundle.putString(PreferenceConstant.WEBSITE_URL, requestFloatsModel?.websiteUrl)
    startFragmentActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle, clearTop = true)
    NavigatorManager.clearStackAndFormData()
  }

  fun isDigitalChannel(): Boolean {
    return if ((binding?.textBtn?.visibility == View.VISIBLE) && binding?.textBtn?.text == resources.getString(R.string.digital_channel)) {
      backToDigitalChannelUpdate()
      true
    } else false
  }
}