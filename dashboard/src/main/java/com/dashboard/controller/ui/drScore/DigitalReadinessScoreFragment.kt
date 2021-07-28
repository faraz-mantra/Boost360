package com.dashboard.controller.ui.drScore

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.PreferenceConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.dashboard.getLocalSession
import com.dashboard.databinding.FragmentDigitalReadinessScoreBinding
import com.dashboard.model.live.drScore.*
import com.dashboard.model.live.shareUser.ShareUserDetailResponse
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.WA_KEY
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.firestore.FirestoreManager
import com.framework.utils.PreferencesUtils
import com.framework.utils.getData
import com.framework.utils.roundToFloat
import com.framework.utils.saveData
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.framework.webengageconstant.DIGITAL_READINESS_PAGE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.android.material.snackbar.Snackbar
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelsAccessTokenResponse
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.VisitingCardSheet

class DigitalReadinessScoreFragment : AppBaseFragment<FragmentDigitalReadinessScoreBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var adapterPager: AppBaseRecyclerViewAdapter<DrScoreSetupData>? = null
  private var session: UserSessionManager? = null
  private var position = 0
  private var isHigh = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): DigitalReadinessScoreFragment {
      val fragment = DigitalReadinessScoreFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_digital_readiness_score
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    position = arguments?.getInt(IntentConstant.POSITION.name) ?: 0
    binding?.btnBack?.setOnClickListener { baseActivity.onNavPressed() }
    WebEngageController.trackEvent(DIGITAL_READINESS_PAGE, PAGE_VIEW, session?.fpTag)
  }

  override fun onResume() {
    super.onResume()
    getSiteMeter()
  }

  private fun getSiteMeter() {
    if (FirestoreManager.getDrScoreData()?.drs_segment.isNullOrEmpty()) FirestoreManager.readDrScoreDocument()
    viewModel?.getDrScoreUi(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? DrScoreUiDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val drScoreData = FirestoreManager.getDrScoreData()
        isHigh = (drScoreData != null && drScoreData.getDrsTotal() >= 85)
        val drScoreSetupList = drScoreData?.getDrScoreData(response.data)
        if (drScoreSetupList.isNullOrEmpty().not()) {
          drScoreSetupList?.map { it1 -> it1.recyclerViewItemType = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout() }
          if (adapterPager == null) {
            binding?.pagerBusinessContentSetup?.apply {
              adapterPager = AppBaseRecyclerViewAdapter(baseActivity, drScoreSetupList!!, this@DigitalReadinessScoreFragment)
              offscreenPageLimit = 3
              clipToPadding = false
              setPadding(34, 0, 34, 0)
              adapter = adapterPager
              currentItem = position
              binding?.dotBusinessContentSetup?.setViewPager2(this)
              setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
            }
          } else adapterPager?.notify(drScoreSetupList)
        } else Snackbar.make(binding?.root!!, getString(R.string.digital_readiness_score_failed_to_load), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry)) { getSiteMeter() }.show()
        binding?.txtDes?.text = resources.getString(R.string.add_missing_info_better_online_traction, if (isHigh) "100%" else "85%")
//        binding?.txtPercentage?.setTextColor(getColor(if (isHigh) R.color.light_green_3 else R.color.accent_dark))
        binding?.txtReadinessScore?.text = "${drScoreData?.getDrsTotal() ?: 0}"
//        binding?.progressBar?.progress = drScoreData?.getDrsTotal() ?: 0
        val percentage = ((100 - drScoreData?.getDrsTotal()!!).toDouble() / 100).roundToFloat(2)
        (binding?.progressBar?.layoutParams as? ConstraintLayout.LayoutParams)?.matchConstraintPercentWidth = percentage
        binding?.progressBar?.requestLayout()
//        binding?.progressBar?.progressDrawable = ContextCompat.getDrawable(baseActivity, if (isHigh) R.drawable.ic_progress_bar_horizontal_high else R.drawable.progress_bar_horizontal)

      } else Snackbar.make(binding?.root!!, getString(R.string.digital_readiness_score_failed_to_load), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry)) { getSiteMeter() }.show()
    })

  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.DIGITAL_SCORE_READINESS_CLICK.ordinal -> {
        val data = item as? DrScoreItem ?: return
        val type = DrScoreItem.DrScoreItemType.fromName(data.drScoreUiData?.id)
        if (type == DrScoreItem.DrScoreItemType.boolean_share_business_card) {
          val messageChannelUrl = PreferencesUtils.instance.getData(PreferenceConstant.CHANNEL_SHARE_URL, "")
          if (messageChannelUrl.isNullOrEmpty().not()) visitingCardDetailText(messageChannelUrl)
          else getChannelAccessToken(true)
        } else clickEventUpdateScoreN(type, baseActivity, session)
      }
    }
  }


  private fun visitingCardDetailText(shareChannelText: String?) {
    viewModel?.getBoostVisitingMessage(baseActivity)?.observeOnce(viewLifecycleOwner, {
      val response = it as? ShareUserDetailResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val messageDetail = response.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }?.message
        if (messageDetail.isNullOrEmpty().not()) {
          val lat = session?.getFPDetails(Key_Preferences.LATITUDE)
          val long = session?.getFPDetails(Key_Preferences.LONGITUDE)
          var location = ""
          if (lat != null && long != null) location = "${if (shareChannelText.isNullOrEmpty().not()) "\n\n" else ""}\uD83D\uDCCD *Find us on map: http://www.google.com/maps/place/$lat,$long*\n\n"
          val txt = String.format(messageDetail!!, session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME) ?: "", session!!.getDomainName(false), shareChannelText, location)
          visitingCard(txt)
        }
      } else visitingCard(getString(R.string.business_card))
    })
  }


  private fun visitingCard(shareChannelText: String) {
    session?.let {
      val dialogCard = VisitingCardSheet()
      dialogCard.setData(getLocalSession(it), shareChannelText)
      dialogCard.show(this@DigitalReadinessScoreFragment.parentFragmentManager, VisitingCardSheet::class.java.name)
    }
  }

  private fun getChannelAccessToken(isShowLoader: Boolean = false) {
    if (isShowLoader) showProgress()
    viewModel?.getChannelsAccessTokenStatus(session?.fPID)?.observeOnce(this, {
      var urlString = ""
      if (it.isSuccess()) {
        val response = it as? ChannelAccessStatusResponse
        if (response?.channels?.facebookpage?.account?.accountId.isNullOrEmpty().not()) {
          urlString = "\n⚡ *Facebook: https://www.facebook.com/${response?.channels?.facebookpage?.account?.accountId}*"
        }
        if (response?.channels?.twitter?.account?.accountName.isNullOrEmpty().not()) {
          urlString += "\n⚡ *Twitter: https://twitter.com/${response?.channels?.twitter?.account?.accountName?.trim()}*"
        }
      }
      getWhatsAppData(urlString, isShowLoader)
    })
  }

  private fun getWhatsAppData(urlString: String, isShowLoader: Boolean = false) {
    var urlStringN = urlString
    viewModel?.getWhatsappBusiness(request = session?.fpTag, auth = WA_KEY)?.observeOnce(this, {
      if (isShowLoader) hideProgress()
      if (it.isSuccess()) {
        val response = ((it as? ChannelWhatsappResponse)?.Data)?.firstOrNull()
        if (response != null && response.active_whatsapp_number.isNullOrEmpty().not()) {
          urlStringN += "\n⚡ *WhatsApp: https://wa.me/${response.active_whatsapp_number}*"
        }
      }
      if (session?.userPrimaryMobile.isNullOrEmpty().not()) urlStringN += "\n\uD83D\uDCDECall: ${session?.userPrimaryMobile}*"
      PreferencesUtils.instance.saveData(PreferenceConstant.CHANNEL_SHARE_URL, urlStringN)
      if (isShowLoader) visitingCardDetailText(urlStringN)
    })
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }

  override fun onStop() {
    super.onStop()
    FirestoreManager.listener = null
  }

  override fun onStart() {
    super.onStart()
    FirestoreManager.listener = { getSiteMeter() }
  }
}

