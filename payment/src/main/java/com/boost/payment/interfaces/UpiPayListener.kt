package com.boost.payment.interfaces

import org.json.JSONObject


interface UpiPayListener {

  fun upiSelected(data: JSONObject)
}