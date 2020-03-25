package com.boost.upgrades.utils

import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.confirmation.OrderConfirmationFragment
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.*
import com.boost.upgrades.ui.razorpay.RazorPayWebView
import com.boost.upgrades.ui.removeaddons.RemoveAddonConfirmationFragment
import com.boost.upgrades.ui.removeaddons.RemoveAddonsFragment
import com.boost.upgrades.ui.thanks.Thanks2Fragment
import com.boost.upgrades.ui.thanks.ThanksFragment
import com.boost.upgrades.ui.webview.WebViewFragment

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
         var CART_VALUE= 0

        // fragments
        var HOME_FRAGMENT: String = HomeFragment::class.java.getName()
        var PACKAGE_FRAGMENT: String = PackageFragment::class.java.getName()
        var CART_FRAGMENT: String = CartFragment::class.java.getName()
        var VIEW_ALL_FEATURE: String = ViewAllFeaturesFragment::class.java.getName()
        var DETAILS_FRAGMENT: String = DetailsFragment::class.java.getName()
        var PAYMENT_FRAGMENT: String = PaymentFragment::class.java.getName()
        var THANKS_FRAGMENT: String = ThanksFragment::class.java.getName()
        var THANKS_2_FRAGMENT: String = Thanks2Fragment::class.java.getName()
        var MYADDONS_FRAGMENT: String = MyAddonsFragment::class.java.getName()
        var WEB_VIEW_FRAGMENT: String = WebViewFragment::class.java.getName()
        var REMOVE_ADDONS_FRAGMENT: String = RemoveAddonsFragment::class.java.getName()
        var REMOVE_ADDONS_CONFIRMATION_FRAGMENT: String = RemoveAddonConfirmationFragment::class.java.getName()
        var ADD_CARD_POPUP_FRAGMENT: String = AddCardPopUpFragement::class.java.getName()
        var NETBANKING_POPUP_FRAGMENT: String = NetBankingPopUpFragement::class.java.getName()
        var UPI_POPUP_FRAGMENT: String = UPIPopUpFragement::class.java.getName()
        var ORDER_CONFIRMATION_FRAGMENT: String = OrderConfirmationFragment::class.java.getName()
        var FAILED_TRANSACTION_FRAGMENT: String = FailedTransactionPopUpFragment::class.java.getName()
        var COUPON_POPUP_FRAGEMENT: String = CouponPopUpFragment::class.java.getName()
        var RAZORPAY_WEBVIEW_POPUP_FRAGMENT: String = RazorPayWebView::class.java.getName()

    }
}