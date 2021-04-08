package com.inventoryorder.model.spaAppointment.bookingslot.response

import java.io.Serializable

data class AppointmentSlot(
    var Date: Date ?= null,
    var Day: String?= null,
    var IsAvailable: Boolean?= null,
    var Slots: ArrayList<Slots>?= null,
    var isSelected : Boolean = false
) : Serializable