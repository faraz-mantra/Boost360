package com.boost.payment.interfaces

import org.json.JSONObject


interface MoreBanksListener {

  fun moreBankSelected(data: JSONObject)
}