package com.boost.upgrades.ui.payment

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Razorpay
import kotlinx.android.synthetic.main.payment_fragment.*
import org.json.JSONObject


class PaymentFragment : BaseFragment(), PaymentListener  {

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
        cartCheckoutData.put("amount", totalAmount * 100)
        cartCheckoutData.put("order_id", arguments!!.getString("order_id"))
//        cartCheckoutData.put("amount", 211*100)
//        cartCheckoutData.put("order_id", "order_ETcdJ4Cuh9yIQq")
        cartCheckoutData.put("email", arguments!!.getString("email"))
        cartCheckoutData.put("currency", arguments!!.getString("currency"));
        cartCheckoutData.put("contact", arguments!!.getString("contact"))

        razorpay = (activity as UpgradeActivity).getRazorpayObject()

        netbankingList = ArrayList<SingleNetBankData>()
        netbankingList.add(SingleNetBankData("UTIB", "Axis",R.drawable.axis_bank))
        netbankingList.add(SingleNetBankData("ICIC", "ICICI",R.drawable.icici_bank))
        netbankingList.add(SingleNetBankData("HDFC", "HDFC",R.drawable.hdfc_bank))
        netbankingList.add(SingleNetBankData("CIUB", "City Union",R.drawable.citi_bank))
        netbankingList.add(SingleNetBankData("SBIN", "SBI",R.drawable.sbi_bank))


        cardPaymentAdapter = CardPaymentAdapter(requireActivity(), ArrayList())
        upiAdapter = UPIAdapter(ArrayList())
        walletAdapter = WalletAdapter(ArrayList(), this)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

        initMvvm()

        initializeCardRecycler()
        initializeNetBankingSelector()
        initializeUPIRecycler()
        initializeWalletRecycler()

        payment_amount_value.setText("₹"+totalAmount)
        order_total_value.setText("₹"+totalAmount)
        payment_total_value.setText("₹"+totalAmount)
        items_cost.setText("₹"+totalAmount)

        back_button.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        payment_submit.setOnClickListener {
            if(paymentData.length() > 0) {
                payThroughRazorPay()
            }
        }

        add_new_card.setOnClickListener {
            val args = Bundle()
            args.putString("customerId", cartCheckoutData.getString("customerId"))
            addCardPopUpFragement.arguments = args
            addCardPopUpFragement.show((activity as UpgradeActivity).supportFragmentManager, ADD_CARD_POPUP_FRAGMENT)
        }

        show_more_bank.setOnClickListener {
            netBankingPopUpFragement.show(
                (activity as UpgradeActivity).supportFragmentManager,
                NETBANKING_POPUP_FRAGMENT
            )
        }

        add_upi_layout.setOnClickListener {
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
    }

    fun initMvvm(){
        viewModel.loadpaymentMethods(razorpay)
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

    fun payThroughRazorPay(){
        try {
            for (key in cartCheckoutData.keys()) {
                if(key != "customerId") {
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

        axis_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(0).bankCode)
        }

        icici_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(1).bankCode)
        }

        hdfc_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(2).bankCode)
        }

        citi_bank_layout.setOnClickListener {
            netbankingSelected(netbankingList.get(3).bankCode)
        }

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
        payThroughRazorPay()
    }

    override fun walletSelected(data: String) {
        Log.i("walletSelected", data)
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
            if(it.value){
                list.add(it.key)
            }
        }
        walletAdapter.addupdates(list)
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().viewModelStore.clear()
    }

}
