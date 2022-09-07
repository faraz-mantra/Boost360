package com.framework.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.startPromotionUpdates() {
    try {
        val posterIntent = if (true/*PreferencesUtils.instance.getData(
                PreferencesKey.UPDATE_STUDIO_FIRST_TIME.name,
                true)*/){
            Intent(this, Class.forName("com.festive.poster.ui.promoUpdates.intro.UpdateStudioIntroActivity"))

        }else{
            Intent(this, Class.forName("com.festive.poster.ui.promoUpdates.PromoUpdatesActivity"))
        }
        startActivity(posterIntent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

fun AppCompatActivity.startBusinessLogo() {
    try {
        val webIntent = Intent(this, Class.forName("com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity"))
        startActivity(webIntent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}