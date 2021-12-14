package com.boost.marketplace.ui.home

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceBinding
import com.boost.marketplace.ui.details.FeatureDetailsViewModel

class MarketPlaceActivity : AppBaseActivity<ActivityMarketplaceBinding, FeatureDetailsViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_marketplace
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }


}