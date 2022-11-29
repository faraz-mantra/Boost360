package com.boost.payment.interfaces

import com.boost.dbcenterapi.data.api_model.stateCode.Data

interface StateListener {

  fun stateSelected(data: Data)
}