package com.onboarding.nowfloats.model.navigator

import android.content.Context
import android.content.Intent
import com.onboarding.nowfloats.ui.AppFragmentContainerActivity
import com.onboarding.nowfloats.ui.category.CategorySelectorActivity
import com.onboarding.nowfloats.ui.channel.ChannelPickerActivity

data class ScreenModel(
        var title: String? = null,
        var type: String = Screen.CATEGORY_SELECT.name
) {

  constructor(screen: Screen,
              title: String? = null) : this(title, screen.name)

  enum class Screen {
    CATEGORY_SELECT,
    CHANNEL_SELECT,
    BUSINESS_INFO,
    BUSINESS_SUBDOMAIN,
    BUSINESS_FACEBOOK_PAGE,
    BUSINESS_FACEBOOK_SHOP,
    BUSINESS_TWITTER,
    BUSINESS_WHATSAPP,
    REGISTRATION_COMPLETE
  }

  fun getIntent(context: Context): Intent? {
    return when (Screen.values().firstOrNull { it.name == type }) {
      Screen.CATEGORY_SELECT -> Intent(context, CategorySelectorActivity::class.java)
      Screen.CHANNEL_SELECT -> Intent(context, ChannelPickerActivity::class.java)
      Screen.BUSINESS_INFO -> Intent(context, AppFragmentContainerActivity::class.java)
      Screen.BUSINESS_SUBDOMAIN -> Intent(context, AppFragmentContainerActivity::class.java)
      Screen.BUSINESS_FACEBOOK_PAGE -> Intent(context, AppFragmentContainerActivity::class.java)
      Screen.BUSINESS_FACEBOOK_SHOP -> Intent(context, AppFragmentContainerActivity::class.java)
      Screen.BUSINESS_TWITTER -> Intent(context, AppFragmentContainerActivity::class.java)
      Screen.BUSINESS_WHATSAPP -> Intent(context, AppFragmentContainerActivity::class.java)
      Screen.REGISTRATION_COMPLETE -> Intent(context, AppFragmentContainerActivity::class.java)
      null -> null
    }
  }
}