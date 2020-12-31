package com.dashboard.controller.ui.customer

import androidx.recyclerview.widget.DividerItemDecoration
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.checkIsPremiumUnlock
import com.dashboard.databinding.FragmentPatientsCustomerBinding
import com.dashboard.model.live.customerItem.BoostCustomerItemResponse
import com.dashboard.model.live.customerItem.CustomerActionItem
import com.dashboard.pref.UserSessionManager
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import java.util.*

class PatientCustomerFragment : AppBaseFragment<FragmentPatientsCustomerBinding, DashboardViewModel>(), RecyclerItemClickListener {

  private var session: UserSessionManager? = null
  private var adapterACustomer: AppBaseRecyclerViewAdapter<CustomerActionItem>? = null

  override fun getLayout(): Int {
    return R.layout.fragment_patients_customer
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    loadData()
    WebEngageController.trackEvent("Customer Page", "pageview", session?.fpTag)
  }

  private fun loadData() {
    viewModel?.getBoostCustomerItem(baseActivity)?.observeOnce(viewLifecycleOwner, { it0 ->
      val response = it0 as? BoostCustomerItemResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.firstOrNull { it.type?.toLowerCase(Locale.ROOT) == session?.fP_AppExperienceCode?.toLowerCase(Locale.ROOT) }
        if (data != null && data.actionItem.isNullOrEmpty().not()) {
          data.actionItem!!.map { it2 -> if (it2.premiumCode.isNullOrEmpty().not() && session.checkIsPremiumUnlock(it2.premiumCode).not()) it2.isLock = true }
          setAdapterCustomer(data.actionItem!!)
        }
      }
    })
  }

  private fun setAdapterCustomer(actionItem: ArrayList<CustomerActionItem>) {
    binding?.rvCustomer?.apply {
      if (adapterACustomer == null) {
        adapterACustomer = AppBaseRecyclerViewAdapter(baseActivity, actionItem, this@PatientCustomerFragment)
        addItemDecoration(DividerItemDecoration(baseActivity, DividerItemDecoration.VERTICAL))
        adapter = adapterACustomer
      } else adapterACustomer?.notify(actionItem)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.CUSTOMER_PATIENT_ITEM_CLICK.ordinal -> {
        val data = item as? CustomerActionItem ?: return
        data.type?.let { CustomerActionItem.IconType.fromName(it) }?.let { clickActionButton(it) }
      }
    }
  }

  private fun clickActionButton(type: CustomerActionItem.IconType) {
    when (type) {
      CustomerActionItem.IconType.customer_orders -> baseActivity.startOrderAptConsultList(session, isOrder = true)
      CustomerActionItem.IconType.in_clinic_appointments -> baseActivity.startOrderAptConsultList(session, isConsult = false)
      CustomerActionItem.IconType.video_consultations -> baseActivity.startOrderAptConsultList(session, isConsult = true)
      CustomerActionItem.IconType.patient_customer_calls -> baseActivity.startVmnCallCard(session)
      CustomerActionItem.IconType.patient_customer_messages -> baseActivity.startBusinessEnquiry(session)
      CustomerActionItem.IconType.newsletter_subscribers -> baseActivity.startSubscriber(session)
    }
  }
}