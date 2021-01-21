package com.appservice.staffs.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class StaffModel(val staffMemberName: String, val description: String, val specialization: String, val experience: String,
                      val isAvailable: Boolean, val serviceProvided: List<String>, val services: List<StaffTimingModel>, val scheduledBreak: List<LeavesModel>)

data class ServiceModel(var serviceName: String? = null, var isChecked: Boolean? = null, var recyclerViewItem: Int = RecyclerViewItemType.SERVICE_ITEM_VIEW.getLayout()) : AppBaseRecyclerViewItem, Serializable {

    override fun getViewType(): Int {
        return recyclerViewItem
    }

    fun serviceData(): ArrayList<ServiceModel> {
        val list = ArrayList<ServiceModel>()
        list.add(ServiceModel("Hair Removal and Waxing", false))
        list.add(ServiceModel("Hair Color for all ages", false))
        list.add(ServiceModel("Facial Makeover", false))
        list.add(ServiceModel("Bridal Makeover", false))
        list.add(ServiceModel("Anti-aging therapy", false))
        list.add(ServiceModel("Anti-pimple", false))
        list.add(ServiceModel("Skin Toning", false))
        return list
    }
}

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


