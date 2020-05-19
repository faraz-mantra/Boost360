package com.boost.upgrades.data.api_model.customerId.get

import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest

data class GetCustomerIDResponse(
        val Error: Error,
        val Result: CreateCustomerInfoRequest,
        val StatusCode: Int
)