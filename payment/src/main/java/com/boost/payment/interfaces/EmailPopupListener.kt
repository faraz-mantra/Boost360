package com.boost.payment.interfaces

import org.json.JSONObject


interface EmailPopupListener {

  fun emailSelected(data: JSONObject)
}