package com.boost.marketplace.ui.My_Plan

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding

class MyPlanHistoryOrdersActivity: AppBaseActivity<ActivityHistoryOrdersBinding, MyPlanHisoryOrdersViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_history_orders
    }


    override fun getViewModelClass(): Class<MyPlanHisoryOrdersViewModel> {
        TODO("Not yet implemented")
    }
}