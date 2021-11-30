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
    @RawRes val lottieRawResource:Int? = null,
    val isLottieRepeat:Boolean? = false,
    @ColorRes val slideBackgroundColor:Int? = null
) : Serializable, AppBaseRecyclerViewItem {

    fun getData(c: Context): ArrayList<IntroItemNew> {
        return arrayListOf(
            IntroItemNew(
                c.getString(R.string.new_into_1_title),
                R.drawable.tutorial_1_new,
                null,
                false,
                R.color.white
            ),
            IntroItemNew(
                c.getString(R.string.new_into_2_title),
                R.drawable.tutorial_new_2,
                R.raw.lottie_marketing_tools_intro_2,
                false,
                R.color.white_F4F8FA
            ),
            IntroItemNew(
                c.getString(R.string.new_into_3_title),
                R.drawable.tutorial_1_new,
                null,
                true,
                R.color.white
            ),
            IntroItemNew(
                c.getString(R.string.new_into_4_title),
                R.drawable.tutorial_3_new,
                R.raw.lottie_collect_payments_intro_4,
                true,
                R.color.white_F7F7F4
            ),
        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.INTRO_NEW_SLIDES.getLayout()
    }
}