package com.boost.payment.ui.popup

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
import com.boost.payment.R
import com.boost.payment.PaymentActivity
import com.boost.payment.adapter.NetBankingPopUpAdaptor
import com.boost.payment.datamodule.SingleNetBankData
import com.boost.payment.interfaces.MoreBanksListener
import com.boost.payment.interfaces.NetBankingListener
import com.boost.payment.ui.payment.PaymentViewModel
import com.boost.payment.utils.WebEngageController
import com.boost.payment.utils.observeOnce
import com.framework.webengageconstant.ADDONS_MARKETPLACE_NET_BANKING_LOADED
import com.framework.webengageconstant.NET_BANKING
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Razorpay
import kotlinx.android.synthetic.main.netbanking_popup.*
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NetBankingPopUpFragement : DialogFragment(), NetBankingListener {

  lateinit var root: View
  private lateinit var viewModel: PaymentViewModel

  val list = ArrayList<SingleNetBankData>()

  lateinit var netBankingPopUpAdaptor: NetBankingPopUpAdaptor
  lateinit var razorpay: Razorpay

  companion object {
    lateinit var listener: MoreBanksListener
    fun newInstance(moreBankListener: MoreBanksListener) = NetBankingPopUpFragement().apply {
      listener = moreBankListener
    }
  }

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
    razorpay = (activity as PaymentActivity).getRazorpayObject()
    netBankingPopUpAdaptor = NetBankingPopUpAdaptor(ArrayList(), this)

    return root

  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

//        loadBanks()
    viewModel.loadMoreBanks(razorpay)
    initMvvm()
    initializeRecycler()


    netbanking_outer_layout.setOnClickListener {
      dialog!!.dismiss()
    }

    WebEngageController.trackEvent(
      ADDONS_MARKETPLACE_NET_BANKING_LOADED,
      NET_BANKING,
      NO_EVENT_VALUE
    )
  }

  private fun initMvvm() {
    viewModel.getPaymentMethods().observeOnce(this, Observer {
      val paymentMethods = it.get("netbanking") as JSONObject
      val retMap: Map<String, String> = Gson().fromJson(
        paymentMethods.toString(), object : TypeToken<HashMap<String, String>>() {}.type
      )
      Log.v("getPaymentMethods", " " + retMap.size)
      retMap.map {
        if (!list.contains(SingleNetBankData(it.key, it.value, null))) {
          Log.d("getPayretMap", " " + list.size)
          list.add(SingleNetBankData(it.key, it.value, null))
        }

      }
      netBankingPopUpAdaptor.addupdates(list)
    })
  }
  /*private fun loadBanks() {
      val paymentMethods = viewModel.getPaymentMethods().get("netbanking") as JSONObject
      val retMap: Map<String, String> = Gson().fromJson(
          paymentMethods.toString(), object : TypeToken<HashMap<String, String>>() {}.type
      )
      retMap.map {
          list.add(SingleNetBankData(it.key,it.value,null))
      }
  }*/

  private fun initializeRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    netbanking_popup_recycler.apply {
      layoutManager = gridLayoutManager
    }
    netbanking_popup_recycler.adapter = netBankingPopUpAdaptor
  }

  override fun popupSelectedBank(v: View) {
    val itemPosition = netbanking_popup_recycler.getChildAdapterPosition(v)
    val selectedBanking = JSONObject()
    selectedBanking.put("method", "netbanking");
    selectedBanking.put("bank", list.get(itemPosition).bankCode);
//        viewModel.UpdateNetBankingData(selectedBanking)
    listener.moreBankSelected(selectedBanking)
    dialog!!.dismiss()
  }

//    override fun onDestroy() {
//        super.onDestroy()
//        requireActivity().viewModelStore.clear()
//    }

}