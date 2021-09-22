package com.inventoryorder.model.spaAppointment.bookingslot.request

import java.io.Serializable

data class AppointmentRequestModel(
  var _id: String? = null,
  var duration: String? = null,
  var endTime: String? = null,
  var scheduledDateTime: String? = null,
  var staffId: String? = null,
  var staffName: String? = null,
  var startTime: String? = null
) : Serializable