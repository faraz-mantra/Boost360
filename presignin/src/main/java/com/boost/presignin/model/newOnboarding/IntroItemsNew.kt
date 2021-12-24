package com.boost.presignin.model.newOnboarding

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class IntroItemNew(
  val title: String? = null,
  @DrawableRes val imageResource: Int? = null,
  @RawRes val lottieRawResource: Int = R.raw.new_lottie_merchants_1,
  val isLottieRepeat: Boolean = false,
  val count: Int = 0,
  @ColorRes val slideBackgroundColor: Int = R.color.white_F4F8FA
) : Serializable, AppBaseRecyclerViewItem {

  fun getData(c: Context): ArrayList<IntroItemNew> {
    return arrayListOf(
      IntroItemNew(
        title = c.getString(R.string.new_into_1_title),
        imageResource = null,
        lottieRawResource = R.raw.new_lottie_merchants_1,
        isLottieRepeat = false,
        slideBackgroundColor = R.color.white_F5F8FD
      ),
      IntroItemNew(
        title = c.getString(R.string.new_into_2_title),
        imageResource = null,
        lottieRawResource = R.raw.new_lottie_marketing_2,
        isLottieRepeat = false,
        slideBackgroundColor = R.color.white_F5F8FD,
        count = 1
      ),
      IntroItemNew(
        title = c.getString(R.string.new_into_3_title),
        imageResource = null,
        lottieRawResource = R.raw.new_lottie_selling_products_3,
        isLottieRepeat = true,
        slideBackgroundColor = R.color.white_F5F8FD
      ),
      IntroItemNew(
        title = c.getString(R.string.new_into_4_title),
        imageResource = null,
        lottieRawResource = R.raw.lottie_intro_4_collect,
        isLottieRepeat = true,
        slideBackgroundColor = R.color.white_F5F8FD
      ),
    )
  }

  override fun getViewType(): Int {
    return RecyclerViewItemType.INTRO_NEW_SLIDES.getLayout()
  }
}