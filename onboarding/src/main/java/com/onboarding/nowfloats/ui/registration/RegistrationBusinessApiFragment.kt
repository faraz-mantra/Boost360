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
import com.framework.pref.WA_KEY
import com.framework.utils.NetworkUtils
import com.framework.views.DotProgressBar

import com.framework.webengageconstant.DIGITAL_CHANNELS
import com.framework.webengageconstant.WHATS_APP_CONNECTED
import com.invitereferrals.invitereferrals.InviteReferralsApi
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessApiBinding
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.business.purchasedOrder.ActivatePurchasedOrderRequest
import com.onboarding.nowfloats.model.business.purchasedOrder.ConsumptionConstraint
import com.onboarding.nowfloats.model.business.purchasedOrder.PurchasedExpiry
import com.onboarding.nowfloats.model.business.purchasedOrder.PurchasedWidget
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.model.channel.getType
import com.onboarding.nowfloats.model.channel.request.*
import com.onboarding.nowfloats.model.plan.Plan15DaysResponse
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.ui.InternetErrorDialog
import com.onboarding.nowfloats.ui.updateChannel.startFragmentChannelActivity
import com.onboarding.nowfloats.utils.WebEngageController
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel
import java.util.*
import kotlin.collections.ArrayList


class RegistrationBusinessApiFragment : BaseRegistrationFragment<FragmentRegistrationBusinessApiBinding>(), RecyclerItemClickListener {

