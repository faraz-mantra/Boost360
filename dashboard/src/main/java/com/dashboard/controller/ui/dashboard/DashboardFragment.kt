package com.dashboard.controller.ui.dashboard

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentDashboardBinding
import com.dashboard.model.ChannelData
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.models.BaseViewModel

class DashboardFragment : AppBaseFragment<FragmentDashboardBinding, BaseViewModel>(), RecyclerItemClickListener {

  override fun getLayout(): Int {
    return R.layout.fragment_dashboard
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.channelList?.apply {
      val data = ChannelData().getDataChannel()
      val adapterN = AppBaseRecyclerViewAdapter(baseActivity, data, this@DashboardFragment)
      adapter = adapterN
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}