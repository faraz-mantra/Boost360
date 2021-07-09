package com.dashboard.model.live.welcomeData

import com.dashboard.R
import com.framework.utils.PreferencesUtils
import com.framework.utils.getData
import com.framework.utils.saveData
import java.io.Serializable

class WelcomeData(
    var title: String? = null,
    var desc: String? = null,
    var btnTitle: String? = null,
    var welcomeType: String? = null,
) : Serializable {

  enum class WelcomeType(var icon: Int) {

    WEBSITE_CONTENT(R.drawable.ic_website_content_d), MANAGE_INTERACTION(R.drawable.ic_customer_interaction), ADD_ON_MARKETPLACE(R.drawable.ic_marketplace_d);

    companion object {
      fun fromName(name: String?): WelcomeType? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
  }

}

fun getIsShowWelcome(type: String): Boolean {
  return PreferencesUtils.instance.getData(type, false)
}

fun saveWelcomeData(type: String, isShow: Boolean = false) {
  PreferencesUtils.instance.saveData(type, isShow)
}


