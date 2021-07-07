package com.inventoryorder.model.spaAppointment.bookingslot.request

data class BookingSlotsRequest(
  val BatchType: String,
  val DateRange: DateRange,
  val ServiceId: String
)