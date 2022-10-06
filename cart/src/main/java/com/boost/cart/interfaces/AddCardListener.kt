package com.boost.cart.interfaces

import org.json.JSONObject


interface AddCardListener {

  fun cardSelected(data: JSONObject)
}