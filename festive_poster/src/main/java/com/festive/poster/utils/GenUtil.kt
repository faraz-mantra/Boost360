package com.festive.poster.utils

import com.festive.poster.constant.Constants
import com.framework.BaseApplication
import com.framework.pref.UserSessionManager


fun isPromoWidgetActive(): Boolean{
    val session = UserSessionManager(BaseApplication.instance)
    return session.getStoreWidgets()?.contains(Constants.PROMO_WIDGET_KEY)==true
}