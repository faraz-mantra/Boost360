package com.boost.upgrades.data.api_model.customerId.get

data class TaxDetails(
        val TanNumber: String,
        val Tax: Int,
        val GSTIN: String,
        val TDS: Int
)