package com.boost.marketplace.ui.home

import android.os.Bundle
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceBinding

class MarketPlaceActivity : AppBaseActivity<ActivityMarketplaceBinding, MarketPlaceViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_marketplace
    }

    override fun getViewModelClass(): Class<MarketPlaceViewModel> {
        return MarketPlaceViewModel::class.java
    }


}