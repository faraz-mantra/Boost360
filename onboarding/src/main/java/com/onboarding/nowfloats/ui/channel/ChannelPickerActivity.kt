package com.onboarding.nowfloats.ui.channel

import android.text.TextUtils
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.WA_KEY
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.constant.PreferenceConstant.GET_FP_DETAILS_TAG
import com.onboarding.nowfloats.constant.PreferenceConstant.GET_FP_EXPERIENCE_CODE
import com.onboarding.nowfloats.constant.PreferenceConstant.IS_UPDATE
import com.onboarding.nowfloats.constant.PreferenceConstant.KEY_FP_ID
import com.onboarding.nowfloats.databinding.ActivityChannelPickerBinding
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.getAccessTokenType
import com.onboarding.nowfloats.model.channel.isWhatsAppChannel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.ChannelActionData
import com.onboarding.nowfloats.model.channel.respose.NFXAccessToken
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.rest.response.category.ResponseDataCategory
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelsAccessTokenResponse
import com.onboarding.nowfloats.viewmodel.category.CategoryViewModel
import java.util.*

class ChannelPickerActivity : AppBaseActivity<ActivityChannelPickerBinding, CategoryViewModel>(), ChannelSelectorAnimator.OnAnimationCompleteListener, MotionLayout.TransitionListener {

  private var requestFloatsModel: RequestFloatsModel? = null
  private val animations = ChannelSelectorAnimator()

  val fragment: ChannelPickerFragment?
    get() = supportFragmentManager.findFragmentById(R.id.channelPickerFragment) as? ChannelPickerFragment

  override fun getLayout(): Int {
    return R.layout.activity_channel_picker
  }

