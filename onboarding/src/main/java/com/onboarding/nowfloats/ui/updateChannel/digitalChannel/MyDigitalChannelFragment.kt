package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.firestore.FirestoreManager
import com.framework.pref.clientId
import com.framework.webengageconstant.MY_DIGITAL_CHANNEL
import com.framework.webengageconstant.MY_DIGITAL_CHANNEL_LOAD
import com.framework.webengageconstant.MY_DIGITAL_CHANNEL_SYNC_BUTTON_CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.constant.*
import com.onboarding.nowfloats.databinding.FragmentDigitalChannelBinding
import com.onboarding.nowfloats.extensions.addInt
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.rest.response.category.ResponseDataCategory
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import com.onboarding.nowfloats.ui.startFragmentActivity
import com.onboarding.nowfloats.ui.updateChannel.ContainerDigitalChannelActivity
import com.onboarding.nowfloats.ui.updateChannel.DigitalChannelActivity
import com.onboarding.nowfloats.utils.WebEngageController
import com.onboarding.nowfloats.viewmodel.category.CategoryViewModel
import io.reactivex.Completable
import java.util.*
import kotlin.collections.ArrayList

class MyDigitalChannelFragment : AppBaseFragment<FragmentDigitalChannelBinding, CategoryViewModel>(), RecyclerItemClickListener {

  private var connectedChannels: ArrayList<String> = arrayListOf()

