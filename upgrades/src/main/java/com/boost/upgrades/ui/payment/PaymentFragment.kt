package com.boost.upgrades.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.CardPaymentAdapter
import com.boost.upgrades.adapter.StateListAdapter
import com.boost.upgrades.adapter.UPIAdapter
import com.boost.upgrades.adapter.WalletAdapter
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentPriorityEmailRequestBody
import com.boost.upgrades.data.api_model.customerId.customerInfo.AddressDetails
import com.boost.upgrades.data.api_model.customerId.customerInfo.BusinessDetails
import com.boost.upgrades.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.upgrades.data.api_model.customerId.customerInfo.TaxDetails
import com.boost.upgrades.data.api_model.customerId.get.Result
import com.boost.upgrades.datamodule.SingleNetBankData
import com.boost.upgrades.interfaces.*
import com.boost.upgrades.ui.checkoutkyc.BusinessDetailsFragment
import com.boost.upgrades.ui.confirmation.OrderConfirmationFragment
import com.boost.upgrades.ui.popup.*
import com.boost.upgrades.ui.razorpay.RazorPayWebView
import com.boost.upgrades.ui.webview.WebViewFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.ADD_CARD_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.BUSINESS_DETAILS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.EXTERNAL_EMAIL_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.NETBANKING_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_WEBVIEW_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.STATE_LIST_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.UPI_POPUP_FRAGMENT
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.boost.upgrades.utils.observeOnce
import com.bumptech.glide.Glide
import com.framework.models.firestore.FirestoreManager
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
import kotlinx.android.synthetic.main.checkoutkyc_fragment.*
import kotlinx.android.synthetic.main.payment_fragment.*
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PaymentFragment : BaseFragment(), PaymentListener, BusinessDetailListener,
    MoreBanksListener,UpiPayListener,EmailPopupListener,AddCardListener {

    lateinit var root: View
    private lateinit var viewModel: PaymentViewModel

    lateinit var razorpay: Razorpay

    lateinit var cardPaymentAdapter: CardPaymentAdapter
    lateinit var upiAdapter: UPIAdapter
    lateinit var walletAdapter: WalletAdapter

    val addCardPopUpFragement = AddCardPopUpFragement()
    val netBankingPopUpFragement = NetBankingPopUpFragement()
    val upiPopUpFragement = UPIPopUpFragement()
    val externalEmailPopUpFragement = ExternalEmailPopUpFragement()
    val razorPayWebView = RazorPayWebView()

    var cartCheckoutData = JSONObject()

    var paymentData = JSONObject()

    var netbankingList = arrayListOf<SingleNetBankData>()

    var totalAmount = 0.0

    var createCustomerInfoRequest: Result? = null
    var customerInfoState = false
    var paymentProceedFlag = true
    private var session: UserSessionManager? = null
    val businessDetailsFragment = BusinessDetailsFragment()
    val stateFragment = StateListPopFragment()
    var gstFlag = false
    lateinit var prefs: SharedPrefs

    companion object {
        fun newInstance() = PaymentFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.payment_fragment, container, false)

        totalAmount = requireArguments().getDouble("amount")
        session = UserSessionManager(requireActivity())
        cartCheckoutData.put("customerId", requireArguments().getString("customerId"))
        cartCheckoutData.put("amount", Math.round(totalAmount * 100).toInt())
        cartCheckoutData.put("order_id", requireArguments().getString("order_id"))
        //subscription testing
//        cartCheckoutData.put("amount", 50000)
//        cartCheckoutData.put("subscription_id", "sub_Fj7nfvetEC7C0W")
        cartCheckoutData.put("transaction_id", requireArguments().getString("transaction_id"))
        cartCheckoutData.put("email", requireArguments().getString("email"))
        cartCheckoutData.put("currency", requireArguments().getString("currency"));
        cartCheckoutData.put("contact", requireArguments().getString("contact"))
        prefs = SharedPrefs(activity as UpgradeActivity)
//        //this is a offer created from admin dashboard.
//        cartCheckoutData.put("offer_id", arguments!!.getString("offer_F5hUaalR9tpSzn"))

        razorpay = (activity as UpgradeActivity).getRazorpayObject()

        netbankingList = ArrayList<SingleNetBankData>()
        netbankingList.add(SingleNetBankData("UTIB", "Axis", razorpay.getBankLogoUrl("UTIB")))
        netbankingList.add(SingleNetBankData("ICIC", "ICICI", razorpay.getBankLogoUrl("ICIC")))
        netbankingList.add(SingleNetBankData("HDFC", "HDFC", razorpay.getBankLogoUrl("HDFC")))
        netbankingList.add(SingleNetBankData("CIUB", "City Union", razorpay.getBankLogoUrl("CIUB")))
        netbankingList.add(SingleNetBankData("SBIN", "SBI", razorpay.getBankLogoUrl("SBIN")))

        cardPaymentAdapter = CardPaymentAdapter(requireActivity(), ArrayList())
        upiAdapter = UPIAdapter(ArrayList())
        walletAdapter = WalletAdapter(razorpay, ArrayList(), this)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

        loadData()
        loadCustomerInfo()
        initMvvm()

        initializeCardRecycler()
        initializeNetBankingSelector()
        initializeUPIRecycler()
        initializeWalletRecycler()
        updateSubscriptionDetails()

        WebEngageController.trackEvent(EVENT_NAME_ADDONS_MARKETPLACE_PAYMENT_LOAD, PAGE_VIEW, ADDONS_MARKETPLACE_PAYMENT_SCREEN)

        var firebaseAnalytics = Firebase.analytics
        val revenue = cartCheckoutData.getDouble("amount")
        val bundle = Bundle()
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue / 100)
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)

        back_button.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_CLICKED_BACK_BUTTON_PAYMENTSCREEN, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        payment_submit.setOnClickListener {
            if (paymentData.length() > 0) {
                payThroughRazorPay()
            }
        }

        add_new_card.setOnClickListener {
            if(paymentProceedFlag){
                WebEngageController.trackEvent(ADDONS_MARKETPLACE_ADD_NEW_CARD_CLICK , ADDONS_MARKETPLACE_ADD_NEW_CARD, NO_EVENT_VALUE)
                val args = Bundle()
                args.putString("customerId", cartCheckoutData.getString("customerId"))
//                addCardPopUpFragement.arguments = args
//                addCardPopUpFragement.show((activity as UpgradeActivity).supportFragmentManager, ADD_CARD_POPUP_FRAGMENT)

                val addCardFragement = AddCardPopUpFragement.newInstance(this)
                addCardFragement.arguments = args
                addCardFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    ADD_CARD_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            }else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
//                payment_main_layout.fullScroll(View.FOCUS_FORWARD)
                payment_main_layout.smoothScrollTo(0,0)
            }
        }

        show_more_bank.setOnClickListener {
            if(paymentProceedFlag){
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_SHOW_MORE_BANK_CLICK , ADDONS_MARKETPLACE_SHOW_MORE_BANK, NO_EVENT_VALUE)
            /*netBankingPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    NETBANKING_POPUP_FRAGMENT
            )*/
            val netBankingFragement = NetBankingPopUpFragement.newInstance(this)
            netBankingFragement.show(
                (activity as UpgradeActivity).supportFragmentManager,
                NETBANKING_POPUP_FRAGMENT
            )
            payment_submit.visibility = View.VISIBLE
        }else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
            }
        }

        add_upi_layout.setOnClickListener {
            if(paymentProceedFlag){
                WebEngageController.trackEvent(ADDONS_MARKETPLACE_UPI_CLICK, ADDONS_MARKETPLACE_UPI, NO_EVENT_VALUE)
               /* upiPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    UPI_POPUP_FRAGMENT
                )*/
                val upiFragment = UPIPopUpFragement.newInstance(this)
                upiFragment.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    UPI_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            }else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
            }
        }

        add_external_email.setOnClickListener {
            if(paymentProceedFlag){
                WebEngageController.trackEvent(ADDONS_MARKETPLACE_PAYMENT_LINK_CLICK, ADDONS_MARKETPLACE_PAYMENT_LINK , NO_EVENT_VALUE)
                /*externalEmailPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    EXTERNAL_EMAIL_POPUP_FRAGMENT
                )*/
                val emailPopUpFragement = ExternalEmailPopUpFragement.newInstance(this)
                emailPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    EXTERNAL_EMAIL_POPUP_FRAGMENT
                )
                payment_submit.visibility = View.VISIBLE
            }else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
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
                (activity as UpgradeActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )*/
            val businessFragment = BusinessDetailsFragment.newInstance(this)
            businessFragment.show(
                (activity as UpgradeActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )

        }

        all_business_button.setOnClickListener{
           /* businessDetailsFragment.show(
                (activity as UpgradeActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )*/
            val businessFragment = BusinessDetailsFragment.newInstance(this)
            businessFragment.show(
                (activity as UpgradeActivity).supportFragmentManager,
                BUSINESS_DETAILS_FRAGMENT
            )

        }
        /*supply_place_button.setOnClickListener{
            stateFragment.show(
                (activity as UpgradeActivity).supportFragmentManager,
                STATE_LIST_FRAGMENT
            )
        }*/
        if(!prefs.getGstRegistered()){
            business_gstin.text = "Have a GST number?"
//            business_gstin_missing.text = "Not Registered with GST"
            business_gstin_missing.text = "No"
        }
        WebEngageController.trackEvent( ADDONS_MARKETPLACE_PAYMENT_SCREEN_LOADED, PAYMENT_SCREEN, NO_EVENT_VALUE)
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
                val orderConfirmationFragment = OrderConfirmationFragment.newInstance()
                val args = Bundle()
                args.putString("payment_type", "External_Link")
                orderConfirmationFragment.arguments = args
                (activity as UpgradeActivity).replaceFragment(
                        orderConfirmationFragment,
                        Constants.ORDER_CONFIRMATION_FRAGMENT
                )
            } else {
                Toasty.error(requireContext(), "Unable To Send Link To Email. Try Later...", Toast.LENGTH_SHORT, true).show();
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



                    if(createCustomerInfoRequest!!.BusinessDetails!!.Email != null){
                        business_email_value.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
                    }else if(session?.fPEmail != null || session?.fPEmail.equals("") ){
                        business_email_value.setText(session?.fPEmail)
                    }

                    if(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null){
                        business_mobile_value.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
                    }else if(session?.userPrimaryMobile != null || session?.userPrimaryMobile.equals("")){
                        business_mobile_value.text = session?.userPrimaryMobile
                    }

                if (createCustomerInfoRequest!!.Name != null ) {
                    business_name_value.setText(createCustomerInfoRequest!!.Name)
                }else{
                    if(session?.fPName == null || session?.fPName.equals("") ){
                        business_name_value.visibility = View.INVISIBLE
                        business_name_missing.visibility = View.VISIBLE
                }else{
                    business_name_value.visibility = View.VISIBLE
                    business_name_value.text = session?.fPName
                    if(createCustomerInfoRequest!!.Name == null || createCustomerInfoRequest!!.Name!!.length < 1){
                        paymentProceedFlag = false
                    }
                }

                }

                if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
                    business_address_value.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                }else{
                    if(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null ||
                        session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).equals("") ){
                        business_address_value.visibility = View.INVISIBLE
                        business_address_missing.visibility = View.VISIBLE
                    }else{
                        business_address_value.visibility = View.VISIBLE
                        business_address_value.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
                        if(createCustomerInfoRequest!!.AddressDetails!!.Line1 == null){
                            paymentProceedFlag = false
                        }
                    }


                }

