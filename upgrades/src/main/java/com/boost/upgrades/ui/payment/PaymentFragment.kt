package com.boost.upgrades.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.boost.upgrades.datamodule.SingleNetBankData
import com.boost.upgrades.interfaces.PaymentListener
import com.boost.upgrades.ui.popup.AddCardPopUpFragement
import com.boost.upgrades.ui.popup.NetBankingPopUpFragement
import com.boost.upgrades.ui.popup.UPIPopUpFragement
import com.boost.upgrades.ui.razorpay.RazorPayWebView
import com.boost.upgrades.utils.Constants.Companion.ADD_CARD_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.NETBANKING_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_WEBVIEW_POPUP_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.UPI_POPUP_FRAGMENT
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Razorpay
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

        WebEngageController.trackEvent("ADDONS_MARKETPLACE PaymentScreen Initialised", "ADDONS_MARKETPLACE PaymentScreen", "")

        back_button.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        payment_submit.setOnClickListener {
            if (paymentData.length() > 0) {
                payThroughRazorPay()
            }
        }

        add_new_card.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE ADD_NEW_CARD Click", "ADDONS_MARKETPLACE ADD_NEW_CARD", "")
            val args = Bundle()
            args.putString("customerId", cartCheckoutData.getString("customerId"))
            addCardPopUpFragement.arguments = args
            addCardPopUpFragement.show((activity as UpgradeActivity).supportFragmentManager, ADD_CARD_POPUP_FRAGMENT)
        }

        show_more_bank.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE SHOW_MORE_BANK Click", "ADDONS_MARKETPLACE SHOW_MORE_BANK", "")
            netBankingPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    NETBANKING_POPUP_FRAGMENT
            )
        }

        add_upi_layout.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE UPI Click", "ADDONS_MARKETPLACE UPI", "")
            upiPopUpFragement.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    UPI_POPUP_FRAGMENT
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

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Payment_Screen Loaded", "Payment_Screen", "")
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
        viewModel.walletPaymentData().observe(this, Observer {
            Log.i("walletPaymentObserver >", it.toString())
            loadWallet(it)
        })
    }

    fun payThroughRazorPay() {
        try {
            for (key in cartCheckoutData.keys()) {
                if (key != "customerId") {
                    paymentData.put(key, cartCheckoutData.get(key))
                }
            }

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

        WebEngageController.trackEvent("ADDONS_MARKETPLACE NET_BANKING Selected", bankCode, "")
        payThroughRazorPay()
    }

    override fun walletSelected(data: String) {
        Log.i("walletSelected", data)
        WebEngageController.trackEvent("ADDONS_MARKETPLACE WALLET Selected", data, "")
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
        requireActivity().viewModelStore.clear()
    }

    fun updateSubscriptionDetails(){
        var prefs = SharedPrefs(activity as UpgradeActivity)

        //cartOriginalPrice
        val cartOriginalPrice = prefs.getCartOriginalAmount()
        payment_amount_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(cartOriginalPrice))

        //coupon discount percentage
        val couponDiscountPercentage = prefs.getCouponDiscountPercentage()
        coupon_discount_title.setText("Coupon discount(" + couponDiscountPercentage.toString() + "%)")

        //coupon discount amount
        val couponDiscountAmount = cartOriginalPrice * couponDiscountPercentage / 100
        coupon_discount_value.setText("-₹"+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount))

        //igsttin value
        val temp = ((cartOriginalPrice-couponDiscountAmount) * 18) / 100
        val taxValue = Math.round(temp * 100) / 100.0
        igst_value.setText("+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue))

        order_total_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
        payment_total_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
        items_cost.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount))
    }

}
