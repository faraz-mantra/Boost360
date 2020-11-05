package com.dashboard.controller.ui.digitalScore

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentDigitalReadinessScoreBinding
import com.dashboard.model.BusinessContentSetupData
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.models.BaseViewModel
import com.framework.views.dotsindicator.OffsetPageTransformer

class DigitalReadinessScoreFragment : AppBaseFragment<FragmentDigitalReadinessScoreBinding, BaseViewModel>(), RecyclerItemClickListener {

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
    binding?.pagerBusinessContentSetup?.apply {
      val adapterPager5 = AppBaseRecyclerViewAdapter(baseActivity, BusinessContentSetupData().getData(), this@DigitalReadinessScoreFragment)
      offscreenPageLimit = 3
      clipToPadding = false
      setPadding(37, 0, 37, 0)
      adapter = adapterPager5
      binding?.dotBusinessContentSetup?.setViewPager2(this)
      setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

}