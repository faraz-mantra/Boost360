package com.boost.cart.interfaces

import org.json.JSONObject


interface UpiPayListener {

  fun upiSelected(data: JSONObject)
}