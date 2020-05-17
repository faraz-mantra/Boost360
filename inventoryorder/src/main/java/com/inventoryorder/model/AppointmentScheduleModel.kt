package com.inventoryorder.model

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class AppointmentScheduleModel (var appointMentSchedule : String? = null,
                                var isSelected : Boolean = false ) : AppBaseRecyclerViewItem{

    override fun getViewType(): Int {
       return RecyclerViewItemType.APPOINTMENT_SCHEDULE.getLayout()
    }

    fun getData() : ArrayList<AppointmentScheduleModel>{

        val list = ArrayList<AppointmentScheduleModel>()
        list.add(AppointmentScheduleModel("Today",true))
        list.add(AppointmentScheduleModel("Tomorrow"))
        list.add(AppointmentScheduleModel("23-MAY"))
        list.add(AppointmentScheduleModel("24-MAY"))
        list.add(AppointmentScheduleModel("25-MAY"))
        return list
    }



}