  override fun getViewModelClass(): Class<CategoryViewModel> {
    return CategoryViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.home)
    updateRequestGetChannelData()
  }

  private fun createViewChannel() {
    requestFloatsModel = NavigatorManager.getRequest() ?: return
    binding?.imageRiya?.visible()
    binding?.categoryView?.visible()
    binding?.imageRiya?.post {
      animations.setViews(
          motionLayout = binding?.motionLayout, imageView = binding?.imageView,
          titleForeground = binding?.titleForeground, subTitleForeground = binding?.subTitleForeground
      )
      animations.listener = this
      animations.startAnimation()
    }
    fragment?.updateBundleArguments(intent.extras)
    setHeaderWelcomeText()
    setCategoryImage()
    binding?.motionLayout?.setTransitionListener(this)
  }

  private fun updateRequestGetChannelData() {
    val bundle = intent?.extras
    val isUpdate = bundle?.getBoolean(IS_UPDATE)
    if (isUpdate != null && isUpdate) {
      NavigatorManager.clearRequest()
      val experienceCode = bundle.getString(GET_FP_EXPERIENCE_CODE)
      if (experienceCode.isNullOrEmpty().not()) {
        val floatingPoint = bundle.getString(KEY_FP_ID)
        val fpTag = bundle.getString(GET_FP_DETAILS_TAG)
        showProgress()
        viewModel.getCategories(this).observeOnce(this, Observer {
          if (it?.error != null) errorMessage(it.error?.localizedMessage ?: "${resources?.getString(R.string.error_getting_category_data)}")
          else {
            val categoryList = (it as? ResponseDataCategory)?.data
            val categoryData = categoryList?.singleOrNull { c -> c.experienceCode() == experienceCode }
            if (categoryData != null) {
              viewModel.getChannelsAccessToken(floatingPoint).observeOnce(this, Observer { it1 ->
                if (it1.error is NoNetworkException) errorMessage(resources.getString(R.string.internet_connection_not_available))
                else if (it1.isSuccess()) {
                  val channelsAccessToken = (it1 as? ChannelsAccessTokenResponse)?.NFXAccessTokens
                  setDataRequestChannels(categoryData, channelsAccessToken, floatingPoint, fpTag)
                } else if (it1.status == 404) {
                  setDataRequestChannels(categoryData, ArrayList(), floatingPoint, fpTag)
                } else errorMessage(it1.message())
              })
            } else errorMessage("${resources?.getString(R.string.error_getting_category_data)}")
          }
        })
      } else showShortToast(resources.getString(R.string.invalid_experience_code))
    } else createViewChannel()
  }


  private fun setDataRequestChannels(categoryData: CategoryDataModel, channelsAccessToken: List<NFXAccessToken>?, floatingPoint: String?, fpTag: String?) {
    val requestFloatsNew = RequestFloatsModel()
    requestFloatsNew.categoryDataModel = categoryData
    requestFloatsNew.categoryDataModel?.resetIsSelect()
    requestFloatsNew.isUpdate = true
    requestFloatsNew.floatingPointId = floatingPoint
    requestFloatsNew.fpTag = fpTag
    if (channelsAccessToken.isNullOrEmpty().not()) {
      channelsAccessToken?.forEach {
        when (it.type()) {
          ChannelAccessToken.AccessTokenType.facebookpage.name,
          ChannelAccessToken.AccessTokenType.facebookshop.name,
          ChannelAccessToken.AccessTokenType.twitter.name,
          -> {
            if (it.isValidType()) {
              val data = ChannelAccessToken(type = it.type(), userAccessTokenKey = it.UserAccessTokenKey,
                  userAccountId = it.UserAccountId, userAccountName = it.UserAccountName)
              requestFloatsNew.channelAccessTokens?.add(data)
            }
          }
          ChannelAccessToken.AccessTokenType.googlemybusiness.name.toLowerCase(Locale.ROOT) -> {
          }
        }
        requestFloatsNew.categoryDataModel?.channels?.forEach { it1 ->
          if (it1.getAccessTokenType() == it.type() || it1.getAccessTokenType() == ChannelAccessToken.AccessTokenType.googlemybusiness.name) {
            it1.isSelected = true
          }
        }
      }
    }
    getWhatsAppData(requestFloatsNew)
  }

  private fun getWhatsAppData(requestFloatsNew: RequestFloatsModel) {
    viewModel.getWhatsappBusiness(request = requestFloatsNew.fpTag, auth = WA_KEY).observeOnce(this, Observer {
      if ((it.error is NoNetworkException).not()) {
        if (it.isSuccess()) {
          val response = ((it as? ChannelWhatsappResponse)?.Data)?.firstOrNull()
          if (response != null && response.active_whatsapp_number.isNullOrEmpty().not()) {
            requestFloatsNew.categoryDataModel?.channels?.forEach { it4 -> if (it4.isWhatsAppChannel()) it4.isSelected = true }
            requestFloatsNew.channelActionDatas?.add(ChannelActionData(response.active_whatsapp_number?.trim()))
          }
        }
      }
      NavigatorManager.updateRequest(requestFloatsNew)
      createViewChannel()
      hideProgress()
    })
  }

  private fun errorMessage(message: String) {
    hideProgress()
    showLongToast(message)
  }


  private fun setHeaderWelcomeText() {
    binding?.digitalPlanWelcomeMessage?.text = "${getString(R.string.business_boost_success)} ${requestFloatsModel?.categoryDataModel?.category_descriptor}"
  }

  private fun setCategoryImage() {
    binding?.categoryImage?.setImageDrawable(requestFloatsModel?.categoryDataModel?.getImage(this))
    binding?.categoryImage?.setTintColor(ResourcesCompat.getColor(resources, R.color.white, theme))
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.home -> onBackPressed()
    }
  }

  override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
  }

  override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

  }

  override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
  }

  override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
    binding?.digitalPlanWelcomeMessage?.ellipsize = TextUtils.TruncateAt.END
    binding?.digitalPlanWelcomeMessage?.maxLines = if (currentId == R.id.expanded) 3 else 1
  }

  override fun onAnimationComplete() {
    super.onAnimationComplete()
    fragment?.startAnimationChannelFragment()
  }

  override fun onBackPressed() {
    super.onBackPressed()
    requestFloatsModel?.channels = null
    NavigatorManager.popCurrentScreen(ScreenModel.Screen.CHANNEL_SELECT)
  }
}
