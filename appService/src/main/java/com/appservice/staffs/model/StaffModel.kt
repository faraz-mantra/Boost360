package com.appservice.staffs.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class SessionModel(var staffTimingModel: StaffTimingModel? = null, var fromTime: String? = null, var toTime: String? = null, var recyclerViewItem: Int = RecyclerViewItemType.SESSION_ITEM_VIEW.getLayout()) : AppBaseRecyclerViewItem, Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }
}


data class StaffTimingModel(
        var day: String? = null, var isTurnedOn: Boolean = false, var isAppliedOnAllDays: Boolean = false, var isAppliedOnAllDaysViewVisible: Boolean = false, var sessions: ArrayList<SessionModel>? = null, var recyclerViewItem: Int = RecyclerViewItemType.SESSION_ITEM_VIEW.getLayout()) : AppBaseRecyclerViewItem, Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }

    fun onDayTurnedOn() {
        isTurnedOn = true
        sessions = java.util.ArrayList()
        sessions!!.add(SessionModel())
    }

    fun onDayTurnedOff() {
        isTurnedOn = false
        sessions!!.clear()
    }

    fun addSession() {
        sessions?.add(SessionModel())
    }

    fun deleteSession(sessionIndex: Int) {
        sessions!!.removeAt(sessionIndex)
    }

    fun getDefaultModel(day: String?, isAppliedOnAllDays: Boolean, isAppliedOnAllDaysVisible: Boolean): StaffTimingModel {
        val m = StaffTimingModel()
        m.day = day
        m.isTurnedOn = false
        m.isAppliedOnAllDays = isAppliedOnAllDays
        m.isAppliedOnAllDaysViewVisible = isAppliedOnAllDaysVisible
        return m
    }

    fun getDefaultTimings(): ArrayList<StaffTimingModel> {
        val list = ArrayList<StaffTimingModel>()
        list.add(StaffTimingModel(day = "Monday", isTurnedOn = false, isAppliedOnAllDays = true))
        list.add(StaffTimingModel(day = "Tuesday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(StaffTimingModel(day = "Wednesday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(StaffTimingModel(day = "Thursday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(StaffTimingModel(day = "Friday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(StaffTimingModel(day = "Saturday", isTurnedOn = false, isAppliedOnAllDays = false))
        list.add(StaffTimingModel(day = "Sunday", isTurnedOn = false, isAppliedOnAllDays = false))
        return list
    }
}


