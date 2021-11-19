package com.boost.upgrades.interfaces

import com.boost.upgrades.data.api_model.customerId.StateModel
import com.boost.upgrades.data.api_model.stateCode.Data

interface StateListener {

  fun stateSelected(data: Data)
}