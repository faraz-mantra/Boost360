package com.boost.marketplace.interfaces

import com.boost.dbcenterapi.data.api_model.CustomDomain.Domain

interface DomainListener {
    fun onSelectedDomain(itemList: Domain?)
}