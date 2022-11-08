package com.boost.marketplace.ui.History_Orders

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2.GetPurchaseOrderResponseV2
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2.WidgetDetail
import com.boost.marketplace.Adapters.HistoryOrdersParentAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.ui.videos.HelpVideosBottomSheet
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryOrdersActivity: AppBaseActivity<ActivityHistoryOrdersBinding, HistoryOrdersViewModel>()
{

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


        historyAdapter = HistoryOrdersParentAdapter(LinkedHashMap<String, java.util.ArrayList<WidgetDetail>>())
        viewModel = ViewModelProviders.of(this).get(HistoryOrdersViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            WindowInsetsControllerCompat(window, window.decorView).setAppearanceLightStatusBars(false)
            window.statusBarColor = ResourcesCompat.getColor(resources, com.boost.cart.R.color.common_text_color, null)
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
                intent.getStringExtra("fpid")?:""
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

            if(it.Result.WidgetDetails.isNotEmpty()){
                updateRecycler(it)
                binding?.emptyHistory?.visibility=View.GONE
            }
            else{
                binding?.emptyHistory?.visibility=View.VISIBLE
            }
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

    fun updateRecycler(Result: GetPurchaseOrderResponseV2) {
        if (Result.StatusCode == 200 && Result.Result != null) {
            if (binding?.shimmerViewHistory?.isShimmerStarted!!) {
                binding?.shimmerViewHistory!!.stopShimmer()
                binding?.shimmerViewHistory!!.visibility = View.GONE
            }
            val list = LinkedHashMap<String, ArrayList<WidgetDetail>>()
            for(singleWidget in Result.Result.WidgetDetails){
                val dataString = singleWidget.CreatedOn
                val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
                val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
                if(list.containsKey(dateFormat.format(date))){
                    val tempList = list.get(dateFormat.format(date))?: arrayListOf<WidgetDetail>()
                    tempList.add(singleWidget)
                    list.put(dateFormat.format(date), tempList)
                }else{
                    list.put(dateFormat.format(date), arrayListOf(singleWidget))
                }
            }
            historyAdapter?.addupdates(list)
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
}
