package com.appservice.ui.catalog.common

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AppointmentModel(
        var day: String? = null,
        var timeSlots: ArrayList<TimeSlot>? = null,
        var isDataAppliedOnMyDay: Boolean? = false,
        var isTurnedOn: Boolean = false, var isAppliedOnAllDays: Boolean = false, var isAppliedOnAllDaysViewVisible: Boolean = false, var toTiming: String? = null, var fromTiming: String? = null, var recyclerViewItem: Int = RecyclerViewItemType.SESSION_ITEM_VIEW.getLayout(),
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

    private fun isDayTurnedOn() {
        timeSlots = ArrayList()
        this.isTurnedOn = true
        timeSlots?.add(TimeSlot.getDefaultTimeSlotObject());
    }

    fun applyOnAllDays(startTime: String, endTime: String) {
        getDefaultTimings().forEach {
            it.fromTiming = startTime
            it.toTiming = endTime
        }
    }

    fun deleteSession(sessionIndex: Int) {
        timeSlots?.removeAt(sessionIndex)

    }

    fun changeDayTurned(dayTurned: Boolean) {
        when {
            dayTurned -> isDayTurnedOn()
            else -> isDayTurnedOff()
        }
    }

    private fun isDayTurnedOff() {
        this.isTurnedOn = false
        timeSlots?.clear()

    }

    fun addSession() {
        timeSlots?.add(TimeSlot())
    }

    fun removeSession(index: Int) {
        timeSlots?.removeAt(index);
    }

    fun removeApplyOnAllDays(data: AppointmentModel) {
        getDefaultTimings().forEach { if (it != data) it.isTurnedOn = false }
    }

    companion object {
        fun getDefaultTimings(): ArrayList<AppointmentModel> {
            val list = ArrayList<AppointmentModel>()
            val monday = AppointmentModel(day = "Monday", isTurnedOn = false, isAppliedOnAllDays = false);
            monday.isAppliedOnAllDaysViewVisible = true;
            list.add(monday);
            list.add(AppointmentModel(day = "Tuesday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(AppointmentModel(day = "Wednesday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(AppointmentModel(day = "Thursday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(AppointmentModel(day = "Friday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(AppointmentModel(day = "Saturday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(AppointmentModel(day = "Sunday", isTurnedOn = false, isAppliedOnAllDays = false))
            return list
        }
    }
}

data class TimingsItem(

        @field:SerializedName("time")
        val time: TimeSlot? = null,

        @field:SerializedName("day")
        val day: String? = null,
)

data class TimeSlot(

        @field:SerializedName("from")
        var from: String? = null,

        @field:SerializedName("to")
        var to: String? = null,
) : Serializable {
    companion object {
        fun getDefaultTimeSlotObject(): TimeSlot {
            val timeSlot = TimeSlot();
            timeSlot.from = "9:30 AM"
            timeSlot.to = "7:00 PM"
            return timeSlot;
        }
    }
}

