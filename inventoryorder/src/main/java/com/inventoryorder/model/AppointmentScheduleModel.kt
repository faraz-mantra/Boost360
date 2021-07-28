package com.inventoryorder.model

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class AppointmentScheduleModel(
  var appointMeantSchedule: String? = null,
  var isSelected: Boolean = false,
  var isLastItem: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.APPOINTMENT_SCHEDULE.getLayout()
  }

  fun setSelectedItemBackground(): Int {
    return takeIf { isSelected }?.let { R.color.light_grey }
      ?: R.drawable.item_appointment_schedule_background_2
  }

  fun setSelectedItemColor(): Int {
    return takeIf { isSelected }?.let { R.color.warm_grey_10 } ?: R.color.primary_grey
  }

  fun getData(): ArrayList<AppointmentScheduleModel> {
    val list = ArrayList<AppointmentScheduleModel>()
    list.add(AppointmentScheduleModel("Today", true, true))
    list.add(AppointmentScheduleModel("Tomorrow"))
    list.add(AppointmentScheduleModel("01-June"))
    list.add(AppointmentScheduleModel("02-June"))
    list.add(AppointmentScheduleModel("03-June"))
    list.add(AppointmentScheduleModel("04-June"))
    list.add(AppointmentScheduleModel("05-June"))
    list.add(AppointmentScheduleModel("06-June"))
    list.add(AppointmentScheduleModel("07-June"))
    list.add(AppointmentScheduleModel("08-June"))
    list.lastOrNull()?.isLastItem = true
    return list
  }

}