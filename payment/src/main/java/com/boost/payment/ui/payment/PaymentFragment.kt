package com.boost.payment.ui.payment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.payment.R
import com.boost.payment.PaymentActivity
import com.boost.payment.adapter.CardPaymentAdapter
import com.boost.payment.adapter.WalletAdapter
import com.boost.payment.base_class.BaseFragment
import com.boost.dbcenterapi.data.api_model.PaymentThroughEmail.PaymentPriorityEmailRequestBody
import com.boost.dbcenterapi.data.api_model.customerId.get.Result
import com.boost.dbcenterapi.data.api_model.datamodule.SingleNetBankData
import com.boost.payment.interfaces.*
import com.boost.payment.ui.checkoutkyc.BusinessDetailsFragment
import com.boost.payment.ui.confirmation.OrderConfirmationFragment
import com.boost.payment.ui.popup.*
import com.boost.payment.ui.razorpay.RazorPayWebView
import com.boost.payment.utils.Constants
import com.boost.payment.utils.Constants.Companion.ADD_CARD_POPUP_FRAGMENT
import com.boost.payment.utils.Constants.Companion.BUSINESS_DETAILS_FRAGMENT
import com.boost.payment.utils.Constants.Companion.EXTERNAL_EMAIL_POPUP_FRAGMENT
import com.boost.payment.utils.Constants.Companion.NETBANKING_POPUP_FRAGMENT
import com.boost.payment.utils.Constants.Companion.RAZORPAY_WEBVIEW_POPUP_FRAGMENT
import com.boost.payment.utils.Constants.Companion.UPI_POPUP_FRAGMENT
import com.boost.payment.utils.SharedPrefs
import com.boost.payment.utils.WebEngageController
import com.boost.payment.utils.observeOnce
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.payments_fragment.*
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentFragment : BaseFragment(), PaymentListener, BusinessDetailListener,
    MoreBanksListener, UpiPayListener, EmailPopupListener, AddCardListener {

    lateinit var root: View
    private lateinit var viewModel: PaymentViewModel

    lateinit var razorpay: Razorpay

    lateinit var cardPaymentAdapter: CardPaymentAdapter
    lateinit var walletAdapter: WalletAdapter

    val razorPayWebView = RazorPayWebView()

    var cartCheckoutData = JSONObject()

    var paymentData = JSONObject()

    var netbankingList = arrayListOf<SingleNetBankData>()

    var totalAmount = 0.0

    var createCustomerInfoRequest: Result? = null
    var customerInfoState = false
    var paymentProceedFlag = true
    private var session: UserSessionManager? = null
    val stateFragment = StateListPopFragment()
    var gstFlag = false
    lateinit var prefs: SharedPrefs
    private var gstResult: com.boost.dbcenterapi.data.api_model.gst.Result? = null
    private lateinit var paymentLL: LinearLayout
    private lateinit var upiLayout: ConstraintLayout
    private lateinit var netBankingLayout: ConstraintLayout
    private lateinit var walletLayout: ConstraintLayout
    private lateinit var savedCardsLayout: ConstraintLayout
    private lateinit var payLinkLayout: ConstraintLayout
    private var isPayViaLink: Boolean = false
    private var lastUsedPaymentMethod: String? = null
    private var autoRenewState = false

    companion object {
        fun newInstance() = PaymentFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.payments_fragment, container, false)
        paymentLL = root.findViewById(R.id.payment_mode_ll)
        upiLayout = root.findViewById(R.id.upi_layout)
        netBankingLayout = root.findViewById(R.id.netbanking_layout)
        walletLayout = root.findViewById(R.id.wallet_layout)
        savedCardsLayout = root.findViewById(R.id.saved_cards_layout)
        payLinkLayout = root.findViewById(R.id.pay_by_link_section)


        totalAmount = requireArguments().getDouble("amount")
        session = UserSessionManager(requireActivity())
        cartCheckoutData.put("customerId", requireArguments().getString("customerId"))
        cartCheckoutData.put("amount", Math.round(totalAmount * 100).toInt())
        updateAutoRenewState()
        cartCheckoutData.put("transaction_id", requireArguments().getString("transaction_id"))
        cartCheckoutData.put("email", requireArguments().getString("email"))
        cartCheckoutData.put("currency", requireArguments().getString("currency"));
        cartCheckoutData.put("contact", requireArguments().getString("contact"))
        prefs = SharedPrefs(activity as PaymentActivity)

        lastUsedPaymentMethod = prefs.getLastUsedPaymentMode()


        paymentLL.removeAllViews()

        if (lastUsedPaymentMethod == "upi") {
            paymentLL.addView(upiLayout)
            paymentLL.addView(netBankingLayout)
            paymentLL.addView(savedCardsLayout)
            paymentLL.addView(walletLayout)
            paymentLL.addView(payLinkLayout)
        } else if (lastUsedPaymentMethod == "wallet") {
            paymentLL.addView(walletLayout)
            paymentLL.addView(upiLayout)
            paymentLL.addView(netBankingLayout)
            paymentLL.addView(savedCardsLayout)
            paymentLL.addView(payLinkLayout)
        } else if (lastUsedPaymentMethod == "netbanking") {
            paymentLL.addView(netBankingLayout)
            paymentLL.addView(upiLayout)
            paymentLL.addView(savedCardsLayout)
            paymentLL.addView(walletLayout)
            paymentLL.addView(payLinkLayout)
        } else if (lastUsedPaymentMethod == "card") {
            paymentLL.addView(savedCardsLayout)
            paymentLL.addView(upiLayout)
            paymentLL.addView(netBankingLayout)
            paymentLL.addView(walletLayout)
            paymentLL.addView(payLinkLayout)
        } else {
            paymentLL.addView(upiLayout)
            paymentLL.addView(netBankingLayout)
            paymentLL.addView(savedCardsLayout)
            paymentLL.addView(walletLayout)
            paymentLL.addView(payLinkLayout)
        }


//        //this is a offer created from admin dashboard.
//        cartCheckoutData.put("offer_id", arguments!!.getString("offer_F5hUaalR9tpSzn"))

        razorpay = (activity as PaymentActivity).getRazorpayObject()

        netbankingList = ArrayList<SingleNetBankData>()
        netbankingList.add(SingleNetBankData("UTIB", "Axis", razorpay.getBankLogoUrl("UTIB")))
        netbankingList.add(SingleNetBankData("ICIC", "ICICI", razorpay.getBankLogoUrl("ICIC")))
        netbankingList.add(SingleNetBankData("HDFC", "HDFC", razorpay.getBankLogoUrl("HDFC")))
        netbankingList.add(SingleNetBankData("CIUB", "City Union", razorpay.getBankLogoUrl("CIUB")))
        netbankingList.add(SingleNetBankData("SBIN", "SBI", razorpay.getBankLogoUrl("SBIN")))

        cardPaymentAdapter = CardPaymentAdapter(requireActivity(), ArrayList())

        walletAdapter = WalletAdapter(razorpay, ArrayList(), this)

        return root
    }

    private fun updateAutoRenewState() {
        if (autoRenewState) {
            //for auto renew sent subscription_id
            cartCheckoutData.put("subscription_id", requireArguments().getString("subscription_id"))
//      cartCheckoutData.put("subscription_id", "sub_Io6LDbGj1FT515")
            if (cartCheckoutData.has("order_id")) cartCheckoutData.remove("order_id")
        } else {
            cartCheckoutData.put("order_id", requireArguments().getString("order_id"))
//      cartCheckoutData.put("order_id", "order_Io6NLckFM8ny8Z")
            if (cartCheckoutData.has("subscription_id")) cartCheckoutData.remove("subscription_id")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

        loadData()
        loadCustomerInfo()
        initMvvm()

        initializeNetBankingSelector()
        initializeCardRecycler()
        initializeWalletRecycler()
        updateSubscriptionDetails()

        WebEngageController.trackEvent(
            EVENT_NAME_ADDONS_MARKETPLACE_PAYMENT_LOAD,
            PAGE_VIEW,
            ADDONS_MARKETPLACE_PAYMENT_SCREEN
        )

        var firebaseAnalytics = Firebase.analytics
        val revenue = cartCheckoutData.getDouble("amount")
        val bundle = Bundle()
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue / 100)
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)

        back_button.setOnClickListener {
            WebEngageController.trackEvent(
                ADDONS_MARKETPLACE_CLICKED_BACK_BUTTON_PAYMENTSCREEN,
                ADDONS_MARKETPLACE,
                NO_EVENT_VALUE
            )
            (activity as PaymentActivity).popFragmentFromBackStack()
        }

        payment_submit.setOnClickListener {
            if (paymentData.length() > 0) {
                payThroughRazorPay()
            }
        }

        add_new_cards.setOnClickListener {
            if (paymentProceedFlag) {
                WebEngageController.trackEvent(
                    ADDONS_MARKETPLACE_ADD_NEW_CARD_CLICK,
                    ADDONS_MARKETPLACE_ADD_NEW_CARD,
                    NO_EVENT_VALUE
                )
                val args = Bundle()
                args.putString("customerId", cartCheckoutData.getString("customerId"))
//                addCardPopUpFragement.arguments = args
//                addCardPopUpFragement.show((activity as PaymentActivity).supportFragmentManager, ADD_CARD_POPUP_FRAGMENT)

                val addCardFragement = AddCardPopUpFragement.newInstance(this)
                addCardFragement.arguments = args
                addCardFragement.show(
                    (activity as PaymentActivity).supportFragmentManager,
                    ADD_CARD_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
//                payment_main_layout.fullScroll(View.FOCUS_FORWARD)
                payment_main_layout.smoothScrollTo(0, 0)
            }
        }

        show_more_bank.setOnClickListener {
            if (paymentProceedFlag) {
                WebEngageController.trackEvent(
                    ADDONS_MARKETPLACE_SHOW_MORE_BANK_CLICK,
                    ADDONS_MARKETPLACE_SHOW_MORE_BANK,
                    NO_EVENT_VALUE
                )
                /*netBankingPopUpFragement.show(
                        (activity as PaymentActivity).supportFragmentManager,
                        NETBANKING_POPUP_FRAGMENT
                )*/
                val netBankingFragement = NetBankingPopUpFragement.newInstance(this)
                netBankingFragement.show(
                    (activity as PaymentActivity).supportFragmentManager,
                    NETBANKING_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }
        }

        add_upi_layout.setOnClickListener {
            if (paymentProceedFlag) {
                WebEngageController.trackEvent(
                    ADDONS_MARKETPLACE_UPI_CLICK,
                    ADDONS_MARKETPLACE_UPI,
                    NO_EVENT_VALUE
                )
                /* upiPopUpFragement.show(
                     (activity as PaymentActivity).supportFragmentManager,
                     UPI_POPUP_FRAGMENT
                 )*/
                val upiFragment = UPIPopUpFragement.newInstance(this)
                upiFragment.show(
                    (activity as PaymentActivity).supportFragmentManager,
                    UPI_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }
        }

        phonePe_layout.setOnClickListener {
            if (appInstalledOrNot("com.phonepe.app")) {
                if (!autoRenewState) {
                    cartCheckoutData.put("method", "upi");  //Method specific fields
                    cartCheckoutData.put("_[flow]", "intent");
                    cartCheckoutData.put("upi_app_package_name", "com.phonepe.app");
                }
                payThroughRazorPay()
            } else {
                Toasty.info(requireContext(), "Install PhonePe to Continue").show()
            }
        }

        google_pay_layout.setOnClickListener {
            if (appInstalledOrNot("com.google.android.apps.nbu.paisa.user")) {
                if (!autoRenewState) {
                    cartCheckoutData.put("method", "upi");  //Method specific fields
                    cartCheckoutData.put("_[flow]", "intent");
                    cartCheckoutData.put(
                        "upi_app_package_name",
                        "com.google.android.apps.nbu.paisa.user"
                    );
                }
                payThroughRazorPay()
            } else {
                Toasty.info(requireContext(), "Install GPay to Continue").show()
            }
        }

        paytm_layout.setOnClickListener {
            if (appInstalledOrNot("net.one97.paytm")) {
                if (!autoRenewState) {
                    cartCheckoutData.put("method", "upi");  //Method specific fields
                    cartCheckoutData.put("_[flow]", "intent");
                    cartCheckoutData.put("upi_app_package_name", "net.one97.paytm");
                }
                payThroughRazorPay()
            } else {
                Toasty.info(requireContext(), "Install PAYTM to Continue").show()
            }
        }

        bhim_upi_layout.setOnClickListener {
            if (appInstalledOrNot("in.org.npci.upiapp")) {
                if (!autoRenewState) {
                    cartCheckoutData.put("method", "upi");  //Method specific fields
                    cartCheckoutData.put("_[flow]", "intent");
                    cartCheckoutData.put("upi_app_package_name", "in.org.npci.upiapp");
                }
                payThroughRazorPay()
            } else {
                Toasty.info(requireContext(), "Install BHIM to Continue").show()
            }
        }


//    add_external_email.setOnClickListener {
        generate_payment_link.setOnClickListener {
            if (paymentProceedFlag) {
                WebEngageController.trackEvent(
                    ADDONS_MARKETPLACE_PAYMENT_LINK_CLICK,
                    ADDONS_MARKETPLACE_PAYMENT_LINK,
                    NO_EVENT_VALUE
                )
                /*externalEmailPopUpFragement.show(
                    (activity as PaymentActivity).supportFragmentManager,
                    EXTERNAL_EMAIL_POPUP_FRAGMENT
                )*/
                val emailPopUpFragement = ExternalEmailPopUpFragement.newInstance(this)
                emailPopUpFragement.show(
                    (activity as PaymentActivity).supportFragmentManager,
                    EXTERNAL_EMAIL_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }
        }

        payment_view_details.setOnClickListener {
            payment_main_layout.post {
                payment_main_layout.fullScroll(View.FOCUS_DOWN)
            }
        }

        coupon_discount_value.setOnClickListener {

        }
        edit_business_details.setOnClickListener {
            /*businessDetailsFragment.show(
                (activity as PaymentActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )*/
            val businessFragment = BusinessDetailsFragment.newInstance(this)
            businessFragment.show(
                (activity as PaymentActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )
        }

        all_business_button.setOnClickListener {
            /* businessDetailsFragment.show(
                 (activity as PaymentActivity).supportFragmentManager,
                 BUSINESS_DETAILS_FRAGMENT
             )*/
            val businessFragment = BusinessDetailsFragment.newInstance(this)
            businessFragment.show(
                (activity as PaymentActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )
        }

        auto_renew_switch.setOnClickListener {
            if (autoRenewState) {
                auto_renew_switch.setImageResource(R.drawable.ic_switch_off)
                auto_renew_extra_offers.background = ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_discount_auto_renew_bg
                )
                auto_renew_extra_offers.text = "Enable auto-renewal to get extra 3% off"
                auto_renew_extra_offers.setTextColor(resources.getColor(R.color.colorPrimary))
                auto_renew_extra_offers.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_discount,
                    0,
                    0,
                    0
                )
                upi_payment_title.text = "UPI"
                netbanking_title.text = "Net Banking"
                saved_cards_layout.visibility = View.VISIBLE
                pay_by_link_section.visibility = View.VISIBLE
                upi_list_layout.visibility = View.VISIBLE
                upi_view_dummy.visibility = View.VISIBLE
                autoRenewState = false
            } else {
                auto_renew_switch.setImageResource(R.drawable.ic_switch_on)
                auto_renew_extra_offers.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_offer_auto_renew_bg)
                auto_renew_extra_offers.text =
                    "Congratulations! You get extra 2% auto renewal discount"
                auto_renew_extra_offers.setTextColor(resources.getColor(R.color.green))
                auto_renew_extra_offers.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                upi_payment_title.text = "UPI Auto Renewal"
                netbanking_title.text = "Bank Enabled Auto Renewal"
                saved_cards_layout.visibility = View.GONE
                pay_by_link_section.visibility = View.GONE
                upi_list_layout.visibility = View.GONE
                upi_view_dummy.visibility = View.GONE
                autoRenewState = true
            }
            updateAutoRenewState()
        }
        /*supply_place_button.setOnClickListener{
            stateFragment.show(
                (activity as PaymentActivity).supportFragmentManager,
                STATE_LIST_FRAGMENT
            )
        }*/
        if (!prefs.getGstRegistered()) {
            business_gstin.text = "Have a GST number?"
//            business_gstin_missing.text = "Not Registered with GST"
            business_gstin_missing.text = "No"
        }
        WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_PAYMENT_SCREEN_LOADED,
            PAYMENT_SCREEN,
            NO_EVENT_VALUE
        )
    }

    private fun appInstalledOrNot(packageName: String): Boolean {
        val pm: PackageManager = requireActivity().packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
        return false
    }


    fun loadData() {
        viewModel.loadpaymentMethods(razorpay)
//        viewModel.getRazorPayToken(cartCheckoutData.getString("customerId"))
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm() {
        viewModel.cardData().observe(this, Observer {
            Log.i("cardObserver >>>>>", it.toString())
            paymentData = it
            payThroughRazorPay()
        })

        viewModel.netBankingData().observe(this, Observer {
            Log.i("netBankingObserver >", it.toString())
            paymentData = it
            payThroughRazorPay()
//            payThroughRazorPayMoreBanks()
        })

        viewModel.upiPaymentData().observe(this, Observer {
            Log.i("upiPaymentObserver >", it.toString())
            paymentData = it
            payThroughRazorPay()
        })
        viewModel.externalEmailPaymentData().observe(this, Observer {
            Log.i("emailPaymentObserver >", it.toString())
            paymentData = it
            payViaPaymentLink()
        })
        viewModel.walletPaymentData().observe(this, Observer {
            Log.i("walletPaymentObserver >", it.toString())
            loadWallet(it)
        })
        viewModel.getPamentUsingExternalLink().observe(this, Observer {
//            if (it != null && it.equals("SUCCESSFULLY ADDED TO QUEUE")) {
            if (it != null && it.equals("OK")) {
                isPayViaLink = true
                val orderConfirmationFragment = OrderConfirmationFragment.newInstance()
                val args = Bundle()
                args.putString("payment_type", "External_Link")
                args.putBoolean("payViaLink", isPayViaLink)
                orderConfirmationFragment.arguments = args
                (activity as PaymentActivity).replaceFragment(
                    orderConfirmationFragment,
                    Constants.ORDER_CONFIRMATION_FRAGMENT
                )
            } else {
                Toasty.error(
                    requireContext(),
                    "Unable To Send Link To Email. Try Later...",
                    Toast.LENGTH_SHORT,
                    true
                ).show();
            }
        })

        viewModel.getCustomerInfoResult().observe(viewLifecycleOwner, Observer {
            createCustomerInfoRequest = it.Result
            if (createCustomerInfoRequest != null) {

                /*Starts*/
//                if(session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")){
//
//                    if (createCustomerInfoRequest!!.BusinessDetails != null) {
//                        if(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null){
//                            business_mobile_value.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
//                        }
//                    }
//                }else{
//                    business_mobile_value.text = session?.userPrimaryMobile
//                }
//
//                if(session?.fPEmail == null || session?.fPEmail.equals("") ){
//                    if (createCustomerInfoRequest!!.BusinessDetails != null) {
//                    if(createCustomerInfoRequest!!.BusinessDetails!!.Email != null){
//                        business_email_value.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
//                    }
//                    }
//
//                }else{
//                    business_email_value.setText(session?.fPEmail)
//                }

//                if(session?.fPName == null || session?.fPName.equals("") ){
//                    if (createCustomerInfoRequest!!.Name != null ) {
//                        business_name_value.setText(createCustomerInfoRequest!!.Name)
//                    }else{
//                        business_name_value.visibility = View.INVISIBLE
//                        business_name_missing.visibility = View.VISIBLE
//                    }
//                }else{
//                    business_name_value.setText(session?.fPName)
//                    business_name_value.visibility = View.VISIBLE
//                    business_name_value.text = session?.fPName
//                    if(createCustomerInfoRequest!!.Name == null || createCustomerInfoRequest!!.Name!!.length < 1){
//                        paymentProceedFlag = false
//                    }
//                }

//                if(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).equals("") ){
//                    if (createCustomerInfoRequest!!.AddressDetails.Line1 != null /*&& createCustomerInfoRequest!!.AddressDetails?.Line1.toString().length > 0*/) {
//                        business_address_value.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
//                    }else{
//                        business_address_value.visibility = View.INVISIBLE
//                        business_address_missing.visibility = View.VISIBLE
//                    }
//                }else{
//                    business_address_value.visibility = View.VISIBLE
//                    business_address_value.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
//                    if(createCustomerInfoRequest!!.AddressDetails!!.Line1 == null){
//                        paymentProceedFlag = false
//                    }
//                }
                /*Ends*/

/*starts one*/



                if (createCustomerInfoRequest!!.BusinessDetails!!.Email != null) {
                    business_email_value.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
                } else if (session?.fPEmail != null || session?.fPEmail.equals("")) {
                    business_email_value.setText(session?.fPEmail)
                }

                if (createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null) {
                    business_mobile_value.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
                } else if (session?.userPrimaryMobile != null || session?.userPrimaryMobile.equals("")) {
                    business_mobile_value.text = session?.userPrimaryMobile
                }

                if (createCustomerInfoRequest!!.Name != null) {
                    business_name_value.setText(createCustomerInfoRequest!!.Name)
                } else {
                    if (session?.fPName == null || session?.fPName.equals("")) {
                        business_name_value.visibility = View.INVISIBLE
                        business_name_missing.visibility = View.VISIBLE
                    } else {
                        business_name_value.visibility = View.VISIBLE
                        business_name_value.text = session?.fPName
                        if (createCustomerInfoRequest!!.Name == null || createCustomerInfoRequest!!.Name!!.length < 1) {
                            paymentProceedFlag = false
                        }
                    }

                }

                if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
                    business_address_value.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                } else {
                    if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null ||
                        session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).equals("")
                    ) {
                        business_address_value.visibility = View.INVISIBLE
                        business_address_missing.visibility = View.VISIBLE
                    } else {
                        business_address_value.visibility = View.VISIBLE
                        business_address_value.text =
                            session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
                        if (createCustomerInfoRequest!!.AddressDetails!!.Line1 == null) {
                            paymentProceedFlag = false
                        }
                    }


                }

/*ends one*/


                Log.v("createCustomerInfoG", " " + createCustomerInfoRequest!!.TaxDetails?.GSTIN)
                Log.v(
                    "createCustomerInfoA",
                    " " + createCustomerInfoRequest!!.AddressDetails?.Line1
                )
                Log.v("createCustomerInfoN", " " + createCustomerInfoRequest!!.Name)
                Log.v(
                    "createCustomerInfoS",
                    " " + createCustomerInfoRequest!!.AddressDetails!!.State
                )
                Log.v(
                    "createCustomerInfoS",
                    " " + createCustomerInfoRequest!!.AddressDetails!!.State
                )
                Log.v("createCustomerInfoE", " " + session?.fPEmail)
                Log.v(
                    "createCustomerInfoE",
                    " " + session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL)
                )
                /* if (createCustomerInfoRequest!!.Name != null ) {
                     business_name_value.setText(createCustomerInfoRequest!!.Name)
                 }else{
                     business_name_value.visibility = View.INVISIBLE
                     business_name_missing.visibility = View.VISIBLE
                 }*/

                if (createCustomerInfoRequest!!.TaxDetails?.GSTIN != null /*|| createCustomerInfoRequest!!.TaxDetails?.GSTIN.equals("")*/) {
                    business_gstin_value.setText(createCustomerInfoRequest!!.TaxDetails.GSTIN)
                } else {
                    business_gstin_value.visibility = View.INVISIBLE
                    business_gstin_missing.visibility = View.VISIBLE
                }

                /*if (createCustomerInfoRequest!!.AddressDetails.Line1 != null *//*&& createCustomerInfoRequest!!.AddressDetails?.Line1.toString().length > 0*//*) {
                    business_address_value.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                }else{
                    business_address_value.visibility = View.INVISIBLE
                    business_address_missing.visibility = View.VISIBLE
                }*/

                if (createCustomerInfoRequest!!.AddressDetails != null) {
                    business_supply_place_value.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                }

                if (createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber == null) {
                    business_mobile_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                } else {
                    business_mobile_missing.visibility = View.GONE
//                    business_mobile_missing.visibility = View.GONE
                }
                /*if(createCustomerInfoRequest!!.Name == null || createCustomerInfoRequest!!.Name!!.length < 1){
                    business_name_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                }else{
                    business_name_missing.visibility = View.GONE
                    business_name_value.setText(createCustomerInfoRequest!!.Name)
                }*/

                if (createCustomerInfoRequest!!.BusinessDetails!!.Email == null) {
                    business_email_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                } else {
                    business_email_missing.visibility = View.GONE
                }
                if (createCustomerInfoRequest!!.AddressDetails!!.State == null && createCustomerInfoRequest!!.AddressDetails!!.City == null) {
                    business_supply_place_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                } else {
                    business_supply_place_missing.visibility = View.GONE
                }

                if (createCustomerInfoRequest!!.TaxDetails?.GSTIN == null) {
                    business_gstin_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                } else {
                    business_gstin_value.visibility = View.VISIBLE
                    business_gstin_missing.visibility = View.GONE
                    business_gstin.text = "GSTIN"
                }

                if (!prefs.getGstRegistered()) {
                    business_gstin_missing.visibility = View.VISIBLE
//                    business_gstin_missing.text = "Not Registered with GST"
                    business_gstin.text = "Have a GST number?"
                    business_gstin_missing.text = "No"
                    business_gstin_value.visibility = View.INVISIBLE
                }

                /*if(createCustomerInfoRequest!!.AddressDetails!!.Line1 == null){
                    business_address_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                }else{
                    business_address_missing.visibility = View.GONE
                }*/


                if (createCustomerInfoRequest!!.AddressDetails!!.State == null || createCustomerInfoRequest!!.AddressDetails!!.State.equals(
                        "string"
                    )
                ) {
                    business_supply_place_missing.visibility = View.VISIBLE
                    business_supply_place_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                } else {
                    business_supply_place_value.visibility = View.VISIBLE
                    business_supply_place_missing.visibility = View.GONE
                    business_supply_place.setTextColor(resources.getColor(R.color.common_text_color))
                    business_supply_place_value.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                }

                if (createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null &&
                    createCustomerInfoRequest!!.BusinessDetails!!.Email != null  /*&&*/
                /*createCustomerInfoRequest!!.Name!!.length > 0*/  /*&&*/
//                    createCustomerInfoRequest!!.TaxDetails.GSTIN != null  &&
                /*createCustomerInfoRequest!!.AddressDetails.Line1.toString() != null*/ /* &&
                    createCustomerInfoRequest!!.AddressDetails.State != null*/) {
                    paymentProceedFlag = true
                    business_button_layout.visibility = View.GONE
                    business_button_separator.visibility = View.GONE
                    edit_business_details.visibility = View.VISIBLE
                    if (createCustomerInfoRequest!!.Name == null || createCustomerInfoRequest!!.AddressDetails.State == null ||
                        createCustomerInfoRequest!!.AddressDetails.Line1 == null || createCustomerInfoRequest!!.AddressDetails!!.State.equals(
                            "string"
                        )
                    ) {
                        paymentProceedFlag = false
                        edit_business_details.visibility = View.GONE
                        business_button_layout.visibility = View.VISIBLE
                        all_business_button.visibility = View.VISIBLE
                        if (createCustomerInfoRequest!!.TaxDetails?.GSTIN == null) {
                            business_gstin_missing.visibility = View.VISIBLE
                            business_gstin.text = "Have a GST number?"
                            business_gstin_missing.text = "No"
                            business_gstin_value.visibility = View.INVISIBLE
                            prefs.storeGstRegistered(false)
                        }
                    } else {
                        paymentProceedFlag = true
                        edit_business_details.visibility = View.VISIBLE
                        business_button_layout.visibility = View.GONE
//                        payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg)
                        if (createCustomerInfoRequest!!.TaxDetails?.GSTIN == null) {
                            business_gstin_missing.visibility = View.VISIBLE
                            business_gstin.text = "Have a GST number?"
                            business_gstin_missing.text = "No"
                            business_gstin_value.visibility = View.INVISIBLE
                            prefs.storeGstRegistered(false)
                        }
                    }
                } else {
                    business_button_layout.visibility = View.VISIBLE
                    all_business_button.visibility = View.VISIBLE
                    if (createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber == null) {
                        paymentProceedFlag = false
                        business_mobile.setTextColor(resources.getColor(R.color.global_red))
                        business_mobile_value.visibility = View.INVISIBLE
                        business_mobile_missing.visibility = View.VISIBLE
                    }
                    if (createCustomerInfoRequest!!.BusinessDetails!!.Email == null) {
                        paymentProceedFlag = false
                        business_email.setTextColor(resources.getColor(R.color.global_red))
                        business_email_value.visibility = View.INVISIBLE
                        business_email_missing.visibility = View.VISIBLE
                    }
                    if (createCustomerInfoRequest!!.Name!!.length < 1) {
                        paymentProceedFlag = false
                        business_name.setTextColor(resources.getColor(R.color.global_red))
                        business_name_value.visibility = View.INVISIBLE
                        business_name_missing.visibility = View.VISIBLE
                    }
                    if (createCustomerInfoRequest!!.AddressDetails.Line1.toString().length < 1) {
                        paymentProceedFlag = false
                        business_address.setTextColor(resources.getColor(R.color.global_red))
                        business_address_value.visibility = View.INVISIBLE
                        business_address_missing.visibility = View.VISIBLE
                    }
                    if (createCustomerInfoRequest!!.AddressDetails.State == null || createCustomerInfoRequest!!.AddressDetails!!.State.equals(
                            "string"
                        )
                    ) {
                        paymentProceedFlag = false
                        business_supply_place.setTextColor(resources.getColor(R.color.global_red))
                        business_supply_place_value.visibility = View.INVISIBLE
                        business_supply_place_missing.visibility = View.VISIBLE
                    }
                    if (createCustomerInfoRequest!!.TaxDetails?.GSTIN == null) {
                        paymentProceedFlag = false
                        business_gstin.setTextColor(resources.getColor(R.color.global_red))
                        business_gstin_value.visibility = View.INVISIBLE
                        business_gstin_missing.visibility = View.VISIBLE
                    }
                }

            }
        })
        viewModel.getCustomerInfoStateResult().observe(viewLifecycleOwner, Observer {
            customerInfoState = it
            if (!customerInfoState) {
                Log.v("getCustomerInfoM", " " + session?.fPPrimaryContactNumber)
                Log.v("getCustomerInfoE", " " + session?.fPEmail)
                Log.v("getCustomerInfoN", " " + session?.fPName)
//                if(session?.getFPDetails(Key_Preferences.PRIMARY_NUMBER) == null || session?.getFPDetails(Key_Preferences.PRIMARY_NUMBER).equals("")){
                if (session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")) {
                    business_mobile_missing.visibility = View.VISIBLE
                    business_mobile_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                } else {
                    business_mobile_value.visibility = View.VISIBLE
                    business_mobile_value.text = session?.userPrimaryMobile
                }

//                if(session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL) == null || session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL).equals("") ){
                if (session?.fPEmail == null || session?.fPEmail.equals("")) {
                    business_email_missing.visibility = View.VISIBLE
                    business_email_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                } else {
                    business_email_value.visibility = View.VISIBLE
                    business_email_value.text = session?.fPEmail
                }

                if (session?.fPName == null || session?.fPName.equals("")) {
                    business_name_missing.visibility = View.VISIBLE
                    business_name_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                } else {
                    business_name_value.visibility = View.VISIBLE
                    business_name_value.text = session?.fPName
                }

                if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(
                        Key_Preferences.GET_FP_DETAILS_ADDRESS
                    ).equals("")
                ) {
                    business_address_missing.visibility = View.VISIBLE
                    business_address_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                } else {
                    business_address_value.visibility = View.VISIBLE
                    business_address_value.text =
                        session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
                }

//                business_gstin.setTextColor(resources.getColor(R.color.global_red))
                business_gstin_value.visibility = View.INVISIBLE
                business_gstin_missing.visibility = View.VISIBLE
                business_gstin_missing.text = "No"
                business_gstin.text = "Have a GST number?"

                business_supply_place.setTextColor(resources.getColor(R.color.global_red))
                business_supply_place_value.visibility = View.INVISIBLE
                business_supply_place_missing.visibility = View.VISIBLE

                /*business_address.setTextColor(resources.getColor(R.color.global_red))
                business_address_value.visibility = View.INVISIBLE
                business_address_missing.visibility = View.VISIBLE

                business_name.setTextColor(resources.getColor(R.color.global_red))
                business_name_value.visibility = View.INVISIBLE
                business_name_missing.visibility = View.VISIBLE

                business_email.setTextColor(resources.getColor(R.color.global_red))
                business_email_value.visibility = View.INVISIBLE
                business_email_missing.visibility = View.VISIBLE

                business_mobile.setTextColor(resources.getColor(R.color.global_red))
                business_mobile_value.visibility = View.INVISIBLE
                business_mobile_missing.visibility = View.VISIBLE*/

                business_supply_place_missing.visibility = View.VISIBLE
                business_supply_place_value.visibility = View.INVISIBLE
                business_button_layout.visibility = View.VISIBLE
//                all_business_button.visibility = View.VISIBLE
//                supply_place_button.visibility = View.VISIBLE
                paymentProceedFlag = false
//                if(session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL).equals("") && session?.getFPDetails(Key_Preferences.PRIMARY_NUMBER).equals("")){
//                if(session?.fPPrimaryContactNumber.equals("") && session?.fPEmail.equals("")){
//                    supply_place_button.visibility = View.GONE
                business_button_layout.visibility = View.VISIBLE
                all_business_button.visibility = View.VISIBLE
//                }
            }
        })

        viewModel.getUpdatedCustomerResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG)
                    .show()
                loadCustomerInfo()
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(
                    requireContext(),
                    "Something went wrong. Try Later!!",
                    Toast.LENGTH_LONG
                )
                    .show()
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(true)
            }
        })
        viewModel.cityResult().observeOnce(this, androidx.lifecycle.Observer {
            if (it != null) {
                val adapter =
                    ArrayAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        it
                    )
                val adapter1 =
                    ArrayAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        it
                    )
//                business_city_name.setAdapter(adapter)
            }

        })

        viewModel.getUpdatedResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Created Profile.", Toast.LENGTH_LONG)
                    .show()
