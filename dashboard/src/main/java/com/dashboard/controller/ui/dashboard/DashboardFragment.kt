package com.dashboard.controller.ui.dashboard

import android.animation.ValueAnimator
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.appservice.utils.setGifAnim
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentDashboardBinding
import com.dashboard.model.*
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.views.dotsindicator.OffsetPageTransformer
import java.util.*
import kotlin.concurrent.schedule


class DashboardFragment : AppBaseFragment<FragmentDashboardBinding, BaseViewModel>(), RecyclerItemClickListener {

  private val isHigh = true

  private var adapterPager1: AppBaseRecyclerViewAdapter<BusinessSetupData>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnVisitingCardUp, binding?.btnVisitingCardDown)
    binding?.rvChannelList?.apply {
      val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, ChannelData().getDataChannel(), this@DashboardFragment)
      adapter = adapter1
    }

    if (isHigh) {
      binding?.viewLowDigitalReadiness?.gone()
      binding?.viewLowTaskManageBusiness?.gone()
      binding?.viewHighDigitalReadiness?.visible()
      binding?.pagerBusinessSetupHigh?.apply {
        val adapterPager2 = AppBaseRecyclerViewAdapter(baseActivity, BusinessSetupHighData().getData(), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager2
        binding?.dotIndicatorBusinessHigh?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    } else {
      binding?.viewHighDigitalReadiness?.gone()
      binding?.viewLowDigitalReadiness?.visible()
      binding?.viewLowTaskManageBusiness?.visible()
      binding?.pagerBusinessSetupLow?.apply {
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
    }

    (if (isHigh) binding?.highRecommendedTask else binding?.lowRecommendedTask)?.apply {
      pagerQuickAction.apply {
        val adapterPager5 = AppBaseRecyclerViewAdapter(baseActivity, QuickActionData().getData(), this@DashboardFragment)
        offscreenPageLimit = 3
        adapter = adapterPager5
        binding?.lowRecommendedTask?.dotIndicatorAction?.setViewPager2(this)
        setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
      }
    }
    (if (isHigh) binding?.highManageBusiness else binding?.lowManageBusiness)?.apply {
      rvManageBusiness.apply {
        val adapter1 = AppBaseRecyclerViewAdapter(baseActivity, ManageBusinessData().getData(), this@DashboardFragment)
        adapter = adapter1
      }
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
      val adapter3 = AppBaseRecyclerViewAdapter(baseActivity, GrowthStatsData().getData(), this@DashboardFragment)
      adapter = adapter3
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnVisitingCardUp -> visitingCardShowHide(true)
      binding?.btnVisitingCardDown -> visitingCardShowHide(false)
    }
  }

  private fun visitingCardShowHide(isDown: Boolean) {
    Timer().schedule(if (isDown) 60 else 150) {
      binding?.viewDigitalScore?.post {
        binding?.viewDigitalScore?.elevation = resources.getDimension(if (isDown) R.dimen.size_2 else R.dimen.size_0)
        binding?.viewAllBusinessContact?.visibility = if (isDown) View.GONE else View.VISIBLE
        binding?.viewVisitingCardProduct?.visibility = if (isDown) View.VISIBLE else View.GONE
        if (isHigh) {
          binding?.viewUpBusinessShadow?.background = if (isDown) ContextCompat.getDrawable(baseActivity, R.drawable.up_shadow_d) else null
          binding?.viewBusinessBgScore?.background = if (isDown) null else ContextCompat.getDrawable(baseActivity, R.drawable.ic_bg_dark_white_vertical)
        }
      }
    }
    binding?.viewDigitalScore?.animateViewTopPadding(isDown)
  }
}

private fun LinearLayoutCompat?.animateViewTopPadding(isDown: Boolean) {
  this?.apply {
    val animator: ValueAnimator = ValueAnimator.ofInt(paddingTop, resources.getDimensionPixelSize(if (isDown) R.dimen.size_0 else R.dimen.size_164))
    animator.addUpdateListener { valueAnimator -> setPadding(0, (valueAnimator.animatedValue as Int), 0, 0) }
    animator.duration = 280
    animator.start()
  }
}
