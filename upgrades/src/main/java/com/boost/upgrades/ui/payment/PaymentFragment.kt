package com.boost.upgrades.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.CardPaymentAdapter
import com.boost.upgrades.adapter.UPIAdapter
import com.boost.upgrades.adapter.WalletAdapter
import com.boost.upgrades.data.api_model.PaymentThroughEmail.PaymentPriorityEmailRequestBody
import com.boost.upgrades.datamodule.SingleNetBankData
import com.boost.upgrades.interfaces.PaymentListener
import com.boost.upgrades.ui.confirmation.OrderConfirmationFragment
import com.boost.upgrades.ui.popup.AddCardPopUpFragement
import com.boost.upgrades.ui.popup.ExternalEmailPopUpFragement
import com.boost.upgrades.ui.popup.NetBankingPopUpFragement
import com.boost.upgrades.ui.popup.UPIPopUpFragement
import com.boost.upgrades.ui.razorpay.RazorPayWebView
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.ADD_CARD_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.EXTERNAL_EMAIL_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.NETBANKING_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_WEBVIEW_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.UPI_POPUP_FRAGMENT
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.bumptech.glide.Glide
import com.framework.webengageconstant.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.payment_fragment.*
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PaymentFragment : BaseFragment(), PaymentListener {

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

    companion object {
        fun newInstance() = PaymentFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.payment_fragment, container, false)

        totalAmount = arguments!!.getDouble("amount")

        cartCheckoutData.put("customerId", arguments!!.getString("customerId"))
        cartCheckoutData.put("amount", Math.round(totalAmount * 100).toInt())
        cartCheckoutData.put("order_id", arguments!!.getString("order_id"))
        //subscription testing
//        cartCheckoutData.put("amount", 50000)
//        cartCheckoutData.put("subscription_id", "sub_Fj7nfvetEC7C0W")
        cartCheckoutData.put("transaction_id", arguments!!.getString("transaction_id"))
        cartCheckoutData.put("email", arguments!!.getString("email"))
        cartCheckoutData.put("currency", arguments!!.getString("currency"));
        cartCheckoutData.put("contact", arguments!!.getString("contact"))

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
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_ADD_NEW_CARD_CLICK , ADDONS_MARKETPLACE_ADD_NEW_CARD, NO_EVENT_VALUE)
            val args = Bundle()
            args.putString("customerId", cartCheckoutData.getString("customerId"))
            addCardPopUpFragement.arguments = args
            addCardPopUpFragement.show((activity as UpgradeActivity).supportFragmentManager, ADD_CARD_POPUP_FRAGMENT)
        }

        show_more_bank.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_SHOW_MORE_BANK_CLICK , ADDONS_MARKETPLACE_SHOW_MORE_BANK, NO_EVENT_VALUE)
            netBankingPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    NETBANKING_POPUP_FRAGMENT
            )
        }

        add_upi_layout.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_UPI_CLICK, ADDONS_MARKETPLACE_UPI, NO_EVENT_VALUE)
            upiPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    UPI_POPUP_FRAGMENT
            )
        }

        add_external_email.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_PAYMENT_LINK_CLICK, ADDONS_MARKETPLACE_PAYMENT_LINK , NO_EVENT_VALUE)
            externalEmailPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    EXTERNAL_EMAIL_POPUP_FRAGMENT
            )
        }

        payment_view_details.setOnClickListener {
            payment_main_layout.post {
                payment_main_layout.fullScroll(View.FOCUS_DOWN)
            }
        }

        coupon_discount_value.setOnClickListener {

        }


//        paypalLayout.setOnClickListener{
//            (activity as UpgradeActivity).addFragment(ThanksFragment.newInstance(),THANKS_FRAGMENT)
////            val intent = Intent(this, Thankyou::class.java)
////            startActivity(intent)
//
//        }

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
            netbankingSelected(netbankingList.get(0).bankCode)
        }

        Glide.with(requireContext()).load(netbankingList.get(1).bankImage).into(icici_bank_image)
        icici_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(1).bankCode)
        }

        Glide.with(requireContext()).load(netbankingList.get(2).bankImage).into(hdfc_bank_image)
        hdfc_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(2).bankCode)
        }

        Glide.with(requireContext()).load(netbankingList.get(3).bankImage).into(citi_bank_image)
        citi_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(3).bankCode)
        }

        Glide.with(requireContext()).load(netbankingList.get(4).bankImage).into(sbi_bank_image)
        sbi_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(4).bankCode)
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
        WebEngageController.trackEvent(ADDONS_MARKETPLACE_WALLET_SELECTED, data, NO_EVENT_VALUE)
        val item = JSONObject()
        item.put("method", "wallet");
        item.put("wallet", data);
        paymentData = item
        payThroughRazorPay()
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
    }

}