  private var list = ArrayList<ProcessApiSyncModel>()
  private val connectedChannels = ArrayList<ChannelModel>()
  private var apiProcessAdapter: AppBaseRecyclerViewAdapter<ProcessApiSyncModel>? = null
  private var floatingPointId = ""
  private var isSyncCreateFpApi = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessApiFragment {
      val fragment = RegistrationBusinessApiFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.next, binding?.retry, binding?.supportCustomer)
    binding?.categoryImage?.setImageDrawable(requestFloatsModel?.categoryDataModel?.getImage(baseActivity))
    binding?.categoryImage?.setTintColor(ResourcesCompat.getColor(resources, R.color.white, baseActivity.theme))
    if (requestFloatsModel?.floatingPointId.isNullOrEmpty().not()) floatingPointId = requestFloatsModel?.floatingPointId!!
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
    val connectedChannelsAccessTokens = requestFloatsModel?.getConnectedAccessToken()?.map { it.getType() }
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
      if (connectedChannels.isNullOrEmpty()) backToDigitalChannelUpdate() else list.addAll(ProcessApiSyncModel().getDataStartUpdate(connectedChannels))
    } else {
      list.addAll(ProcessApiSyncModel().getDataStart(connectedChannels))
    }
    binding?.title?.text = resources.getString(if (requestFloatsModel?.isUpdate == false) R.string.processing_your_business_information else R.string.processing_your_digital_channel)
  }

  private fun putCreateBusinessOnboarding(dotProgressBar: DotProgressBar) {
    if (checkFpCreate().not()) {
      val request = getBusinessRequest()
      isSyncCreateFpApi = true
      viewModel?.putCreateBusinessOnboarding(userProfileId, request)?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          if (it.stringResponse.isNullOrEmpty().not()) {
            connectedChannels.forEach { it1 ->
              it1.status = takeIf { (ChannelType.G_SEARCH == it1.getType() || ChannelType.G_MAPS == it1.getType()) }?.let { ProcessApiSyncModel.SyncStatus.SUCCESS.name }
            }
            floatingPointId = it.stringResponse ?: ""
            saveFpCreateData()
            setReferralCode(floatingPointId)
            apiProcessChannelWhatsApp(dotProgressBar, floatingPointId)
          } else updateError("Floating point return null", it.status, "CREATE")
        } else updateError(it.error?.localizedMessage, it.status, "CREATE")
      })
    } else apiProcessChannelWhatsApp(dotProgressBar, floatingPointId)
  }

  private fun setReferralCode(floatingPointId: String) {
    if (prefReferral?.getString(PreferenceConstant.REFER_CODE_APP, "").isNullOrEmpty().not()) {
      var email = pref?.getString(PreferenceConstant.PERSON_EMAIL, "")
      if (email.isNullOrEmpty().not()) email = requestFloatsModel?.contactInfo?.email
      InviteReferralsApi.getInstance(baseActivity).tracking("register", email, 0, prefReferral?.getString(PreferenceConstant.REFER_CODE_APP, ""), floatingPointId)
      prefReferral?.edit()?.apply {
        putString(PreferenceConstant.REFER_CODE_APP, "")
        apply()
      }
    }
  }

  private fun saveFpCreateData() {
    requestFloatsModel?.isFpCreate = true
    requestFloatsModel?.floatingPointId = floatingPointId
    updateInfo()
  }

  private fun checkFpCreate(): Boolean {
    if (floatingPointId.isNotEmpty()) {
      connectedChannels.forEach { it1 ->
        it1.status = takeIf { (ChannelType.G_SEARCH == it1.getType() || ChannelType.G_MAPS == it1.getType()) }?.let { ProcessApiSyncModel.SyncStatus.SUCCESS.name }
      }
      return true
    }
    return false
  }

  private fun apiProcessChannelWhatsApp(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.channelActionDatas.isNullOrEmpty().not()) {
      val dataRequest = UpdateChannelActionDataRequest(requestFloatsModel?.channelActionDatas?.firstOrNull(), requestFloatsModel?.getWebSiteId())
      viewModel?.postUpdateWhatsappRequest(dataRequest,WA_KEY)
          ?.observeOnce(viewLifecycleOwner, {
            if (it.isSuccess()) {
              requestFloatsModel?.fpTag?.let { WebEngageController.trackEvent(WHATS_APP_CONNECTED, DIGITAL_CHANNELS, it) }
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
    if (requestFloatsModel?.getConnectedAccessToken().isNullOrEmpty().not()) {
      if (clientId != null) {
        var index = 0
        requestFloatsModel?.getConnectedAccessToken()?.forEach { channelAccessToken ->
          var isBreak = false
          viewModel?.updateChannelAccessToken(UpdateChannelAccessTokenRequest(channelAccessToken, clientId!!, floatingPointId))?.observeOnce(viewLifecycleOwner, Observer {
            index += 1
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              if (requestFloatsModel?.getConnectedAccessToken()?.size == index) apiBusinessActivatePlan(dotProgressBar, floatingPointId)
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
    } else apiBusinessActivatePlan(dotProgressBar, floatingPointId)
  }

  private fun updateError(message: String?, status: Int?, type: String) {
    list.clear()
    when (type) {
      "CREATE" -> {
        isSyncCreateFpApi = false
        list.addAll(ProcessApiSyncModel().getDataErrorFP(connectedChannels))
      }
      "CHANNELS" -> {
        if (requestFloatsModel?.isUpdate == true) {
          list.addAll(ProcessApiSyncModel().getDataErrorChannelsUpdate(connectedChannels))
        } else list.addAll(ProcessApiSyncModel().getDataErrorChannels(connectedChannels))
      }
      "PLAN_ACTIVATION" -> list.addAll(ProcessApiSyncModel().getDataErrorActivatePlan(connectedChannels))
    }
    apiProcessAdapter?.notify(list)
    binding?.errorView?.visible()
    binding?.errorApi?.text = if (message.isNullOrEmpty().not()) "$message: $status" else "Connection error: $status"
    binding?.next?.gone()
  }

  private fun apiBusinessActivatePlan(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.isUpdate == true) {
      apiBusinessComplete(dotProgressBar, floatingPointId)
    } else {
      viewModel?.getCategoriesPlan(baseActivity)?.observeOnce(viewLifecycleOwner, { res ->
        val responsePlan = res as? Plan15DaysResponse
        val request = getRequestPurchasedOrder(floatingPointId, responsePlan)
        viewModel?.postActivatePurchasedOrder(clientId, request)?.observeOnce(viewLifecycleOwner, {
          if (it.isSuccess()) {
            apiBusinessComplete(dotProgressBar, floatingPointId)
          } else {
            updateError(it.error?.localizedMessage, it.status, "PLAN_ACTIVATION")
          }
        })
      })
    }
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
      binding?.next?.removeView(dotProgressBar)
      dotProgressBar.removeAllViews()
      binding?.errorView?.gone()
      binding?.next?.visible()
      binding?.next?.alpha = 1F
      binding?.textBtn?.visibility = View.VISIBLE

      binding?.textBtn?.text = resources.getString(if (requestFloatsModel?.isUpdate == true) R.string.digital_channel else R.string.start_your_digital_journey)
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

  private fun getRequestPurchasedOrder(floatingPointId: String, responsePlan: Plan15DaysResponse?): ActivatePurchasedOrderRequest {
    val widList = java.util.ArrayList<PurchasedWidget>()
    requestFloatsModel?.categoryDataModel?.getEmptySections()?.forEach {
      it.getWidList().forEach { key ->
        val widget = PurchasedWidget(widgetKey = key, name = it.title, quantity = 1, desc = it.desc, recurringPaymentFrequency = "MONTHLY",
            isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
            consumptionConstraint = ConsumptionConstraint("DAYS", 30), images = java.util.ArrayList(),
            expiry = PurchasedExpiry("YEARS", 10))
        widList.add(widget)
      }
    }

    if (responsePlan?.isSuccess() == true && responsePlan.data.isNullOrEmpty().not()) {
      val response = responsePlan.data?.get(0)!!
      response.widgetKeys?.forEach { key ->
        val widgetN = widList.find { it.widgetKey.equals(key) }
        if (widgetN != null) {
          widgetN.consumptionConstraint?.metricValue = 15
          widgetN.expiry?.key = "DAYS"
          widgetN.expiry?.value = 15
        } else {
          widList.add(
              PurchasedWidget(widgetKey = key, name = "", quantity = 1, desc = "", recurringPaymentFrequency = "MONTHLY",
                  isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
                  consumptionConstraint = ConsumptionConstraint("DAYS", 15), images = java.util.ArrayList(),
                  expiry = PurchasedExpiry("DAYS", 15))
          )
        }
      }
      response.extraProperties?.forEach { keyValue ->
        val widgetN2 = widList.find { it.widgetKey.equals(keyValue.widget) }
        if (widgetN2 != null) {
          widgetN2.consumptionConstraint?.metricValue = 15
          widgetN2.expiry?.key = "DAYS"
          widgetN2.expiry?.value = keyValue.value
        } else {
          widList.add(
              PurchasedWidget(widgetKey = keyValue.widget, name = "", quantity = 1, desc = "", recurringPaymentFrequency = "MONTHLY",
                  isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
                  consumptionConstraint = ConsumptionConstraint("DAYS", 15), images = java.util.ArrayList(),
                  expiry = PurchasedExpiry("DAYS", keyValue.value))
          )
        }
      }
    }
    return ActivatePurchasedOrderRequest(com.framework.pref.clientId, floatingPointId, "EXTENSION", widList)
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
        if (binding?.textBtn?.text == resources.getString(R.string.digital_channel)) backToDigitalChannelUpdate() else gotoRegistrationComplete()
      }
      binding?.supportCustomer -> {
        try {
          val intent = Intent(Intent.ACTION_CALL)
          intent.data = Uri.parse("tel:${resources.getString(R.string.contact_us_number)}")
          if (ContextCompat.checkSelfPermission(baseActivity, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            baseActivity.startActivity(intent)
          } else requestPermissions(arrayOf(CALL_PHONE), 1)
        } catch (e: ActivityNotFoundException) {
          showLongToast(getString(R.string.error_in_your_phone_call))
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
    createRequest.address = ""
    createRequest.city = requestFloatsModel?.contactInfo?.addressCity
    createRequest.pincode = ""
    createRequest.country = "India"
    createRequest.primaryNumber = requestFloatsModel?.contactInfo?.getNumberN()
    createRequest.email = requestFloatsModel?.contactInfo?.getEmailN()
    createRequest.primaryNumberCountryCode = "+91"
    createRequest.uri = ""
    createRequest.fbPageName = requestFloatsModel?.getConnectedAccessToken()?.firstOrNull { it.getType() == ChannelAccessToken.AccessTokenType.facebookpage }?.userAccountName
    createRequest.primaryCategory = requestFloatsModel?.categoryDataModel?.category_key
    createRequest.appExperienceCode = requestFloatsModel?.categoryDataModel?.experience_code
    createRequest.whatsAppNumber = requestFloatsModel?.channelActionDatas?.firstOrNull()?.getNumberWithCode()
    createRequest.whatsAppNotificationOptIn = requestFloatsModel?.whatsappEntransactional ?: false
    createRequest.boostXWebsiteUrl = "www.${requestFloatsModel?.contactInfo?.domainName?.toLowerCase(Locale.ROOT)}.nowfloats.com"
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
    startFragmentChannelActivity(FragmentType.MY_DIGITAL_CHANNEL, bundle, clearTop = true)
    NavigatorManager.clearStackAndFormData()
  }

  fun isDigitalChannel(): Boolean {
    return if ((binding?.textBtn?.visibility == View.VISIBLE) && binding?.textBtn?.text == resources.getString(R.string.digital_channel)) {
      backToDigitalChannelUpdate()
      true
    } else false
  }

  fun isBackBlock(): Boolean {
    return (isSyncCreateFpApi || requestFloatsModel?.isFpCreate ?: false)
  }
}