package com.boost.upgrades.interfaces

import org.json.JSONObject


interface MoreBanksListener {

    fun moreBankSelected(data: JSONObject)
}