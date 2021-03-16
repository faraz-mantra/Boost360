package com.inventoryorder.model.order

import java.io.Serializable

data class KeySpecification(
    val key: String ?= null,
    val value: String ?= null
) : Serializable