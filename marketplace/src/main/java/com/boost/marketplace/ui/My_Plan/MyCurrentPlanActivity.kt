package com.boost.marketplace.ui.My_Plan

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.holder.MyPlanFreeFeaturesViewHolder
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.ui.History_Orders.HistoryOrdersActivity

import com.boost.marketplace.ui.home.MarketPlaceActivity
import com.bumptech.glide.Glide
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_my_current_plan.*
import kotlinx.android.synthetic.main.item_myplan_features.*
import kotlinx.android.synthetic.main.item_myplan_features.view.*
import kotlinx.android.synthetic.main.item_order_history.view.*
import kotlinx.android.synthetic.main.item_packs_list.view.*

class MyCurrentPlanActivity : AppBaseActivity<ActivityMyCurrentPlanBinding,MyCurrentPlanViewModel>() {

    var featuresModel: FeaturesModel? = null
    var freeAddonsAdapter: AppBaseRecyclerViewAdapter<FeaturesModel>?=null

    var totalActiveWidgetCount = 0
    var totalActiveFreeWidgetCount = 0
    var totalActivePremiumWidgetCount = 0

    var totalFreeItemList: List<FeaturesModel>? = null
    var totalPaidItemList: List<FeaturesModel>? = null

    lateinit var progressDialog: ProgressDialog

    var purchasedPackages = ArrayList<String>()


