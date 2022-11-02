package com.boost.cart.interfaces

import org.json.JSONObject


interface EmailPopupListener {

  fun emailSelected(data: JSONObject)
}