fun clickEventUpdateScoreN(type: DrScoreItem.DrScoreItemType?, baseActivity: AppCompatActivity, session: UserSessionManager?) {
  WebEngageController.trackEvent(DIGITAL_READINESS_PAGE, PAGE_VIEW, session?.fpTag)
  when (type) {
    DrScoreItem.DrScoreItemType.boolean_add_business_name -> {
//        if (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME).isNullOrEmpty())
      baseActivity.startBusinessProfileDetailEdit(session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_business_description -> {
//        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION).isNullOrEmpty())
      baseActivity.startBusinessProfileDetailEdit(session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_clinic_logo -> {
//        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl).isNullOrEmpty())
      baseActivity.startBusinessLogo(session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_business_hours -> {
      if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS) == "TIMINGS") baseActivity.startBusinessHours(session)
      else alertDialogBusinessHours(baseActivity, session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_contact_details -> {
      baseActivity.startBusinessInfoEmail(session)
    }
    DrScoreItem.DrScoreItemType.boolean_create_staff -> {
      baseActivity.startAddStaff(session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_custom_domain_name_and_ssl -> {
      baseActivity.startDomainDetail(session)
    }
    DrScoreItem.DrScoreItemType.number_updates_posted -> {
      session?.let { baseActivity.startUpdateLatestStory(it) }
    }
    DrScoreItem.DrScoreItemType.boolean_social_channel_connected -> {
      session?.let { baseActivity.startDigitalChannel(it) }
    }
    DrScoreItem.DrScoreItemType.number_services_added, DrScoreItem.DrScoreItemType.number_products_added -> {
      baseActivity.startListServiceProduct(session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_bank_account -> {
      baseActivity.startMyBankAccount(session)
    }
    DrScoreItem.DrScoreItemType.boolean_image_uploaded_to_gallery -> {
      baseActivity.startAddImageGallery(session, false)
    }
    DrScoreItem.DrScoreItemType.boolean_create_custom_page -> {
      baseActivity.startCustomPage(session, false)
    }
    DrScoreItem.DrScoreItemType.boolean_create_sample_in_clinic_appointment -> {
      baseActivity.startOrderAptConsultList(session, isConsult = false)
    }
    DrScoreItem.DrScoreItemType.boolean_create_sample_video_consultation -> {
      baseActivity.startOrderAptConsultList(session, isConsult = true)
    }
    DrScoreItem.DrScoreItemType.boolean_respond_to_customer_enquiries -> {
      baseActivity.startBusinessEnquiry(session)
    }
    DrScoreItem.DrScoreItemType.boolean_add_featured_image_video -> {
      baseActivity.startFeatureLogo(session)
    }
    DrScoreItem.DrScoreItemType.boolean_select_what_you_sell,
    DrScoreItem.DrScoreItemType.boolean_create_doctor_e_profile,
    DrScoreItem.DrScoreItemType.boolean_manage_appointment_settings,
    -> Toast.makeText(baseActivity, "Coming soon...", Toast.LENGTH_SHORT).show()
  }
}

fun alertDialogBusinessHours(baseActivity: AppCompatActivity, session: UserSessionManager?) {
  AlertDialog.Builder(
    ContextThemeWrapper(baseActivity,R.style.CustomAlertDialogTheme))
    .setTitle(baseActivity.getString(R.string.features_not_available))
    .setMessage(baseActivity.getString(R.string.check_store_for_upgrade_info))
    .setPositiveButton(baseActivity.getString(R.string.goto_store)) { dialogInterface, i ->
      baseActivity.startPricingPlan(session)
      dialogInterface.dismiss()
    }.setNegativeButton(baseActivity.getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.dismiss() }.show()
}
