package com.boost.presignin.model

import androidx.annotation.DrawableRes
import java.io.Serializable

class IntroItem(
        val title:String,
        val subTitle:String,
        @DrawableRes val imageResource:Int
):Serializable