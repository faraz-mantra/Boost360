package com.boost.marketplace.ui.History_Orders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.marketplace.Adapters.HistoryOrdersParentAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.interfaces.HistoryFragmentListener
import com.boost.marketplace.ui.Invoice.InvoiceActivity
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.google.gson.Gson

import kotlinx.android.synthetic.main.item_myplan_features.view.title


class HistoryOrdersActivity: AppBaseActivity<ActivityHistoryOrdersBinding, HisoryOrdersViewModel>(),
    HistoryFragmentListener,
    RecyclerItemClickListener {


    lateinit var historyAdapter: HistoryOrdersParentAdapter
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

    companion object {
        fun newInstance() = HistoryOrdersActivity()
    }

   // private lateinit var viewModel: HisoryOrdersViewModel


    override fun getLayout(): Int {
        return R.layout.activity_history_orders
    }


    override fun getViewModelClass(): Class<HisoryOrdersViewModel> {
        return HisoryOrdersViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()


        historyAdapter = HistoryOrdersParentAdapter(ArrayList(), this)
        viewModel = ViewModelProviders.of(this).get(HisoryOrdersViewModel::class.java)

        loadData()
        initMVVM()
        initRecyclerView()
        binding?.shimmerViewHistory?.startShimmer()
        binding?.addonsBack?.setOnClickListener {

        }

    }

        private fun loadData() {
        viewModel.loadPurchasedItems(
            (this).getAccessToken() ?:"",
            intent.getStringExtra("fpid")?:"" ,
            (this).clientid
        )
    }
    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }

    private fun initMVVM() {
        viewModel.updateResult().observe(this, Observer {
            updateRecycler(it)
        })
        viewModel.updatesLoader().observe(this, Observer {
            if (!it) {
                if (binding?.shimmerViewHistory!!.isShimmerStarted) {
                    binding?.shimmerViewHistory!!.stopShimmer()
                    binding?.shimmerViewHistory!!.visibility = View.GONE
                }
            }
        })
    }

    fun updateRecycler(result: GetPurchaseOrderResponse) {
        if (result.StatusCode == 200 && result.Result != null) {
            if (binding?.shimmerViewHistory?.isShimmerStarted!!) {
                binding?.shimmerViewHistory!!.stopShimmer()
                binding?.shimmerViewHistory!!.visibility = View.GONE
            }
            historyAdapter?.addupdates(result.Result)
            historyAdapter?.notifyDataSetChanged()
            binding?.orderHistoryRecycler?.setFocusable(false)
            binding?.orderHistoryRecycler?.visibility = View.VISIBLE
            binding?.emptyHistory?.visibility = View.GONE
        } else {
            if (binding?.shimmerViewHistory!!.isShimmerStarted) {
                binding?.shimmerViewHistory!!.stopShimmer()
                binding?.shimmerViewHistory!!.visibility = View.GONE
            }
            binding?.orderHistoryRecycler?.visibility = View.GONE
            binding?.emptyHistory?.visibility = View.VISIBLE
        }
    }

    fun initRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(this )
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.orderHistoryRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.orderHistoryRecycler?.adapter = historyAdapter

        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        TODO("Not yet implemented")
    }

    override fun viewHistoryItem(item: Result) {
        TODO("Not yet implemented")
    }
}
