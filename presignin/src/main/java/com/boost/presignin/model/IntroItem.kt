package com.boost.presignin.model

import android.content.Context
import androidx.annotation.DrawableRes
import com.boost.presignin.R
import java.io.Serializable

class IntroItem(
  val title: String? = null,
  val subTitle: String? = null,
  @DrawableRes val imageResource: Int? = null
) : Serializable {

  fun getData(c: Context): List<IntroItem> {
    return listOf(
      IntroItem(
        c.getString(R.string.intro_title_0),
        c.getString(R.string.intro_sub_title_0),
        R.drawable.psn_intro_asset_1
      ),
      IntroItem(
        c.getString(R.string.intro_title_1),
        c.getString(R.string.intro_sub_title_1),
        R.drawable.psn_intro_asset_2
      ),
      IntroItem(
        c.getString(R.string.intro_title_2),
        c.getString(R.string.intro_sub_title_2),
        R.drawable.psn_intro_asset_3
      ),
      IntroItem(
        c.getString(R.string.intro_title_3),
        c.getString(R.string.intro_sub_title_3),
        R.drawable.psn_intro_asset_4
      ),
      IntroItem(
        c.getString(R.string.intro_title_4),
        c.getString(R.string.intro_sub_title_4),
        R.drawable.psn_intro_asset_5
      ),
      IntroItem(
        c.getString(R.string.intro_title_5),
        c.getString(R.string.intro_sub_title_5),
        R.drawable.psn_intro_asset_6
      ),
      IntroItem(
        c.getString(R.string.intro_title_6),
        c.getString(R.string.intro_sub_title_6),
        R.drawable.psn_intro_asset_7
      ),
      IntroItem(
        c.getString(R.string.intro_title_7),
        c.getString(R.string.intro_sub_title_7),
        R.drawable.psn_intro_asset_8
      ),
    )
  }
}