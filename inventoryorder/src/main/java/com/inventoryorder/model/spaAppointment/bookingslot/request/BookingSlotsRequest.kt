package com.inventoryorder.model.spaAppointment.bookingslot.request

import java.io.Serializable

data class BookingSlotsRequest(
  val BatchType: Int? = 0,
  val DateRange: DateRange? = null,
  val ServiceId: String? = null,
  val filterBy: FilterBy? = FilterBy()
) : Serializable {

  enum class AppointmentBatchType(var value: Int) {
    BATCH_0(0), BATCH_1(1), BATCH_2(2)
  }
}

data class FilterBy(
  val type: Int? = 0
) : Serializable {
  enum class ScheduleFilterObject(var value: Int) {
    SCHEDULE_0(0), SCHEDULE_1(1), SCHEDULE_2(2)
  }
}
