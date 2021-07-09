package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class AppointMentTypeModel(var appointmentTypeSelectedName: String? = null,
                           var isSelected: Boolean = false) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.APPOINTMENT_TYPE.getLayout()
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected_orange } ?: R.drawable.ic_option_unselected
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.lightest_grey } ?: R.color.white
  }

  fun getData(): ArrayList<AppointMentTypeModel> {
    val list = ArrayList<AppointMentTypeModel>()
    list.add(AppointMentTypeModel("AppointmentType 1"))
    list.add(AppointMentTypeModel("AppointmentType 2"))
    list.add(AppointMentTypeModel("AppointmentType 3", true))
    list.add(AppointMentTypeModel("AppointmentType 4"))
    list.add(AppointMentTypeModel("AppointmentType 5"))
    list.add(AppointMentTypeModel("AppointmentType 6"))
    return list
  }
}