package com.inventoryorder.model.spaAppointment.bookingslot.response

import java.io.Serializable

data class Result(
    var Day: Int?= null,
    var Month: Int?= null,
    var selectedPosition : Int = -1,
    var Staff: ArrayList<Staff>?= null,
    var Week: Int?= null
): Serializable