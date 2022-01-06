package com.boost.marketplace.ui.My_Plan

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.boost.dbcenterapi.infra.api.models.test.TestData
import com.boost.dbcenterapi.infra.api.models.test.getData
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.infra.api.models.test.*
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter

import com.framework.webengageconstant.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.VisitingCardSheet
import kotlinx.android.synthetic.main.activity_my_current_plan.*

import kotlinx.android.synthetic.main.item_myplan_features.*
import kotlinx.android.synthetic.main.item_myplan_features.view.*
import kotlinx.android.synthetic.main.item_order_history.view.*
import kotlinx.android.synthetic.main.item_packs_list.view.*

class MyCurrentPlanActivity : AppBaseActivity<ActivityMyCurrentPlanBinding,MyCurrentPlanViewModel>(),
    RecyclerItemClickListener {


    private var freeAddonsAdapter: AppBaseRecyclerViewAdapter<TestData>? = null
    private var freeAddons: AppBaseRecyclerViewAdapter<Packs_Data>? = null
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
                adapter = freeAddonsAdapter
            }

        }
        //   recycler.adapter = freeAddonsAdapter
    }

    private fun initializePaidAddonsRecyclerView() {

        binding?.premiumRecycler?.apply {
            if (freeAddons == null) {
                freeAddons = AppBaseRecyclerViewAdapter(
                    this@MyCurrentPlanActivity,
                    getDatas2(RecyclerViewItemType.FEATURES_MODEL.ordinal),
                    this@MyCurrentPlanActivity
                )
                adapter = freeAddons

            }

        }
    }

    private fun cardView1Visibilty() {
        if (binding?.expandableView1?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView1, AutoTransition())
            binding!!.expandableView1.visibility = View.VISIBLE
            binding?.arrowBtn1?.animate()?.rotation(180f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(cardView1, AutoTransition())
            binding?.expandableView1?.visibility = View.GONE
            binding?.arrowBtn1?.animate()?.rotation(0f)?.start()
        }
    }


    private fun cardViewVisibilty() {
        if (binding?.expandableView?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            binding?.expandableView!!.visibility = View.VISIBLE
            binding?.arrowBtn?.animate()?.rotation(180f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            binding?.expandableView?.visibility = View.GONE
            binding?.arrowBtn?.animate()?.rotation(0f)?.start()
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

//        val dialog = BottomSheetDialog(this@MyCurrentPlanActivity)
//        val view = layoutInflater.inflate(R.layout.bottom_sheet_myplan, null)
//        dialog.setContentView(view)
//        dialog.show()
        val dialogCard = MyPlanBottomSheet()
      //  dialogCard.setData(getLocalSession(it), shareChannelText)
        dialogCard.show(this@MyCurrentPlanActivity.supportFragmentManager, MyPlanBottomSheet::class.java.name)




    }



}