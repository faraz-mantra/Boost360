package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.upgrades.R
import com.boost.upgrades.adapter.NetBankingPopUpAdaptor
import com.boost.upgrades.datamodule.SingleNetBankData
import com.boost.upgrades.interfaces.NetBankingListener
import com.boost.upgrades.ui.payment.PaymentViewModel
import com.boost.upgrades.utils.WebEngageController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_NET_BANKING_LOADED
import com.framework.webengageconstant.NET_BANKING
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.netbanking_popup.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NetBankingPopUpFragement: DialogFragment(), NetBankingListener {

    lateinit var root: View
    private lateinit var viewModel: PaymentViewModel

    val list = ArrayList<SingleNetBankData>()

    lateinit var netBankingPopUpAdaptor: NetBankingPopUpAdaptor

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.netbanking_popup, container, false)

        netBankingPopUpAdaptor = NetBankingPopUpAdaptor(list, this)

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

        loadBanks()
        initializeRecycler()

        netbanking_outer_layout.setOnClickListener {
            dialog!!.dismiss()
        }

        WebEngageController.trackEvent(ADDONS_MARKETPLACE_NET_BANKING_LOADED, NET_BANKING, NO_EVENT_VALUE)
    }

    private fun loadBanks() {
        val paymentMethods = viewModel.getPaymentMethods().get("netbanking") as JSONObject
        val retMap: Map<String, String> = Gson().fromJson(
            paymentMethods.toString(), object : TypeToken<HashMap<String, String>>() {}.type
        )
        retMap.map {
            list.add(SingleNetBankData(it.key,it.value,null))
        }
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        netbanking_popup_recycler.apply {
            layoutManager=gridLayoutManager
        }
        netbanking_popup_recycler.adapter = netBankingPopUpAdaptor
    }

    override fun popupSelectedBank(v: View) {
        val itemPosition = netbanking_popup_recycler.getChildAdapterPosition(v)
        val selectedBanking = JSONObject()
        selectedBanking.put("method", "netbanking");
        selectedBanking.put("bank", list.get(itemPosition).bankCode);
        viewModel.UpdateNetBankingData(selectedBanking)
        dialog!!.dismiss()
    }


}