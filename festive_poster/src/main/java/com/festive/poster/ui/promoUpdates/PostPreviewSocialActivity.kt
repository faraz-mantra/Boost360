package com.festive.poster.ui.promoUpdates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.View.LAYER_TYPE_HARDWARE
import androidx.lifecycle.lifecycleScope
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.ActivityPostPreviewSocialBinding
import com.festive.poster.models.CustomerDetails
import com.festive.poster.models.MerchantSummaryResponse
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.models.PosterModel
import com.festive.poster.models.promoModele.SocialPlatformModel
import com.festive.poster.models.promoModele.SocialPreviewChannel
import com.festive.poster.models.promoModele.SocialPreviewModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.ui.promoUpdates.bottomSheet.PostSuccessBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.PostingProgressBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.SubscribePlanBottomSheet
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.MarketPlaceUtils
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.isPromoWidgetActive
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.constants.Constants.MARKET_PLACE_ORIGIN_NAV_DATA
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.convertListObjToString
import com.framework.utils.convertStringToObj
import com.framework.utils.saveAsTempFile
import com.framework.utils.toArrayList
import com.framework.webengageconstant.EVENT_LABEL_NULL
import com.framework.webengageconstant.POST_AN_UPDATE
import com.google.gson.Gson
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.rest.response.category.ResponseDataCategory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PostPreviewSocialActivity : AppBaseActivity<ActivityPostPreviewSocialBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {

    private var chkChannelAdapter: AppBaseRecyclerViewAdapter<SocialPlatformModel>?=null
    private var uiChBoxChannelList: ArrayList<SocialPlatformModel>?=null
    private var uiPreviewChannelList: ArrayList<SocialPreviewModel>?=null
    private var posterProgressSheet: PostingProgressBottomSheet?=null
    private var connectedChannels: ArrayList<String> = arrayListOf()
    private var session:UserSessionManager?=null
    private var captionIntent :String?=null
    private val posterImgPath by lazy {
        intent?.getBundleExtra(MARKET_PLACE_ORIGIN_NAV_DATA)?.getString(IK_POSTER)
    }

    companion object{
        val IK_CAPTION_KEY="IK_CAPTION_KEY"
        val IK_POSTER="IK_POSTER"

        fun launchActivity(activity:Activity,caption:String?,posterImgPath:String){
            activity.startActivity(Intent(activity,PostPreviewSocialActivity::class.java)
                .putExtra(MARKET_PLACE_ORIGIN_NAV_DATA, Bundle().apply {
                    putString(IK_CAPTION_KEY,caption)
                    putString(EditPostActivity.IK_POSTER, posterImgPath)
                })


            )
        }
    }
    private val pref: SharedPreferences?
        get() {
            return getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, 0)
        }

    private val mPrefTwitter: SharedPreferences?
        get() {
            return getSharedPreferences(
                PreferenceConstant.PREF_NAME_TWITTER,
                Context.MODE_PRIVATE
            )
        }
    override fun getLayout(): Int {
        return R.layout.activity_post_preview_social
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        session = UserSessionManager(this)
        captionIntent =intent?.getBundleExtra(MARKET_PLACE_ORIGIN_NAV_DATA)?.getString(IK_CAPTION_KEY)


        initUI()

    }

    override fun onResume() {
        super.onResume()

        refreshUserWidgets()

    }

    private fun refreshUserWidgets() {
        showProgress()
        viewModel.getUserDetails(session?.fpTag, clientId).observe(this,{
            if (it.isSuccess()){
               val detail = it as? CustomerDetails
                detail?.FPWebWidgets?.let { list ->
                    session?.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(list))
                    isUserPremium(isPromoWidgetActive())

                }
            }
            hideProgress()
        })
    }

    private fun initUI() {
        binding?.tvChooseAPromoPack?.setOnClickListener {
            SubscribePlanBottomSheet.newInstance(object :SubscribePlanBottomSheet.Callbacks{
                override fun onBuyClick() {
                    MarketPlaceUtils.launchCartActivity(this@PostPreviewSocialActivity,
                        PostPreviewSocialActivity::class.java.name,posterImgPath,captionIntent)

                }
            }).show(supportFragmentManager, SubscribePlanBottomSheet::class.java.name)
        }

        binding?.tvPostUpdate?.setOnClickListener {
            posterProgressSheet =  PostingProgressBottomSheet.newInstance(posterImgPath)
            saveUpdatePost()
            posterProgressSheet?.show(supportFragmentManager, PostingProgressBottomSheet::class.java.name)
        }

        binding?.ivClosePreview?.setOnClickListener {
            onBackPressed()
        }


        setChannels()




    }

    private fun isUserPremium(isUserPremium:Boolean = false){
        if (isUserPremium.not()){
            binding?.tvChooseAPromoPack?.visible()
            binding?.tvPostUpdate?.gone()
            enableGrayScale(binding?.tvPreviewTitle, binding?.tvSelected, binding?.rvSocialPlatforms)
        }else{
            binding?.tvChooseAPromoPack?.gone()
            binding?.tvPostUpdate?.visible()
        }
    }

    private fun enableGrayScale(vararg views: View?){
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val greyscalePaint = Paint()
        greyscalePaint.colorFilter = ColorMatrixColorFilter(cm)
        for (view in views) view?.setLayerType(LAYER_TYPE_HARDWARE, greyscalePaint)
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

        when(actionType){
            RecyclerViewActionType.SOCIAL_CHANNEL_CHECK_CLICKED.ordinal->{
                val model = uiChBoxChannelList?.get(position)
                uiPreviewChannelList?.find { it.channelType==model?.channelType }?.shouldShow = model?.isChecked == true
                setupPreviewList()
                binding?.tvSelected?.text = getString(R.string.placeholder_selected,getCheckedChannelCount())

            }
        }
    }

    fun setChannels(){
        val requestFloatsModel = NavigatorManager.getRequest()
        if (true/*requestFloatsModel==null*/){
            getChannelsFromJson()
        }else{
            val  channelList = requestFloatsModel?.categoryDataModel?.channels

            if (channelList.isNullOrEmpty()){
                showLongToast(getString(R.string.channel_not_found))
            }else{
                setChannelAdapter(/*channelList*/)
            }
        }

    }

    fun setChannelAdapter(){
        //   val socialPlatformModel = SocialPlatformModel().getData(this@PostPreviewSocialActivity)
        chkChannelAdapter = AppBaseRecyclerViewAdapter(
            this@PostPreviewSocialActivity,
            uiChBoxChannelList!!,
            this@PostPreviewSocialActivity
        )
        binding?.rvSocialPlatforms?.apply {
            adapter = chkChannelAdapter
        }

        binding?.tvSelected?.text = getString(R.string.placeholder_selected,getCheckedChannelCount())
        binding?.tvPostUpdate?.text = getString(R.string.post_on_placeholder_platform,getCheckedChannelCount())

    }

    fun getCheckedChannelCount(): Int {
       return uiChBoxChannelList?.filter { it.isChecked==true }?.size?:0
    }
    private fun getChannelsFromJson() {
        binding?.progressBar?.visible()
            NavigatorManager.clearRequest()
            val experienceCode = session?.fP_AppExperienceCode
            if (experienceCode.isNullOrEmpty().not()) {
                val floatingPoint = session?.fPID
                val fpTag = session?.fpTag
             //   showProgress(getString(com.onboarding.nowfloats.R.string.refreshing_your_channels), false)
                viewModel.getCategories(this).observeOnce(this, {
                    if (it?.error != null) errorMessage(
                        it.error?.localizedMessage ?: resources.getString(com.onboarding.nowfloats.R.string.error_getting_category_data)
                    )
                    else {
                        val categoryList = (it as? ResponseDataCategory)?.data
                        val categoryData =
                            categoryList?.singleOrNull { c -> c.experienceCode() == experienceCode }
                        if (categoryData != null) {
                            getChannelAccessToken(categoryData, floatingPoint, fpTag)

                        } else errorMessage(resources.getString(com.onboarding.nowfloats.R.string.error_getting_category_data))
                    }
                })
            } else showShortToast(resources.getString(com.onboarding.nowfloats.R.string.invalid_experience_code))

    }

    private fun getChannelAccessToken(
        categoryData: CategoryDataModel?,
        floatingPoint: String?,
        fpTag: String?
    ) {
        viewModel.getChannelsAccessTokenStatus(floatingPoint).observeOnce(this, { it1 ->
            when {
                it1.error is NoNetworkException -> errorMessage(resources.getString(com.onboarding.nowfloats.R.string.internet_connection_not_available))
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
        uiChBoxChannelList= ArrayList()
        uiPreviewChannelList = ArrayList()
        val requestFloatsNew = RequestFloatsModel()
        requestFloatsNew.categoryDataModel = categoryData
        requestFloatsNew.isUpdate = true
        requestFloatsNew.floatingPointId = floatingPoint
        requestFloatsNew.fpTag = fpTag
        requestFloatsNew.websiteUrl = session?.getDomainName(false)
        requestFloatsNew.categoryDataModel?.resetIsSelect()
        requestFloatsNew.categoryDataModel?.channels?.map {
            if (it.isGoogleSearch()) it.websiteUrl = session?.getDomainName(false)
        }

            connectedChannels?.clear()
            var title:String?=null
            var subTitle:String?=null
            var isConnected=false
            var isEnabled=false
            var channelType:SocialPreviewChannel?=null
            requestFloatsNew.categoryDataModel?.channels?.forEach { it1 ->
                /*if (it1.isWhatsAppChannel()){
                    return
                }*/
                if (it1.type==ChannelType.G_SEARCH.name){
                    isConnected=true
                    isEnabled = false
                    channelType = SocialPreviewChannel.WEBSITE
                    subTitle=session?.getDomainName(false)
                }else{
                    subTitle=null
                    isEnabled=true
                    channelType = null
                    isConnected=false
                }
                title = it1.getName()
                var data: ChannelAccessToken? = null
                if (channelsAccessToken!=null){
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
                                title =fbPage.account?.accountName
                                subTitle = fbPage.account?.accountId
                                isConnected = true
                                requestFloatsNew.channelAccessTokens?.add(data)
                                it1.isSelected = true
                                it1.channelAccessToken = data
                                connectedChannels?.add(ChannelsType.AccountType.facebookpage.name)



                            }
                            channelType = SocialPreviewChannel.FACEBOOK

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
                                subTitle = fpShop?.account?.userAccountId
                                isConnected = true
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
                                title =twitter?.account?.accountName
                                subTitle = twitter?.account?.accountId
                                isConnected = true
                                requestFloatsNew.channelAccessTokens?.add(data)
                                it1.isSelected = true
                                it1.channelAccessToken = data
                                connectedChannels.add(ChannelsType.AccountType.twitter.name)


                            }
                            channelType = SocialPreviewChannel.TWITTER

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
                                title =gmb?.account?.accountName
                                subTitle = gmb?.account?.accountId
                                isConnected = true
                                requestFloatsNew.channelAccessTokens?.add(data)
                                it1.isSelected = true
                                it1.channelAccessToken = data
                                connectedChannels.add(ChannelsType.AccountType.googlemybusiness.name)



                            }
                            channelType = SocialPreviewChannel.GMB

                        }
                    }

                }

                if (shouldAddToChannelList(it1)){
                    uiChBoxChannelList?.add(SocialPlatformModel(title,subTitle,isEnabled,isConnected,isConnected,channelType!!).apply {
                        generateImageResource(this@PostPreviewSocialActivity)
                    })

                    uiPreviewChannelList?.add(SocialPreviewModel(posterImgPath,title,captionIntent,isConnected,channelType!!))
                }


            }

            if (categoryData?.channels.isNullOrEmpty()){
                showLongToast(getString(R.string.channel_not_found))
            }else{
                fetchSubscriberCount()
            }

            setupPreviewList()

     //   getWhatsAppData(requestFloatsNew, channelsAccessToken)
    }

    private fun setupPreviewList() {
        val filteredList = uiPreviewChannelList?.filter { it.shouldShow }?.toArrayList()
        binding?.rvPostPreview?.apply {
            adapter  = AppBaseRecyclerViewAdapter(this@PostPreviewSocialActivity, filteredList!!, this@PostPreviewSocialActivity)
        }
    }

    private fun shouldAddToChannelList(channel: ChannelModel): Boolean {

        if (channel.isWhatsAppChannel()||channel.getAccessTokenType()==ChannelsType.AccountType.facebookshop.name){
            return false
        }
        return true
    }

    /*  private fun getWhatsAppData(
          requestFloatsNew: RequestFloatsModel,
          channelsAccessToken: ChannelsType?
      ) {
          viewModel.getWhatsappBusiness(request = requestFloatsNew.fpTag, auth = WA_KEY)
              ?.observeOnce(this, {
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
                  setSharePrefDataFpPageAndTwitter(channelsAccessToken)
                  hideProgress()
              })
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

  */
    private fun errorMessage(message: String) {
        hideProgress()
        showLongToast(message)
    }

    private fun saveUpdatePost() {
        WebEngageController.trackEvent(POST_AN_UPDATE, EVENT_LABEL_NULL, session?.fpTag)
        var socialShare = ""
           if (uiChBoxChannelList?.find { it.channelType==SocialPreviewChannel.FACEBOOK }?.isChecked == true) socialShare += "FACEBOOK."
/*
           if (fbPageStatusEnable.value == true) socialShare += "FACEBOOK_PAGE."
*/
        if (uiChBoxChannelList?.find { it.channelType==SocialPreviewChannel.TWITTER }?.isChecked == true) socialShare += "TWITTER."
        val merchantId = if (session?.iSEnterprise == "true") null else session?.fPID
        val parentId = if (session?.iSEnterprise == "true") session?.fPParentId else null
        val sendToSubscribe = uiChBoxChannelList?.find { it.channelType==SocialPreviewChannel.EMAIL }?.isChecked
        val isPicMes = posterImgPath!=null
        val request = PostUpdateTaskRequest(
            clientId,
            captionIntent,
            isPicMes,
            merchantId,
            parentId,
            sendToSubscribe,
            socialShare
        )

        /*viewModel.putBizMessageUpdate(request).observeOnce(this, {
            if (it.isSuccess() && it.stringResponse.isNullOrEmpty().not()) {

                if (isPicMes){
                    lifecycleScope.launch {
                        val bodyImage = File(posterImgPath).asRequestBody("image/*".toMediaTypeOrNull())
                        val s_uuid = UUID.randomUUID().toString().replace("-", "")
                        viewModel.putBizImageUpdate(
                            clientId, "sequential", s_uuid, 1, 1,
                            socialShare, it.stringResponse, sendToSubscribe, bodyImage
                        ).observeOnce(this@PostPreviewSocialActivity, { it1 ->
                            if (it1.isSuccess()) {
                                // successResult()
                                posterProgressSheet?.dismiss()
                                PostSuccessBottomSheet.newInstance(posterImgPath).show(supportFragmentManager, PostSuccessBottomSheet::class.java.name)

                            } else{
                                posterProgressSheet?.dismiss()
                                showShortToast("Image uploading error, please try again.")
                            }

                        })
                    }

                }

                }
             else {
                posterProgressSheet?.dismiss()

                showShortToast("Post updating error, please try again.")
            }
        })*/

         */

        PostSuccessBottomSheet.newInstance(posterImgPath,captionIntent).show(supportFragmentManager, PostSuccessBottomSheet::class.java.name)

    }

    fun fetchSubscriberCount(){
        viewModel.getMerchantSummary(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID), session?.fpTag).observeOnce(this, {
            val response = it as? MerchantSummaryResponse
            val subscriber = response?.Entity?.firstOrNull()?.get("NoOfSubscribers")
            val subTitle = if (subscriber==0){
                getString(R.string.no_recipients)

            }else{
                getString(R.string.placeholder_recipients,subscriber)
            }
            uiChBoxChannelList?.add(SocialPlatformModel(getString(R.string.email_sub),subTitle,true,
                true,true,SocialPreviewChannel.EMAIL).apply {
                icon =getDrawable(R.drawable.ic_promo_emailers)
            })

            binding?.progressBar?.gone()

            setChannelAdapter()

        })
    }
}