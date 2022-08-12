package com.boost.payment.ui.confirmation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.boost.payment.PaymentActivity
import com.boost.payment.R
import com.boost.payment.base_class.BaseFragment
import com.boost.payment.ui.razorpay.RazorPayWebView
import com.boost.payment.utils.Constants
import com.boost.payment.utils.SharedPrefs
import com.boost.payment.utils.Utils
import com.boost.payment.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FAILED_PAYMENT_TRANSACTION_LOADED
import com.framework.webengageconstant.FAILED_PAYMENT_TRANSACTION
import com.framework.webengageconstant.MARKETPLACE_FALIURE_TRY_AGAIN_CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE
import kotlinx.android.synthetic.main.failed_transaction_fragment.transaction_failed_retry
import kotlinx.android.synthetic.main.payment_failure_v3.*

class FailedTransactionFragment : BaseFragment() {

    lateinit var root: View

    lateinit var razorPayWebView: RazorPayWebView
    lateinit var prefs: SharedPrefs
    var session: UserSessionManager? = null
    var screenType: String = ""
    var buyItemKey: String? = ""

    var data: String? = null

    companion object {
        fun newInstance() = FailedTransactionFragment()
    }

    private lateinit var viewModel: OrderConfirmationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.payment_failure_v3, container, false)
        session = UserSessionManager(activity as PaymentActivity)
        prefs = SharedPrefs(activity as PaymentActivity)
        razorPayWebView = RazorPayWebView.newInstance()
        data = arguments?.getString("data")
        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)
        viewModel.emptyCurrentCart((activity as PaymentActivity).application);

        transaction_failed_retry.setOnClickListener {

            WebEngageController.trackEvent(
                MARKETPLACE_FALIURE_TRY_AGAIN_CLICK,
                FAILED_PAYMENT_TRANSACTION,
                NO_EVENT_VALUE
            )
            val args = Bundle()
            args.putString("data", data)
            razorPayWebView.arguments = args

            //RazorPay web
            razorPayWebView.show(
                (activity as PaymentActivity).supportFragmentManager,
                Constants.RAZORPAY_WEBVIEW_POPUP_FRAGMENT
            )

        }

        marketplace_btn.setOnClickListener {

            goToMarketPlace()
            (activity as PaymentActivity).finish()
        }

        WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_FAILED_PAYMENT_TRANSACTION_LOADED,
            FAILED_PAYMENT_TRANSACTION,
            NO_EVENT_VALUE
        )

            val months = if(prefs.getValidityMonths().isNullOrEmpty()) 0 else prefs.getValidityMonths()!!.toInt()
            order_details_feature_count.text =
                "You have ordered " + prefs.getFeaturesCountInLastOrder()
            order_details_feature_count1.text = "₹" + prefs.getLatestPurchaseOrderTotalPrice() +"/"+ if(months>1) months else "" + Utils.yearOrMonthText(months,requireActivity(), false)//"/month."
            order_id_details.text = prefs.getLatestPurchaseOrderId()
            order_id_details1.text = prefs.getTransactionIdFromCart()

    }

    private fun goToMarketPlace() {
        try {
            val intent = Intent(context, Class.forName("com.boost.marketplace.ui.home.MarketplaceActivity"))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("isComingFromOrderConfirmActivation",true)
            intent.putExtra("expCode", session?.fP_AppExperienceCode)
            intent.putExtra("fpName", session?.fPName)
            intent.putExtra("fpid", session?.fPID)
            intent.putExtra("fpTag", session?.fpTag)
            intent.putExtra("screenType", screenType)
            intent.putExtra("accountType", session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
            intent.putExtra("boost_widget_key","TESTIMONIALS")
            intent.putExtra("feature_code","TESTIMONIALS")

            intent.putStringArrayListExtra(
                "userPurchsedWidgets",
                session?.getStoreWidgets() as ArrayList<String>
            )
            if (session?.userProfileEmail != null) {
                intent.putExtra("email", session?.userProfileEmail)
            } else {
                intent.putExtra("email", "ria@nowfloats.com")
            }
            if (session?.userPrimaryMobile != null) {
                intent.putExtra("mobileNo", session?.userPrimaryMobile)
            } else {
                intent.putExtra("mobileNo", "9160004303")
            }
            if (buyItemKey != null && buyItemKey!!.isNotEmpty()) intent.putExtra("buyItemKey", buyItemKey)
            intent.putExtra("profileUrl", session?.fPLogo)
            startActivity(intent)
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
