package com.boost.cart.utils

import com.boost.cart.ui.autorenew.AutoRenewSubsFragment
import com.boost.cart.ui.compare.ComparePackageFragment
import com.boost.cart.ui.confirmation.AutoRenewOrderConfirmationFragment
import com.boost.cart.ui.details.DetailsFragment
import com.boost.cart.ui.features.ViewAllFeaturesFragment
import com.boost.cart.ui.freeaddons.FreeAddonsFragment
import com.boost.cart.ui.history.HistoryFragment
import com.boost.cart.ui.historydetails.HistoryDetailsFragment
import com.boost.cart.ui.home.CartFragment
import com.boost.cart.ui.myaddons.MyAddonsFragment
import com.boost.cart.ui.packages.PackageFragment
import com.boost.cart.ui.popup.CouponPopUpFragment
import com.boost.cart.ui.popup.GSTINPopUpFragment
import com.boost.cart.ui.popup.ImagePreviewPopUpFragement
import com.boost.cart.ui.popup.TANPopUpFragment
import com.boost.cart.ui.splash.SplashFragment
import com.boost.cart.ui.webview.WebViewFragment


class Constants {
    companion object {

        //razorpay credentials
        val RAZORPAY_KEY: String = "rzp_live_NsMLSX2HTaiEC9"
        val RAZORPAY_SECREAT: String = "Gfq8pQyFbu5BOoY5SeT6UuQk"
//        val RAZORPAY_KEY: String = "rzp_test_OlLpIGwhA7bATX"
//        val RAZORPAY_SECREAT: String = "wMa4K0UW4dPXD4ZJrkVg64mX"

        const val BASE_URL = "https://api.withfloats.com/"
        var SPLASH_FRAGMENT: String = SplashFragment::class.java.getName()
        var DETAILS_FRAGMENT: String = DetailsFragment::class.java.getName()
        var CART_FRAGMENT: String = CartFragment::class.java.getName()
        var FREEADDONS_FRAGMENT: String = FreeAddonsFragment::class.java.getName()
        var PACKAGE_FRAGMENT: String = PackageFragment::class.java.getName()
        var COUPON_POPUP_FRAGEMENT: String = CouponPopUpFragment::class.java.getName()
        var GSTIN_POPUP_FRAGEMENT: String = GSTINPopUpFragment::class.java.getName()
        var TAN_POPUP_FRAGEMENT: String = TANPopUpFragment::class.java.getName()
        var COMPARE_FRAGMENT: String = ComparePackageFragment::class.java.getName()
        var AUTO_RENEW_FRAGEMENT: String = AutoRenewSubsFragment::class.java.getName()
        var AUTO_RENEW_ORDER_CONFIRMATION_FRAGMENT: String =
            AutoRenewOrderConfirmationFragment::class.java.getName()
        var VIEW_ALL_FEATURE: String = ViewAllFeaturesFragment::class.java.getName()
        var MYADDONS_FRAGMENT: String = MyAddonsFragment::class.java.getName()
        var HISTORY_DETAILS_FRAGMENT: String = HistoryDetailsFragment::class.java.getName()
        var HISTORY_FRAGMENT: String = HistoryFragment::class.java.getName()
        var IMAGE_PREVIEW_POPUP_FRAGMENT: String = ImagePreviewPopUpFragement::class.java.getName()
        var WEB_VIEW_FRAGMENT: String = WebViewFragment::class.java.getName()
        var STATE_LIST_FRAGMENT1: String = com.boost.cart.ui.popup.StateListPopFragment::class.java.getName()

        const val ALERT_YES = 1
        const val ALERT_NO = 2

        var COMPARE_BACK_VALUE = 0
        var COMPARE_CART_COUNT = 0
        var CART_VALUE = 0
    }
}