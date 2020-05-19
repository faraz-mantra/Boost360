package com.inventoryorder.model.bottomsheet

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class AppointMentTypeModel(var appointmentTypeSelectedName: String? = null,
                           var isSelected : Boolean = false ) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.APPOINTMENT_TYPE.getLayout()
    }

    fun getData() : ArrayList<AppointMentTypeModel> {
        val list = ArrayList<AppointMentTypeModel>()
        list.add(AppointMentTypeModel("AppointmentType 1",false))
        list.add(AppointMentTypeModel("AppointmentType 2",false))
        list.add(AppointMentTypeModel("AppointmentType 3",false))
        list.add(AppointMentTypeModel("AppointmentType 4",false))
        list.add(AppointMentTypeModel("AppointmentType 5",false))
        list.add(AppointMentTypeModel("AppointmentType 6",false))
        return list
    }


}