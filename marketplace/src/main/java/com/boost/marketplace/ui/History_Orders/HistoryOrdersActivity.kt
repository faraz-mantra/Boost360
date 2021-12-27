package com.boost.marketplace.ui.History_Orders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.ui.Invoice.InvoiceActivity
import com.boost.marketplace.ui.Marketplace_Offers.MarketPlaceOffersViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.google.gson.Gson
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.activity_history_orders.*
import kotlinx.android.synthetic.main.item_myplan_features.*
import kotlinx.android.synthetic.main.item_myplan_features.view.*
import kotlinx.android.synthetic.main.item_myplan_features.view.title
import kotlinx.android.synthetic.main.item_order_history.view.*

class HistoryOrdersActivity: AppBaseActivity<ActivityHistoryOrdersBinding, HisoryOrdersViewModel>() {

//    lateinit var historyAdapter: AppBaseRecyclerViewAdapter<Result>
//
//    var fpid: String? = null
//    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

    override fun getLayout(): Int {
        return R.layout.activity_history_orders
    }


    override fun getViewModelClass(): Class<HisoryOrdersViewModel> {
        return HisoryOrdersViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()

//        loadData()
//       initMVVM()
//        initRecyclerView()
//       shimmer_view_history.startShimmer()
//       binding?.addonsBack?.setOnClickListener {
//
//        }

        val listData = listOf(
            "Daily Stories",
            "Custom Domain", "Daily Stories", "Daily Stories",
            "Email Accounts",
        )

        binding?.orderHistoryRecycler?.setupAdapter<String>(R.layout.item_order_history) { adapter, context, list ->
            bind { itemView, position, item ->
                itemView.title.text = item
                itemView.btn_invoice.setOnClickListener {
                    val appInfo: Intent = Intent(
                        this@HistoryOrdersActivity,
                        InvoiceActivity::class.java
                    )
                    startActivity(appInfo)

                }
            }

            submitList(listData)

        }
   }
//    private fun loadData() {
//        viewModel.loadPurchasedItems(
//            (this).getAccessToken() ?:"",
//            (this).fpid!!,
//            (this).clientid
//        )
//    }
//    fun getAccessToken(): String {
//        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken()?:""
//    }
//
//    private fun initMVVM() {
//        viewModel.updateResult().observe(this, Observer {
//            updateRecycler(it)
//        })
//        viewModel.updatesLoader().observe(this, Observer {
//            if (!it) {
//                if (shimmer_view_history.isShimmerStarted) {
//                    shimmer_view_history.stopShimmer()
//                    shimmer_view_history.visibility = View.GONE
//                }
//            }
//        })
//    }
//
//    fun updateRecycler(result: GetPurchaseOrderResponse) {
//        if (result.StatusCode == 200 && result.Result != null) {
//            if (binding?.shimmerViewHistory?.isShimmerStarted!!) {
//                shimmer_view_history.stopShimmer()
//                shimmer_view_history.visibility = View.GONE
//            }
//            historyAdapter.addupdates(result.Result)
//            historyAdapter.notifyDataSetChanged()
//            binding?.orderHistoryRecycler?.setFocusable(false)
//            binding?.orderHistoryRecycler?.visibility = View.VISIBLE
//           binding?.emptyHistory?.visibility = View.GONE
//        } else {
//            if (shimmer_view_history.isShimmerStarted) {
//                shimmer_view_history.stopShimmer()
//                shimmer_view_history.visibility = View.GONE
//            }
//            binding?.orderHistoryRecycler?.visibility = View.GONE
//            binding?.emptyHistory?.visibility= View.VISIBLE
//        }
//    }
//
//    fun initRecyclerView() {
//        val gridLayoutManager = GridLayoutManager(this, 1)
//        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        binding?.orderHistoryRecycler?.apply {
//            layoutManager = gridLayoutManager
//            binding?.orderHistoryRecycler?.adapter = historyAdapter
//
//        }
//    }

}
