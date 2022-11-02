package com.boost.marketplace.infra.utils

import com.boost.marketplace.ui.popup.ImagePreviewPopUpFragement

class Constants {
  companion object {
    //        const val BASE_URL = "https://5df0c0789df6fb00142bd1c8.mockapi.io/"
//        const val BASE_URL = "http://www.mocky.io/v2/"
    const val BASE_URL = "https://api.withfloats.com/"
    const val USER_PREFERENCES = "SHARED_PREFERENCES"
    const val ALERT_YES = 1
    const val ALERT_NO = 2
    const val START_ZERO_VALUE = "0"
    const val DATABASE_NAME = "updates_db"
    var CART_VALUE = 0
    var COMPARE_BACK_VALUE = 0
    var COMPARE_CART_COUNT = 0

    //razorpay credentials
    val RAZORPAY_KEY: String = "rzp_live_NsMLSX2HTaiEC9"
    val RAZORPAY_SECREAT: String = "Gfq8pQyFbu5BOoY5SeT6UuQk"
//        val RAZORPAY_KEY: String = "rzp_test_OlLpIGwhA7bATX"
//        val RAZORPAY_SECREAT: String = "wMa4K0UW4dPXD4ZJrkVg64mX"


    // fragments
    var IMAGE_PREVIEW_POPUP_FRAGMENT: String = ImagePreviewPopUpFragement::class.java.getName()

  }
}