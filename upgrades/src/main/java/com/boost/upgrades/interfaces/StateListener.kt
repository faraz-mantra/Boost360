package com.boost.upgrades.interfaces

import com.boost.upgrades.data.api_model.customerId.StateModel

interface StateListener {

    fun stateSelected(data: StateModel)
}