/*ends one*/


                Log.v("createCustomerInfoG", " "+ createCustomerInfoRequest!!.TaxDetails?.GSTIN)
                Log.v("createCustomerInfoA", " "+ createCustomerInfoRequest!!.AddressDetails?.Line1)
                Log.v("createCustomerInfoN", " "+ createCustomerInfoRequest!!.Name)
                Log.v("createCustomerInfoS", " "+ createCustomerInfoRequest!!.AddressDetails!!.State)
                Log.v("createCustomerInfoS", " "+ createCustomerInfoRequest!!.AddressDetails!!.State)
                Log.v("createCustomerInfoE", " "+ session?.fPEmail)
                Log.v("createCustomerInfoE", " "+ session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL))
               /* if (createCustomerInfoRequest!!.Name != null ) {
                    business_name_value.setText(createCustomerInfoRequest!!.Name)
                }else{
                    business_name_value.visibility = View.INVISIBLE
                    business_name_missing.visibility = View.VISIBLE
                }*/

                if (createCustomerInfoRequest!!.TaxDetails?.GSTIN != null /*|| createCustomerInfoRequest!!.TaxDetails?.GSTIN.equals("")*/ ) {
                    business_gstin_value.setText(createCustomerInfoRequest!!.TaxDetails.GSTIN)
                }else{
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

                if(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber == null){
                    business_mobile_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                }else{
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

                if(createCustomerInfoRequest!!.BusinessDetails!!.Email == null){
                    business_email_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                }else{
                        business_email_missing.visibility = View.GONE
                }
                if(createCustomerInfoRequest!!.AddressDetails!!.State == null && createCustomerInfoRequest!!.AddressDetails!!.City == null ){
                    business_supply_place_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                }else{
                    business_supply_place_missing.visibility = View.GONE
                }

                if(createCustomerInfoRequest!!.TaxDetails?.GSTIN == null){
                    business_gstin_missing.visibility = View.VISIBLE
                    paymentProceedFlag = false
                }else{
                    business_gstin_value.visibility = View.VISIBLE
                    business_gstin_missing.visibility = View.GONE
                    business_gstin.text = "GSTIN"
                }

                if(!prefs.getGstRegistered()){
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


                if(createCustomerInfoRequest!!.AddressDetails!!.State == null || createCustomerInfoRequest!!.AddressDetails!!.State.equals("string")){
                    business_supply_place_missing.visibility = View.VISIBLE
                    business_supply_place_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                }else{
                    business_supply_place_missing.visibility = View.GONE
                    business_supply_place_value.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                }

                if(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null &&
                    createCustomerInfoRequest!!.BusinessDetails!!.Email != null  /*&&*/
                    /*createCustomerInfoRequest!!.Name!!.length > 0*/  /*&&*/
//                    createCustomerInfoRequest!!.TaxDetails.GSTIN != null  &&
                    /*createCustomerInfoRequest!!.AddressDetails.Line1.toString() != null*/ /* &&
                    createCustomerInfoRequest!!.AddressDetails.State != null*/     ){
                    paymentProceedFlag = true
                    business_button_layout.visibility = View.GONE
                    business_button_separator.visibility = View.GONE
                    edit_business_details.visibility = View.VISIBLE
                    if(createCustomerInfoRequest!!.Name == null || createCustomerInfoRequest!!.AddressDetails.State == null ||
                        createCustomerInfoRequest!!.AddressDetails.Line1 == null || createCustomerInfoRequest!!.AddressDetails!!.State.equals("string")){
                        paymentProceedFlag = false
                        business_button_layout.visibility = View.VISIBLE
                        all_business_button.visibility = View.VISIBLE
                    }else{
                        paymentProceedFlag = true
                        business_button_layout.visibility = View.GONE
                    }
                }else{
                    business_button_layout.visibility = View.VISIBLE
                    all_business_button.visibility = View.VISIBLE
                    if(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber == null){
                        paymentProceedFlag = false
                        business_mobile.setTextColor(resources.getColor(R.color.global_red))
                        business_mobile_value.visibility = View.INVISIBLE
                        business_mobile_missing.visibility = View.VISIBLE
                    }
                    if(createCustomerInfoRequest!!.BusinessDetails!!.Email == null){
                        paymentProceedFlag = false
                        business_email.setTextColor(resources.getColor(R.color.global_red))
                        business_email_value.visibility = View.INVISIBLE
                        business_email_missing.visibility = View.VISIBLE
                    }
                    if(createCustomerInfoRequest!!.Name!!.length < 1){
                        paymentProceedFlag = false
                        business_name.setTextColor(resources.getColor(R.color.global_red))
                        business_name_value.visibility = View.INVISIBLE
                        business_name_missing.visibility = View.VISIBLE
                    }
                    if(createCustomerInfoRequest!!.AddressDetails.Line1.toString().length < 1){
                        paymentProceedFlag = false
                        business_address.setTextColor(resources.getColor(R.color.global_red))
                        business_address_value.visibility = View.INVISIBLE
                        business_address_missing.visibility = View.VISIBLE
                    }
                    if(createCustomerInfoRequest!!.AddressDetails.State == null || createCustomerInfoRequest!!.AddressDetails!!.State.equals("string") ){
                        paymentProceedFlag = false
                        business_supply_place.setTextColor(resources.getColor(R.color.global_red))
                        business_supply_place_value.visibility = View.INVISIBLE
                        business_supply_place_missing.visibility = View.VISIBLE
                    }
                    if(createCustomerInfoRequest!!.TaxDetails?.GSTIN == null){
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
            if(!customerInfoState){
Log.v("getCustomerInfoM"," "+ session?.fPPrimaryContactNumber)
Log.v("getCustomerInfoE"," "+ session?.fPEmail)
Log.v("getCustomerInfoN"," "+ session?.fPName)
//                if(session?.getFPDetails(Key_Preferences.PRIMARY_NUMBER) == null || session?.getFPDetails(Key_Preferences.PRIMARY_NUMBER).equals("")){
                if(session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")){
                    business_mobile_missing.visibility = View.VISIBLE
                    business_mobile_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                }else{
                    business_mobile_value.visibility = View.VISIBLE
                    business_mobile_value.text = session?.userPrimaryMobile
                }

//                if(session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL) == null || session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL).equals("") ){
                if(session?.fPEmail == null || session?.fPEmail.equals("") ){
                    business_email_missing.visibility = View.VISIBLE
                    business_email_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                }else{
                    business_email_value.visibility = View.VISIBLE
                    business_email_value.text = session?.fPEmail
                }

                if(session?.fPName == null || session?.fPName.equals("") ){
                    business_name_missing.visibility = View.VISIBLE
                    business_name_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                }else{
                    business_name_value.visibility = View.VISIBLE
                    business_name_value.text = session?.fPName
                }

                if(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS).equals("") ){
                    business_address_missing.visibility = View.VISIBLE
                    business_address_value.visibility = View.INVISIBLE
                    paymentProceedFlag = false
                }else{
                    business_address_value.visibility = View.VISIBLE
                    business_address_value.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
                }

                business_gstin.setTextColor(resources.getColor(R.color.global_red))
                business_gstin_value.visibility = View.INVISIBLE
                business_gstin_missing.visibility = View.VISIBLE

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
                Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG).show()
                loadCustomerInfo()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG).show()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
            }
        })
        viewModel.cityResult().observeOnce(this, androidx.lifecycle.Observer {
            if(it != null){
                val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
                val adapter1 = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
//                business_city_name.setAdapter(adapter)
            }

        })

        viewModel.getUpdatedResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Created Profile.", Toast.LENGTH_LONG).show()
