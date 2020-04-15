package com.boost.upgrades.ui.historydetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.HistoryDetailsAdapter
import com.boost.upgrades.data.api_model.GetPurchaseOrder.Result
import com.google.gson.Gson
import kotlinx.android.synthetic.main.history_details_fragment.*
import java.lang.Long
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.history_details_fragment, container, false)

        data = Gson().fromJson(arguments!!.getString("data"), Result::class.java)

        historyDetailsAdapter = HistoryDetailsAdapter(ArrayList())
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(HistoryDetailsViewModel::class.java)

        initRecyclerView()
        updateRecyclerView()

        if (data.orderId != null) {
            history_details_header.setText("ID #" + data.orderId!!.replace("order_", ""))
        }
        val dataString = data.CreatedOn
        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val timeFormat = SimpleDateFormat("hh:mm a")
        layout1_date.setText(dateFormat.format(date))
        layout1_time.setText(timeFormat.format(date))
        val amountLayout1 = StringBuilder()
        amountLayout1.append("₹" + data.paidAmount)
        if (data.purchasedPackageDetails.WidgetPacks.size > 1) {
            amountLayout1.append("(" + data.purchasedPackageDetails.WidgetPacks.size + "items)")
        }
        layout1_total.setText(amountLayout1)
        history_details_MRPPrice.setText("NULL")
        history_details_selling_price.setText("₹" + data.paidAmount)
        history_details_total.setText("₹" + data.paidAmount)
        if (data.PaymentMethod != null) {
            history_details_payment_type.setText(data.PaymentMethod + " ₹" + data.paidAmount)
        }

        history_details_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }


    }

    private fun updateRecyclerView() {
        historyDetailsAdapter.addupdates(data.purchasedPackageDetails.WidgetPacks)
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
