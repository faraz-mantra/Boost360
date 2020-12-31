package com.dashboard.controller.ui.academy

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.databinding.FragmentAcademyBinding
import com.dashboard.model.live.premiumBanner.*
import com.dashboard.pref.UserSessionManager
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.promoBannerMarketplace
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible

class AcademyFragment : AppBaseFragment<FragmentAcademyBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var adapterAcademy: AppBaseRecyclerViewAdapter<PromoAcademyBanner>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_academy
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    getPremiumBanner()
  }

  private fun getPremiumBanner() {
    val listAcademy = PremiumFeatureData().getAcademyBanners() ?: ArrayList()
    if (listAcademy.isNullOrEmpty().not()) setDataRiaAcademy(listAcademy)
    else showProgress()
    viewModel?.getUpgradePremiumBanner()?.observeOnce(viewLifecycleOwner, {
      val response = it as? UpgradePremiumFeatureResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.get(0)
        val academyBannerFilter = (data?.academyBanner ?: ArrayList()).marketBannerFilter(session)
        saveDataAcademy(academyBannerFilter)
        setDataRiaAcademy(academyBannerFilter)
        hideProgress()
      }
    })
  }

  private fun setDataRiaAcademy(academyBanner: ArrayList<PromoAcademyBanner>) {
    binding?.rvRiaAcademy?.apply {
      if (academyBanner.isNotEmpty()) {
        academyBanner.map { it.recyclerViewItemType = RecyclerViewItemType.RIA_ACADEMY_ITEM_NEW_VIEW.getLayout() }
        visible()
        binding?.errorTxt?.gone()
        if (adapterAcademy == null) {
          adapterAcademy = AppBaseRecyclerViewAdapter(baseActivity, academyBanner, this@AcademyFragment)
          adapter = adapterAcademy
        } else adapterAcademy?.notify(academyBanner)
      } else {
        gone()
        binding?.errorTxt?.visible()
      }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.PROMO_BANNER_CLICK.ordinal -> {
        val data = item as? PromoAcademyBanner ?: return
        session?.let { baseActivity.promoBannerMarketplace(it, data) }
      }
    }
  }

  override fun showProgress(title: String?, cancelable: Boolean?) {
    binding?.progress?.visible()
  }

  override fun hideProgress() {
    binding?.progress?.gone()
  }
}