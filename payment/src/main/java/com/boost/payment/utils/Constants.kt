package com.boost.payment.utils

import com.boost.payment.ui.checkoutkyc.BusinessDetailsFragment
import com.boost.payment.ui.confirmation.FailedTransactionFragment
import com.boost.payment.ui.confirmation.OrderConfirmationFragment
import com.boost.payment.ui.payment.PaymentFragment
import com.boost.payment.ui.popup.*
import com.boost.payment.ui.razorpay.RazorPayWebView
import com.boost.payment.ui.webview.WebViewFragment


class Constants {
    companion object {

        //razorpay credentials
        val RAZORPAY_KEY: String = "rzp_live_NsMLSX2HTaiEC9"
//        val RAZORPAY_SECREAT: String = "UPgGtpXJIZ9pPEqSRrw4PqV5" //new key which is in use 17 june 2022
//        val RAZORPAY_SECREAT: String = "Gfq8pQyFbu5BOoY5SeT6UuQk" // old one
//        val RAZORPAY_KEY: String = "rzp_test_OlLpIGwhA7bATX"
//        val RAZORPAY_SECREAT: String = "wMa4K0UW4dPXD4ZJrkVg64mX"

        const val BASE_URL = "https://api.withfloats.com/"
        var PAYMENT_FRAGMENT: String = PaymentFragment::class.java.getName()
        var ADD_CARD_POPUP_FRAGMENT: String = AddCardPopUpFragement::class.java.getName()
        var EXTERNAL_EMAIL_POPUP_FRAGMENT: String = ExternalEmailPopUpFragement::class.java.getName()
        var NETBANKING_POPUP_FRAGMENT: String = NetBankingPopUpFragement::class.java.getName()
        var UPI_POPUP_FRAGMENT: String = UPIPopUpFragement::class.java.getName()
        var RAZORPAY_WEBVIEW_POPUP_FRAGMENT: String = RazorPayWebView::class.java.getName()
        var STATE_LIST_FRAGMENT: String = StateListPopFragment::class.java.getName()
        var ORDER_CONFIRMATION_FRAGMENT: String = OrderConfirmationFragment::class.java.getName()
        var WEB_VIEW_FRAGMENT: String = WebViewFragment::class.java.getName()
        var FAILED_TRANSACTION_FRAGMENT: String = FailedTransactionFragment::class.java.getName()
        var BUSINESS_DETAILS_FRAGMENT: String = BusinessDetailsFragment::class.java.getName()

        const val ALERT_YES = 1
        const val ALERT_NO = 2

        var COMPARE_BACK_VALUE = 0
        var COMPARE_CART_COUNT = 0
        var CART_VALUE = 0
    }
}