  private val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, 0)
    }

  private val mPrefTwitter: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(
        PreferenceConstant.PREF_NAME_TWITTER,
        Context.MODE_PRIVATE
      )
    }

  private var requestFloatsModel: RequestFloatsModel? = null
  private var adapterConnect: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var adapterDisconnect: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private var listDisconnect: ArrayList<ChannelModel>? = null
  private var listConnect: ArrayList<ChannelModel>? = null
  private var channelTypeClick: String = ""
  var websiteUrl: String = ""
  private lateinit var progress: ProgressChannelDialog

  private val selectedChannels: ArrayList<ChannelModel>
    get() {
      return (listDisconnect?.filter { it -> it.isSelected == true }
        ?: ArrayList()) as ArrayList<ChannelModel>
    }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): MyDigitalChannelFragment {
      val fragment = MyDigitalChannelFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_digital_channel
  }

  override fun getViewModelClass(): Class<CategoryViewModel> {
    return CategoryViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(MY_DIGITAL_CHANNEL_LOAD, MY_DIGITAL_CHANNEL, NO_EVENT_VALUE)
    progress = ProgressChannelDialog.newInstance()
    updateRequestGetChannelData()
    binding?.syncBtn?.setOnClickListener { syncChannels() }
  }

  private fun updateRequestGetChannelData() {
    val bundle = arguments
    val isUpdate = bundle?.getBoolean(PreferenceConstant.IS_UPDATE)
    websiteUrl = bundle?.getString(PreferenceConstant.WEBSITE_URL) ?: ""
    channelTypeClick = bundle?.getString(IntentConstant.CHANNEL_TYPE.name) ?: ""
    if (isUpdate != null && isUpdate) {
      NavigatorManager.clearRequest()
      val experienceCode = bundle.getString(PreferenceConstant.GET_FP_EXPERIENCE_CODE)
      if (experienceCode.isNullOrEmpty().not()) {
        val floatingPoint = bundle.getString(PreferenceConstant.KEY_FP_ID)
        val fpTag = bundle.getString(PreferenceConstant.GET_FP_DETAILS_TAG)
        showProgress(context?.getString(R.string.refreshing_your_channels), false)
        viewModel?.getCategories(baseActivity)?.observeOnce(viewLifecycleOwner, {
          if (it?.error != null) errorMessage(
            it.error?.localizedMessage ?: resources.getString(R.string.error_getting_category_data)
          )
          else {
            val categoryList = (it as? ResponseDataCategory)?.data
            val categoryData =
              categoryList?.singleOrNull { c -> c.experienceCode() == experienceCode }
            if (categoryData != null) {
              getChannelAccessToken(categoryData, floatingPoint, fpTag)
            } else errorMessage(resources.getString(R.string.error_getting_category_data))
          }
        })
      } else showShortToast(resources.getString(R.string.invalid_experience_code))
    }
  }

  private fun getChannelAccessToken(
    categoryData: CategoryDataModel?,
    floatingPoint: String?,
    fpTag: String?
  ) {
    viewModel?.getChannelsAccessTokenStatus(floatingPoint)?.observeOnce(viewLifecycleOwner, { it1 ->
      when {
        it1.error is NoNetworkException -> errorMessage(resources.getString(R.string.internet_connection_not_available))
        it1.isSuccess() -> {
          val response = it1 as? ChannelAccessStatusResponse
          setDataRequestChannels(categoryData, response?.channels, floatingPoint, fpTag)
        }
        it1.status == 404 || it1.status == 400 -> setDataRequestChannels(
          categoryData,
          null,
          floatingPoint,
          fpTag
        )
        else -> errorMessage(it1.message())
      }
    })
  }

  private fun setDataRequestChannels(
    categoryData: CategoryDataModel?,
    channelsAccessToken: ChannelsType?,
    floatingPoint: String?,
    fpTag: String?
  ) {
    val requestFloatsNew = RequestFloatsModel()
    requestFloatsNew.categoryDataModel = categoryData
    requestFloatsNew.isUpdate = true
    requestFloatsNew.floatingPointId = floatingPoint
    requestFloatsNew.fpTag = fpTag
    requestFloatsNew.websiteUrl = websiteUrl
    requestFloatsNew.categoryDataModel?.resetIsSelect()
    requestFloatsNew.categoryDataModel?.channels?.map {
      if (it.isGoogleSearch()) it.websiteUrl = websiteUrl
    }
    if (channelsAccessToken != null) {
      connectedChannels.clear()
      requestFloatsNew.categoryDataModel?.channels?.forEach { it1 ->
        var data: ChannelAccessToken? = null
        when {
          it1.getAccessTokenType() == ChannelsType.AccountType.facebookpage.name -> {
            val fbPage = channelsAccessToken.facebookpage
            if (fbPage?.status?.equals(CHANNEL_STATUS_SUCCESS, true) == true) {
              data = ChannelAccessToken(
                type = ChannelsType.AccountType.facebookpage.name,
                userAccessTokenKey = null,
                userAccountId = fbPage.account?.accountId,
                userAccountName = fbPage.account?.accountName
              )
              requestFloatsNew.channelAccessTokens?.add(data)
              it1.isSelected = true
              it1.channelAccessToken = data
              connectedChannels.add(ChannelsType.AccountType.facebookpage.name)
            }
          }
          it1.getAccessTokenType() == ChannelsType.AccountType.facebookshop.name -> {
            val fpShop = channelsAccessToken.facebookshop
            if (channelsAccessToken.facebookshop?.status?.equals(
                CHANNEL_STATUS_SUCCESS,
                true
              ) == true
            ) {
              data = ChannelAccessToken(
                type = ChannelsType.AccountType.facebookshop.name,
                userAccessTokenKey = null,
                userAccountId = fpShop?.account?.userAccountId,
                userAccountName = null,
                pixelId = null,
                catalogId = fpShop?.account?.catalogId,
                merchantSettingsId = fpShop?.account?.merchantSettingsId
              )
              requestFloatsNew.channelAccessTokens?.add(data)
              it1.isSelected = true
              it1.channelAccessToken = data
              connectedChannels.add(ChannelsType.AccountType.facebookshop.name)
            }
          }
          it1.getAccessTokenType() == ChannelsType.AccountType.twitter.name -> {
            val twitter = channelsAccessToken.twitter
            if (channelsAccessToken.twitter?.status?.equals(CHANNEL_STATUS_SUCCESS, true) == true) {
              data = ChannelAccessToken(
                type = ChannelsType.AccountType.twitter.name,
                userAccessTokenKey = null,
                userAccountId = twitter?.account?.accountId,
                userAccountName = twitter?.account?.accountName
              )
              requestFloatsNew.channelAccessTokens?.add(data)
              it1.isSelected = true
              it1.channelAccessToken = data
              connectedChannels.add(ChannelsType.AccountType.twitter.name)
            }
          }
          it1.getAccessTokenType() == ChannelsType.AccountType.googlemybusiness.name -> {
            val gmb = channelsAccessToken.googlemybusiness
            if (channelsAccessToken.googlemybusiness?.status?.equals(
                CHANNEL_STATUS_SUCCESS,
                true
              ) == true
            ) {
              data = ChannelAccessToken(
                type = ChannelsType.AccountType.googlemybusiness.name,
                token_expiry = null,
                invalid = null,
                token_response = ChannelTokenResponse(),
                refresh_token = null,
                userAccountName = gmb?.account?.accountName,
                userAccountId = gmb?.account?.accountId,
                LocationId = gmb?.account?.locationId,
                LocationName = gmb?.account?.locationName,
                userAccessTokenKey = null,
                verified_location = null
              )
              requestFloatsNew.channelAccessTokens?.add(data)
              it1.isSelected = true
              it1.channelAccessToken = data
              connectedChannels.add(ChannelsType.AccountType.googlemybusiness.name)
            }
          }
        }
      }
    }
    getWhatsAppData(requestFloatsNew, channelsAccessToken)
  }

  private fun getWhatsAppData(
    requestFloatsNew: RequestFloatsModel,
    channelsAccessToken: ChannelsType?
  ) {
    viewModel?.getWhatsappBusiness(request = requestFloatsNew.fpTag, auth = WA_KEY)
      ?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          val response = ((it as? ChannelWhatsappResponse)?.Data)?.firstOrNull()
          if (response != null && response.active_whatsapp_number.isNullOrEmpty().not()) {
            val channelActionData = ChannelActionData(response.active_whatsapp_number?.trim())
            requestFloatsNew.channelActionDatas?.add(channelActionData)
            connectedChannels.add(ChannelsType.AccountType.WAB.name)
            requestFloatsNew.categoryDataModel?.channels?.forEach { it1 ->
              if (it1.isWhatsAppChannel()) {
                it1.isSelected = true
                it1.channelActionData = channelActionData
              }
            }
          }
        }
        ChannelAccessStatusResponse.saveDataConnectedChannel(connectedChannels)
        NavigatorManager.updateRequest(requestFloatsNew)
        setViewChannels(channelsAccessToken)
        hideProgress()
      })
  }

  private fun errorMessage(message: String) {
    hideProgress()
    showLongToast(message)
  }

  private fun setViewChannels(channelsAccessToken: ChannelsType?) {
    requestFloatsModel = NavigatorManager.getRequest()
    listDisconnect = requestFloatsModel?.categoryDataModel?.channels?.filter { it.isSelected == false } as? ArrayList<ChannelModel>
    listConnect = requestFloatsModel?.categoryDataModel?.channels?.filter { it.isSelected == true } as? ArrayList<ChannelModel>
    binding?.connectedTxt?.text = "Connected (${listConnect?.size}/${requestFloatsModel?.categoryDataModel?.channels?.size})"
    binding?.notConnectedTxt?.text = "Not connected (${listDisconnect?.size}/${requestFloatsModel?.categoryDataModel?.channels?.size})"

    binding?.disconnectedBg?.post {
      var animObserver: Completable? = null
      animObserver = if (listDisconnect.isNullOrEmpty()) {
        changeView(true)
        binding?.connectedRiya?.fadeIn(200L)?.andThen(binding?.imageRiya?.fadeIn(200L))
          ?.andThen(binding?.riyaConnectedTxt?.fadeIn(200L))
      } else {
        changeView(false)
        binding?.viewDisconnect?.fadeIn(200L)
          ?.doOnComplete { setAdapterDisconnected(listDisconnect) }
          ?.andThen(binding?.viewConnect?.fadeIn(500L))
      }
      animObserver?.doOnComplete { setAdapterConnected(listConnect) }
        ?.andThen(binding?.noteTxt?.fadeIn(100L)?.mergeWith(binding?.noteAboutTxt?.fadeIn(100L)))
        ?.subscribe()
    }
    setSharePrefDataFpPageAndTwitter(channelsAccessToken)
  }

  private fun setSharePrefDataFpPageAndTwitter(channelsAccessToken: ChannelsType?) {
    val editorFp = pref?.edit()
    editorFp?.putBoolean("fbShareEnabled", false)
    editorFp?.putString("fbAccessId", null)
    editorFp?.putBoolean("fbPageShareEnabled", false)
    editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_NAME, "")
    editorFp?.putString("fbPageAccessId", null)
    editorFp?.putInt("fbStatus", 0)
    val fpPage = channelsAccessToken?.facebookpage
    if (fpPage != null && fpPage.status.equals(CHANNEL_STATUS_SUCCESS, true)) {
      editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_PAGE, fpPage.account?.accountName ?: "")
      editorFp?.putBoolean(PreferenceConstant.FP_PAGE_SHARE_ENABLED, true)
      editorFp?.putInt(PreferenceConstant.FP_PAGE_STATUS, 1)
      editorFp?.putString("fbPageAccessId", fpPage.account?.accountId ?: "")
    } else {
      editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_PAGE, null)
      editorFp?.putBoolean(PreferenceConstant.FP_PAGE_SHARE_ENABLED, false)
      editorFp?.putInt(PreferenceConstant.FP_PAGE_STATUS, 0)
    }
    val timeLine = channelsAccessToken?.facebookusertimeline
    if (timeLine != null && timeLine.status.equals(CHANNEL_STATUS_SUCCESS, true)) {
      editorFp?.putString(PreferenceConstant.KEY_FACEBOOK_NAME, timeLine.account?.accountName)
      if (timeLine.account?.accountName.isNullOrEmpty()
          .not()
      ) editorFp?.putBoolean("fbShareEnabled", true)
      editorFp?.putString("fbAccessId", timeLine.account?.accountId)
    }
    editorFp?.apply()

    val twitter = channelsAccessToken?.twitter
    val editorTwitter = mPrefTwitter?.edit()
    if (twitter != null && twitter.status.equals(CHANNEL_STATUS_SUCCESS, true)) {
      editorTwitter?.putString(PreferenceConstant.TWITTER_USER_NAME, twitter.account?.accountName)
      editorTwitter?.putBoolean(PreferenceConstant.PREF_KEY_TWITTER_LOGIN, true)
    } else {
      editorTwitter?.putString(PreferenceConstant.TWITTER_USER_NAME, null)
      editorTwitter?.putBoolean(PreferenceConstant.PREF_KEY_TWITTER_LOGIN, false)
    }
    editorTwitter?.apply()
  }

  private fun changeView(isConnect: Boolean) {
    (baseActivity as? DigitalChannelActivity)?.changeTheme(if (isConnect) R.color.colorAccent else R.color.bg_dark_grey)
    (baseActivity as? ContainerDigitalChannelActivity)?.changeTheme(if (isConnect) R.color.colorAccent else R.color.bg_dark_grey)
    binding?.disconnectedBg?.visibility = if (isConnect) View.GONE else View.VISIBLE
    binding?.viewConnect?.visibility = if (isConnect) View.GONE else View.VISIBLE
    binding?.connectedRiya?.visibility = if (isConnect) View.VISIBLE else View.GONE
    if (isConnect.not()) binding?.connectedBg?.visibility = View.VISIBLE
    onDigitalChannelAddedOrUpdated(isConnect)
  }

  private fun onDigitalChannelAddedOrUpdated(isAdded: Boolean) {
    binding?.root?.post {
      val instance = FirestoreManager
      if (instance.getDrScoreData()?.metricdetail == null) return@post
      instance.getDrScoreData()?.metricdetail?.boolean_social_channel_connected = isAdded
      instance.updateDocument()
    }
  }

  private fun setAdapterDisconnected(list: ArrayList<ChannelModel>?) {
    binding?.recycleDisconnect?.post {
      listDisconnect = list?.map {
        if (it.getAccessTokenType() == channelTypeClick) {
          it.isSelectedClick = true
        }
        it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM_DISCONNECT.getLayout(); it
      } as? ArrayList<ChannelModel>
//      listDisconnect = list?.map { it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM_DISCONNECT.getLayout(); it } as? ArrayList<ChannelModel>
      listDisconnect?.let {
        adapterDisconnect = AppBaseRecyclerViewAdapter(baseActivity, it, this)
        binding?.recycleDisconnect?.adapter = adapterDisconnect
        adapterDisconnect?.runLayoutAnimation(binding?.recycleDisconnect)
      }
    }

  }

  private fun setAdapterConnected(list: ArrayList<ChannelModel>?) {
    binding?.recycleConnect?.post {
      listConnect = list?.map {
        it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM_CONNECT.getLayout(); it
      } as? ArrayList<ChannelModel>
      listConnect?.let {
        adapterConnect = AppBaseRecyclerViewAdapter(baseActivity, it, this)
        binding?.recycleConnect?.adapter = adapterConnect
        adapterConnect?.runLayoutAnimation(binding?.recycleConnect)
      }
    }
  }

  @SuppressLint("SetTextI18n")
  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val channel = item as ChannelModel
    when (actionType) {
      RecyclerViewActionType.CHANNEL_DISCONNECT_CLICKED.ordinal -> {
        if (channel.isFacebookShop()) {
          val s = SpannableString(resources.getString(R.string.fp_shop_awaited_desc))
          Linkify.addLinks(s, Linkify.ALL)
          AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
            .setTitle(getString(R.string.fp_shop_awaited_title))
            .setMessage(s)
            .setPositiveButton(resources.getString(R.string.okay), null).show()
            .findViewById<TextView>(android.R.id.message).movementMethod = LinkMovementMethod.getInstance()
          return
        }
        listDisconnect?.map {
          if (channel.getType() == it.getType()) {
            it.isSelected = it.isSelected?.not()
            it.isSelectedClick = false
          };it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM_DISCONNECT.getLayout(); it
        }
        adapterDisconnect?.notifyItemChanged(position)
        val count = listDisconnect?.filter { it.isSelected == true }?.size ?: 0
        if (count > 0) {
          if (count == 1) binding?.syncBtn?.text = "${resources.getString(R.string.continue_syncing)} $count ${resources.getString(R.string.string_channel)}"
          else binding?.syncBtn?.text = "${resources.getString(R.string.continue_syncing)} $count ${resources.getString(R.string.string_channels)}"
          binding?.syncBtn?.visible()
        } else binding?.syncBtn?.gone()
        if (channelTypeClick.isNotEmpty()) {
          channelTypeClick = ""
          binding?.syncBtn?.performClick()
        }
      }
      RecyclerViewActionType.CHANNEL_DISCONNECT_WHY_CLICKED.ordinal -> openWhyChannelDialog(channel)
      RecyclerViewActionType.CHANNEL_CONNECT_CLICKED.ordinal -> {

      }
      RecyclerViewActionType.CHANNEL_CONNECT_INFO_CLICKED.ordinal -> openInfoChannelDialog(channel)
    }
  }

  private fun responseManage(it: BaseResponse) {
    if (it.isSuccess()) {
      getChannelAccessToken(
        requestFloatsModel?.categoryDataModel,
        requestFloatsModel?.floatingPointId,
        requestFloatsModel?.fpTag
      )
    } else {
      showLongToast(context?.getString(R.string.failed_to_disconnecting))
      hideProgress()
    }
  }

  private fun openWhyChannelDialog(channel: ChannelModel) {
    DigitalChannelWhyDialog().apply {
      setChannels(channel)
      show(this@MyDigitalChannelFragment.parentFragmentManager, "")
    }
  }

  private fun syncChannels() {
    if (selectedChannels.isNullOrEmpty().not()) {
      WebEngageController.trackEvent(MY_DIGITAL_CHANNEL_SYNC_BUTTON_CLICK, MY_DIGITAL_CHANNEL, NO_EVENT_VALUE)
      val bundle = Bundle()
      var totalPages = if (requestFloatsModel?.isUpdate == true) 0 else 2
      selectedChannels.let { channels ->
        if (channels.haveGoogleBusinessChannel()) totalPages++
        if (channels.haveFacebookShop()) totalPages++
        if (channels.haveFacebookPage()) totalPages++
        if (channels.haveTwitterChannels()) totalPages++
        if (channels.haveWhatsAppChannels()) totalPages++
      }
      requestFloatsModel?.channels = ArrayList(selectedChannels)
      NavigatorManager.pushToStackAndSaveRequest(ScreenModel(ScreenModel.Screen.CHANNEL_SELECT, getToolbarTitle()), requestFloatsModel)
      bundle.addInt(IntentConstant.TOTAL_PAGES, totalPages).addInt(IntentConstant.CURRENT_PAGES, 1)
      val channels = requestFloatsModel?.channels ?: return
      when {
        channels.haveGoogleBusinessChannel() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE,
          bundle
        )
        channels.haveFacebookPage() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE,
          bundle
        )
        channels.haveFacebookShop() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP,
          bundle
        )
        channels.haveTwitterChannels() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS,
          bundle
        )
        channels.haveWhatsAppChannels() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_WHATSAPP,
          bundle
        )
      }
    } else showShortToast(resources.getString(R.string.at_least_one_channel_selected))
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    title?.let { progress.setTitle(it) }
    cancelable?.let { progress.isCancelable = it }
    activity?.let { progress.showProgress(it.supportFragmentManager) }
  }

  override fun hideProgress() {
    progress.hideProgress()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_alert_icon, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_info -> {
        digitalChannelBottomSheet()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun digitalChannelBottomSheet() {
    DigitalChannelSheetDialog().show(
      this.parentFragmentManager,
      DigitalChannelSheetDialog::class.java.name
    )
  }

  private fun openInfoChannelDialog(channel: ChannelModel) {
    DigitalChannelInfoDialog().apply {
      setChannels(channel)
      onClickedDisconnect = { disConnectChannel(it) }
      show(this@MyDigitalChannelFragment.parentFragmentManager, "")
    }
  }

  private fun disConnectChannel(channel: ChannelModel) {
    showProgress(context?.getString(R.string.disconnecting_your_channel), false)
    if (channel.isWhatsAppChannel()) {
      val request = UpdateChannelActionDataRequest(ChannelActionData(), requestFloatsModel?.getWebSiteId())
      viewModel?.postUpdateWhatsappRequest(request = request, auth = WA_KEY)
        ?.observeOnce(viewLifecycleOwner, { responseManage(it) })
    } else {
      val request = UpdateChannelAccessTokenRequest(
        ChannelAccessToken(type = channel.getAccessTokenType()),
        clientId,
        requestFloatsModel?.floatingPointId!!
      )
      viewModel?.updateChannelAccessToken(request)
        ?.observeOnce(viewLifecycleOwner, { responseManage(it) })
    }
  }
}