    override fun getLayout(): Int {
        return R.layout.activity_my_current_plan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }
//
    override fun onCreateView() {
        super.onCreateView()
//
//        progressDialog = ProgressDialog(this)
//        var purchasedPack = intent.extras?.getStringArrayList("userPurchsedWidgets")
//        if (purchasedPack != null) {
//            purchasedPackages = purchasedPack
//        }


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

        binding?.help?.setOnClickListener {
            val intent = Intent(this, HistoryOrdersActivity::class.java)
            startActivity(intent)

        }

        binding?.addonsBack?.setOnClickListener {
            val intent = Intent(this, MarketPlaceActivity::class.java)
            startActivity(intent)

        }

     //   loadData()
//        initMVVM()
        initializeFreeAddonsRecyclerView()
       initializePaidAddonsRecyclerView()
//        shimmer_view_paidaddon.startShimmer()
//        shimmer_view_freeaddon.startShimmer()


//        top_line_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    }



//    private fun loadData() {
//        viewModel.loadUpdates(
//            (activity as? UpgradeActivity)?.getAccessToken()?:"",
//            (activity as UpgradeActivity).fpid!!,
//            (activity as UpgradeActivity).clientid
//        )
//    }


//    private fun initMVVM() {
//        viewModel.getActiveFreeWidgets().observe(this, Observer {
//            totalFreeItemList = it
//
//            totalActiveFreeWidgetCount = totalFreeItemList!!.size
//            totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount
//
//            setHeadlineTexts()
//
////            initializeFreeAddonsRecyclerView()
////            initializePaidAddonsRecyclerView()
//
//            if (totalFreeItemList != null) {
//                if (totalFreeItemList!!.size > 6) {
//                    if (shimmer_view_freeaddon.isShimmerStarted) {
//                        shimmer_view_freeaddon.stopShimmer()
//                        shimmer_view_freeaddon.visibility = View.GONE
//                    }
//                    val lessList = totalFreeItemList!!.subList(0, 6)
//                    updateFreeAddonsRecycler(lessList)
//                    myaddons_view1.visibility = View.VISIBLE
//                    read_more_less_free_addons.visibility = View.VISIBLE
//                } else {
//                    if (shimmer_view_freeaddon.isShimmerStarted) {
//                        shimmer_view_freeaddon.stopShimmer()
//                        shimmer_view_freeaddon.visibility = View.GONE
//                    }
//                    myaddons_view1.visibility = View.INVISIBLE
//                    read_more_less_free_addons.visibility = View.GONE
//                    updateFreeAddonsRecycler(totalFreeItemList!!)
//                }
//            }
//        })
//        viewModel.getActivePremiumWidgets().observe(this, Observer {
//            Log.i("getActiveWidgets", it.toString())
//            totalPaidItemList = it
//
//            totalActivePremiumWidgetCount = totalPaidItemList!!.size
//            totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount
//
//            setHeadlineTexts()
//
//            val paidItemsCount = totalPaidItemList!!.size
//
//            if (paidItemsCount != null && paidItemsCount > 0) {
//                if (shimmer_view_paidaddon.isShimmerStarted) {
//                    shimmer_view_paidaddon.stopShimmer()
//                    shimmer_view_paidaddon.visibility = View.GONE
//                }
//                paid_title.setText(totalPaidItemList!!.size.toString() + " Premium add-ons")
//                paid_subtitle.setText(totalPaidItemList!!.size.toString() + " Activated, 0 Syncing and 0 needs Attention")
//                read_more_less_paid_addons.visibility = View.VISIBLE
//                premium_account_flag.visibility = View.VISIBLE
//            } else {
//                if (shimmer_view_paidaddon.isShimmerStarted) {
//                    shimmer_view_paidaddon.stopShimmer()
//                    shimmer_view_paidaddon.visibility = View.GONE
//                }
//                paid_title.setText("No Premium add-ons active.")
//                paid_subtitle.setText("check out the recommended add-ons for your business")
//                read_more_less_paid_addons.visibility = View.GONE
//            }
//
//            if (totalPaidItemList != null) {
//                if (totalPaidItemList!!.size > 4) {
//                    val lessList = totalPaidItemList!!.subList(0, 4)
//                    updatePaidAddonsRecycler(lessList)
//                    myaddons_view2.visibility = View.VISIBLE
//                    read_more_less_paid_addons.visibility = View.VISIBLE
//                } else {
//                    myaddons_view2.visibility = View.INVISIBLE
//                    read_more_less_paid_addons.visibility = View.GONE
//                    updatePaidAddonsRecycler(totalPaidItemList!!)
//                }
//            }
//        })
//        viewModel.updatesLoader().observe(this, Observer {
//            if (it) {
//                val status = "Loading. Please wait..."
//                progressDialog.setMessage(status)
//                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
//                progressDialog.show()
//            } else {
//                progressDialog.dismiss()
//            }
//        })
//    }

//    private fun setHeadlineTexts() {
//        free_addons_name.setText("Currently using\n" + totalActiveWidgetCount + " add-ons")
//        bottom_free_addons.setText(totalActiveFreeWidgetCount.toString() + " free, " + totalActivePremiumWidgetCount.toString() + " premium")
//        free_addons_title.setText(totalActiveFreeWidgetCount.toString() + " Free Add-ons")
//    }

//    private fun updateFreeAddonsRecycler(list: List<FeaturesModel>) {
//        freeAddonsAdapter.addupdates(list)
//        recycler_freeaddons.adapter = freeAddonsAdapter
//        freeAddonsAdapter.notifyDataSetChanged()
//    }

//    private fun updatePaidAddonsRecycler(list: List<FeaturesModel>) {
//        paidaddons_layout.visibility = View.VISIBLE
//        paidAddonsAdapter.addupdates(list)
//        recycler_paidaddons.adapter = paidAddonsAdapter
//        paidAddonsAdapter.notifyDataSetChanged()
//    }

    fun initializeFreeAddonsRecyclerView() {
        val linearLayout = LinearLayoutManager(this)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        binding?.recycler?.apply {
            layoutManager = linearLayout

        }
        recycler.adapter = freeAddonsAdapter
    }

    fun initializePaidAddonsRecyclerView() {
        val linearLayout =LinearLayoutManager(this)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        binding?.recycler?.apply {
            layoutManager = linearLayout

        }
        premium_recycler.adapter = freeAddonsAdapter
    }

//    override fun onFreeAddonsClicked(v: View?) {
//        if (add_remove_layout.visibility == View.VISIBLE) {
//            add_remove_layout.visibility = View.GONE
//        } else {
//            val itemPosition = recycler_freeaddons.getChildAdapterPosition(v!!)
//
//        }
//    }
//
//    override fun onPaidAddonsClicked(v: View?) {
//        if (add_remove_layout.visibility == View.VISIBLE) {
//            add_remove_layout.visibility = View.GONE
//        } else {
//            val itemPosition = recycler_paidaddons.getChildAdapterPosition(v!!)
//
//        }
//    }


    private fun cardView1Visibilty() {
        if (expandableView1.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView1, AutoTransition())
            expandableView1.visibility = View.VISIBLE
            arrowBtn1.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        } else {
            TransitionManager.beginDelayedTransition(cardView1, AutoTransition())
            expandableView1.visibility = View.GONE
            arrowBtn1.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }



    private fun cardViewVisibilty() {
        if (expandableView.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            expandableView.visibility = View.VISIBLE
            arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        } else {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            expandableView.visibility = View.GONE
            arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }


}