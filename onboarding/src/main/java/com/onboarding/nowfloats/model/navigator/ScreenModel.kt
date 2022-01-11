package com.onboarding.nowfloats.model.navigator

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.ui.AppFragmentContainerActivity
import com.onboarding.nowfloats.ui.category.CategorySelectorActivity
import com.onboarding.nowfloats.ui.channel.ChannelPickerActivity
import com.onboarding.nowfloats.ui.setFragmentType

data class ScreenModel(
  var title: String? = null,
  var type: String? = Screen.CATEGORY_SELECT.name,
) : Parcelable {

  constructor(parcel: Parcel) : this(
    parcel.readString(),
    parcel.readString() ?: Screen.CATEGORY_SELECT.name
  )

  constructor(screen: Screen? = null, title: String? = null) : this(title, screen?.name)

  enum class Screen {
    CATEGORY_SELECT,
    CHANNEL_SELECT,
    BUSINESS_INFO,
    BUSINESS_SUBDOMAIN,
    BUSINESS_GOOGLE_PAGE,
    BUSINESS_FACEBOOK_PAGE,
    BUSINESS_INSTAGRAM,
    BUSINESS_FACEBOOK_SHOP,
    BUSINESS_TWITTER,
    BUSINESS_WHATSAPP,
    REGISTERING,
    REGISTRATION_COMPLETE
  }

  fun getIntent(context: Context): Intent? {
    return when (Screen.values().firstOrNull { it.name == type }) {
      Screen.CATEGORY_SELECT -> Intent(context, CategorySelectorActivity::class.java)
      Screen.CHANNEL_SELECT -> Intent(context, ChannelPickerActivity::class.java)
      Screen.BUSINESS_INFO -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS)
      Screen.BUSINESS_SUBDOMAIN -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_WEBSITE)
      Screen.BUSINESS_GOOGLE_PAGE -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_GOOGLE_PAGE)
      Screen.BUSINESS_FACEBOOK_PAGE -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE)
      Screen.BUSINESS_INSTAGRAM -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_INSTAGRAM)
      Screen.BUSINESS_FACEBOOK_SHOP -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP)
      Screen.BUSINESS_TWITTER -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS)
      Screen.BUSINESS_WHATSAPP -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_WHATSAPP)
      Screen.REGISTERING -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_BUSINESS_API_CALL)
      Screen.REGISTRATION_COMPLETE -> Intent(
        context,
        AppFragmentContainerActivity::class.java
      ).setFragmentType(FragmentType.REGISTRATION_COMPLETE)
      else -> null
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(title)
    parcel.writeString(type)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ScreenModel> {
    override fun createFromParcel(parcel: Parcel): ScreenModel {
      return ScreenModel(parcel)
    }

    override fun newArray(size: Int): Array<ScreenModel?> {
      return arrayOfNulls(size)
    }
  }
}