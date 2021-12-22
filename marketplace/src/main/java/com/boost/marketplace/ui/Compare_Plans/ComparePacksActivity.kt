package com.boost.marketplace.ui.Compare_Plans

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksBinding
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.ui.History_Orders.HisoryOrdersViewModel

class ComparePacksActivity: AppBaseActivity<ActivityComparePacksBinding, ComparePacksViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_history_orders
    }


    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        TODO("Not yet implemented")
    }
}