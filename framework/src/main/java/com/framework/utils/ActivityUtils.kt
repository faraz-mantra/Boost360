package com.framework.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.framework.R
import com.framework.analytics.NFWebEngageController
import com.framework.webengageconstant.Post_Promotional_Update_Click

fun AppCompatActivity.startPromotionUpdates() {
    try {
        val posterIntent = if (PreferencesUtils.instance.getData(
                PreferencesKey.UPDATE_STUDIO_FIRST_TIME.name,
                true)){
            Intent(this, Class.forName("com.festive.poster.ui.promoUpdates.intro.UpdateStudioIntroActivity"))

        }else{
            Intent(this, Class.forName("com.festive.poster.ui.promoUpdates.PromoUpdatesActivity"))
        }
        startActivity(posterIntent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}