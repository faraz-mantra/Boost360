package com.boost.upgrades.interfaces

import com.boost.dbcenterapi.data.api_model.customerId.StateModel
import com.boost.dbcenterapi.data.api_model.stateCode.Data

interface StateListener {

  fun stateSelected(data: Data)
}