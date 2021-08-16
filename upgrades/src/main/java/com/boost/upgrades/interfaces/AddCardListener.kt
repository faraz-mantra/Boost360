package com.boost.upgrades.interfaces

import org.json.JSONObject


interface AddCardListener {

  fun cardSelected(data: JSONObject)
}