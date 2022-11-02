package com.boost.cart.ui.historydetails

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.R
import com.boost.cart.adapter.HistoryDetailsAdapter
import com.boost.cart.base_class.BaseFragment

import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.dbcenterapi.upgradeDB.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.history_details_fragment.*
import java.lang.Long
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryDetailsFragment : BaseFragment() {

  lateinit var root: View
  lateinit var historyDetailsAdapter: HistoryDetailsAdapter

  lateinit var data: Result

  companion object {
    fun newInstance() = HistoryDetailsFragment()
  }

  private lateinit var viewModel: HistoryDetailsViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.history_details_fragment, container, false)

    data = Gson().fromJson(arguments!!.getString("data"), Result::class.java)

    historyDetailsAdapter = HistoryDetailsAdapter(ArrayList())
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(HistoryDetailsViewModel::class.java)

    loadData()
    initMvvm()
    initRecyclerView()

    view_dummy1.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    view_dummy2.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    if (data.orderId != null) {
      layout1_order_id.setText("#" + data.orderId!!.replace("order_", ""))
    } else {
      layout1_order_id.setText("####")
    }
    val inputDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val inputTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputDate = SimpleDateFormat("dd-MMM-yyyy")
    val outputTime = SimpleDateFormat("hh:mm a")

    /*var date: Date? = null
    var time: Date? = null
    try {
        date = inputDate.parse(data.CreatedOn)
        time = inputTime.parse(data.CreatedOn)

        layout1_date.setText(outputDate.format(date))
        layout1_time.setText(outputTime.format(time))
    } catch (e: ParseException) {
        e.printStackTrace()
    }*/

    val dataString = data.CreatedOn
    val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("hh:mm a")
    layout1_date.setText(dateFormat.format(date))
    layout1_time.setText(timeFormat.format(date))
    val amountLayout1 = StringBuilder()
    amountLayout1.append(
      "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(data.paidAmount)
    )
    if (data.purchasedPackageDetails.WidgetPacks.size > 1) {
      amountLayout1.append("(" + data.purchasedPackageDetails.WidgetPacks.size + "items)")
    }
    history_details_selling_price.setText(amountLayout1)
    val mrpPrice =  data.baseAmount + data.taxAmount //(data.paidAmount * (100 / (100 - data.discount)))
    history_details_MRPPrice.setText(
      "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(mrpPrice)
    )
//    val discountAmount = mrpPrice - data.paidAmount
    history_details_discount_amount.setText(
      "- ₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(data.discount)//discountAmount)
    )
    if (data.PaymentMethod != null) {
      history_details_payment_type.setText("Payment via " + data.PaymentMethod)
    } else {
      history_details_payment_type.visibility = View.GONE
    }

    if (data.paymentTransactionId != null) {
      history_details_transaction_id.setText(
        "Transaction ID: #" + data.paymentTransactionId.replace(
          "pay_",
          ""
        )
      )
    } else {
      history_details_transaction_id.visibility = View.GONE
    }

    history_details_back.setOnClickListener {
      (activity as CartActivity).popFragmentFromBackStack()
    }


  }

  @SuppressLint("FragmentLiveDataObserve")
  private fun initMvvm() {
    viewModel.getResult().observe(this, androidx.lifecycle.Observer {
      val list = ArrayList<FeaturesModel>()
      list.addAll(it)
      for (item in data.purchasedPackageDetails.WidgetPacks) {
        var status = true
        for (singleItem in list) {
          if (singleItem.boost_widget_key == item.WidgetKey) {
            status = false
          }
        }
        if (status) {
          list.add(
            FeaturesModel(
              feature_id = "",
              boost_widget_key = item.WidgetKey,
              name = item.Name,
              discount_percent = item.Discount,
              price = item.Price
            )
          )
        }
      }
      history_title_count.setText("Items Included (" + list.size + ")")
      updateRecyclerView(list)
    })
  }

  private fun loadData() {
    val list = ArrayList<String>()
    for (item in data.purchasedPackageDetails.WidgetPacks) {
      list.add(item.WidgetKey)
    }
    viewModel.getDetailsOfWidgets(list)
  }

  private fun updateRecyclerView(list: List<FeaturesModel>) {
    historyDetailsAdapter.addupdates(list)
    history_single_item_recycler.adapter = historyDetailsAdapter
    historyDetailsAdapter.notifyDataSetChanged()
  }

  private fun initRecyclerView() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    history_single_item_recycler.apply {
      layoutManager = gridLayoutManager
      history_single_item_recycler.adapter = historyDetailsAdapter

    }
  }

}
