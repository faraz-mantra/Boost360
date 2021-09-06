package com.dashboard.controller.ui.more.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MoreSettingsResponse(

  @field:SerializedName("useful_links")
  var usefulLinks: List<UsefulLinksItem?>? = null,

  @field:SerializedName("about_app_section")
  var aboutAppSection: List<AboutAppSectionItem?>? = null
) : BaseResponse(), Serializable

data class AboutAppSectionItem(
  @field:SerializedName("actionBtn")
  var actionBtn: ActionBtn? = null,
  @field:SerializedName("subtitle")
  var subtitle: String? = null,

  @field:SerializedName("icon")
  var icon: String? = null,

  @field:SerializedName("title")
  var title: String? = null,
  @field:SerializedName("view_type_2")
  var view_type_2: String? = null,
) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.RECYCLER_ABOUT_APP.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  enum class IconType(var icon: Int) {
    whats_new_version(R.drawable.whats_new_version),
    frequently_asked_question(R.drawable.frequently_asked_question),
    like_us_on_facebook(R.drawable.like_us_on_facebook),
    follow_us_on_twitter(R.drawable.follow_us_on_twitter),
    rate_us_on_google_play(R.drawable.rate_us_on_google_play),
    terms_of_usages(R.drawable.terms_of_usages),
    privacy_policy(R.drawable.privacy_policy),
    help_us_make_boost_better(R.drawable.help_us_make_boost_better);

    companion object {
      fun fromName(name: String?): IconType? =
        values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
  }

}

data class UsefulLinksItem(

  @field:SerializedName("isActionButtonEnabled")
  var isActionButtonEnabled: Boolean? = false,

  @field:SerializedName("actionBtn")
  var actionBtn: ActionBtn? = null,

  @field:SerializedName("subtitle")
  var subtitle: String? = null,

  @field:SerializedName("icon")
  var icon: String? = null,

  @field:SerializedName("title")
  var title: String? = null,


  ) : Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.RECYCLER_USEFUL_LINKS.getLayout()
  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  enum class IconType(var icon: Int) {
    ria_digital_assistant(R.drawable.ria_digital_assistant),
    business_kyc(R.drawable.business_kyc),
    boost_keyboard(R.drawable.boost_keyboard),
    refer_and_earn(R.drawable.refer_and_earn),
    my_bank_acccount(R.drawable.my_bank_acccount),
    boost_academy(R.drawable.boost_academy),
    training_and_certification(R.drawable.training_and_certification),
    boost_extension(R.drawable.boost_extension);

    companion object {
      fun fromName(name: String?): IconType? =
        values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
  }
}

data class ActionBtn(

  @field:SerializedName("color")
  var color: String? = null,

  @field:SerializedName("title")
  var title: String? = null,

  @field:SerializedName("text_color")
  var textColor: String? = null
)
