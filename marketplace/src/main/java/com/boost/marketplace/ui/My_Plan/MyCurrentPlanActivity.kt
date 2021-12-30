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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.holder.MyPlanFreeFeaturesViewHolder
import com.boost.marketplace.infra.api.models.test.*
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

class MyCurrentPlanActivity : AppBaseActivity<ActivityMyCurrentPlanBinding,MyCurrentPlanViewModel>(),
    RecyclerItemClickListener {


   private var freeAddonsAdapter: AppBaseRecyclerViewAdapter<TestData>?=null
    private var freeAddons: AppBaseRecyclerViewAdapter<Packs_Data>?=null
  //  private var adapterPacks: AppBaseRecyclerViewAdapter<Packs_Data>? = null


    override fun getLayout(): Int {
        return R.layout.activity_my_current_plan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

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



        initializeFreeAddonsRecyclerView()
        initializePaidAddonsRecyclerView()

    }

    private fun initializeFreeAddonsRecyclerView() {

        binding?.recycler?.apply {
            if (freeAddonsAdapter == null) {
               freeAddonsAdapter = AppBaseRecyclerViewAdapter(
                    this@MyCurrentPlanActivity,
                    getData(RecyclerViewItemType.FEATURES_MODEL.ordinal), this@MyCurrentPlanActivity
                )
                adapter=freeAddonsAdapter
            }

        }
     //   recycler.adapter = freeAddonsAdapter
    }

    private fun initializePaidAddonsRecyclerView() {

        binding?.premiumRecycler?.apply {
            if (freeAddons == null) {
                freeAddons = AppBaseRecyclerViewAdapter(
                    this@MyCurrentPlanActivity,
                    getDatas2(RecyclerViewItemType.FEATURES_MODEL.ordinal), this@MyCurrentPlanActivity
                )
                adapter = freeAddons

            }

        }
    }

    private fun cardView1Visibilty() {
        if (binding?.expandableView1?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView1, AutoTransition())
            binding!!.expandableView1.visibility = View.VISIBLE
           binding?. arrowBtn1?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        } else {
            TransitionManager.beginDelayedTransition(cardView1, AutoTransition())
           binding?.expandableView1?.visibility = View.GONE
           binding?.arrowBtn1?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }



    private fun cardViewVisibilty() {
        if (binding?.expandableView?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            binding?.expandableView!!.visibility = View.VISIBLE
           binding?.arrowBtn?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        } else {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            binding?.expandableView?.visibility = View.GONE
            binding?.arrowBtn?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        binding?.expandableView?.setOnClickListener {
            Toast.makeText(this, "Clicked on More Button", Toast.LENGTH_LONG).show()
        }

    }


}