package com.appservice.model.keyboard

import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val KEYBOARD_TABS_DATA = "KEYBOARD_TABS_DATA"

data class KeyboardData(
  @SerializedName("action_item")
  var actionItem: ArrayList<KeyboardActionItem>? = null,
  @SerializedName("types")
  var types: String? = null
) : Serializable

fun getKeyboardTabs(): ArrayList<KeyboardActionItem> {
  val resp = PreferencesUtils.instance.getData(KEYBOARD_TABS_DATA, "") ?: ""
  return ArrayList(convertStringToList(resp) ?: ArrayList())
}

fun saveKeyboardTabs(channelStatus: ArrayList<KeyboardActionItem>?) {
  PreferencesUtils.instance.saveData(KEYBOARD_TABS_DATA, convertListObjToString(channelStatus ?: ArrayList()) ?: "")
}