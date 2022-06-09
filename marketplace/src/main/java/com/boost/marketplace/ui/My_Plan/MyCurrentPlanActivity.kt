package com.boost.marketplace.ui.My_Plan

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.utils.WebEngageController
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.Adapters.FreeAddonsAdapter
import com.boost.marketplace.Adapters.PaidAddonsAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.interfaces.CompareBackListener
import com.boost.marketplace.ui.History_Orders.HistoryOrdersActivity
import com.boost.marketplace.ui.popup.myplan.MyPlanBottomSheet
import com.boost.marketplace.ui.popup.myplan.MyPlanBottomSheetFreeAddons
import com.boost.marketplace.ui.videos.HelpVideosBottomSheet
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.webengageconstant.ADDONS_MARKETPLACE_MY_ADDONS_LOADED
import com.framework.webengageconstant.MY_ADDONS
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_my_current_plan.*

//import kotlinx.android.synthetic.main.activity_my_current_plan.*
//import kotlinx.android.synthetic.main.activity_my_current_plan.all_addons_view_layout


class MyCurrentPlanActivity :
    AppBaseActivity<ActivityMyCurrentPlanBinding, MyCurrentPlanViewModel>(),
    CompareBackListener, com.boost.marketplace.interfaces.MyAddonsListener {


    lateinit var freeAddonsAdapter: FreeAddonsAdapter
    lateinit var paidAddonsAdapter: PaidAddonsAdapter

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var fpid: String? = null

    //    var totalActiveWidgetCount = 0
    var totalActiveFreeWidgetCount = 0
    var totalActivePremiumWidgetCount = 0

    var totalFreeItemList: List<FeaturesModel>? = null
    var totalPaidItemList: List<FeaturesModel>? = null

    lateinit var progressDialog: ProgressDialog
    var purchasedPackages = ArrayList<String>()

    companion object {
        fun newInstance() = MyCurrentPlanActivity()
    }


    override fun getLayout(): Int {
        return R.layout.activity_my_current_plan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()

        fpid = intent.getStringExtra("fpid")
        viewModel.setApplicationLifecycle(application, this)

        viewModel = ViewModelProviders.of(this)
            .get(MyCurrentPlanViewModel::class.java)

        progressDialog = ProgressDialog(this)
        var purchasedPack = intent.extras?.getStringArrayList("userPurchsedWidgets")
        if (purchasedPack != null) {
            purchasedPackages = purchasedPack
        }

        freeAddonsAdapter = FreeAddonsAdapter(this, ArrayList(), this)
        paidAddonsAdapter = PaidAddonsAdapter(this, ArrayList(), this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        loadData()
        initMVVM()
        initializeFreeAddonsRecyclerView()
        initializePaidAddonsRecyclerView()

        addons_back.setOnClickListener {
            super.onBackPressed()
        }

        binding?.history?.setOnClickListener {
            val intent= Intent(this, HistoryOrdersActivity::class.java)
            intent.putExtra("fpid",fpid)
            startActivity(intent)

        }

        binding?.renwhistry?.setOnClickListener {
            val intent= Intent(this, HistoryOrdersActivity::class.java)
            intent.putExtra("fpid",fpid)
            startActivity(intent)
        }

           WebEngageController.trackEvent(ADDONS_MARKETPLACE_MY_ADDONS_LOADED, MY_ADDONS, NO_EVENT_VALUE)

        search_icon.setOnClickListener {
            search_icon.visibility = View.GONE
            search_layout.visibility = View.VISIBLE
        }

        clear_text.setOnClickListener {
            search_value.setText("")
            search_icon.visibility = View.VISIBLE
            search_layout.visibility = View.GONE
        }

        search_value.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!=null && p0?.length!! >3){
                    updateAllItemBySearchValue(p0.toString())
                } else{
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                    updatePaidAddonsRecycler(totalPaidItemList!!)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        help.setOnClickListener {
            val videoshelp = HelpVideosBottomSheet()
            videoshelp.show(this.supportFragmentManager, HelpVideosBottomSheet::class.java.name)
        }

        binding?.arrowBtn?.setOnClickListener {
            cardViewVisibilty()
        }
        binding?.cardView?.setOnClickListener {
            cardViewVisibilty()
        }

        binding?.arrowBtn1?.setOnClickListener {
            cardView1Visibilty()
        }

        binding?.cardView1?.setOnClickListener {
            cardView1Visibilty()
        }

    }

    fun updateAllItemBySearchValue(searchValue: String){
        var freeitemList: java.util.ArrayList<FeaturesModel> = arrayListOf()
        var paiditemList: java.util.ArrayList<FeaturesModel> = arrayListOf()

        for(singleFreeFeature in totalFreeItemList!!){
            if(singleFreeFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
                freeitemList.add(singleFreeFeature)
            }
        }
        updateFreeAddonsRecycler(freeitemList)

        for(singlePaidFeature in totalFreeItemList!!){
            if(singlePaidFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
                paiditemList.add(singlePaidFeature)
            }
        }
        updatePaidAddonsRecycler(paiditemList)
    }

    private fun cardView1Visibilty() {
        if (binding?.expandableView1?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding!!.expandableView1.visibility = View.VISIBLE
            binding?.arrowBtn1?.animate()?.rotation(180f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding?.expandableView1?.visibility = View.GONE
            binding?.arrowBtn1?.animate()?.rotation(0f)?.start()
        }
    }


    private fun cardViewVisibilty() {
        if (binding?.expandableView?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView!!.visibility = View.VISIBLE
            binding?.arrowBtn?.animate()?.rotation(180f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView?.visibility = View.GONE
            binding?.arrowBtn?.animate()?.rotation(0f)?.start()
        }
    }

    private fun loadData() {
        try {
            viewModel.loadPurchasedItems(
                intent.getStringExtra("fpid") ?: "",
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun initMVVM() {

        viewModel.getActivePremiumWidgets().observe(this,androidx.lifecycle.Observer{
            totalPaidItemList = it
            totalActivePremiumWidgetCount = totalPaidItemList!!.size
//            totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount

            val paidItemsCount = totalPaidItemList!!.size

            if (paidItemsCount != null && paidItemsCount > 0) {
                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                    binding?.shimmerViewHistory?.stopShimmer()
                    binding?.shimmerViewHistory?.visibility = View.GONE
                    binding?.nestedscroll?.visibility=View.VISIBLE
                }
                binding?.paidTitle1?.text = totalPaidItemList!!.size.toString() + " Premium add-ons"
                binding?.paidSubtitle1?.text =
                    totalPaidItemList!!.size.toString() + " Activated, 0 Syncing and 0 needs Attention"
//                premium_account_flag.visibility = View.VISIBLE
            } else {
                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                    binding?.shimmerViewHistory?.stopShimmer()
                    binding?.shimmerViewHistory?.visibility = View.GONE
                }
                binding?.paidTitle1?.text = "No Premium add-ons active."
                binding?.paidSubtitle1?.text = "check out the recommended add-ons for your business"
            }

            if (totalPaidItemList != null) {
                if (totalPaidItemList!!.size > 1) {
                    updatePaidAddonsRecycler(totalPaidItemList!!)
//                    myaddons_view2.visibility = View.VISIBLE
                } else {
//                    myaddons_view2.visibility = View.INVISIBLE
                    updatePaidAddonsRecycler(totalPaidItemList!!)
                }
            }
            updatePaidAddonsRecycler(it)
        })

        viewModel.getActiveFreeWidgets().observe(this,androidx.lifecycle.Observer{
            totalFreeItemList = it
            totalActiveFreeWidgetCount = totalFreeItemList!!.size
            binding?.paidTitle?.text = totalActiveFreeWidgetCount.toString() + " Free Add-ons"
            binding?.paidSubtitle?.text =
                totalActiveFreeWidgetCount!!.toString() + " Activated, 0 Syncing and 0 needs Attention"
            if (totalFreeItemList != null) {
                if (totalFreeItemList!!.size > 0) {
                    if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                        binding?.shimmerViewHistory?.stopShimmer()
                        binding?.shimmerViewHistory?.visibility = View.GONE
                        binding?.nestedscroll?.visibility=View.VISIBLE
                    }
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                    binding?.expandableView?.visibility = View.VISIBLE
                } else {
                    if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                        binding?.shimmerViewHistory?.stopShimmer()
                        binding?.shimmerViewHistory?.visibility = View.GONE
                    }
                    binding?.expandableView?.visibility = View.INVISIBLE
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                }
            }
        })
        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
    }

    private fun updateFreeAddonsRecycler(list: List<FeaturesModel>) {
        freeAddonsAdapter.addupdates(list)
        binding?.recycler?.adapter = freeAddonsAdapter
        freeAddonsAdapter.notifyDataSetChanged()
    }

    private fun updatePaidAddonsRecycler(list: List<FeaturesModel>) {
        binding?.premiumRecycler?.visibility = View.VISIBLE
        paidAddonsAdapter.addupdates(list)
        paidAddonsAdapter.notifyDataSetChanged()
    }

    private fun initializeFreeAddonsRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.recycler?.apply {
            layoutManager = linearLayoutManager
            binding?.recycler?.adapter = freeAddonsAdapter
        }
    }

    private fun initializePaidAddonsRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.premiumRecycler?.apply {
            layoutManager = linearLayoutManager
            binding?.premiumRecycler?.adapter = paidAddonsAdapter
        }
    }

    override fun onFreeAddonsClicked(item: FeaturesModel) {
        val dialogCard = MyPlanBottomSheetFreeAddons()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        dialogCard.arguments = args
        dialogCard.show(this@MyCurrentPlanActivity.supportFragmentManager, MyPlanBottomSheetFreeAddons::class.java.name)
    }

    override fun onPaidAddonsClicked(item: FeaturesModel) {

        val dialogCard = MyPlanBottomSheet()
        val args = Bundle()
        args.putString("fpid",fpid)
        args.putString("bundleData", Gson().toJson(item))
        dialogCard.arguments = args
        dialogCard.show(this@MyCurrentPlanActivity.supportFragmentManager, MyPlanBottomSheet::class.java.name)
    }

    override fun backComparePress() {

    }

}