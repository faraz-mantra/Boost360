package com.boost.marketplace.ui.home

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceBinding

class MarketPlaceActivity : AppBaseActivity<ActivityMarketplaceBinding, MarketPlaceHomeViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_marketplace
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }


}