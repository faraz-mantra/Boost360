package com.onboarding.nowfloats.managers

import android.app.Activity
import android.os.Bundle
import com.framework.utils.PreferencesKey.NAVIGATION_STACK
import com.framework.utils.PreferencesKey.REQUEST_FLOAT
import com.framework.utils.PreferencesUtils
import com.framework.utils.convertStringToList
import com.framework.utils.getData
import com.framework.utils.saveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.model.navigator.ScreenModel

object NavigatorManager {

  var stack = ArrayList<ScreenModel>()

  fun initialize() {
    try {
      val stackJson = PreferencesUtils.instance.getData(NAVIGATION_STACK,"") ?: return
      stack=  ArrayList(convertStringToList(stackJson)?:ArrayList())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun push(screen: ScreenModel) {
    if (stack.firstOrNull { it.type == screen.type } == null) {
      stack.add(screen)
    }
  }

  fun pushToStackAndSaveRequest(screen: ScreenModel, request: RequestFloatsModel?) {
    push(screen)
    request?.let {
      updateRequest(it)
      updateStackInPreferences()
    }
  }

  fun updateRequest(request: RequestFloatsModel?) {
    PreferencesUtils.instance.saveData(REQUEST_FLOAT, Gson().toJson(request))
  }

  fun clearRequest() {
    PreferencesUtils.instance.saveData(REQUEST_FLOAT, "")
  }

  fun getRequest(): RequestFloatsModel? {
    return try {
      Gson().fromJson(PreferencesUtils.instance.getData(REQUEST_FLOAT,"") ?: "", RequestFloatsModel::class.java)
    } catch (e: JsonSyntaxException) {
      e.printStackTrace()
      null
    }
  }

  fun peek(): ScreenModel? {
    return if (stack.isEmpty()) null
    else stack.lastOrNull()
  }

  fun popCurrentScreen(screen: ScreenModel.Screen) {
    removeStoredRequestEntries(screen)

    if (stack.isEmpty()) return
    val model = stack.firstOrNull { it.type == screen.name } ?: return
    stack.remove(model)
    updateStackInPreferences()
  }

  private fun removeStoredRequestEntries(screen: ScreenModel.Screen) {
    val request = getRequest() ?: return
    when (screen) {
      ScreenModel.Screen.CATEGORY_SELECT -> request.categoryDataModel = null
      ScreenModel.Screen.CHANNEL_SELECT -> request.channels = null
      ScreenModel.Screen.BUSINESS_INFO -> request.contactInfo?.clearAllDomain()
      ScreenModel.Screen.BUSINESS_SUBDOMAIN -> request.contactInfo?.domainName = null
      ScreenModel.Screen.BUSINESS_GOOGLE_PAGE -> request.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.googlemybusiness }
      ScreenModel.Screen.BUSINESS_FACEBOOK_PAGE -> request.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.facebookpage }
      ScreenModel.Screen.BUSINESS_FACEBOOK_SHOP -> request.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.facebookshop }
      ScreenModel.Screen.BUSINESS_TWITTER -> request.channelAccessTokens?.removeAll { it.getType() == ChannelAccessToken.AccessTokenType.twitter }
      ScreenModel.Screen.BUSINESS_WHATSAPP -> request.channelActionDatas?.clear()
      ScreenModel.Screen.REGISTRATION_COMPLETE -> {
      }
    }
    PreferencesUtils.instance.saveData(REQUEST_FLOAT, Gson().toJson(request))
  }

  private fun updateStackInPreferences() {
    PreferencesUtils.instance.saveData(NAVIGATION_STACK, Gson().toJson(stack))
  }

  fun clearStackAndFormData() {
    stack.clear()
    PreferencesUtils.instance.saveData(NAVIGATION_STACK, Gson().toJson(stack))
    PreferencesUtils.instance.saveData(REQUEST_FLOAT, "")
  }

  fun peekAndPop(): ScreenModel? {
    val screen = peek()
    popStack()
    return screen
  }

  fun popStack() {
    if (stack.isNotEmpty()) {
      stack[stack.lastIndex]
      stack.removeAt(stack.lastIndex)
    }
  }

  fun startActivities(activity: Activity) {
    val bundle = Bundle()
//    bundle.putParcelable(IntentConstant.REQUEST_FLOATS_INTENT.name, getRequest())

    if (stack.isEmpty()) {
      activity.startActivity(ScreenModel(ScreenModel.Screen.CATEGORY_SELECT).getIntent(activity))
      return
    } else if (stack.lastOrNull { (it.type == ScreenModel.Screen.REGISTRATION_COMPLETE.name) } != null) {
      val screen = stack[stack.size - 1]
      bundle.putString(IntentConstant.TOOLBAR_TITLE.name, screen.title)
      val intent = screen.getIntent(activity)
      intent?.putExtras(bundle)
      activity.startActivity(intent)
      return
    }

    for (index in stack.indices) {
      val screen = stack[index]
      bundle.putString(IntentConstant.TOOLBAR_TITLE.name, screen.title)
      val intent = screen.getIntent(activity)
      intent?.putExtras(bundle)
      activity.startActivity(intent)
      if (index < stack.size - 1) {
        activity.overridePendingTransition(0, 0)
      }
    }
  }
}