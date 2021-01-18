package com.dashboard.controller.ui.customer

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.ui.dashboard.checkIsPremiumUnlock
import com.dashboard.controller.ui.dashboard.saveUserSummary
import com.dashboard.databinding.FragmentPatientsCustomerBinding
import com.dashboard.model.live.customerItem.BoostCustomerItemResponse
import com.dashboard.model.live.customerItem.CustomerActionItem
import com.dashboard.pref.UserSessionManager
import com.dashboard.pref.clientId
import com.dashboard.pref.clientId_ORDER
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.summary.SummaryEntity
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.response.OrderSummaryResponse
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
    setDataSellerSummary(OrderSummaryModel().getSellerSummary(), getSummaryDetail(), CallSummaryResponse().getCallSummary())
    WebEngageController.trackEvent("Customer Page", "pageview", session?.fpTag)
  }

  override fun onResume() {
    super.onResume()
    apiSellerSummary()
  }

  private fun apiSellerSummary() {
    viewModel?.getSellerSummary(clientId_ORDER, session?.fpTag)?.observeOnce(viewLifecycleOwner, {
      val response1 = it as? OrderSummaryResponse
      if (response1?.isSuccess() == true && response1.Data != null) response1.Data?.saveData()
      val scope = if (session?.iSEnterprise == "true") "1" else "0"
      viewModel?.getUserSummary(clientId, session?.fPParentId, scope)?.observeOnce(viewLifecycleOwner, { it1 ->
        val response2 = it1 as? UserSummaryResponse
        response2?.getSummary()?.noOfSubscribers = session?.subcribersCount?.toIntOrNull() ?: 0
        session?.saveUserSummary(response2?.getSummary())
        val identifierType = if (session?.iSEnterprise == "true") "MULTI" else "SINGLE"
        viewModel?.getUserCallSummary(clientId, session?.fPParentId, identifierType)?.observeOnce(viewLifecycleOwner, { it2 ->
          val response3 = it2 as? CallSummaryResponse
          response3?.saveData()
          setDataSellerSummary(response1?.Data, response2?.getSummary(), response3)
        })
      })
    })
  }

  private fun setDataSellerSummary(sellerOrder: OrderSummaryModel?, summary: SummaryEntity?, callSummary: CallSummaryResponse?) {
    viewModel?.getBoostCustomerItem(baseActivity)?.observeOnce(viewLifecycleOwner, { it0 ->
      val response = it0 as? BoostCustomerItemResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.firstOrNull { it.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }
        if (data != null && data.actionItem.isNullOrEmpty().not()) {
          data.actionItem!!.map { it2 -> if (it2.premiumCode.isNullOrEmpty().not() && session.checkIsPremiumUnlock(it2.premiumCode).not()) it2.isLock = true }
          data.actionItem?.forEach { it3 ->
            when (CustomerActionItem.IconType.fromName(it3.type)) {
              CustomerActionItem.IconType.customer_orders,
              CustomerActionItem.IconType.in_clinic_appointments,
              -> it3.orderCount = sellerOrder?.getTotalOrders() ?: ""
              CustomerActionItem.IconType.video_consultations -> it3.consultCount = ""
              CustomerActionItem.IconType.patient_customer_calls -> it3.customerCalls = callSummary?.getTotalCalls() ?: ""
              CustomerActionItem.IconType.patient_customer_messages -> it3.messageCount = summary?.getNoOfMessages() ?: ""
              CustomerActionItem.IconType.newsletter_subscribers -> it3.subscriptionCount = summary?.getNoOfSubscribers()
            }
          }
          setAdapterCustomer(data.actionItem!!)
        }
      }
    })
  }

  private fun setAdapterCustomer(actionItem: ArrayList<CustomerActionItem>) {
    actionItem.map { it.recyclerViewItemType = RecyclerViewItemType.BOOST_CUSTOMER_ITEM_VIEW.getLayout() }
    binding?.rvCustomer?.apply {
      if (adapterACustomer == null) {
        adapterACustomer = AppBaseRecyclerViewAdapter(baseActivity, actionItem, this@PatientCustomerFragment)
//        addItemDecoration(DividerItemDecoration(baseActivity, DividerItemDecoration.VERTICAL))
        adapter = adapterACustomer
      } else adapterACustomer?.notify(actionItem)
    }
  }

  private fun getSummaryDetail(): SummaryEntity? {
    return SummaryEntity(session?.enquiryCount?.toIntOrNull() ?: 0, session?.subcribersCount?.toIntOrNull() ?: 0, session?.visitorsCount?.toIntOrNull() ?: 0, session?.visitsCount?.toIntOrNull() ?: 0)
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