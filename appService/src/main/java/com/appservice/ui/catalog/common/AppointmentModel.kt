package com.appservice.ui.catalog.common

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable


data class AppointmentModel(
        var day: String? = null, var isTurnedOn: Boolean = false, var isAppliedOnAllDays: Boolean = false, var isAppliedOnAllDaysViewVisible: Boolean = false, var toTiming: String? = null, var fromTiming: String? = null, var recyclerViewItem: Int = RecyclerViewItemType.SESSION_ITEM_VIEW.getLayout(),
) : AppBaseRecyclerViewItem, Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }

    fun getDefaultModel(day: String?, isAppliedOnAllDays: Boolean, isAppliedOnAllDaysVisible: Boolean): AppointmentModel {
        val m = AppointmentModel()
        m.day = day
        m.isTurnedOn = false
        m.isAppliedOnAllDays = isAppliedOnAllDays
        m.isAppliedOnAllDaysViewVisible = isAppliedOnAllDaysVisible
        return m
    }

    fun getDefaultTimings(): ArrayList<AppointmentModel> {
        val list = ArrayList<AppointmentModel>()
        list.add(AppointmentModel(day = "Monday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(AppointmentModel(day = "Tuesday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(AppointmentModel(day = "Wednesday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(AppointmentModel(day = "Thursday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(AppointmentModel(day = "Friday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(AppointmentModel(day = "Saturday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(AppointmentModel(day = "Sunday", isTurnedOn = false, isAppliedOnAllDays = false))
        return list
    }
}


