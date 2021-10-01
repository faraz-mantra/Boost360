package com.appservice.ui.catalog.common

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.model.staffModel.StaffDetailsResult
import com.appservice.viewmodel.StaffViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeeklyAppointmentFragment : AppBaseFragment<FragmentStaffTimingBinding, StaffViewModel>(),
  RecyclerItemClickListener {

  private var isEdit: Boolean? = null
  private var staffData: StaffDetailsResult? = null

  private lateinit var defaultTimings: ArrayList<AppointmentModel>
  private lateinit var adapter: AppBaseRecyclerViewAdapter<AppointmentModel>

  companion object {
    fun newInstance(): WeeklyAppointmentFragment = WeeklyAppointmentFragment()
  }

  override fun getLayout(): Int {
    return R.layout.fragment_staff_timing
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  private fun getBundleData() {
    staffData = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
    initDefaultTiming()
    setTimings()
  }

  private fun initDefaultTiming() {
    if (staffData?.timings.isNullOrEmpty().not()) {
      this.defaultTimings = ArrayList();
      for (item in this.staffData?.timings!!) {
        if (item?.timeSlots.isNullOrEmpty().not()) {
          item?.isTurnedOn = true
        }
        if (item?.day?.toLowerCase().equals("monday")) {
          item?.isAppliedOnAllDaysViewVisible = true;
        }
        this.defaultTimings?.add(item);
      }
    } else {
      this.defaultTimings = AppointmentModel.getDefaultTimings()
    }
  }

  private fun setTimings() {
    if (defaultTimings.isNullOrEmpty().not()) {
      this.adapter = AppBaseRecyclerViewAdapter(
        baseActivity,
        this.defaultTimings,
        this@WeeklyAppointmentFragment
      )
      binding?.rvStaffTiming?.adapter = adapter
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    getBundleData()
    setOnClickListener(binding?.btnSave)
    this.adapter =
      AppBaseRecyclerViewAdapter(baseActivity, this.defaultTimings, this@WeeklyAppointmentFragment)
    binding!!.rvStaffTiming.adapter = adapter

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal -> {
        adapter.notifyDataSetChanged()
      }
      RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal -> {
        item as AppointmentModel
        applyOnAllDays(item)
        adapter.notifyDataSetChanged()
      }
      RecyclerViewActionType.ADD_SESSION.ordinal -> {
        item as AppointmentModel
        when (item.isAppliedOnAllDays == true) {
          true -> applyOnAllDays(item);
        }
        adapter.notifyDataSetChanged()
      }
    }

  }

  private fun applyOnAllDays(data: AppointmentModel) {
    if (data.isAppliedOnAllDays!!) {
      applyOnAllDaysTurnedOn(data)
    } else {
      applyOnAllDaysTurnedOff(data)
    }
  }

  private fun applyOnAllDaysTurnedOn(data: AppointmentModel) {
    for (i in 1 until defaultTimings.size) {
      val item = defaultTimings[i]
      item.isTurnedOn = true
      item.timeSlots = ArrayList()
      for (t in data.timeSlots) {
        val t1 = TimeSlot();
        t1.to = t.to
        t1.from = t.from
        item.timeSlots.add(t1)
      }
    }
  }


  private fun applyOnAllDaysTurnedOff(data: AppointmentModel) {
    for (i in 1 until defaultTimings.size) {
      val item = defaultTimings[i]
      item.isViewEnabled = true
      item.isDataAppliedOnMyDay = false
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSave -> {
        if (isValid()) {
          finishAndGoBack()
        }
      }
    }
  }

  fun isValid(): Boolean {
    var i = 0
    this.defaultTimings.forEachIndexed { index, appointmentModel ->
      appointmentModel.timeSlots.forEachIndexed { pos, timeSlot ->
        if (timeSlot.from == timeSlot.to) {
          showLongToast(getString(R.string.start_end_can_not_be_same))
          return false
        }

        if (convertAndCompareTime(timeSlot.from!!, timeSlot.to!!)) {
          showLongToast(getString(R.string.endtime_cannot_be_before_starttime))
          return false
        }

        if ((pos + 1) < defaultTimings[index].timeSlots.size && defaultTimings[index].timeSlots[pos + 1].from != null) {
          if (convertAndCompareTime(
              defaultTimings[index].timeSlots[pos].to ?: "",
              defaultTimings[index].timeSlots[pos + 1].from ?: ""
            )
          ) {
            showLongToast(getString(R.string.starttime_on_one_slot_cannot_be_before_endtime_of_other_slot))
            return false
          }
        }
      }
    }
    staffData?.timings = this.defaultTimings
    return true
  }

  private fun convertAndCompareTime(startTime: String, endTime: String): Boolean {
    val sdf = SimpleDateFormat("hh:mma")
    var d1 = Date()
    var d2 = Date()
    try {
      d1 = sdf.parse(startTime)
      d2 = sdf.parse(endTime)
    } catch (e: Exception) {
    }

    return d2.before(d1)
  }

  private fun finishAndGoBack() {
    val intent = Intent()
    intent.putExtra(IntentConstant.STAFF_TIMINGS.name, staffData)
    requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
    baseActivity.finish()
  }
}