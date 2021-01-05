package com.appservice.ui.staffs.ui.home.model

import com.appservice.ui.staffs.ui.details.timing.model.StaffTimingModel

data class StaffModel(val staffMemberName: String, val description: String, val specialization: String, val experience: String,
                      val isAvailable: Boolean, val serviceProvided: List<String>, val services: List<StaffTimingModel>, val scheduledBreak: List<Leaves>)

data class Leaves(val startingFromDate: String, val startTime: String, val tillDate: String, val tillTime: String, val additionalBreakInfo: String)
