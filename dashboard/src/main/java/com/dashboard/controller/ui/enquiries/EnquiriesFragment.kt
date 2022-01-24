package com.dashboard.controller.ui.enquiries

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.controller.ui.dashboard.*
import com.dashboard.databinding.FragmentPatientsCustomerBinding
import com.dashboard.model.live.customerItem.BoostCustomerItemResponse
import com.dashboard.model.live.customerItem.CustomerActionItem
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.webengageconstant.*
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.ordersummary.TOTAL_SELLER_ENQUIRIES
import com.inventoryorder.model.summary.SummaryEntity
import com.inventoryorder.model.summary.USER_MY_ENQUIRIES
import com.inventoryorder.model.summary.UserSummaryResponse
import com.inventoryorder.model.summaryCall.CALL_MY_ENQUIRIES
import com.inventoryorder.model.summaryCall.CallSummaryResponse
import com.inventoryorder.rest.response.OrderSummaryResponse
import java.util.*

class EnquiriesFragment : AppBaseFragment<FragmentPatientsCustomerBinding, DashboardViewModel>(), RecyclerItemClickListener {

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
    WebEngageController.trackEvent(DASHBOARD_ENQUIRIES_PAGE, PAGE_VIEW, session?.fpTag)
    setDataSellerSummary(
      OrderSummaryModel().getTotalOrder(TOTAL_SELLER_ENQUIRIES),
      SummaryEntity().getUserSummary(USER_MY_ENQUIRIES),
      CallSummaryResponse().getCallSummary(CALL_MY_ENQUIRIES)
    )
    binding?.filterBtn?.setOnClickListener { showFilterBottomSheet() }
  }

  private fun showFilterBottomSheet() {
    val filterBottomSheet = FilterBottomSheet()
    filterBottomSheet.setData(FilterDateModel().getDateFilter(FILTER_MY_ENQUIRIES))
    filterBottomSheet.onClicked = {
      apiSellerSummary(it, true)
      it.saveData(FILTER_MY_ENQUIRIES)
      binding?.titleFilter?.text = it.title
      WebEngageController.trackEvent(CLICK_DATE_RANGE, CLICK, TO_BE_ADDED)
    }
    filterBottomSheet.show(
      this@EnquiriesFragment.parentFragmentManager,
      FilterBottomSheet::class.java.name
    )
  }

  private fun apiSellerSummary(enquiriesFilter: FilterDateModel, isLoader: Boolean = false) {
    if (isLoader) showProgress()
    viewModel?.getSellerSummaryV2_5(
      AppConstant.CLIENT_ID_2,
      session?.fpTag,
      getRequestSellerSummary(enquiriesFilter)
    )?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      val response1 = it as? OrderSummaryResponse
      response1?.Data?.saveTotalOrder(TOTAL_SELLER_ENQUIRIES)
      val scope = if (session?.iSEnterprise == "true") "1" else "0"
      viewModel?.getUserSummary(
        session?.fpTag,
        clientId,
        session?.fPParentId,
        scope,
        enquiriesFilter.startDate,
        enquiriesFilter.endDate
      )?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer { it1 ->
        val response2 = it1 as? UserSummaryResponse
        viewModel?.getSubscriberCount(
          session?.fpTag,
          clientId,
          enquiriesFilter.startDate,
          enquiriesFilter.endDate
        )?.observeOnce(viewLifecycleOwner, { it2 ->
          val subscriberCount = (it2.anyResponse as? Double)?.toInt() ?: 0
          val summary = response2?.getSummary()
          summary?.noOfSubscribers = subscriberCount
          summary?.saveData(USER_MY_ENQUIRIES)
          val identifierType = if (session?.iSEnterprise == "true") "MULTI" else "SINGLE"
          viewModel?.getUserCallSummary(
            clientId,
            session?.fPParentId,
            identifierType,
            enquiriesFilter.startDate,
            enquiriesFilter.endDate
          )?.observeOnce(viewLifecycleOwner, { it2 ->
            val response3 = it2 as? CallSummaryResponse
            response3?.saveData(CALL_MY_ENQUIRIES)
            setDataSellerSummary(
              response1?.Data?.getTotalOrders(),
              response2?.getSummary(),
              response3?.getTotalCalls()
            )
            if (isLoader) hideProgress()
          })
        })
      })
    })
  }

  private fun setDataSellerSummary(
    orderCount: String?,
    summary: SummaryEntity?,
    callCount: String?
  ) {
    viewModel?.getBoostCustomerItem(baseActivity)?.observeOnce(viewLifecycleOwner, { it0 ->
      val response = it0 as? BoostCustomerItemResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        val data = response.data?.firstOrNull {
          it.type.equals(
            session?.fP_AppExperienceCode,
            ignoreCase = true
          )
        }
        if (data != null && data.actionItem.isNullOrEmpty().not()) {
          data.actionItem!!.map { it2 ->
            if (it2.premiumCode.isNullOrEmpty()
                .not() && session.checkIsPremiumUnlock(it2.premiumCode).not()
            ) it2.isLock = true
          }
          data.actionItem?.forEach { it3 ->
            when (CustomerActionItem.IconType.fromName(it3.type)) {
              CustomerActionItem.IconType.customer_orders,
              CustomerActionItem.IconType.in_clinic_appointments,
              -> it3.orderCount = orderCount ?: ""
              CustomerActionItem.IconType.video_consultations -> it3.consultCount = ""
              CustomerActionItem.IconType.patient_customer_calls -> it3.customerCalls =
                callCount ?: ""
              CustomerActionItem.IconType.patient_customer_messages -> it3.messageCount =
                summary?.getNoOfMessages() ?: ""
              CustomerActionItem.IconType.newsletter_subscribers -> it3.subscriptionCount =
                summary?.getNoOfSubscribers()
            }
          }
          setAdapterCustomer(data.actionItem!!)
        }
      }
    })
  }

  private fun setAdapterCustomer(actionItem: ArrayList<CustomerActionItem>) {
    actionItem.map {
      it.recyclerViewItemType = RecyclerViewItemType.BOOST_ENQUIRIES_ITEM_VIEW.getLayout()
    }
    binding?.rvCustomer?.apply {
      if (adapterACustomer == null) {
        adapterACustomer =
          AppBaseRecyclerViewAdapter(baseActivity, actionItem, this@EnquiriesFragment)
//        addItemDecoration(DividerItemDecoration(baseActivity, DividerItemDecoration.VERTICAL))
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
      CustomerActionItem.IconType.customer_orders -> baseActivity.startOrderAptConsultList(
        session,
        isOrder = true
      )
      CustomerActionItem.IconType.in_clinic_appointments -> baseActivity.startOrderAptConsultList(
        session,
        isConsult = false
      )
      CustomerActionItem.IconType.video_consultations -> baseActivity.startOrderAptConsultList(
        session,
        isConsult = true
      )
      CustomerActionItem.IconType.patient_customer_calls -> baseActivity.startVmnCallCard(session)
      CustomerActionItem.IconType.patient_customer_messages -> baseActivity.startBusinessEnquiry(
        session
      )
      CustomerActionItem.IconType.newsletter_subscribers -> baseActivity.startSubscriber(session)
    }
  }

  override fun onResume() {
    super.onResume()
    var enquiriesFilter = FilterDateModel().getDateFilter(FILTER_MY_ENQUIRIES)
    if (enquiriesFilter == null) {
      enquiriesFilter = FilterDateModel().getFilterDate().last()
      enquiriesFilter.saveData(FILTER_MY_ENQUIRIES)
    }
    binding?.titleFilter?.apply { text = enquiriesFilter.title }
    apiSellerSummary(enquiriesFilter)
  }
}