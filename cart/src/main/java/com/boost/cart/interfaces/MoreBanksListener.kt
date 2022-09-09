package com.boost.cart.interfaces

import org.json.JSONObject


interface MoreBanksListener {

  fun moreBankSelected(data: JSONObject)
}