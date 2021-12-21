package com.boost.presignin.constant


enum class FragmentType {
  ENTER_PHONE_FRAGMENT,
  INTRO_SLIDE_SHOW_FRAGMENT,
  SET_UP_MY_WEBSITE_FRAGMENT,
  VERIFY_PHONE_FRAGMENT,
  WELCOME_FRAGMENT,
  LOADING_ANIMATION_DASHBOARD_FRAGMENT;

  companion object{
    fun fromValue(name: String): FragmentType? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
  }
}