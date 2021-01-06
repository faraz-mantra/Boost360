package com.appservice.ui.staffs.ui.home.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.ui.staffs.ui.details.timing.model.StaffTimingModel
import java.io.Serializable

data class StaffModel(val staffMemberName: String, val description: String, val specialization: String, val experience: String,
                      val isAvailable: Boolean, val serviceProvided: List<String>, val services: List<StaffTimingModel>, val scheduledBreak: List<LeavesModel>)

data class LeavesModel(var startingFromDate: String, var startTime: String, var tillDate: String, var tillTime: String, var additionalBreakInfo: String)
data class ServiceModel(var serviceName: String? = null, var isChecked: Boolean? = null, var recyclerViewItem: Int = RecyclerViewItemType.SERVICE_ITEM_VIEW.getLayout()) : AppBaseRecyclerViewItem, Serializable {

    override fun getViewType(): Int {
        return recyclerViewItem
    }

    fun serviceData(): ArrayList<ServiceModel> {
        val list = ArrayList<ServiceModel>()
        list.add(ServiceModel("Hair Removal and Waxing", false))
        list.add(ServiceModel("Hair Color for all ages", false))
        list.add(ServiceModel("Facial Makover", false))
        list.add(ServiceModel("Bridal Makover", false))
        list.add(ServiceModel("Anti-aging therapy", false))
        list.add(ServiceModel("Anti-pimple", false))
        list.add(ServiceModel("Skin Toning", false))
        return list
    }
}


