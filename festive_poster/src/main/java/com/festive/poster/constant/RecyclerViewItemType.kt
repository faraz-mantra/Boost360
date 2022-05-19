package com.festive.poster.constant

import androidx.annotation.LayoutRes
import com.festive.poster.R

enum class RecyclerViewItemType {
  POSTER_PACK,
  POSTER,
  POSTER_PACK_PURCHASED,
  POSTER_SHARE,
  TODAYS_PICK_TEMPLATE_VIEW,
  TEMPLATE_VIEW_FOR_VP,
  TEMPLATE_VIEW_FOR_RV,
  TEMPLATE_VIEW_FOR_FAV,
  SOCIAL_CONN,
  BROWSE_TAB_TEMPLATE_CAT,
  BROWSE_ALL_TEMPLATE_CAT,
  SOCIAL_PLATFORM_POST_OPTIONS_LIST,
  TWITTER_PREVIEW,
  INSTAGRAM_PREVIEW,
  FB_PREVIEW,
  WEBSITE_PREVIEW,
  EMAIL_PREVIEW,
  GMB_PREVIEW,
  FB_PREVIEW_NO_IMAGE,
  WEBSITE_PREVIEW_NO_IMAGE,
  EMAIL_PREVIEW_NO_IMAGE,
  GMB_PREVIEW_NO_IMAGE,
  TWITTER_PREVIEW_NO_IMAGE,
  VIEW_MORE_POSTER,
  FAV_CAT;


  @LayoutRes
  fun getLayout(): Int {
    return when (this) {
      POSTER_PACK -> R.layout.list_item_poster_pack
      POSTER -> R.layout.list_item_poster
      POSTER_PACK_PURCHASED -> R.layout.list_item_purchased_poster_pack
      POSTER_SHARE -> R.layout.list_item_poster_share
      TODAYS_PICK_TEMPLATE_VIEW -> R.layout.list_item_todays_pick_template
      TEMPLATE_VIEW_FOR_VP -> R.layout.list_item_template_for_vp
      TEMPLATE_VIEW_FOR_RV -> R.layout.list_item_template_for_rv
      TEMPLATE_VIEW_FOR_FAV -> R.layout.list_item_template_for_fav
      SOCIAL_CONN -> R.layout.list_item_social_conn
      BROWSE_TAB_TEMPLATE_CAT->R.layout.list_item_browse_tab_template_cat
      BROWSE_ALL_TEMPLATE_CAT->R.layout.list_item_browse_all_cat
      SOCIAL_PLATFORM_POST_OPTIONS_LIST -> R.layout.item_social_platform_promo_adap
      TWITTER_PREVIEW -> R.layout.social_preview_twitter
      INSTAGRAM_PREVIEW->R.layout.social_preview_instagram
      FB_PREVIEW->R.layout.social_preview_fb
      WEBSITE_PREVIEW->R.layout.social_preview_website
      GMB_PREVIEW->R.layout.social_preview_gmb
      EMAIL_PREVIEW->R.layout.social_preview_email
      FB_PREVIEW_NO_IMAGE->R.layout.social_preview_fb_no_image
      WEBSITE_PREVIEW_NO_IMAGE->R.layout.social_preview_website_no_image
      GMB_PREVIEW_NO_IMAGE->R.layout.social_preview_gmb_no_image
      EMAIL_PREVIEW_NO_IMAGE->R.layout.social_preview_email_no_image
      TWITTER_PREVIEW_NO_IMAGE -> R.layout.social_preview_twitter_no_image

      VIEW_MORE_POSTER->R.layout.layout_view_more_template
      FAV_CAT->R.layout.list_item_fav_cat

    }
  }
}
