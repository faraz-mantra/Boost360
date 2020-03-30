package com.onboarding.nowfloats.managers

import com.framework.utils.PreferencesKey.REQUEST_FLOAT
import com.framework.utils.PreferencesUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.navigator.ScreenModel
import java.util.*

object NavigatorManager {

  val stack = Stack<ScreenModel>()

  fun push(screen: ScreenModel) {
    stack.push(screen)
  }

  fun pushToStackAndSaveRequest(screen: ScreenModel, request: RequestFloatsModel?){
    push(screen)

    request?.let { PreferencesUtils.instance.saveData(REQUEST_FLOAT, Gson().toJson(it)) }
  }

  fun getRequest(): RequestFloatsModel? {
    return try {
      Gson().fromJson(PreferencesUtils.instance.getData(REQUEST_FLOAT) ?: "", RequestFloatsModel::class.java)
    }
    catch (e: JsonSyntaxException){
      e.printStackTrace()
      null
    }
  }

  fun peek(): ScreenModel? {
    return if (stack.isEmpty()) null
    else stack.peek()
  }

  fun popStack() {
    if (stack.isNotEmpty()) {
      stack.pop()
    }
  }
}