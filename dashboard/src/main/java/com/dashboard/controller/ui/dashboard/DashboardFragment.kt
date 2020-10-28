package com.dashboard.controller.ui.dashboard

import androidx.viewpager2.widget.ViewPager2
import com.appservice.utils.setGifAnim
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.databinding.FragmentDashboardBinding
import com.dashboard.model.*
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.models.BaseViewModel
import com.framework.views.dotsindicator.OffsetPageTransformer

class DashboardFragment : AppBaseFragment<FragmentDashboardBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var adapterPager1: AppBaseRecyclerViewAdapter<BusinessSetupData>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.rvChannelList?.apply {
      val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, ChannelData().getDataChannel(), this@DashboardFragment)
      adapter = adapter1
    }

    binding?.pagerBusinessSetup?.apply {
      adapterPager1 = AppBaseRecyclerViewAdapter(baseActivity, BusinessSetupData().getData(), this@DashboardFragment)
      offscreenPageLimit = 3
      adapter = adapterPager1
      binding?.dotIndicator?.setViewPager2(this)
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          super.onPageSelected(position)
          binding?.motionOne?.loadLayoutDescription(takeIf { position == 0 }?.let { R.xml.fragment_dashboard_scene } ?: 0)
        }
      })
      baseActivity.setGifAnim(binding?.missingDetailsGif!!, R.raw.ic_missing_setup_gif_d, R.drawable.ic_custom_page_d)
      baseActivity.setGifAnim(binding?.arrowLeftGif!!, R.raw.ic_arrow_left_gif_d, R.drawable.ic_arrow_right_14_d)
    }

    binding?.pagerQuickAction?.apply {
      val adapterPager2 = AppBaseRecyclerViewAdapter(baseActivity, QuickActionData().getData(), this@DashboardFragment)
      offscreenPageLimit = 3
      adapter = adapterPager2
      binding?.dotIndicatorAction?.setViewPager2(this)
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding?.rvManageBusiness?.apply {
      val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, ManageBusinessData().getData(), this@DashboardFragment)
      adapter = adapter1
    }
    binding?.pagerRiaAcademy?.apply {
      val adapterPager3 = AppBaseRecyclerViewAdapter(baseActivity, RiaAcademyData().getData(), this@DashboardFragment)
      offscreenPageLimit = 3
      adapter = adapterPager3
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding?.pagerBoostPremium?.apply {
      val adapterPager4 = AppBaseRecyclerViewAdapter(baseActivity, BoostPremiumData().getDataChannel(), this@DashboardFragment)
      offscreenPageLimit = 3
      adapter = adapterPager4
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding?.rvRoiSummary?.apply {
      val adapter2 = AppBaseRecyclerViewAdapter(baseActivity, RoiSummaryData().getData(), this@DashboardFragment)
      adapter = adapter2
    }

    binding?.rvGrowthState?.apply {
      val adapter3 = AppBaseRecyclerViewAdapter(baseActivity, ChannelData().getDataChannel(RecyclerViewItemType.GROWTH_STATE_ITEM_VIEW.getLayout()), this@DashboardFragment)
      adapter = adapter3
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}
