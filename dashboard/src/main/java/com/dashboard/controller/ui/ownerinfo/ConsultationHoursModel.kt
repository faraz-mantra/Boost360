package com.dashboard.controller.ui.ownerinfo

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ConsultationHoursModel(
        @field:SerializedName("day", alternate = ["Day"])
        var day: String? = null,
        var isViewEnabled: Boolean? = true,
        @field:SerializedName("timing", alternate = ["Timing"])
        var timeSlots: ArrayList<TimeSlot> = arrayListOf(),
        var isDataAppliedOnMyDay: Boolean? = false,
        var isTurnedOn: Boolean? = false, var isAppliedOnAllDays: Boolean? = false, var isAppliedOnAllDaysViewVisible: Boolean? = false, var toTiming: String? = null, var fromTiming: String? = null, var recyclerViewItem: Int = RecyclerViewItemType.CONSULTATION_VIEW.getLayout(),
) : AppBaseRecyclerViewItem, Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }

    fun getDefaultModel(day: String?, isAppliedOnAllDays: Boolean, isAppliedOnAllDaysVisible: Boolean): ConsultationHoursModel {
        val m = ConsultationHoursModel()
        m.day = day
        m.isTurnedOn = false
        m.isAppliedOnAllDays = isAppliedOnAllDays
        m.isAppliedOnAllDaysViewVisible = isAppliedOnAllDaysVisible
        return m
    }

    private fun isDayTurnedOn() {
        timeSlots = ArrayList()
        this.isTurnedOn = true
        timeSlots.add(TimeSlot.getDefaultTimeSlotObject())
    }

    fun applyOnAllDays(startTime: String, endTime: String) {
        getDefaultTimings().forEach {
            it.fromTiming = startTime
            it.toTiming = endTime
        }
    }

    fun deleteSession(sessionIndex: Int) {
        timeSlots.removeAt(sessionIndex)

    }

    fun changeDayTurned(dayTurned: Boolean) {
        when {
            dayTurned -> isDayTurnedOn()
            else -> isDayTurnedOff()
        }
    }

    private fun isDayTurnedOff() {
        this.isTurnedOn = false
        timeSlots.clear()

    }

    fun addSession() {
        timeSlots.add(TimeSlot.getDefaultTimeSlotObject())
    }

    fun removeSession(index: Int){
        timeSlots.removeAt(index)
    }

    fun removeApplyOnAllDays(data: ConsultationHoursModel) {
        getDefaultTimings().forEach { if (it != data) it.isTurnedOn = false }
    }

    companion object {
        fun getDefaultTimings(): ArrayList<ConsultationHoursModel> {
            val list = ArrayList<ConsultationHoursModel>()
            val monday = ConsultationHoursModel(day = "Monday", isTurnedOn = false, isAppliedOnAllDays = false)
            monday.isAppliedOnAllDaysViewVisible = true
            list.add(monday)
            list.add(ConsultationHoursModel(day = "Tuesday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ConsultationHoursModel(day = "Wednesday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ConsultationHoursModel(day = "Thursday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ConsultationHoursModel(day = "Friday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ConsultationHoursModel(day = "Saturday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ConsultationHoursModel(day = "Sunday", isTurnedOn = false, isAppliedOnAllDays = false))
            return list
        }
    }
}


data class TimeSlot(

        @field:SerializedName("from",alternate = ["From"])
        var from: String? = null,

        @field:SerializedName("to",alternate = ["To"])
        var to: String? = null,
) : Serializable{
    companion object {
       fun getDefaultTimeSlotObject(): TimeSlot {
            val timeSlot = TimeSlot()
           timeSlot.from = "09:30AM"
            timeSlot.to = "07:00PM"
            return timeSlot
       }

    }
}

