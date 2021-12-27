package com.boost.marketplace.ui.Compare_Plans

import android.content.Intent
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksBinding
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.ui.History_Orders.HisoryOrdersViewModel
import com.boost.marketplace.ui.Invoice.InvoiceActivity
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanViewModel
import com.utsman.recycling.setupAdapter
import kotlinx.android.synthetic.main.activity_packs.view.*
import kotlinx.android.synthetic.main.item_myplan_features.view.*
import kotlinx.android.synthetic.main.item_order_history.view.*

class ComparePacksActivity: AppBaseActivity<ActivityComparePacksBinding, ComparePacksViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_compare_packs
    }


    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
    }


}