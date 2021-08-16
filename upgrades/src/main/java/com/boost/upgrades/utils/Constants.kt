package com.boost.upgrades.utils

import com.boost.upgrades.ui.autorenew.AutoRenewSubsFragment
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.checkoutkyc.BusinessDetailsFragment
import com.boost.upgrades.ui.checkoutkyc.CheckoutKycFragment
import com.boost.upgrades.ui.compare.ComparePackageFragment
import com.boost.upgrades.ui.confirmation.AutoRenewOrderConfirmationFragment
import com.boost.upgrades.ui.confirmation.OrderConfirmationFragment
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.freeaddons.FreeAddonsFragment
import com.boost.upgrades.ui.history.HistoryFragment
import com.boost.upgrades.ui.historydetails.HistoryDetailsFragment
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.marketplace_offers.MarketPlaceOfferFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.packages.PackageFragmentNew
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.*
import com.boost.upgrades.ui.razorpay.RazorPayWebView
import com.boost.upgrades.ui.removeaddons.RemoveAddonConfirmationFragment
import com.boost.upgrades.ui.removeaddons.RemoveAddonsFragment
import com.boost.upgrades.ui.splash.SplashFragment
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
    var CART_VALUE = 0
    var COMPARE_BACK_VALUE = 0
    var COMPARE_CART_COUNT = 0

    //razorpay credentials
    val RAZORPAY_KEY: String = "rzp_live_o8qdD1DZ3PHBn0"
    val RAZORPAY_SECREAT: String = "Gfq8pQyFbu5BOoY5SeT6UuQk"
//        val RAZORPAY_KEY: String = "rzp_test_OlLpIGwhA7bATX"
//        val RAZORPAY_SECREAT: String = "wMa4K0UW4dPXD4ZJrkVg64mX"


    // fragments
    var SPLASH_FRAGMENT: String = SplashFragment::class.java.getName()
    var HOME_FRAGMENT: String = HomeFragment::class.java.getName()
    var PACKAGE_FRAGMENT: String = PackageFragment::class.java.getName()
    var CART_FRAGMENT: String = CartFragment::class.java.getName()
    var VIEW_ALL_FEATURE: String = ViewAllFeaturesFragment::class.java.getName()
    var HISTORY_FRAGMENT: String = HistoryFragment::class.java.getName()
    var HISTORY_DETAILS_FRAGMENT: String = HistoryDetailsFragment::class.java.getName()
    var DETAILS_FRAGMENT: String = DetailsFragment::class.java.getName()
    var PAYMENT_FRAGMENT: String = PaymentFragment::class.java.getName()
    var MYADDONS_FRAGMENT: String = MyAddonsFragment::class.java.getName()
    var WEB_VIEW_FRAGMENT: String = WebViewFragment::class.java.getName()
    var REMOVE_ADDONS_FRAGMENT: String = RemoveAddonsFragment::class.java.getName()
    var REMOVE_ADDONS_CONFIRMATION_FRAGMENT: String =
      RemoveAddonConfirmationFragment::class.java.getName()
    var ADD_CARD_POPUP_FRAGMENT: String = AddCardPopUpFragement::class.java.getName()
    var NETBANKING_POPUP_FRAGMENT: String = NetBankingPopUpFragement::class.java.getName()
    var UPI_POPUP_FRAGMENT: String = UPIPopUpFragement::class.java.getName()
    var EXTERNAL_EMAIL_POPUP_FRAGMENT: String = ExternalEmailPopUpFragement::class.java.getName()
    var IMAGE_PREVIEW_POPUP_FRAGMENT: String = ImagePreviewPopUpFragement::class.java.getName()
    var ORDER_CONFIRMATION_FRAGMENT: String = OrderConfirmationFragment::class.java.getName()
    var FAILED_TRANSACTION_FRAGMENT: String = FailedTransactionPopUpFragment::class.java.getName()
    var COUPON_POPUP_FRAGEMENT: String = CouponPopUpFragment::class.java.getName()
    var GSTIN_POPUP_FRAGEMENT: String = GSTINPopUpFragment::class.java.getName()
    var TAN_POPUP_FRAGEMENT: String = TANPopUpFragment::class.java.getName()
    var RAZORPAY_WEBVIEW_POPUP_FRAGMENT: String = RazorPayWebView::class.java.getName()
    var RENEW_POPUP_FRAGEMENT: String = RenewalPopUpFragment::class.java.getName()
    var AUTO_RENEW_FRAGEMENT: String = AutoRenewSubsFragment::class.java.getName()
    var AUTO_RENEW_ORDER_CONFIRMATION_FRAGMENT: String =
      AutoRenewOrderConfirmationFragment::class.java.getName()
    var COMPARE_FRAGMENT: String = ComparePackageFragment::class.java.getName()
    var FREEADDONS_FRAGMENT: String = FreeAddonsFragment::class.java.getName()
    var CHECKOUT_KYC_FRAGMENT: String = CheckoutKycFragment::class.java.getName()
    var MARKET_OFFER_FRAGMENT: String = MarketPlaceOfferFragment::class.java.getName()
    var BUSINESS_DETAILS_FRAGMENT: String = BusinessDetailsFragment::class.java.getName()
    var STATE_LIST_FRAGMENT: String = StateListPopFragment::class.java.getName()
    var NEW_PACKAGE_FRAGMENT: String = PackageFragmentNew::class.java.getName()

  }
}