package com.boost.payment.interfaces

import com.boost.payment.data.api_model.stateCode.Data

interface StateListener {

  fun stateSelected(data: Data)
}