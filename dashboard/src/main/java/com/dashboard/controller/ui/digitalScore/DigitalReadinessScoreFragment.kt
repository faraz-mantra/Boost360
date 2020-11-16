package com.dashboard.controller.ui.digitalScore

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.databinding.FragmentDigitalReadinessScoreBinding
import com.dashboard.model.BusinessContentSetupData
import com.dashboard.model.getListDigitalScore
import com.dashboard.pref.UserSessionManager
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.siteMeterData
import com.framework.models.BaseViewModel
import com.framework.views.dotsindicator.OffsetPageTransformer

class DigitalReadinessScoreFragment : AppBaseFragment<FragmentDigitalReadinessScoreBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var adapterPager: AppBaseRecyclerViewAdapter<BusinessContentSetupData>? = null
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

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    position = arguments?.getInt(IntentConstant.POSITION.name) ?: 0
    binding?.btnBack?.setOnClickListener { baseActivity.onNavPressed() }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

  override fun onResume() {
    super.onResume()
    getSiteMeter()
  }

  private fun getSiteMeter() {
    val siteMeterData = session?.siteMeterData() ?: return
    isHigh = (siteMeterData.siteMeterTotalWeight >= 80)
    val listDigitalScore = siteMeterData.getListDigitalScore()
    val list = ArrayList(listDigitalScore.map { it.recyclerViewItemType = RecyclerViewItemType.BUSINESS_CONTENT_SETUP_ITEM_VIEW.getLayout();it })
    if (adapterPager == null) {
      binding?.pagerBusinessContentSetup?.apply {
        adapterPager = AppBaseRecyclerViewAdapter(baseActivity, list, this@DigitalReadinessScoreFragment)
        offscreenPageLimit = 3
        clipToPadding = false
        setPadding(36, 0, 36, 0)
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