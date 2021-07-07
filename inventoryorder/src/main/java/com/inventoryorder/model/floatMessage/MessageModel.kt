package com.inventoryorder.model.floatMessage

import com.framework.base.BaseResponse
import com.framework.utils.*
import java.io.Serializable
import java.util.*

const val MESSAGE_FLOAT_DATA = "MESSAGE_FLOAT_DATA"

data class MessageModel(
  var floats: ArrayList<FloatsMessageModel>? = null,
  var moreFloatsAvailable: Boolean = false,
) : BaseResponse(), Serializable {

  fun getMessageFloatData(): MessageModel? {
    val resp = PreferencesUtils.instance.getData(MESSAGE_FLOAT_DATA, "") ?: ""
    return convertStringToObj(resp)
  }

  fun saveData() {
    PreferencesUtils.instance.saveData(MESSAGE_FLOAT_DATA, convertObjToString(this) ?: "")
  }

  fun getStoreBizFloatSize(): Int {
    return getMessageFloatData()?.floats?.size ?: 0
  }
}