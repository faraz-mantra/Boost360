package com.boost.upgrades.interfaces

import org.json.JSONObject


interface EmailPopupListener {

  fun emailSelected(data: JSONObject)
}