//                supply_place_button.visibility = View.GONE
                all_business_button.visibility = View.GONE
                business_supply_place_missing.visibility = View.GONE
                edit_business_details.visibility = View.VISIBLE
//                business_supply_place_value.text = it
                loadCustomerInfo()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG).show()
//                (activity as UpgradeActivity).prefs.storeInitialLoadMarketPlace(true)
            }
        })

        viewModel.getGstSwitchFlag().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(!it){
                gstFlag = it
                business_gstin.setTextColor(resources.getColor(R.color.common_text_color))
                business_gstin.visibility = View.INVISIBLE
                business_gstin_missing.visibility = View.VISIBLE
            }
        })

        viewModel.getBusinessPopup().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                val businessFragment = BusinessDetailsFragment.newInstance(this)
//                businessFragment.dismiss()
//                businessFragment.fragmentManager?.beginTransaction()?.remove(businessFragment)
                fragmentManager?.beginTransaction()?.remove(businessFragment)
            }
        })
    }

    fun payViaPaymentLink() {

        try {
            val paymentLink = "https://www.getboost360.com/subscriptions/" + cartCheckoutData.get("transaction_id") + "/pay-now"
            val emailBody = "You can securely pay for your Boost360 subscription (Order #" + cartCheckoutData.get("transaction_id") + ") using the link below." +
                    "<br/>The subscription will be activated against the account of " + (activity as UpgradeActivity).fpName + ".<br/><br/>Payment Link: " + paymentLink

            var prefs = SharedPrefs(activity as UpgradeActivity)
            val emailArrayList = ArrayList<String>()
            emailArrayList.add(paymentData.get("userEmail").toString())
            emailArrayList.add(prefs.getFPEmail())

            /*viewModel.loadPamentUsingExternalLink((activity as UpgradeActivity).clientid,
                    PaymentThroughEmailRequestBody((activity as UpgradeActivity).clientid,
                            emailBody,
                            "alerts@nowfloats.com",
                            "\uD83D\uDD50 Payment link for your Boost360 Subscription [Order #" + cartCheckoutData.get("transaction_id") + "]",
                            emailArrayList,
                            0
                    ))*/
            viewModel.loadPaymentLinkPriority((activity as UpgradeActivity).clientid,
                    PaymentPriorityEmailRequestBody((activity as UpgradeActivity).clientid,
                            emailBody,
                            "\uD83D\uDD50 Payment link for your Boost360 Subscription [Order #" + cartCheckoutData.get("transaction_id") + "]",
                            emailArrayList,
                    ))
        } catch (e: Exception) {
            e.printStackTrace()
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
            razorPayWebView.show((activity as UpgradeActivity).supportFragmentManager, RAZORPAY_WEBVIEW_POPUP_FRAGMENT)

            paymentData = JSONObject()

        } catch (e: Exception) {
            e.printStackTrace()
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

            razorPayWebViewBank.show((activity as UpgradeActivity).supportFragmentManager, RAZORPAY_WEBVIEW_POPUP_FRAGMENT)

            paymentData = JSONObject()

        } catch (e: Exception) {
            e.printStackTrace()
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
            Log.v("axis_bank_layout"," "+ paymentProceedFlag )
            if(paymentProceedFlag){
                netbankingSelected(netbankingList.get(0).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
            }
        }

        Glide.with(requireContext()).load(netbankingList.get(1).bankImage).into(icici_bank_image)
        icici_bank_layout.setOnClickListener {
            if(paymentProceedFlag){
                netbankingSelected(netbankingList.get(1).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
            }

        }

        Glide.with(requireContext()).load(netbankingList.get(2).bankImage).into(hdfc_bank_image)
        hdfc_bank_layout.setOnClickListener {
            if(paymentProceedFlag){
                netbankingSelected(netbankingList.get(2).bankCode)
                payment_submit.visibility = View.VISIBLE
            }else{
            payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
        }

        }

        Glide.with(requireContext()).load(netbankingList.get(3).bankImage).into(citi_bank_image)
        citi_bank_layout.setOnClickListener {
            if(paymentProceedFlag){
                netbankingSelected(netbankingList.get(3).bankCode)
                payment_submit.visibility = View.VISIBLE
            } else{
            payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
        }

        }

        Glide.with(requireContext()).load(netbankingList.get(4).bankImage).into(sbi_bank_image)
        sbi_bank_layout.setOnClickListener {
            if(paymentProceedFlag){
                netbankingSelected(netbankingList.get(4).bankCode)
                payment_submit.visibility = View.VISIBLE
            }else{
                payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
                payment_main_layout.smoothScrollTo(0,0)
            }

        }


    }

    fun initializeUPIRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        upi_recycler.apply {
            layoutManager = gridLayoutManager
            upi_recycler.adapter = upiAdapter
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

        WebEngageController.trackEvent(ADDONS_MARKETPLACE_NET_BANKING_SELECTED, bankCode, NO_EVENT_VALUE)
        payThroughRazorPay()
    }

    override fun walletSelected(data: String) {
        Log.i("walletSelected", data)
        if(paymentProceedFlag){
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_WALLET_SELECTED, data, NO_EVENT_VALUE)
            val item = JSONObject()
            item.put("method", "wallet");
            item.put("wallet", data);
            paymentData = item
            payThroughRazorPay()
            payment_submit.visibility = View.VISIBLE
        }else{
            payment_business_details_layout.setBackgroundResource(R.drawable.all_side_curve_bg_payment)
            payment_main_layout.smoothScrollTo(0,0)
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
        var prefs = SharedPrefs(activity as UpgradeActivity)

        //cartOriginalPrice
        val cartOriginalPrice = prefs.getCartOriginalAmount()
        payment_amount_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(cartOriginalPrice))

        //coupon discount percentage
        val couponDiscountPercentage = prefs.getCouponDiscountPercentage()
        coupon_discount_title.setText("Coupon discount(" + couponDiscountPercentage.toString() + "%)")

        //coupon discount amount
        val couponDiscountAmount = cartOriginalPrice * couponDiscountPercentage / 100
        coupon_discount_value.setText("-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount))

        //igsttin value
        val temp = ((cartOriginalPrice - couponDiscountAmount) * 18) / 100
        val taxValue = Math.round(temp * 100) / 100.0
        igst_value.setText("+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue))

        order_total_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
        payment_total_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
        items_cost.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
        paymentBannerAmount.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
    }

    private fun loadCustomerInfo() {
        viewModel.getCustomerInfo((activity as UpgradeActivity).fpid!!, (activity as UpgradeActivity).clientid)
    }

    override fun backListener(flag: Boolean) {
        Log.v("backListener", " "+ flag)
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
