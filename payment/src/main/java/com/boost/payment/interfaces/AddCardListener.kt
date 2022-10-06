package com.boost.payment.interfaces

import org.json.JSONObject


interface AddCardListener {

  fun cardSelected(data: JSONObject)
}