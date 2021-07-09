package com.boost.upgrades.interfaces

import org.json.JSONObject


interface UpiPayListener {

    fun upiSelected(data: JSONObject)
}