//                supply_place_button.visibility = View.GONE
                all_business_button.visibility = View.GONE
                business_supply_place_missing.visibility = View.GONE
                edit_business_details.visibility = View.VISIBLE
                business_supply_place.setTextColor(resources.getColor(R.color.common_text_color))
//                business_supply_place_value.text = it
//                loadCustomerInfo()
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(
                    requireContext(),
                    "Something went wrong. Try Later!!",
                    Toast.LENGTH_LONG
                )
                    .show()
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(true)
            }
        })

        viewModel.getGstSwitchFlag().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                gstFlag = it
                business_gstin.setTextColor(resources.getColor(R.color.common_text_color))
                business_gstin.visibility = View.INVISIBLE
                business_gstin_missing.visibility = View.VISIBLE
            }
        })

        viewModel.getBusinessPopup().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                val businessFragment = BusinessDetailsFragment.newInstance(this)
//                businessFragment.dismiss()
//                businessFragment.fragmentManager?.beginTransaction()?.remove(businessFragment)
                fragmentManager?.beginTransaction()?.remove(businessFragment)
            }
        })


    }

    fun payViaPaymentLink() {

        try {
            val paymentLink =
                "https://www.getboost360.com/subscriptions/" + cartCheckoutData.get("transaction_id") + "/pay-now"
            val emailBody =
                "You can securely pay for your Boost360 subscription (Order #" + cartCheckoutData.get(
                    "transaction_id"
                ) + ") using the link below." +
                        "<br/>The subscription will be activated against the account of " + (activity as PaymentActivity).fpName + ".<br/><br/>Payment Link: " + paymentLink

            var prefs = SharedPrefs(activity as PaymentActivity)
            val emailArrayList = ArrayList<String>()
            emailArrayList.add(paymentData.get("userEmail").toString())
            emailArrayList.add(prefs.getFPEmail())

            /*viewModel.loadPamentUsingExternalLink((activity as PaymentActivity).clientid,
                    PaymentThroughEmailRequestBody((activity as PaymentActivity).clientid,
                            emailBody,
                            "alerts@nowfloats.com",
                            "\uD83D\uDD50 Payment link for your Boost360 Subscription [Order #" + cartCheckoutData.get("transaction_id") + "]",
                            emailArrayList,
                            0
                    ))*/
            viewModel.loadPaymentLinkPriority(
                (activity as? PaymentActivity)?.getAccessToken() ?: "",
                (activity as PaymentActivity).clientid,
                PaymentPriorityEmailRequestBody(
                    (activity as PaymentActivity).clientid,
                    emailBody,
                    "\uD83D\uDD50 Payment link for your Boost360 Subscription [Order #" + cartCheckoutData.get(
                        "transaction_id"
                    ) + "]",
                    emailArrayList,
                )
            )
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
            SentryController.captureException(e)
        }
    }

    fun payThroughRazorPay() {
        try {
            for (key in cartCheckoutData.keys()) {
                if (key != "customerId" && key != "transaction_id") {
                    paymentData.put(key, cartCheckoutData.get(key))
                }
            }
            var firebaseAnalytics = Firebase.analytics
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, null)

            val args = Bundle()
            args.putString("data", paymentData.toString())
            razorPayWebView.arguments = args

            //RazorPay web
            razorPayWebView.show(
                (activity as PaymentActivity).supportFragmentManager,
                RAZORPAY_WEBVIEW_POPUP_FRAGMENT
            )

            paymentData = JSONObject()

        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
            SentryController.captureException(e)
        }
    }

    fun payThroughRazorPayMoreBanks() {
        try {
            for (key in cartCheckoutData.keys()) {
                if (key != "customerId" && key != "transaction_id") {
                    paymentData.put(key, cartCheckoutData.get(key))
                }
            }
            var firebaseAnalytics = Firebase.analytics
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, null)
            val razorPayWebViewBank = RazorPayWebView.newInstance()
            val args = Bundle()
            args.putString("data", paymentData.toString())
            razorPayWebView.arguments = args
            razorPayWebViewBank.arguments = args

            //RazorPay web

            razorPayWebViewBank.show(
                (activity as PaymentActivity).supportFragmentManager,
                RAZORPAY_WEBVIEW_POPUP_FRAGMENT
            )

            paymentData = JSONObject()

        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
            SentryController.captureException(e)
        }
    }

    fun initializeCardRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        card_recycler.apply {
            layoutManager = gridLayoutManager
            card_recycler.adapter = cardPaymentAdapter

        }
    }

    fun initializeNetBankingSelector() {

        Glide.with(requireContext()).load(netbankingList.get(0).bankImage).into(axis_bank_image)
        axis_bank_layout.setOnClickListener {
            Log.v("axis_bank_layout", " " + paymentProceedFlag)
            if (paymentProceedFlag) {
                netbankingSelected(netbankingList.get(0).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }
        }

        Glide.with(requireContext()).load(netbankingList.get(1).bankImage).into(icici_bank_image)
        icici_bank_layout.setOnClickListener {
            if (paymentProceedFlag) {
                netbankingSelected(netbankingList.get(1).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }

        }

        Glide.with(requireContext()).load(netbankingList.get(2).bankImage).into(hdfc_bank_image)
        hdfc_bank_layout.setOnClickListener {
            if (paymentProceedFlag) {
                netbankingSelected(netbankingList.get(2).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }

        }

        Glide.with(requireContext()).load(netbankingList.get(3).bankImage).into(citi_bank_image)
        citi_bank_layout.setOnClickListener {
            if (paymentProceedFlag) {
                netbankingSelected(netbankingList.get(3).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }

        }

        Glide.with(requireContext()).load(netbankingList.get(4).bankImage).into(sbi_bank_image)
        sbi_bank_layout.setOnClickListener {
            if (paymentProceedFlag) {
                netbankingSelected(netbankingList.get(4).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else {
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0, 0)
            }

        }
    }

    fun initializeWalletRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        wallet_recycler.apply {
            layoutManager = gridLayoutManager
            wallet_recycler.adapter = walletAdapter
        }
    }

    fun netbankingSelected(bankCode: String) {
        Log.i("netbankingSelected", bankCode)
        val item = JSONObject()
        item.put("method", "netbanking");
        item.put("bank", bankCode)
        paymentData = item

        WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_NET_BANKING_SELECTED,
            bankCode,
            NO_EVENT_VALUE
        )
        payThroughRazorPay()
    }

    override fun walletSelected(data: String) {
        Log.i("walletSelected", data)
        if (paymentProceedFlag) {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_WALLET_SELECTED, data, NO_EVENT_VALUE)
            val item = JSONObject()
            item.put("method", "wallet");
            item.put("wallet", data);
            paymentData = item
            payThroughRazorPay()
            payment_submit.visibility = View.VISIBLE
        } else {
            payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
            payment_main_layout.smoothScrollTo(0, 0)
        }
    }

    private fun loadWallet(data: JSONObject) {
        val paymentMethods = data.get("wallet") as JSONObject
        val retMap: Map<String, Boolean> = Gson().fromJson(
            paymentMethods.toString(), object : TypeToken<HashMap<String, Boolean>>() {}.type
        )
        val list = ArrayList<String>()
        retMap.map {
            if (it.value) {
                list.add(it.key)
            }
        }
        walletAdapter.addupdates(list)
    }


    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().viewModelStore.clear()
    }

    fun updateSubscriptionDetails() {
        var prefs = SharedPrefs(activity as PaymentActivity)

        //cartOriginalPrice
        val cartOriginalPrice = prefs.getCartOriginalAmount()

        //coupon discount percentage
        val couponDiscountPercentage = prefs.getCouponDiscountPercentage()
        long_validity_discount_title.setText("Long validity discount (" + couponDiscountPercentage.toString() + "%)")
        long_validity_discount_value.setText(
            "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(0)
        )

        //coupon discount amount
        val couponDiscountAmount = cartOriginalPrice * couponDiscountPercentage / 100
        if (couponDiscountAmount.toInt() > 0) {
            coupon_discount_title.setText("‘FESTIVE’ coupon discount")
            coupon_discount_value.setText(
                "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)
            )
            coupon_discount_title.visibility = View.VISIBLE
            coupon_discount_value.visibility = View.VISIBLE
        } else {
            coupon_discount_title.visibility = View.GONE
            coupon_discount_value.visibility = View.GONE
        }

        payment_amount_value.setText(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(totalAmount + couponDiscountAmount)
        )


        validity_months.setText(
            if (prefs.getValidityMonths()!!.toInt() > 1)
                prefs.getValidityMonths() + " Months"
            else
                prefs.getValidityMonths() + " Month"
        )

        auto_renew_title.setText(
            "Automatically renew every " +
                    if (prefs.getValidityMonths()!!.toInt() > 1)
                        prefs.getValidityMonths() + " Months"
                    else
                        prefs.getValidityMonths() + " Month"
                                + "?"
        )

        val oneMonthFromNow = Calendar.getInstance()
        oneMonthFromNow.add(Calendar.MONTH, 1) // Added one month
        val nowFormat = SimpleDateFormat("dd MMM yy")
        nowFormat.setTimeZone(Calendar.getInstance().getTimeZone())
        val oneMonthFormat = SimpleDateFormat("dd MMM yy")
        oneMonthFormat.setTimeZone(oneMonthFromNow.getTimeZone())
        validity_period_value.setText(
            nowFormat.format(Calendar.getInstance().time) + " - " + nowFormat.format(oneMonthFromNow.time)
        )


        auto_renew_description.setText(
            "You are paying ₹" + totalAmount + " only for " +
                    if (prefs.getValidityMonths()!!.toInt() > 1) prefs.getValidityMonths() + " Months"
                    else prefs.getValidityMonths() + " Month"
                                + ". Your subscription will end on " +
                                nowFormat.format(oneMonthFromNow.time)
        )

//    //igsttin value
//    val temp = ((cartOriginalPrice - couponDiscountAmount) * 18) / 100
//    val taxValue = Math.round(temp * 100) / 100.0
//    coupon_discount_title.setText("‘FESTIVE’ coupon discount")
//    coupon_discount_value.setText("+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue))

//    order_total_value.setText(
//      "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount)
//    )
        payment_subscription_details_title.setText(
            "PAYABLE AMOUNT: ₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount)
        )
        payment_total_value.setText(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount)
        )
        items_cost.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
//    paymentBannerAmount.setText(
//      "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount)
//    )
    }

    private fun loadCustomerInfo() {
        viewModel.getCustomerInfo(
            (activity as? PaymentActivity)?.getAccessToken() ?: "",
            (activity as PaymentActivity).fpid!!,
            (activity as PaymentActivity).clientid
        )
    }


    override fun backListener(flag: Boolean) {
        Log.v("backListener", " " + flag)
        loadCustomerInfo()
//       loadData()
    }


    override fun moreBankSelected(data: JSONObject) {
        paymentData = data
        payThroughRazorPay()
    }

    override fun upiSelected(data: JSONObject) {
        Log.i("upiSelected >", data.toString())
        paymentData = data
        payThroughRazorPay()
    }

    override fun emailSelected(data: JSONObject) {
        paymentData = data
        payViaPaymentLink()
    }

    override fun cardSelected(data: JSONObject) {
        paymentData = data
        payThroughRazorPay()
    }


}