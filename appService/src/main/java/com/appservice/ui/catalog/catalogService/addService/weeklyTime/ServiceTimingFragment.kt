package com.appservice.ui.catalog.catalogService.addService.weeklyTime

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentServiceTimingBinding
import com.appservice.model.serviceTiming.ServiceTime
import com.appservice.model.serviceTiming.ServiceTiming
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.viewmodel.StaffViewModel
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.parseDate
import java.util.*

class ServiceTimingFragment : AppBaseFragment<FragmentServiceTimingBinding, StaffViewModel>(),
  RecyclerItemClickListener {
  private var isEdit: Boolean = false
  private var serviceTimingList: ArrayList<ServiceTiming>? = null
  private var adapterTiming: AppBaseRecyclerViewAdapter<ServiceTiming>? = null

  companion object {
    fun newInstance(): ServiceTimingFragment = ServiceTimingFragment()
  }

  override fun getLayout(): Int {
    return R.layout.fragment_service_timing
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    getBundleData()
    setOnClickListener(binding?.btnSave)
    setServiceTimingAdapter(this.serviceTimingList!!)
  }

  private fun getBundleData() {
    this.serviceTimingList =
      arguments?.getSerializable(IntentConstant.SERVICE_TIMING_DATA.name) as? ArrayList<ServiceTiming>
    this.isEdit = arguments?.getBoolean(IntentConstant.IS_EDIT.name) ?: false
    if (this.isEdit.not()) updateTimingMapWithBusinessHour()
  }

  private fun updateTimingMapWithBusinessHour() {
    this.serviceTimingList?.map {
      if (it.isOpenDay()) it.time =
        ServiceTime(it.businessTiming?.startTime, it.businessTiming?.endTime)
    }
  }

  private fun setServiceTimingAdapter(serviceTimingList: ArrayList<ServiceTiming>) {
    binding?.rvStaffTiming?.apply {
      adapterTiming = AppBaseRecyclerViewAdapter(
        activity = baseActivity,
        serviceTimingList,
        this@ServiceTimingFragment
      )
      adapter = adapterTiming
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal -> {
        val data = (item as? ServiceTiming)
        if (data?.appliedOnPosition != position) {
          data?.appliedOnPosition = null
          adapterTiming?.notifyItemChanged(position)
        } else applyOnAllDays(position, (item as? ServiceTiming))
      }
      RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal -> applyOnAllDays(
        position,
        (item as? ServiceTiming)
      )
    }
  }

  private fun applyOnAllDays(position: Int, serviceTiming: ServiceTiming?) {
    val appliedOnP = serviceTiming?.appliedOnPosition
    adapterTiming?.list()?.forEach {
      if (appliedOnP != null) {
        if (serviceTiming.day.equals(it.day)) it.appliedOnPosition = null
        it.time = ServiceTime(it.businessTiming?.startTime, it.businessTiming?.endTime)
        it.isToggle = false
      } else {
        var startDate = it.businessTiming?.startTime?.parseDate(DateUtils.FORMAT_HH_MMA)
        if (startDate == null) startDate =
          it.businessTiming?.startTime?.parseDate(DateUtils.FORMAT_HH_MM_A)
        var endDate = it.businessTiming?.endTime?.parseDate(DateUtils.FORMAT_HH_MMA)
        if (endDate == null) endDate =
          it.businessTiming?.endTime?.parseDate(DateUtils.FORMAT_HH_MM_A)
        val startDateNew = serviceTiming?.time?.from?.parseDate(DateUtils.FORMAT_HH_MM_A)
        val endDateNew = serviceTiming?.time?.to?.parseDate(DateUtils.FORMAT_HH_MM_A)
        if (startDate != null && endDate != null && startDateNew != null && endDateNew != null && DateUtils.isBetweenValidTime(
            startDate,
            endDate,
            startDateNew
          )
          && DateUtils.isBetweenValidTime(startDate, endDate, endDateNew)
        ) {
          it.time = serviceTiming.time
          it.isToggle = true
          it.appliedOnPosition = position
        } else it.isToggle = false
      }
    }
    adapterTiming?.notifyDataSetChanged()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSave -> finishAndGoBack()
    }
  }

  private fun finishAndGoBack() {
    val intent = Intent()
    intent.putExtra(IntentConstant.SERVICE_TIMING_DATA.name, adapterTiming?.list())
    baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
    baseActivity.finish()
  }
}