package com.appservice.ui.catalog

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ServiceModel(
        @field:SerializedName("day", alternate = ["Day"])
        var day: String? = null,
        var isViewEnabled: Boolean? = true,
        @field:SerializedName("timing", alternate = ["Timing"])
        var timeSlots: TimeSlot ?= null,
        var isDataAppliedOnMyDay: Boolean? = false,
        var isTurnedOn: Boolean? = false,
        var isAppliedOnAllDays: Boolean? = false,
        var isAppliedOnAllDaysViewVisible: Boolean? = false,
        var toTiming: String? = null,
        var fromTiming: String? = null,
        var recyclerViewItem: Int = RecyclerViewItemType.SERVICE_SESSION_ITEM_VIEW.getLayout(),
) : AppBaseRecyclerViewItem, Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }

    fun getDefaultModel(day: String?, isAppliedOnAllDays: Boolean, isAppliedOnAllDaysVisible: Boolean): ServiceModel {
        val m = ServiceModel()
        m.day = day
        m.isTurnedOn = false
        m.isAppliedOnAllDays = isAppliedOnAllDays
        m.isAppliedOnAllDaysViewVisible = isAppliedOnAllDaysVisible
        return m
    }

    private fun isDayTurnedOn() {
        this.isTurnedOn = true
        timeSlots = TimeSlot.getDefaultTimeSlotObject()
    }

   /* fun applyOnAllDays(startTime: String, endTime: String) {
        getDefaultTimings().forEach {
            it.fromTiming = startTime
            it.toTiming = endTime
        }
    }*/

    fun deleteSession(sessionIndex: Int) {
        //timeSlots.removeAt(sessionIndex)
    }

    fun changeDayTurned(dayTurned: Boolean) {
        when {
            dayTurned -> isDayTurnedOn()
            else -> isDayTurnedOff()
        }
    }

    private fun isDayTurnedOff() {
        this.isTurnedOn = false
        timeSlots = null
    }

    fun addSession() {
        //timeSlots.add(TimeSlot.getDefaultTimeSlotObject())
    }

    fun removeSession(index: Int){
        //timeSlots.removeAt(index);
    }

    fun removeApplyOnAllDays(data: ServiceModel) {
       // getDefaultTimings().forEach { if (it != data) it.isTurnedOn = false }
    }

    companion object {
        fun getDefaultTimings(): ArrayList<ServiceModel> {
            val list = ArrayList<ServiceModel>()
            val monday = ServiceModel(day = "Monday", isTurnedOn = false, isAppliedOnAllDays = false);
            monday.isAppliedOnAllDaysViewVisible = true;
            list.add(monday);
            list.add(ServiceModel(day = "Tuesday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ServiceModel(day = "Wednesday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ServiceModel(day = "Thursday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ServiceModel(day = "Friday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ServiceModel(day = "Saturday", isTurnedOn = false, isAppliedOnAllDays = false))
            list.add(ServiceModel(day = "Sunday", isTurnedOn = false, isAppliedOnAllDays = false))
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
            val timeSlot = TimeSlot();
            timeSlot.from = "09:30AM"
            timeSlot.to = "07:00PM"
            return timeSlot;
        }

    }
}

