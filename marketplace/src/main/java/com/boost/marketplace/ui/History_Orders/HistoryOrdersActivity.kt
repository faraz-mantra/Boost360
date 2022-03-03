package com.boost.marketplace.ui.History_Orders

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.marketplace.Adapters.HistoryOrdersParentAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.interfaces.HistoryFragmentListener
import com.boost.marketplace.ui.videos.HelpVideosBottomSheet
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth

class HistoryOrdersActivity: AppBaseActivity<ActivityHistoryOrdersBinding, HistoryOrdersViewModel>(),
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


    override fun getViewModelClass(): Class<HistoryOrdersViewModel> {
        return HistoryOrdersViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()


        historyAdapter = HistoryOrdersParentAdapter(ArrayList(), this)
        viewModel = ViewModelProviders.of(this).get(HistoryOrdersViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        loadData()
        initMVVM()
        initRecyclerView()
        binding?.shimmerViewHistory?.startShimmer()
        binding?.addonsBack?.setOnClickListener {
            finish()
        }

        binding?.help?.setOnClickListener {
            val videoshelp = HelpVideosBottomSheet()
            videoshelp.show(this.supportFragmentManager, HelpVideosBottomSheet::class.java.name)
        }

    }

    private fun loadData() {
        try {
            viewModel.loadPurchasedItems(
                (this).getAccessToken() ?:"",
                intent.getStringExtra("fpid")?:"" ,
                clientid
            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
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

    }

    override fun viewHistoryItem(item: Result) {
    }
}
