package com.inventoryorder.model.spaAppointment

import java.io.Serializable

data class Result(
    var Data: List<ServiceItem> ?= null,
    var Paging: Paging ?= null
): Serializable