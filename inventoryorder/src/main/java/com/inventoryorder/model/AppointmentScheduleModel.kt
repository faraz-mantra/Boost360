package com.inventoryorder.model

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class AppointmentScheduleModel(var appointMeantSchedule: String? = null,
                               var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.APPOINTMENT_SCHEDULE.getLayout()
    }

    fun setSelectedItemBackground() : Int{
        return takeIf { isSelected }?.let { R.color.light_grey } ?: R.color.black
//                drawable.item_appointment_schedule_background_2  // R.color.white
    }

    fun setSelectedItemColor() : Int{
      return takeIf { isSelected }?.let { R.color.warm_grey_10 } ?: R.color.primary_grey
    }

    fun getData(): ArrayList<AppointmentScheduleModel> {
        val list = ArrayList<AppointmentScheduleModel>()
        list.add(AppointmentScheduleModel("Today", true))
        list.add(AppointmentScheduleModel("Tomorrow"))
        list.add(AppointmentScheduleModel("28-MAY"))
        list.add(AppointmentScheduleModel("29-MAY"))
        list.add(AppointmentScheduleModel("30-MAY"))
        list.add(AppointmentScheduleModel("31-MAY"))
        list.add(AppointmentScheduleModel("01-June"))
        return list
    }

}