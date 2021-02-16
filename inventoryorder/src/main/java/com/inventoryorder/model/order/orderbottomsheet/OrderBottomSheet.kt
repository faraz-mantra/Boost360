package com.inventoryorder.model.order.orderbottomsheet

data class OrderBottomSheet(
        var decription: String ?= null,
        var items: List<BottomSheetOptionsItem>  ?= null,
        var title: String ?= null
)