package com.dashboard.controller.ui.digitalScore

import android.app.AlertDialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.ui.dashboard.getRequestFloat
import com.dashboard.databinding.FragmentDigitalReadinessScoreBinding
import com.dashboard.model.BusinessContentSetupData
import com.dashboard.model.getListDigitalScore
import com.dashboard.model.live.SiteMeterModel
import com.dashboard.pref.Key_Preferences
import com.dashboard.pref.UserSessionManager
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.inventoryorder.model.floatMessage.MessageModel

class DigitalReadinessScoreFragment : AppBaseFragment<FragmentDigitalReadinessScoreBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var adapterPager: AppBaseRecyclerViewAdapter<BusinessContentSetupData>? = null
  private var session: UserSessionManager? = null
  private var position = 0
  private var isHigh = false
  private var isReload = false

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
    WebEngageController.trackEvent("Digital Readiness Page", "pageview", session?.fpTag)
  }

  override fun onResume() {
    super.onResume()
    getFloatMessage(isReload)
  }

  private fun getSiteMeter() {
    session?.siteMeterData { siteMeterData ->
      if (siteMeterData == null) return@siteMeterData
      isHigh = (siteMeterData.siteMeterTotalWeight >= 80)
      val listDigitalScore = siteMeterData.getListDigitalScore()
      val list = ArrayList(listDigitalScore.map { it.recyclerViewItemType = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout();it })
      if (adapterPager == null) {
        binding?.pagerBusinessContentSetup?.apply {
          adapterPager = AppBaseRecyclerViewAdapter(baseActivity, list, this@DigitalReadinessScoreFragment)
          offscreenPageLimit = 3
          clipToPadding = false
          setPadding(34, 0, 34, 0)
          adapter = adapterPager
          currentItem = position
          binding?.dotBusinessContentSetup?.setViewPager2(this)
          setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
        }
      } else adapterPager?.notify(list)

      binding?.txtDes?.text = resources.getString(R.string.add_missing_info_better_online_traction, if (isHigh) "100%" else "90%")
      binding?.txtPercentage?.setTextColor(getColor(if (isHigh) R.color.light_green_3 else R.color.accent_dark))
      binding?.txtPercentage?.text = "${siteMeterData.siteMeterTotalWeight}%"
      binding?.progressBar?.progress = siteMeterData.siteMeterTotalWeight
      binding?.progressBar?.progressDrawable = ContextCompat.getDrawable(baseActivity, if (isHigh) R.drawable.ic_progress_bar_horizontal_high else R.drawable.progress_bar_horizontal)
    }
  }

  private fun getFloatMessage(isReload: Boolean) {
    if (isReload) showProgress()
    else getSiteMeter()
    viewModel?.getBizFloatMessage(session!!.getRequestFloat())?.observeOnce(viewLifecycleOwner, Observer{
      if (it?.isSuccess() == true) (it as? MessageModel)?.saveData()
      getSiteMeter()
      if (isReload) {
        hideProgress()
      }
      this.isReload = true
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.DIGITAL_SCORE_READINESS_CLICK.ordinal -> {
        val data = item as? SiteMeterModel ?: return
        clickEventUpdateScore(SiteMeterModel.TypePosition.fromValue(data.position))
      }
    }
  }

  private fun clickEventUpdateScore(value: SiteMeterModel.TypePosition?) {
    when (value) {
      SiteMeterModel.TypePosition.BUSINESS_NAME -> {
        if (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME).isNullOrEmpty()) baseActivity.startBusinessDescriptionEdit(session)
      }
      SiteMeterModel.TypePosition.DESCRIPTION -> {
        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION).isNullOrEmpty()) baseActivity.startBusinessDescriptionEdit(session)
      }
      SiteMeterModel.TypePosition.CATEGORY -> {
        if (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY).isNullOrEmpty()) baseActivity.startBusinessDescriptionEdit(session)
      }
      SiteMeterModel.TypePosition.EMAIL -> {
        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL).isNullOrEmpty()) baseActivity.startBusinessInfoEmail(session)
      }
      SiteMeterModel.TypePosition.PHONE -> {
        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER).isNullOrEmpty()) baseActivity.startBusinessContactInfo(session)
      }
      SiteMeterModel.TypePosition.ADDRESS -> {
        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).isNullOrEmpty() || session?.getFPDetails(Key_Preferences.LATITUDE).isNullOrEmpty() || session?.getFPDetails(Key_Preferences.LONGITUDE).isNullOrEmpty()) {
          baseActivity.startBusinessAddress(session)
        }
      }
      SiteMeterModel.TypePosition.BUSINESS_HOURS -> {
        if (session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS) == "TIMINGS") baseActivity.startBusinessHours(session)
        else alertDialogBusinessHours()
      }
      SiteMeterModel.TypePosition.IMAGE -> {
        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI).isNullOrEmpty()) baseActivity.startBusinessDescriptionEdit(session)
      }
      SiteMeterModel.TypePosition.LOGO -> {
        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl).isNullOrEmpty()) baseActivity.startBusinessLogo(session)
      }
      SiteMeterModel.TypePosition.POST -> if (MessageModel().getStoreBizFloatSize() < 5) baseActivity.startPostUpdate(session)
      SiteMeterModel.TypePosition.SOCIAL -> session?.let { baseActivity.startDigitalChannel(it) }
      SiteMeterModel.TypePosition.DOMAIN -> {
      }
    }
  }

  private fun alertDialogBusinessHours() {
    AlertDialog.Builder(baseActivity)
        .setTitle(getString(R.string.features_not_available))
        .setMessage(getString(R.string.check_store_for_upgrade_info))
        .setPositiveButton(getString(R.string.goto_store)) { dialogInterface, i ->
          baseActivity.startPricingPlan(session)
          dialogInterface.dismiss()
        }.setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.dismiss() }.show()
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }
}
