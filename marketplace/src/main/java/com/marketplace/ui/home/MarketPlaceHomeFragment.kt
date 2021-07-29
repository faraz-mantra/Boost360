package com.marketplace.ui.home

import com.framework.models.BaseViewModel
import com.marketplace.R
import com.marketplace.base.AppBaseFragment
import com.marketplace.databinding.FrgamentMarketplaceHomeBinding

class MarketPlaceHomeFragment :AppBaseFragment<FrgamentMarketplaceHomeBinding,BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.frgament_marketplace_home
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
    }
}