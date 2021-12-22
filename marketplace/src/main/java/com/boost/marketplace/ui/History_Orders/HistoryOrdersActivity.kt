package com.boost.marketplace.ui.History_Orders

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityHistoryOrdersBinding
import com.boost.marketplace.ui.Marketplace_Offers.MarketPlaceOffersViewModel

class HistoryOrdersActivity: AppBaseActivity<ActivityHistoryOrdersBinding, HisoryOrdersViewModel>() {


    override fun getLayout(): Int {
        return R.layout.activity_history_orders
    }


    override fun getViewModelClass(): Class<HisoryOrdersViewModel> {
        return HisoryOrdersViewModel::class.java
    }

}